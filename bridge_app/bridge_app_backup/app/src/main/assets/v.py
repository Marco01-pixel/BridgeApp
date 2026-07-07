#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
DEMAND RADAR ENGINE v3.2 + RADIO ENVIRONMENT ANALYZER + BLE BROADCAST
======================================================================
Sistema completo de detección de demanda + análisis de entorno radioeléctrico
+ Transmisión BLE continua de prompt (zlib + fragmentación + ciclo infinito)

CORRECCIONES v3.2:
- Imports corregidos (requests al inicio)
- Firmas de métodos unificadas (bridge_app)
- Código Java corregido (Foreground Service, permisos, tema)
- compileSdk 34, namespace correcto, Gradle 8.2
- Función start_web_server implementada
- MainActivity sin finish() inmediato
"""
from __future__ import annotations
import os
import sys
import json
import time
import threading
import math
import random
import uuid
import csv
import io
import signal
import subprocess
import platform
import zlib
import base64
import hashlib
import shutil
import socket
from abc import ABC, abstractmethod
from dataclasses import dataclass, field, asdict
from datetime import datetime, timezone, timedelta
from typing import Dict, List, Optional, Tuple, Any, Callable, Union, Protocol, runtime_checkable
from collections import deque, defaultdict
from enum import Enum
from pathlib import Path
from http.server import HTTPServer, BaseHTTPRequestHandler

# Intentar importar requests; si no está, usar fallback con urllib
try:
    import requests
    HAS_REQUESTS = True
except ImportError:
    HAS_REQUESTS = False
    import urllib.request
    import urllib.error
    import urllib.parse

# ================================================================================
# FALLBACK PARA REQUESTS (si no está instalado)
# ================================================================================
class _RequestsFallback:
    """Fallback mínimo usando urllib para evitar dependencia de requests."""
    class Response:
        def __init__(self, status_code, text="", json_data=None):
            self.status_code = status_code
            self.text = text
            self._json = json_data
        def json(self):
            if self._json is not None:
                return self._json
            return json.loads(self.text)

    @staticmethod
    def get(url, timeout=5, params=None):
        if params:
            url = url + "?" + urllib.parse.urlencode(params)
        try:
            req = urllib.request.Request(url, headers={"User-Agent": "RadarBridge/3.2"})
            with urllib.request.urlopen(req, timeout=timeout) as resp:
                data = resp.read().decode("utf-8")
                return _RequestsFallback.Response(resp.status, data)
        except urllib.error.HTTPError as e:
            return _RequestsFallback.Response(e.code, e.read().decode("utf-8", errors="replace"))
        except Exception:
            raise

if not HAS_REQUESTS:
    requests = _RequestsFallback()

# ================================================================================
# CONSTANTE GLOBAL: PROMPT PARA TRANSMISIÓN BLE
# ================================================================================
MEJOR_OPCION_PROMPT_TEXTO = """
<system_directive lang="dsl-decision-engine" version="4.0">
  <agent_role>
    agent_id: "MEJOR_OPCION"
    task_domain: ride_hailing_offer_evaluation
  </agent_role>
  <hardcoded_rules>
    <economic_block>
      rule_01: if price < 3.13 -> AUTO_REJECT
      rule_02: if price >= 3.13 AND price < 5.13 -> EVALUATE_RISK
      rule_03: if price >= 5.13 AND price < 10.13 -> ACCEPT_STANDARD
      rule_04: if price >= 10.13 -> IMMEDIATE_ACCEPT
    </economic_block>
  </hardcoded_rules>
