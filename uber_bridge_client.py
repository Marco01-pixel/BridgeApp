#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Uber Bridge Client (HTTP v2) - Conecta con BridgeService Android en puerto 9876
Compatible con sistema_uber_unificado.py
"""

import json
import time
import threading
import urllib.request
import urllib.error
from typing import Optional, Dict, List, Any
from dataclasses import dataclass
from collections import deque

# ==============================================================================
# CLIENTE PRINCIPAL (HTTP en 9876)
# ==============================================================================
class UberBridgeClient:
    """Cliente HTTP para conectar con BridgeService Android en puerto 9876"""

    # Mapeo de comandos TCP antiguos → endpoints HTTP nuevos
    ENDPOINTS = {
        "GET_GPS": "/gps",
        "GET_BLUETOOTH": "/bluetooth",
        "GET_WIFI": "/wifi",
        "GET_CELL": "/cell",
        "GET_ALL": "/all",
        "PING": "/ping",
        "STATUS": "/ping"
    }

    def __init__(self, host: str = "127.0.0.1", port: int = 9876, timeout: float = 5.0):
        self.host = host
        self.port = port
        self.base_url = f"http://{host}:{port}"
        self.timeout = timeout
        self._available = None
        self._last_check = 0
        self._cache = {}
        self._cache_time = {}
        self._cache_ttl = 2.0
        self._lock = threading.RLock()
        self._stats = {
            'calls': 0,
            'success': 0,
            'failures': 0,
            'last_error': None
        }

    # ==========================================================================
    # MÉTODOS DE COMUNICACIÓN (HTTP en vez de TCP)
    # ==========================================================================

    def _http_get(self, endpoint: str) -> Optional[str]:
        """Petición HTTP GET al endpoint del puente."""
        with self._lock:
            self._stats['calls'] += 1
            try:
                url = f"{self.base_url}{endpoint}"
                response = urllib.request.urlopen(url, timeout=self.timeout)
                data = response.read().decode('utf-8')
                self._stats['success'] += 1
                self._stats['last_error'] = None
                return data
            except urllib.error.URLError as e:
                self._stats['failures'] += 1
                self._stats['last_error'] = str(e)
                return None
            except Exception as e:
                self._stats['failures'] += 1
                self._stats['last_error'] = str(e)
                return None

    def _send_command(self, command: str) -> Optional[str]:
        """Convierte comando TCP antiguo a endpoint HTTP (compatibilidad)."""
        endpoint = self.ENDPOINTS.get(command.strip().upper())
        if endpoint:
            return self._http_get(endpoint)
        # Fallback: intentar como endpoint directo
        return self._http_get(f"/{command.lower()}")

    # ==========================================================================
    # VERIFICACIÓN DE DISPONIBILIDAD
    # ==========================================================================

    def is_available(self, force_check: bool = False) -> bool:
        """Verifica si el puente Android está respondiendo."""
        now = time.time()
        if not force_check and self._available is not None and (now - self._last_check) < 10:
            return self._available

        try:
            response = self._http_get("/ping")
            self._available = (response is not None and "pong" in response.lower())
        except:
            self._available = False

        self._last_check = now
        return self._available

    # ==========================================================================
    # GPS
    # ==========================================================================

    def get_gps(self):
        """Obtiene datos GPS del dispositivo Android."""
        if not self.is_available():
            return None

        now = time.time()
        with self._lock:
            if "gps" in self._cache and (now - self._cache_time.get("gps", 0)) < self._cache_ttl:
                return self._cache["gps"]

        response = self._http_get("/gps")
        if response:
            try:
                data = json.loads(response)
                if "error" not in data:
                    coord = self._create_coordinate(
                        latitude=data.get('latitude', 0.0),
                        longitude=data.get('longitude', 0.0),
                        altitude=data.get('altitude', 0.0),
                        accuracy=data.get('accuracy', 10.0),
                        timestamp=data.get('timestamp', time.time()) / 1000.0,
                        source='android_gps'
                    )
                    with self._lock:
                        self._cache["gps"] = coord
                        self._cache_time["gps"] = now
                    return coord
            except (json.JSONDecodeError, KeyError):
                pass
        return None

    def _create_coordinate(self, latitude, longitude, altitude=0.0, accuracy=10.0, 
                           timestamp=None, source='android'):
        """Crea un objeto coordenada con helpers de distancia."""
        if timestamp is None:
            timestamp = time.time()

        @dataclass
        class Coord:
            latitude: float
            longitude: float
            altitude: float = 0.0
            accuracy: float = 0.0
            source: str = "unknown"
            timestamp: float = 0.0

            def is_valid(self):
                return (-90.0 <= self.latitude <= 90.0 and
                        -180.0 <= self.longitude <= 180.0 and
                        self.accuracy >= 0.0)

            def distance_to(self, other):
                import math
                lat1 = math.radians(self.latitude)
                lon1 = math.radians(self.longitude)
                lat2 = math.radians(other.latitude)
                lon2 = math.radians(other.longitude)
                dlat = lat2 - lat1
                dlon = lon2 - lon1
                a = math.sin(dlat * 0.5) ** 2 + math.cos(lat1) * math.cos(lat2) * math.sin(dlon * 0.5) ** 2
                return 6371.0 * 2.0 * math.atan2(math.sqrt(a), math.sqrt(1.0 - a))

            def distance_to_meters(self, other):
                return self.distance_to(other) * 1000.0

        return Coord(
            latitude=float(latitude),
            longitude=float(longitude),
            altitude=float(altitude),
            accuracy=float(accuracy),
            source=source,
            timestamp=float(timestamp) if timestamp else time.time()
        )

    # ==========================================================================
    # BLUETOOTH CLÁSICO
    # ==========================================================================

    def scan_bluetooth(self) -> List[Dict[str, Any]]:
        """Obtiene dispositivos Bluetooth emparejados/cercanos."""
        if not self.is_available():
            return []

        now = time.time()
        cache_key = "bluetooth"
        with self._lock:
            if cache_key in self._cache and (now - self._cache_time.get(cache_key, 0)) < self._cache_ttl:
                return self._cache[cache_key]

        response = self._http_get("/bluetooth")
        if response:
            try:
                data = json.loads(response)
                if "error" not in data:
                    devices = data.get('devices', [])
                    with self._lock:
                        self._cache[cache_key] = devices
                        self._cache_time[cache_key] = now
                    return devices
            except json.JSONDecodeError:
                pass
        return []

    # ==========================================================================
    # WIFI
    # ==========================================================================

    def scan_wifi(self) -> List[Dict[str, Any]]:
        """Obtiene redes WiFi cercanas."""
        if not self.is_available():
            return []

        now = time.time()
        cache_key = "wifi"
        with self._lock:
            if cache_key in self._cache and (now - self._cache_time.get(cache_key, 0)) < self._cache_ttl:
                return self._cache[cache_key]

        response = self._http_get("/wifi")
        if response:
            try:
                data = json.loads(response)
                if "error" not in data:
                    networks = data.get('networks', [])
                    with self._lock:
                        self._cache[cache_key] = networks
                        self._cache_time[cache_key] = now
                    return networks
            except json.JSONDecodeError:
                pass
        return []

    # ==========================================================================
    # CELULAR
    # ==========================================================================

    def get_cell_info(self) -> List[Dict[str, Any]]:
        """Obtiene información de torres celulares."""
        if not self.is_available():
            return []

        now = time.time()
        cache_key = "cell"
        with self._lock:
            if cache_key in self._cache and (now - self._cache_time.get(cache_key, 0)) < self._cache_ttl:
                return self._cache[cache_key]

        response = self._http_get("/cell")
        if response:
            try:
                data = json.loads(response)
                if "error" not in data:
                    cells = data.get('cells', [])
                    with self._lock:
                        self._cache[cache_key] = cells
                        self._cache_time[cache_key] = now
                    return cells
            except json.JSONDecodeError:
                pass
        return []

    # ==========================================================================
    # TODOS LOS DATOS
    # ==========================================================================

    def get_all(self) -> Optional[Dict[str, Any]]:
        """Obtiene todos los datos del puente (GPS + BT + WiFi + Cell)."""
        if not self.is_available():
            return None

        response = self._http_get("/all")
        if response:
            try:
                return json.loads(response)
            except json.JSONDecodeError:
                pass
        return None

    # ==========================================================================
    # PING
    # ==========================================================================

    def ping(self) -> bool:
        """Verifica conectividad con el puente."""
        response = self._http_get("/ping")
        return response is not None and "pong" in response.lower()

    # ==========================================================================
    # BLE SCAN Y ADVERTISE (NUEVOS)
    # ==========================================================================

    def ble_scan(self) -> Optional[Dict[str, Any]]:
        """Escaneo BLE (Bluetooth Low Energy)."""
        response = self._http_get("/scan")
        if response:
            try:
                return json.loads(response)
            except json.JSONDecodeError:
                pass
        return None

    def ble_advertise(self, payload_b64: str) -> bool:
        """Envía datos por BLE Advertising."""
        try:
            url = f"{self.base_url}/advertise?payload={payload_b64}"
            response = urllib.request.urlopen(url, timeout=self.timeout)
            return response.status == 200
        except:
            return False

    # ==========================================================================
    # ESTADÍSTICAS Y CACHE
    # ==========================================================================

    def get_stats(self) -> Dict[str, Any]:
        """Retorna estadísticas del cliente."""
        with self._lock:
            total = max(1, self._stats['calls'])
            return {
                "available": self.is_available(),
                "host": self.host,
                "port": self.port,
                "protocol": "HTTP",
                "cache_size": len(self._cache),
                "calls": self._stats['calls'],
                "success": self._stats['success'],
                "failures": self._stats['failures'],
                "success_rate": round((self._stats['success'] / total) * 100, 1),
                "last_error": self._stats['last_error']
            }

    def clear_cache(self) -> None:
        """Limpia la caché de datos."""
        with self._lock:
            self._cache.clear()
            self._cache_time.clear()


# ==============================================================================
# BROADCASTER BLE
# ==============================================================================
class PromptBLEBroadcaster:
    """Transmisor BLE para prompts usando el puente."""

    def __init__(self, bridge_client: Optional[UberBridgeClient] = None):
        self.bridge = bridge_client or UberBridgeClient()
        self._running = False
        self._thread = None
        self._stop_event = threading.Event()
        self._listeners = []

    def start_broadcasting(self, interval: float = 5.0) -> None:
        """Inicia el broadcast BLE."""
        if self._running:
            return
        self._running = True
        self._stop_event.clear()
        self._thread = threading.Thread(target=self._broadcast_loop, daemon=True, args=(interval,))
        self._thread.start()

    def stop_broadcasting(self) -> None:
        """Detiene el broadcast BLE."""
        self._running = False
        self._stop_event.set()
        if self._thread:
            self._thread.join(timeout=2.0)

    def _broadcast_loop(self, interval: float) -> None:
        """Loop principal de broadcast."""
        while not self._stop_event.is_set():
            if self.bridge.is_available():
                gps = self.bridge.get_gps()
                if gps:
                    for listener in self._listeners:
                        try:
                            listener(gps)
                        except:
                            pass
            time.sleep(interval)

    def add_listener(self, callback: callable) -> None:
        """Añade un listener para recibir actualizaciones."""
        self._listeners.append(callback)

    def remove_listener(self, callback: callable) -> None:
        """Elimina un listener."""
        if callback in self._listeners:
            self._listeners.remove(callback)


# ==============================================================================
# FUNCIÓN DE PARCHADO (COMPATIBILIDAD)
# ==============================================================================
def apply_all_patches(system_instance) -> bool:
    """Aplica todos los parches al sistema. Retorna True si el puente está disponible."""
    bridge = UberBridgeClient()
    if not bridge.is_available():
        return False
    return True


# ==============================================================================
# TEST RÁPIDO
# ==============================================================================
if __name__ == "__main__":
    client = UberBridgeClient()
    print(f"Puente HTTP en {client.base_url}")
    print(f"Disponible? {client.is_available()}")

    if client.is_available():
        print("\n--- GPS ---")
        gps = client.get_gps()
        if gps:
            print(f"  Lat: {gps.latitude}, Lon: {gps.longitude}")
            print(f"  Speed: {gps.altitude}m/s, Bearing: {gps.accuracy}°")

        print("\n--- Bluetooth ---")
        bt = client.scan_bluetooth()
        print(f"  Dispositivos: {len(bt)}")

        print("\n--- WiFi ---")
        wifi = client.scan_wifi()
        print(f"  Redes: {len(wifi)}")

        print("\n--- Cell ---")
        cell = client.get_cell_info()
        print(f"  Torres: {len(cell)}")

        print("\n--- BLE Scan ---")
        ble = client.ble_scan()
        if ble:
            print(f"  Dispositivos BLE: {len(ble.get('devices', []))}")

        print("\n--- Stats ---")
        print(json.dumps(client.get_stats(), indent=2))
    else:
        print("❌ Servicio no disponible.")
        print("   Asegúrate de que la app Android (BridgeService) esté corriendo en puerto 9876")