</system_directive>
"""

# ================================================================================
# SECCION 1: ESTRUCTURAS DE DATOS
# ================================================================================
class SupplyLevel(Enum):
    CRITICAL = "critical"
    LOW = "low"
    MEDIUM = "medium"
    HIGH = "high"
    UNKNOWN = "unknown"

@dataclass
class GeoPoint:
    latitude: float = 0.0
    longitude: float = 0.0
    def is_valid(self) -> bool:
        return -90 <= self.latitude <= 90 and -180 <= self.longitude <= 180
    def distance_km(self, other: 'GeoPoint') -> float:
        R = 6371.0
        lat1, lon1 = math.radians(self.latitude), math.radians(self.longitude)
        lat2, lon2 = math.radians(other.latitude), math.radians(other.longitude)
        dlat, dlon = lat2 - lat1, lon2 - lon1
        a = math.sin(dlat/2)**2 + math.cos(lat1)*math.cos(lat2)*math.sin(dlon/2)**2
        return R * 2 * math.atan2(math.sqrt(a), math.sqrt(1-a))
    def to_dict(self) -> Dict[str, float]:
        return {"latitude": self.latitude, "longitude": self.longitude}

@dataclass
class Zone:
    zone_id: str
    name: str
    location: GeoPoint = field(default_factory=GeoPoint)
    metadata: Dict[str, Any] = field(default_factory=dict)
    tags: List[str] = field(default_factory=list)
    enabled: bool = True
    priority_weight: float = 1.0
    def to_dict(self) -> Dict[str, Any]:
        return {"zone_id": self.zone_id, "name": self.name,
                "location": self.location.to_dict(), "metadata": self.metadata,
                "tags": self.tags, "enabled": self.enabled,
                "priority_weight": self.priority_weight}

@dataclass
class ZoneMetrics:
    zone_id: str
    timestamp: datetime = field(default_factory=lambda: datetime.now(timezone.utc))
    demand_score: float = 0.0
    supply_level: SupplyLevel = SupplyLevel.UNKNOWN
    surge_detected: bool = False
    surge_multiplier: float = 1.0
    average_wait_time: int = 0
    average_value: float = 0.0
    available_capacity: int = 0
    active_requests: int = 0
    is_simulated: bool = False
    raw_data: Dict[str, Any] = field(default_factory=dict)
    quality_index: float = 0.0
    efficiency_ratio: float = 0.0
    fuzzy_trust: float = 0.8
    @property
    def is_hotspot(self) -> bool: return self.demand_score >= 7.0
    @property
    def is_critical(self) -> bool: return self.demand_score >= 9.0
    @property
    def wait_time_minutes(self) -> int: return self.average_wait_time // 60
    def to_dict(self) -> Dict[str, Any]:
        return {"zone_id": self.zone_id, "timestamp": self.timestamp.isoformat(),
                "demand_score": self.demand_score,
                "supply_level": self.supply_level.value,
                "surge_multiplier": self.surge_multiplier,
                "average_wait_time": self.average_wait_time,
                "average_value": self.average_value,
                "is_hotspot": self.is_hotspot}

@dataclass
class Opportunity:
    opportunity_id: str = field(default_factory=lambda: str(uuid.uuid4())[:12])
    timestamp: datetime = field(default_factory=lambda: datetime.now(timezone.utc))
    zone_id: str = ""
    zone_name: str = ""
    location: Optional[GeoPoint] = None
    demand_score: float = 0.0
    surge_multiplier: float = 1.0
    estimated_value: float = 0.0
    hourly_potential: float = 0.0
    wait_time_seconds: int = 0
    priority_score: float = 0.0
    confidence: float = 0.0
    recommendation: str = ""
    risk_level: str = "medium"
    trend_direction: str = "stable"
    metadata: Dict[str, Any] = field(default_factory=dict)
    def to_dict(self) -> Dict[str, Any]:
        return {"opportunity_id": self.opportunity_id,
                "timestamp": self.timestamp.isoformat(),
                "zone_id": self.zone_id, "zone_name": self.zone_name,
                "demand_score": self.demand_score,
                "surge_multiplier": self.surge_multiplier,
                "hourly_potential": self.hourly_potential,
                "priority_score": self.priority_score,
                "recommendation": self.recommendation}

@dataclass
class RadarConfig:
    scan_interval_seconds: int = 1
    min_demand_threshold: float = 5.0
    hotspot_threshold: float = 7.0
    critical_threshold: float = 9.0
    hot_zones_only: bool = False
    max_opportunities_cached: int = 200
    max_consecutive_errors: int = 10
    watchdog_interval_seconds: int = 30
    error_backoff_base: int = 2
    error_backoff_max: int = 30
    scan_timeout_per_zone: float = 3.0
    enable_watchdog: bool = True
    enable_alerts: bool = True
    enable_analytics: bool = True
    enable_prediction: bool = True
    history_max_per_zone: int = 100
    alerts_max_cached: int = 50
    priority_weights: Dict[str, float] = field(
        default_factory=lambda: {"demand_score": 0.35, "surge_multiplier": 0.25,
                                  "hourly_potential": 0.25, "confidence": 0.15})
    max_radius_km: float = 30.0

# ================================================================================
# SECCION 2: INTERFAZ DE PROVEEDOR DE DATOS
# ================================================================================
@runtime_checkable
class DataProvider(Protocol):
    def get_zone_data(self, zone: Zone) -> Dict[str, Any]: ...
    def is_available(self) -> bool: ...
    def get_provider_name(self) -> str: ...

class DataProviderBase(ABC):
    @abstractmethod
    def get_zone_data(self, zone: Zone) -> Dict[str, Any]: pass
    @abstractmethod
    def is_available(self) -> bool: pass
    @abstractmethod
    def get_provider_name(self) -> str: pass

# ================================================================================
# SECCION 3: CALCULADORA DE DEMANDA
# ================================================================================
class DemandCalculator:
    @staticmethod
    def calculate_demand_score(wait_times, surge_multipliers, capacity, requests):
        if not wait_times: return 0.0
        avg_wait = sum(wait_times) / len(wait_times)
        if avg_wait < 120: wait_factor = 2.0
        elif avg_wait < 300: wait_factor = 4.0
        elif avg_wait < 480: wait_factor = 6.5
        elif avg_wait < 720: wait_factor = 8.0
        else: wait_factor = 9.5
        max_surge = max(surge_multipliers) if surge_multipliers else 1.0
        surge_factor = min(10.0, max_surge * 4.0)
        if capacity > 0:
            saturation_factor = min(10.0, (requests / capacity) * 5.0)
        else:
            saturation_factor = 8.0 if requests > 0 else 0.0
        score = wait_factor * 0.40 + surge_factor * 0.35 + saturation_factor * 0.25
        if max_surge > 1.0:
            score = min(10.0, score + (max_surge - 1.0) * 2.0)
        return round(max(0.0, min(10.0, score)), 2)

    @staticmethod
    def determine_supply_level(avg_wait):
        if avg_wait >= 720: return SupplyLevel.CRITICAL
        elif avg_wait >= 480: return SupplyLevel.LOW
        elif avg_wait >= 300: return SupplyLevel.MEDIUM
        elif avg_wait > 0: return SupplyLevel.HIGH
        return SupplyLevel.UNKNOWN

    @staticmethod
    def calculate_priority_score(demand_score, surge_multiplier, hourly_potential,
                                  confidence=0.8, weights=None):
        w = weights or {"demand_score": 0.35, "surge_multiplier": 0.25,
                        "hourly_potential": 0.25, "confidence": 0.15}
        demand_comp = (demand_score / 10.0) * 100.0
        surge_comp = min(100.0, max(0.0, (surge_multiplier - 1.0) * 50.0))
        value_comp = min(100.0, hourly_potential)
        conf_comp = confidence * 100.0
        priority = (demand_comp * w.get("demand_score", 0.35) +
                    surge_comp * w.get("surge_multiplier", 0.25) +
                    value_comp * w.get("hourly_potential", 0.25) +
                    conf_comp * w.get("confidence", 0.15))
        return round(priority, 2)

    @staticmethod
    def calculate_hourly_potential(avg_value, transactions_per_hour, surge_multiplier):
        return round(avg_value * transactions_per_hour * surge_multiplier, 2)

    @staticmethod
    def generate_recommendation(demand_score, surge, trend="stable"):
        trend_symbol = {"up": ">>>", "down": "<<<", "stable": "---"}.get(trend, "---")
        if demand_score >= 8.5 and surge >= 1.5:
            return "URGENTE - Demanda extrema {}".format(trend_symbol)
        elif demand_score >= 8.0:
            return "IR AHORA - Demanda muy alta {}".format(trend_symbol)
        elif demand_score >= 7.0 and surge >= 1.2:
            return "RECOMENDADO - Alta demanda {}".format(trend_symbol)
        elif demand_score >= 7.0:
            return "Recomendado - Buena demanda {}".format(trend_symbol)
        elif demand_score >= 5.5:
            return "Monitorear - Demanda moderada {}".format(trend_symbol)
        else:
            return "Evitar - Demanda mínima {}".format(trend_symbol)

# ================================================================================
# SECCION 4: DETECTOR DE HOTSPOTS
# ================================================================================
class HotspotDetector:
    def __init__(self, threshold=7.0):
        self.threshold = threshold
        self._history = {}
        self._history_max = 20

    def update_history(self, zone_id, score, timestamp=None):
        if zone_id not in self._history:
            self._history[zone_id] = deque(maxlen=self._history_max)
        self._history[zone_id].append({"score": score, "timestamp": timestamp or time.time()})

    def get_trend_direction(self, zone_id):
        history = self._history.get(zone_id, deque())
        if len(history) < 2: return "stable"
        recent = list(history)[-3:]
        scores = [h["score"] for h in recent]
        if scores[-1] > scores[0] * 1.1: return "up"
        if scores[-1] < scores[0] * 0.85: return "down"
        return "stable"

    def find_hotspots(self, metrics, include_trending=False):
        results = []
        for zone_id, m in metrics.items():
            if m.demand_score >= self.threshold:
                trend = self.get_trend_direction(zone_id)
                results.append((zone_id, m, trend, 0.0))
        results.sort(key=lambda x: x[1].demand_score, reverse=True)
        return results

# ================================================================================
# SECCION 5: SIMULADOR DE DATOS
# ================================================================================
class SimulatedDataProvider(DataProviderBase):
    def __init__(self, seed=None):
        self._rng = random.Random(seed)
        self._available = True
        self._call_count = 0

    def get_zone_data(self, zone):
        self._call_count += 1
        demand = self._rng.uniform(3.0, 9.5)
        if demand >= 8.0: surge = 1.5 + self._rng.uniform(0, 1.0)
        elif demand >= 6.0: surge = 1.2 + self._rng.uniform(0, 0.5)
        else: surge = 1.0
        surge = round(min(surge, 2.5), 2)
        wait = int(120 + (10 - demand) * 60)
        wait_times = [max(60, int(wait * self._rng.uniform(0.8, 1.2))) for _ in range(3)]
        surges = [round(surge * self._rng.uniform(0.95, 1.05), 2) for _ in range(3)]
        values = [round(10.0 * surge * self._rng.uniform(0.9, 1.1), 2) for _ in range(3)]
        capacity = self._rng.randint(5, 15)
        requests = self._rng.randint(3, 20)
        return {"wait_times": wait_times, "values": values,
                "surge_multipliers": surges, "capacity": capacity,
                "requests": requests, "_simulated_demand": round(demand, 2),
                "_simulated_surge": surge}

    def is_available(self): return self._available
    def get_provider_name(self): return "SimulatedDataProvider"

# ================================================================================
# SECCION 6: MOTOR DE RADAR PRINCIPAL
# ================================================================================
class RadarEngine:
    def __init__(self, zones, data_provider, config=None):
        self.zones = {z.zone_id: z for z in zones}
        self.provider = data_provider
        self.config = config or RadarConfig()
        self.calculator = DemandCalculator()
        self.detector = HotspotDetector(threshold=self.config.hotspot_threshold)
        self._running = False
        self._scan_thread = None
        self._lock = threading.RLock()
        self._zone_metrics = {}
        self._opportunities = deque(maxlen=self.config.max_opportunities_cached)
        self._total_scans = 0
        self._successful_scans = 0
        self._error_count = 0
        self._consecutive_errors = 0
        self._start_time = None
        self._dynamic_zone_updater = None

    def start(self):
        if self._running: return
        self._running = True
        self._start_time = time.time()
        self._scan_thread = threading.Thread(target=self._scan_loop, daemon=True)
        self._scan_thread.start()
        print("[+] Radar iniciado - {} zonas".format(len(self.zones)))

    def stop(self):
        self._running = False
        if self._scan_thread:
            self._scan_thread.join(timeout=2)

    def set_dynamic_zone_updater(self, updater):
        self._dynamic_zone_updater = updater

    def _perform_scan(self):
        opportunities = []
        for zone in list(self.zones.values()):
            if not self._running or not zone.enabled: continue
            try:
                raw_data = self.provider.get_zone_data(zone)
                if not raw_data: continue
                metrics = self._calculate_metrics(zone.zone_id, raw_data)
                self.detector.update_history(zone.zone_id, metrics.demand_score)
                with self._lock:
                    self._zone_metrics[zone.zone_id] = metrics
                trend = self.detector.get_trend_direction(zone.zone_id)
                opportunity = self._create_opportunity(zone, metrics, trend)
                opportunities.append(opportunity)
            except Exception as e:
                self._error_count += 1
                self._consecutive_errors += 1
        with self._lock:
            self._opportunities.extend(opportunities)
        return opportunities

    def _calculate_metrics(self, zone_id, raw_data):
        wait_times = raw_data.get("wait_times", [])
        values = raw_data.get("values", [])
        surges = raw_data.get("surge_multipliers", [])
        capacity = raw_data.get("capacity", 0)
        requests_count = raw_data.get("requests", 0)
        demand_score = self.calculator.calculate_demand_score(
            wait_times=wait_times, surge_multipliers=surges,
            capacity=capacity, requests=requests_count)
        avg_wait = int(sum(wait_times)/len(wait_times)) if wait_times else 0
        supply_level = self.calculator.determine_supply_level(avg_wait)
        max_surge = max(surges) if surges else 1.0
        avg_value = sum(values)/len(values) if values else 0.0
        if raw_data.get("_simulated_demand") is not None:
            demand_score = raw_data.get("_simulated_demand", demand_score)
            max_surge = raw_data.get("_simulated_surge", max_surge)
        return ZoneMetrics(
            zone_id=zone_id, demand_score=demand_score,
            supply_level=supply_level, surge_detected=max_surge > 1.0,
            surge_multiplier=round(max_surge, 2),
            average_wait_time=avg_wait, average_value=round(avg_value, 2),
            available_capacity=capacity, active_requests=requests_count,
            is_simulated=raw_data.get("_simulated_demand") is not None,
            raw_data=raw_data)

    def _create_opportunity(self, zone, metrics, trend):
        base_transactions = 3.0
        supply_factor = {SupplyLevel.HIGH: 1.0, SupplyLevel.MEDIUM: 0.8,
                         SupplyLevel.LOW: 0.6, SupplyLevel.CRITICAL: 0.4,
                         SupplyLevel.UNKNOWN: 0.7}.get(metrics.supply_level, 0.7)
        transactions_per_hour = base_transactions * supply_factor * metrics.surge_multiplier
        hourly_potential = self.calculator.calculate_hourly_potential(
            avg_value=metrics.average_value,
            transactions_per_hour=transactions_per_hour,
            surge_multiplier=metrics.surge_multiplier)
        confidence = min(0.95, 0.6 + len(metrics.raw_data.get("wait_times", [])) * 0.1)
        priority = self.calculator.calculate_priority_score(
            demand_score=metrics.demand_score,
            surge_multiplier=metrics.surge_multiplier,
            hourly_potential=hourly_potential, confidence=confidence,
            weights=self.config.priority_weights)
        priority *= zone.priority_weight
        recommendation = self.calculator.generate_recommendation(
            metrics.demand_score, metrics.surge_multiplier, trend)
        return Opportunity(
            opportunity_id="opp_{}_{}".format(zone.zone_id, int(time.time())),
            timestamp=metrics.timestamp, zone_id=zone.zone_id,
            zone_name=zone.name, location=zone.location,
            demand_score=metrics.demand_score,
            surge_multiplier=metrics.surge_multiplier,
            estimated_value=metrics.average_value * 0.75,
            hourly_potential=hourly_potential,
            wait_time_seconds=metrics.average_wait_time,
            priority_score=priority, confidence=round(confidence, 2),
            recommendation=recommendation,
            risk_level="medium", trend_direction=trend)

    def get_best_opportunity(self):
        with self._lock:
            return max(self._opportunities, key=lambda o: o.priority_score) if self._opportunities else None

    def get_top_opportunities(self, n=5):
        with self._lock:
            return sorted(self._opportunities, key=lambda o: o.priority_score, reverse=True)[:n]

    def get_hotspots(self, include_trending=False):
        with self._lock:
            return self.detector.find_hotspots(self._zone_metrics, include_trending)

    def get_health(self):
        success_rate = (self._successful_scans / self._total_scans * 100) if self._total_scans > 0 else 0
        uptime = time.time() - self._start_time if self._start_time else 0
        return {"running": self._running, "uptime_seconds": round(uptime),
                "provider": self.provider.get_provider_name(),
                "zones_monitored": len(self.zones),
                "zones_with_data": len(self._zone_metrics),
                "total_scans": self._total_scans,
                "successful_scans": self._successful_scans,
                "success_rate": round(success_rate, 1),
                "error_count": self._error_count,
                "opportunities_cached": len(self._opportunities),
                "hotspots_detected": len(self.get_hotspots())}

    def _scan_loop(self):
        while self._running:
            try:
                if self._dynamic_zone_updater:
                    with self._lock:
                        self.zones = {z.zone_id: z for z in self._dynamic_zone_updater()}
                self._perform_scan()
                self._consecutive_errors = 0
                self._successful_scans += 1
            except Exception as e:
                self._error_count += 1
                self._consecutive_errors += 1
                time.sleep(min(self.config.error_backoff_base ** self._consecutive_errors,
                               self.config.error_backoff_max))
                continue
            self._total_scans += 1
            time.sleep(max(0, self.config.scan_interval_seconds - 0.1))

# ================================================================================
# SECCION 7: PREDICTIVE ZONE MANAGER
# ================================================================================
class PredictiveZoneManager:
    def __init__(self, max_radius_km=30.0):
        self.max_radius = max_radius_km
        self._current_location = None
        self._current_bearing = 0.0
        self._current_speed_kmh = 0.0
        self._lock = threading.RLock()

    def update_state(self, lat, lon, bearing, speed_kmh):
        with self._lock:
            self._current_location = (lat, lon)
            self._current_bearing = bearing
            self._current_speed_kmh = speed_kmh

    def get_all_zones(self):
        with self._lock:
            if not self._current_location:
                return []
            lat, lon = self._current_location
            zones = []
            for dist_km in [5, 10, 15, 20, 25, 30]:
                for angle in range(0, 360, 45):
                    rad = math.radians(angle)
                    delta_lat = dist_km / 111.0
                    delta_lon = dist_km / (111.0 * math.cos(math.radians(lat)))
                    zlat = lat + delta_lat * math.cos(rad)
                    zlon = lon + delta_lon * math.sin(rad)
                    if not (-90 <= zlat <= 90 and -180 <= zlon <= 180):
                        continue
                    zid = "r{}_{}".format(dist_km, angle)
                    zones.append(Zone(
                        zone_id=zid,
                        name="Radial {}km @{}".format(dist_km, angle),
                        location=GeoPoint(zlat, zlon),
                        metadata={"distance_km": dist_km, "bearing": angle},
                        tags=["radial"],
                        priority_weight=1.0 - dist_km / 60.0))
            return zones

# ================================================================================
# SECCION 8: CONTROLADOR INTEGRADO (CORREGIDO)
# ================================================================================
class IntegratedRadarController:
    """Controlador que integra Radar Engine con GPS y BLE Broadcast."""

    def __init__(self, user_session=None, bridge_app=None):
        self.user_session = user_session
        self.bridge_app = bridge_app
        self.radar = None
        self.predictive_zones = None
        self.data_provider = None
        self.ble_broadcaster = None
        self._running = False

    def initialize(self, use_real_gps=False, use_bluetooth=False,
                   allow_sim=False, symbiosis_instance=None,
                   enable_ble_broadcast=False, prompt_text=None):
        print("[INFO] Iniciando Radar v3.2")
        self.predictive_zones = PredictiveZoneManager(max_radius_km=30.0)

        if allow_sim:
            self.data_provider = SimulatedDataProvider(seed=42)
        else:
            raise RuntimeError("Se requiere allow_sim=True en esta versión")

        config = RadarConfig(scan_interval_seconds=1, watchdog_interval_seconds=30,
                             max_radius_km=30.0, enable_alerts=True,
                             enable_analytics=True, enable_prediction=True)
        self.radar = RadarEngine(zones=[], data_provider=self.data_provider, config=config)
        self._setup_fallback_updater()

        if enable_ble_broadcast:
            text_to_broadcast = prompt_text or MEJOR_OPCION_PROMPT_TEXTO
            if text_to_broadcast and text_to_broadcast.strip():
                try:
                    self.ble_broadcaster = BLEBroadcastService(text_to_broadcast, interval=0.2)
                    self.ble_broadcaster.start()
                    print("[BLE Broadcast] Servicio iniciado")
                except Exception as e:
                    print("[BLE Broadcast] Error: {}".format(e))
        return self

    def _setup_fallback_updater(self):
        _fallback = {"lat": 19.4326, "lon": -99.1332, "bearing": 0.0, "speed": 30.0}
        def update_zones():
            _fallback["bearing"] = (_fallback["bearing"] + 0.5) % 360
            _fallback["lat"] += math.sin(math.radians(_fallback["bearing"])) * 0.0001
            _fallback["lon"] += math.cos(math.radians(_fallback["bearing"])) * 0.0001
            self.predictive_zones.update_state(_fallback["lat"], _fallback["lon"],
                                               _fallback["bearing"], _fallback["speed"])
            return self.predictive_zones.get_all_zones()
        self.radar.set_dynamic_zone_updater(update_zones)

    def start(self):
        if self.radar:
            self.radar.start()
        self._running = True

    def stop(self):
        self._running = False
        if self.radar:
            self.radar.stop()
        if self.ble_broadcaster:
            self.ble_broadcaster.stop()

# ================================================================================
# SECCION 9: SISTEMA UNIFICADO (CORREGIDO)
# ================================================================================
class UnifiedSystem:
    """Gestiona el arranque de todos los componentes."""

    def __init__(self, user_session=None, bridge_app=None):
        self.user_session = user_session
        self.bridge_app = bridge_app
        self.controller = None

    def start(self, use_real_gps=False, use_bluetooth=False,
              allow_sim=False, enable_ble_broadcast=False,
              prompt_text=None, bridge_app=None):
        # bridge_app puede venir como parámetro o desde __init__
        effective_bridge = bridge_app if bridge_app is not None else self.bridge_app
        print("[SYSTEM] Iniciando Sistema Unificado v3.2")
        self.controller = IntegratedRadarController(
            user_session=self.user_session, bridge_app=effective_bridge)
        self.controller.initialize(
            use_real_gps=use_real_gps, use_bluetooth=use_bluetooth,
            allow_sim=allow_sim, symbiosis_instance=None,
            enable_ble_broadcast=enable_ble_broadcast,
            prompt_text=prompt_text)
        self.controller.start()

# ================================================================================
# SECCION 10: BLE BROADCAST SERVICE
# ================================================================================
class PromptBroadcast:
    """Comprime y fragmenta un texto para transmisión BLE."""

    def __init__(self, texto: str, fragment_size: int = 180):
        self.texto_original = texto
        self.fragment_size = fragment_size
        self.compressed = zlib.compress(texto.encode('utf-8'))
        self.msg_id = uuid.uuid4().hex[:8]
        self.fragments = self._create_fragments()
        self.current_fragment = 0

    def _create_fragments(self):
        total = (len(self.compressed) + self.fragment_size - 1) // self.fragment_size
        fragments = []
        for i in range(total):
            chunk = self.compressed[i*self.fragment_size:(i+1)*self.fragment_size]
            payload = base64.b64encode(chunk).decode('ascii')
            check = hashlib.md5(chunk).hexdigest()[:4]
            frame = "ID:{}|{}/{}|{}|CHK:{}".format(self.msg_id, i+1, total, payload, check)
            fragments.append(frame)
        return fragments

    def get_next_fragment(self) -> str:
        frag = self.fragments[self.current_fragment]
        self.current_fragment = (self.current_fragment + 1) % len(self.fragments)
        return frag


class BLEBroadcastService(threading.Thread):
    """Servicio de transmisión BLE continua del prompt fragmentado."""

    def __init__(self, texto: str, interval: float = 0.2):
        super().__init__(daemon=True)
        self.broadcast = PromptBroadcast(texto)
        self.interval = interval
        self._running = False
        self._termux_available = shutil.which("termux-bluetooth-advertise") is not None

    def run(self):
        self._running = True
        if self._termux_available:
            self._log("BLE Broadcast iniciado en modo nativo Termux.")
            while self._running:
                frag = self.broadcast.get_next_fragment()
                self._send_native(frag)
                time.sleep(self.interval)
        else:
            self._log("BLE Broadcast en modo DEMO (consola).")
            while self._running:
                frag = self.broadcast.get_next_fragment()
                self._log_demo(frag)
                time.sleep(self.interval)

    def _send_native(self, data: str):
        try:
            subprocess.run(['termux-bluetooth-advertise', '-d', data],
                           capture_output=True, timeout=5)
        except Exception as e:
            self._log("Error enviando fragmento BLE: {}".format(e))

    def _log_demo(self, data: str):
        print("[BLE-DEMO] {}".format(data[:80] + ("..." if len(data) > 80 else "")))

    def _log(self, msg: str):
        print("[BLE Broadcast] {}".format(msg))

    def stop(self):
        self._running = False

# ================================================================================
# SECCION 11: BLE BRIDGE APP (CORREGIDO - JAVA CON FOREGROUND SERVICE)
# ================================================================================
class BLEBridgeApp:
    """
    App puente autocontenida. Contiene el código fuente Java CORREGIDO de una app Android
    que expone endpoints HTTP locales para escaneo BLE y advertising.
    
    CORRECCIONES APLICADAS:
    - MainActivity sin finish() inmediato (muestra UI)
    - BridgeService como Foreground Service (Android 8+)
    - Verificación de permisos en runtime (Android 6+)
    - BLUETOOTH_CONNECT para getName() (Android 12+)
    - ScanCallback reutilizable (no crear instancia nueva para stopScan)
    """

    # --- CÓDIGO JAVA CORREGIDO ---
    MAIN_ACTIVITY_JAVA = r"""
package com.radar.bridge;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.view.Gravity;
import android.graphics.Color;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    
    private static final int PERMISSION_REQUEST_CODE = 100;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (checkAndRequestPermissions()) {
            startBridgeService();
            showStatusUI("RadarBridge activo\nPuerto: 9876");
        } else {
            showStatusUI("Solicitando permisos...");
        }
    }
    
    private boolean checkAndRequestPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) 
                != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.BLUETOOTH_SCAN);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE) 
                != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.BLUETOOTH_ADVERTISE);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) 
                != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.BLUETOOTH_CONNECT);
            }
        }
        
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
        
        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, 
                permissionsNeeded.toArray(new String[0]), 
                PERMISSION_REQUEST_CODE);
            return false;
        }
        
        return true;
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                startBridgeService();
                showStatusUI("RadarBridge activo\nPuerto: 9876");
            } else {
                showStatusUI("Permisos denegados\nLa app no puede funcionar");
            }
        }
    }
    
    private void startBridgeService() {
        Intent intent = new Intent(this, BridgeService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }
    
    private void showStatusUI(String message) {
        TextView textView = new TextView(this);
        textView.setText(message);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(18);
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundColor(Color.BLACK);
        textView.setPadding(50, 50, 50, 50);
        setContentView(textView);
    }
}
"""

    BRIDGE_SERVICE_JAVA = r"""
package com.radar.bridge;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.util.Base64;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import com.google.gson.Gson;
import fi.iki.elonen.NanoHTTPD;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BridgeService extends Service {
    
    private static final String TAG = "BridgeService";
    private static final int PORT = 9876;
    private static final String CHANNEL_ID = "BridgeServiceChannel";
    private static final int NOTIFICATION_ID = 1;
    
    private WebServer server;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner scanner;
    private BluetoothLeAdvertiser advertiser;
    private final Map<String, Object> lastScanResults = new HashMap<>();
    private Gson gson = new Gson();
    private AdvertiseCallback advertiseCallback;
    private ScanCallback currentScanCallback;
    private boolean hasBluetoothPermissions = false;
    
    @Override
    public void onCreate() {
        super.onCreate();
        checkBluetoothPermissions();
        
        if (!hasBluetoothPermissions) {
            Log.e(TAG, "Sin permisos de Bluetooth");
            stopSelf();
            return;
        }
        
        BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (manager != null) {
            bluetoothAdapter = manager.getAdapter();
            if (bluetoothAdapter != null) {
                scanner = bluetoothAdapter.getBluetoothLeScanner();
                advertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
            }
        }
        
        try {
            server = new WebServer(PORT);
            server.start();
            Log.d(TAG, "Servidor HTTP iniciado en puerto " + PORT);
        } catch (IOException e) {
            Log.e(TAG, "Error iniciando servidor", e);
        }
        
        startForegroundNotification();
    }
    
    private void checkBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            hasBluetoothPermissions = 
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_ADVERTISE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
        } else {
            hasBluetoothPermissions = 
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }
    }
    
    private void startForegroundNotification() {
        createNotificationChannel();
        
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("RadarBridge")
            .setContentText("Servicio activo en puerto " + PORT)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build();
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE);
        } else {
            startForeground(NOTIFICATION_ID, notification);
        }
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, "RadarBridge Service", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Servicio de puente Bluetooth-WiFi");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
    
    @Override
    public IBinder onBind(Intent intent) { return null; }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (server != null) server.stop();
    }
    
    private class WebServer extends NanoHTTPD {
        public WebServer(int port) { super(port); }
        
        @Override
        public Response serve(IHTTPSession session) {
            String uri = session.getUri();
            Map<String, String> params = session.getParms();
            try {
                if (uri.equals("/scan")) {
                    return handleScan();
                } else if (uri.equals("/advertise")) {
                    return handleAdvertise(params);
                } else if (uri.equals("/ping")) {
                    return newFixedLengthResponse(Response.Status.OK, "text/plain", "pong");
                } else {
                    return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Endpoint no encontrado");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error en endpoint", e);
                return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", e.getMessage());
            }
        }
        
        private Response handleScan() {
            if (scanner == null || !hasBluetoothPermissions) {
                return newFixedLengthResponse(Response.Status.SERVICE_UNAVAILABLE, 
                    "text/plain", "BLE scanner no disponible o sin permisos");
            }
            
            final List<Map<String, Object>> devices = new ArrayList<>();
            
            currentScanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    BluetoothDevice device = result.getDevice();
                    Map<String, Object> devMap = new HashMap<>();
                    
                    if (ActivityCompat.checkSelfPermission(BridgeService.this, 
                            android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                        devMap.put("mac", device.getAddress());
                        devMap.put("name", device.getName());
                    } else {
                        devMap.put("mac", device.getAddress());
                        devMap.put("name", "Sin permiso");
                    }
                    devMap.put("rssi", result.getRssi());
                    devices.add(devMap);
                }
            };
            
            scanner.startScan(currentScanCallback);
            
            try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
            
            scanner.stopScan(currentScanCallback);
            currentScanCallback = null;
            
            lastScanResults.put("devices", devices);
            lastScanResults.put("timestamp", System.currentTimeMillis());
            
            String json = gson.toJson(lastScanResults);
            return newFixedLengthResponse(Response.Status.OK, "application/json", json);
        }
        
        private Response handleAdvertise(Map<String, String> params) {
            String payload = params.get("payload");
            if (payload == null || payload.isEmpty()) {
                return newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/plain", "Falta payload");
            }
            if (advertiser == null || !hasBluetoothPermissions) {
                return newFixedLengthResponse(Response.Status.SERVICE_UNAVAILABLE, 
                    "text/plain", "Advertiser no disponible o sin permisos");
            }
            
            byte[] data = Base64.decode(payload, Base64.DEFAULT);
            AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .setConnectable(false)
                .build();
            AdvertiseData advData = new AdvertiseData.Builder()
                .addServiceData(new ParcelUuid(UUID.fromString("0000ffff-0000-1000-8000-00805f9b34fb")), data)
                .setIncludeDeviceName(false)
                .build();
            
            advertiseCallback = new AdvertiseCallback() {
                @Override
                public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                    super.onStartSuccess(settingsInEffect);
                    Log.d(TAG, "Advertising iniciado");
                }
                @Override
                public void onStartFailure(int errorCode) {
                    super.onStartFailure(errorCode);
                    Log.e(TAG, "Error advertising: " + errorCode);
                }
            };
            
            advertiser.startAdvertising(settings, advData, advertiseCallback);
            return newFixedLengthResponse(Response.Status.OK, "text/plain", "Advertising iniciado");
        }
    }
}
"""

    def __init__(self, base_dir="./bridge_app"):
        self.base_dir = Path(base_dir)
        self.base_dir.mkdir(parents=True, exist_ok=True)
        self.apk_path = self.base_dir / "app" / "build" / "outputs" / "apk" / "debug" / "app-debug.apk"
        self._bridge_running = False

    def is_bridge_installed(self) -> bool:
        try:
            resp = requests.get("http://127.0.0.1:9876/ping", timeout=1)
            return resp.status_code == 200
        except Exception:
            return False

    def write_source_files(self):
        """Genera un proyecto Android completo con código Java CORREGIDO."""
        project_root = self.base_dir
        app_dir = project_root / "app"
        src_main = app_dir / "src" / "main"
        java_dir = src_main / "java" / "com" / "radar" / "bridge"
        res_values = src_main / "res" / "values"
        gradle_wrapper_dir = project_root / "gradle" / "wrapper"

        for d in [app_dir, java_dir, res_values, gradle_wrapper_dir]:
            d.mkdir(parents=True, exist_ok=True)

        # --- Archivos raíz ---
        with open(project_root / "settings.gradle", "w") as f:
            f.write("rootProject.name = 'RadarBridge'\ninclude ':app'\n")

        with open(project_root / "build.gradle", "w") as f:
            f.write("""buildscript {
    repositories { google(); mavenCentral() }
    dependencies { classpath 'com.android.tools.build:gradle:8.2.0' }
}
allprojects { repositories { google(); mavenCentral() } }
""")

        with open(project_root / "gradle.properties", "w") as f:
            f.write("android.useAndroidX=true\nandroid.enableJetifier=true\n")

        # --- Wrapper ---
        with open(project_root / "gradlew", "w") as f:
            f.write('#!/bin/sh\nexec java -cp "gradle/wrapper/gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain "$@"\n')
        os.chmod(project_root / "gradlew", 0o755)

        with open(project_root / "gradlew.bat", "w") as f:
            f.write('@echo off\njava -cp "gradle\\wrapper\\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*\n')

        with open(gradle_wrapper_dir / "gradle-wrapper.properties", "w") as f:
            f.write("""distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\\://services.gradle.org/distributions/gradle-8.2-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
""")

        # --- app/build.gradle (CORREGIDO: compileSdk 34, namespace) ---
        with open(app_dir / "build.gradle", "w") as f:
            f.write("""apply plugin: 'com.android.application'

android {
    namespace 'com.radar.bridge'
    compileSdk 34

    defaultConfig {
        applicationId "com.radar.bridge"
        minSdk 21
        targetSdk 34
        versionCode 1
        versionName "1.0"
    }

    buildTypes {
        release { minifyEnabled false }
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }
}

dependencies {
    implementation 'androidx.appcompat:appcompat:1.5.1'
    implementation 'androidx.activity:activity:1.6.1'
    implementation 'androidx.core:core:1.9.0'
    implementation 'androidx.lifecycle:lifecycle-service:2.6.1'
    implementation 'com.google.code.gson:gson:2.10'
    implementation 'org.nanohttpd:nanohttpd:2.3.1'
}

configurations.all {
    resolutionStrategy {
        force 'org.jetbrains.kotlin:kotlin-stdlib:1.8.10'
        force 'org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.10'
        force 'org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.10'
    }
}
""")

        # --- AndroidManifest.xml (CORREGIDO: sin package, con foregroundServiceType) ---
        with open(src_main / "AndroidManifest.xml", "w") as f:
            f.write("""<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_CONNECTED_DEVICE" />
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADVERTISE" />
    <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    
    <application
        android:allowBackup="true"
        android:label="RadarBridge"
        android:supportsRtl="true"
        android:theme="@style/Theme.AppCompat.Light.NoActionBar">
        
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>
                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>
        </activity>
        
        <service
            android:name=".BridgeService"
            android:exported="false"
            android:foregroundServiceType="connectedDevice"/>
    </application>
</manifest>
""")

        # --- Java source (CORREGIDO) ---
        with open(java_dir / "MainActivity.java", "w") as f:
            f.write(self.MAIN_ACTIVITY_JAVA)

        with open(java_dir / "BridgeService.java", "w") as f:
            f.write(self.BRIDGE_SERVICE_JAVA)

        # --- Resources ---
        with open(res_values / "themes.xml", "w") as f:
            f.write("""<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.AppCompat.Light.NoActionBar" parent="android:Theme.Material.Light.NoActionBar">
    </style>
</resources>
""")

        print("✅ Proyecto Android generado correctamente en: {}".format(project_root))
        print("   ATENCIÓN: el archivo 'gradle/wrapper/gradle-wrapper.jar' debe existir.")

    def build_with_gradle(self) -> Optional[Path]:
        gradle_bin = shutil.which("gradle")
        if not gradle_bin:
            print("Gradle no encontrado. Usa ./gradlew manualmente.")
            return None
        result = subprocess.run([gradle_bin, "assembleDebug"], cwd=self.base_dir, capture_output=True, text=True)
        if result.returncode != 0:
            print("Error compilando:", result.stderr)
            return None
        apk = next(self.base_dir.glob("**/app-debug.apk"), None)
        if apk:
            self.apk_path = apk
            print("APK generado: {}".format(apk))
        return apk

    def start_bridge_service(self):
        if not self.is_bridge_installed():
            print("App puente no responde. Instálala primero.")
            return
        self._bridge_running = True

    def stop_bridge_service(self):
        self._bridge_running = False

    def scan(self) -> List[Dict]:
        if not self._bridge_running:
            return []
        try:
            resp = requests.get("http://127.0.0.1:9876/scan", timeout=8)
            if resp.status_code == 200:
                data = resp.json()
                return data.get("devices", [])
        except Exception as e:
            print("Error en escaneo puente: {}".format(e))
        return []

    def advertise(self, payload_b64: str):
        if not self._bridge_running:
            return
        try:
            resp = requests.get("http://127.0.0.1:9876/advertise", params={"payload": payload_b64})
        except Exception as e:
            print("Error en advertising puente: {}".format(e))

# ================================================================================
# SECCION 12: WEB SERVER (IMPLEMENTADO - FALTABA EN v3.1)
# ================================================================================
class RadarRequestHandler(BaseHTTPRequestHandler):
    """Handler HTTP para el dashboard web del radar."""

    radar_instance = None

    def log_message(self, format, *args):
        pass  # Silenciar logs HTTP

    def do_GET(self):
        if self.path == "/" or self.path == "/index.html":
            self._serve_dashboard()
        elif self.path == "/api/health":
            self._serve_json(self.radar_instance.get_health() if self.radar_instance else {})
        elif self.path == "/api/hotspots":
            hotspots = self.radar_instance.get_hotspots() if self.radar_instance else []
            data = [{"zone_id": z[0], "demand_score": z[1].demand_score, 
                     "trend": z[2]} for z in hotspots[:10]]
            self._serve_json(data)
        elif self.path == "/api/opportunities":
            opps = self.radar_instance.get_top_opportunities(10) if self.radar_instance else []
            self._serve_json([o.to_dict() for o in opps])
        elif self.path == "/api/scan":
            opps = self.radar_instance.force_scan() if self.radar_instance else []
            self._serve_json([o.to_dict() for o in opps])
        else:
            self.send_error(404, "Not Found")

    def _serve_json(self, data):
        response = json.dumps(data, indent=2, default=str).encode("utf-8")
        self.send_response(200)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Content-Length", str(len(response)))
        self.end_headers()
        self.wfile.write(response)

    def _serve_dashboard(self):
        html = """<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Radar Bridge Dashboard</title>
    <style>
        body { font-family: Arial; background: #1a1a1a; color: #fff; padding: 20px; }
        .card { background: #2a2a2a; padding: 15px; margin: 10px 0; border-radius: 8px; }
        h1 { color: #4CAF50; }
        .metric { font-size: 24px; font-weight: bold; color: #4CAF50; }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 8px; text-align: left; border-bottom: 1px solid #444; }
        th { background: #333; }
        button { background: #4CAF50; color: white; padding: 10px 20px; border: none; 
                 border-radius: 4px; cursor: pointer; margin: 5px; }
        button:hover { background: #45a049; }
    </style>
</head>
<body>
    <h1>🛰️ Radar Bridge Dashboard</h1>
    <div class="card">
        <h3>Estado del Sistema</h3>
        <div id="health">Cargando...</div>
    </div>
    <div class="card">
        <h3>Hotspots Detectados</h3>
        <div id="hotspots">Cargando...</div>
    </div>
    <div class="card">
        <h3>Mejores Oportunidades</h3>
        <div id="opportunities">Cargando...</div>
    </div>
    <button onclick="refresh()">🔄 Actualizar</button>
    <button onclick="forceScan()">📡 Forzar Escaneo</button>
    <script>
        async function refresh() {
            const health = await (await fetch('/api/health')).json();
            document.getElementById('health').innerHTML = 
                '<p>Estado: <span class="metric">' + (health.running ? 'ACTIVO' : 'INACTIVO') + '</span></p>' +
                '<p>Zonas: ' + health.zones_with_data + '/' + health.zones_monitored + '</p>' +
                '<p>Escaneos: ' + health.successful_scans + '</p>' +
                '<p>Hotspots: ' + health.hotspots_detected + '</p>';
            
            const hotspots = await (await fetch('/api/hotspots')).json();
            let html = '<table><tr><th>Zona</th><th>Demanda</th><th>Tendencia</th></tr>';
            hotspots.forEach(h => {
                html += '<tr><td>' + h.zone_id + '</td><td>' + h.demand_score.toFixed(1) + 
                        '/10</td><td>' + h.trend + '</td></tr>';
            });
            html += '</table>';
            document.getElementById('hotspots').innerHTML = html || '<p>Sin hotspots</p>';
            
            const opps = await (await fetch('/api/opportunities')).json();
            html = '<table><tr><th>Zona</th><th>Demanda</th><th>Surge</th><th>$$/h</th><th>Recomendación</th></tr>';
            opps.forEach(o => {
                html += '<tr><td>' + o.zone_name + '</td><td>' + o.demand_score.toFixed(1) + 
                        '</td><td>x' + o.surge_multiplier.toFixed(2) + '</td><td>$' + 
                        o.hourly_potential.toFixed(0) + '</td><td>' + o.recommendation + '</td></tr>';
            });
            html += '</table>';
            document.getElementById('opportunities').innerHTML = html || '<p>Sin oportunidades</p>';
        }
        
        async function forceScan() {
            await fetch('/api/scan');
            refresh();
        }
        
        refresh();
        setInterval(refresh, 5000);
    </script>
</body>
</html>"""
        response = html.encode("utf-8")
        self.send_response(200)
        self.send_header("Content-Type", "text/html; charset=utf-8")
        self.send_header("Content-Length", str(len(response)))
        self.end_headers()
        self.wfile.write(response)


def start_web_server(radar_instance, port: int = 8080) -> None:
    """Inicia el servidor web del dashboard en un thread separado."""
    RadarRequestHandler.radar_instance = radar_instance
    try:
        server = HTTPServer(("0.0.0.0", port), RadarRequestHandler)
        thread = threading.Thread(target=server.serve_forever, daemon=True)
        thread.start()
        print("[+] Dashboard web disponible en: http://localhost:{}/".format(port))
    except Exception as e:
        print("[!] Error iniciando servidor web: {}".format(e))

# ================================================================================
# SECCION 13: FUNCIONES DE DIAGNÓSTICO Y TEST
# ================================================================================
def run_diagnostics() -> Dict[str, Any]:
    return {
        "python_version": sys.version,
        "algorithms_available": True,
        "threading_available": True,
        "dataclasses_available": True,
        "zlib_available": True,
        "ble_broadcast_supported": True,
        "requests_available": HAS_REQUESTS,
        "platform": platform.system()
    }

def quick_test() -> None:
    print("=" * 60)
    print("QUICK TEST - Demand Radar v3.2")
    print("=" * 60)
    zones = [Zone(zone_id="test_1", name="Zona Test 1",
                  location=GeoPoint(19.4326, -99.1332),
                  metadata={"distance_km": 5, "type": "test"})]
    provider = SimulatedDataProvider(seed=123)
    radar = RadarEngine(zones=zones, data_provider=provider)
    for i in range(3):
        opps = radar._perform_scan()
        print("Escaneo {}: {} oportunidades".format(i+1, len(opps)))
        time.sleep(0.5)
    best = radar.get_best_opportunity()
    if best:
        print("\nMejor oportunidad:")
        print("  Zona: {} - Demanda: {}/10".format(best.zone_name, best.demand_score))
        print("  Surge: x{}".format(best.surge_multiplier))
        print("  Recomendación: {}".format(best.recommendation))
    print("\nQUICK TEST COMPLETADO")
    print("=" * 60)

# ================================================================================
# SECCION 14: FUNCIONES HELPER
# ================================================================================
def prepare_bridge():
    print("[BRIDGE] Preparando archivos fuente de la app puente...")
    bridge = BLEBridgeApp()
    bridge.write_source_files()
    print("\n✅ Fuentes generados en ./bridge_app/")
    print("📌 Para compilar:")
    print("   1. Asegúrate de tener 'gradle/wrapper/gradle-wrapper.jar'")
    print("   2. Ejecuta './gradlew assembleDebug'")
    print("   3. Instala el APK en tu teléfono")
    print("   4. Vuelve a ejecutar este script\n")

# ================================================================================
# SECCION 15: ENTRY POINT FINAL CON SOPORTE DE PUENTE Y OPCIONES
# ================================================================================
def main():
    """Punto de entrada principal con manejo correcto de bridge_app."""
    args = set(sys.argv[1:]) if len(sys.argv) > 1 else set()

    # --- MODO DIAGNÓSTICO ---
    if "--diag" in args:
        print(json.dumps(run_diagnostics(), indent=2))
        return

    # --- MODO TEST RÁPIDO ---
    if "--test" in args:
        quick_test()
        return

    # --- MODO PREPARAR PUENTE ---
    if "--prepare-bridge" in args:
        prepare_bridge()
        return

    # --- MODO SIMULACIÓN PURA ---
    if "--sim" in args:
        system = UnifiedSystem()
        system.start(use_bluetooth=False, allow_sim=True, enable_ble_broadcast=False)
        try:
            while True:
                time.sleep(1)
        except KeyboardInterrupt:
            system.controller.stop()
            print("[SYSTEM] Sistema detenido.")
        return

    # --- MODO BROADCAST (con puente opcional) ---
    if "--broadcast" in args:
        bridge = BLEBridgeApp()
        use_bridge = False
        if bridge.is_bridge_installed():
            bridge.start_bridge_service()
            use_bridge = True
            print("[MAIN] Puente activo, usando datos reales.")
        else:
            print("[MAIN] App puente no instalada. Cambiando a modo simulación.")

        system = UnifiedSystem(bridge_app=bridge if use_bridge else None)
        system.start(
            use_bluetooth=use_bridge,
            allow_sim=not use_bridge,
            enable_ble_broadcast=True,
            prompt_text=MEJOR_OPCION_PROMPT_TEXTO
        )

        # Iniciar dashboard web
        if system.controller and system.controller.radar:
            start_web_server(system.controller.radar, port=8080)

        try:
            while True:
                time.sleep(1)
        except KeyboardInterrupt:
            system.controller.stop()
            print("[SYSTEM] Sistema detenido.")
        return

    # --- MODO POR DEFECTO (detección automática de puente) ---
    bridge = BLEBridgeApp()
    use_bridge = False

    if bridge.is_bridge_installed():
        bridge.start_bridge_service()
        print("[MAIN] Puente activo, usando datos reales.")
        use_bridge = True
    else:
        print("[MAIN] No se detectó la app puente.")
        respuesta = input("¿Quieres generar los archivos fuente para compilarla? (s/N): ").strip().lower()
        if respuesta in ('s', 'si', 'y', 'yes'):
            prepare_bridge()
            return
        else:
            print("[MAIN] Continuando en modo simulación.")
            use_bridge = False

    # Crear sistema unificado con bridge_app correcto
    system = UnifiedSystem(bridge_app=bridge if use_bridge else None)
    system.start(
        use_bluetooth=use_bridge,
        allow_sim=not use_bridge,
        enable_ble_broadcast=True,
        prompt_text=MEJOR_OPCION_PROMPT_TEXTO
    )

    # Iniciar dashboard web si el radar está disponible
    if system.controller and system.controller.radar:
        start_web_server(system.controller.radar, port=8080)

    # Bucle principal
    try:
        while True:
            # Mostrar estado cada 10 segundos
            if system.controller and system.controller.radar:
                health = system.controller.radar.get_health()
                if health.get("running"):
                    best = system.controller.radar.get_best_opportunity()
                    if best:
                        print("[{:.0f}s] Mejor: {} - Demanda: {}/10 - Surge: x{:.2f}".format(
                            health.get("uptime_seconds", 0),
                            best.zone_name,
                            best.demand_score,
                            best.surge_multiplier
                        ))
            time.sleep(10)
    except KeyboardInterrupt:
        if system.controller:
            system.controller.stop()
        print("\n[SYSTEM] Sistema detenido por el usuario.")


# ================================================================================
# ENTRY POINT
# ================================================================================
if __name__ == "__main__":
    try:
        main()
    except Exception as e:
        print("\n[ERROR FATAL] {}".format(e))
        import traceback
        traceback.print_exc()
        sys.exit(1)
