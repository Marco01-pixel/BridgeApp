import os, re, shutil

ARCHIVO = "sistema_uber_unificado.py"
if not os.path.exists(ARCHIVO):
    print("❌ No encuentro", ARCHIVO)
    exit(1)

shutil.copy2(ARCHIVO, ARCHIVO + ".backup")
print("✅ Backup creado")

with open(ARCHIVO, "r") as f:
    c = f.read()

# 1. Insertar función de permisos después de imports
imports_end = max(c.rfind("\nimport "), c.rfind("\nfrom "))
if imports_end > 0:
    c = c[:imports_end] + """

# ========================================================================
# FUNCIÓN PARA SOLICITAR PERMISOS DE TERMUX (Android)
# ========================================================================
def solicitar_permisos_termux():
    permisos = ["android.permission.ACCESS_FINE_LOCATION","android.permission.ACCESS_COARSE_LOCATION","android.permission.ACCESS_WIFI_STATE","android.permission.BLUETOOTH","android.permission.BLUETOOTH_SCAN","android.permission.READ_PHONE_STATE","android.permission.ACCESS_BACKGROUND_LOCATION"]
    if not IS_TERMUX: return
    print("[+] Intentando conceder permisos...")
    for p in permisos:
        try:
            subprocess.run(f"pm grant com.termux {p}", shell=True, capture_output=True, timeout=3)
        except: pass
    print("[!] Si fallaron, ejecuta: termux-location, termux-bluetooth-scan, termux-wifi-scaninfo, termux-telephony-cellinfo")
""" + c[imports_end:]

# 2. Agregar self.last_radar en GPSCore.__init__
c = c.replace("self.last_wifi_networks: List[Dict] = []",
              "self.last_wifi_networks: List[Dict] = []\n        self.last_radar = [0] * 8")

# 3. Reemplazar bloque WiFi en _update_loop
old_wifi = """            try:
                wifi_nets = self.wifi.scan()
                self.last_wifi_networks = wifi_nets
                wifi_pos = self.wifi.get_location(wifi_nets)
                if wifi_pos:
                    wifi_loc = Coordinate(wifi_pos[0], wifi_pos[1],
                                          accuracy=wifi_pos[2], source='wifi')
                    locations.append((wifi_loc, self.source_weights['wifi']))
                    self.stats['wifi_updates'] += 1
            except Exception:
                pass"""
new_wifi = """            try:
                wifi_nets = self.wifi.scan()
                self.last_wifi_networks = wifi_nets
                if wifi_nets and len(wifi_nets) > 0:
                    self.last_radar = compute_wifi_radar(wifi_nets)
                else:
                    self.last_radar = [0] * 8
                wifi_pos = self.wifi.get_location(wifi_nets)
                if wifi_pos:
                    wifi_loc = Coordinate(wifi_pos[0], wifi_pos[1],
                                          accuracy=wifi_pos[2], source='wifi')
                    locations.append((wifi_loc, self.source_weights['wifi']))
                    self.stats['wifi_updates'] += 1
            except Exception:
                pass"""
c = c.replace(old_wifi, new_wifi)

# 4. Cambiar condición de calidad GPS
c = c.replace("if accuracy < 10.0 and (self.kalman is None or self.kalman.get_quality() > 0.5):",
              "if accuracy < 25.0 and (self.kalman is None or self.kalman.get_quality() > 0.3):")

# 5. Reemplazar endpoint /api/gps/estado_completo
old_endpoint = """@app.route('/api/gps/estado_completo')
def gps_estado_completo():
    user_id = _get_user_id_from_request()
    sess = get_session(user_id)
    if not sess or not sess.symbiosis:
        return jsonify({"error": "GPS Symbiosis no disponible para esta sesion"}), 503
    try:
        status = sess.symbiosis.get_system_status()
        if sess.symbiosis.rl:
            status["rl"] = {
                "step": sess.symbiosis.rl.step,
                "weights": sess.symbiosis.rl.weights,
                "performance": sess.symbiosis.rl.perf
            }
        if sess.symbiosis.gps.geofence:
            status["geofences_stats"] = sess.symbiosis.gps.geofence.get_statistics()
        return jsonify(status), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500"""

new_endpoint = """@app.route('/api/gps/estado_completo')
def gps_estado_completo():
    user_id = _get_user_id_from_request()
    sess = get_session(user_id)
    if not sess or not sess.symbiosis:
        return jsonify({"error": "GPS Symbiosis no disponible para esta sesion"}), 503
    try:
        status = sess.symbiosis.get_system_status()
        if sess.symbiosis.rl:
            status["rl"] = {
                "step": sess.symbiosis.rl.step,
                "weights": sess.symbiosis.rl.weights,
                "performance": sess.symbiosis.rl.perf
            }
        if sess.symbiosis.gps.geofence:
            status["geofences_stats"] = sess.symbiosis.gps.geofence.get_statistics()
        if hasattr(sess.symbiosis.gps, 'last_radar'):
            radar = sess.symbiosis.gps.last_radar
            if radar and len(radar) == 8:
                status['radar'] = radar
                if 'gps_stats' not in status:
                    status['gps_stats'] = {}
                status['gps_stats']['radar'] = radar
                sectores = ['north', 'northeast', 'east', 'southeast', 'south', 'southwest', 'west', 'northwest']
                for i, sec in enumerate(sectores):
                    status['gps_stats'][f'wifi_radar_{sec}'] = radar[i]
        return jsonify(status), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500"""
c = c.replace(old_endpoint, new_endpoint)

# 6. Insertar endpoint /api/bluetooth/scan después del anterior
bt_endpoint = """
@app.route('/api/bluetooth/scan', methods=['POST', 'OPTIONS'])
def api_bluetooth_scan():
    if request.method == 'OPTIONS':
        return '', 204
    user_id = _get_user_id_from_request()
    sess = get_session(user_id)
    if not sess or not sess.symbiosis:
        return jsonify({"success": False, "error": "Sesion no disponible"}), 404
    try:
        bt_devices = sess.symbiosis.gps.bluetooth.scan_advanced()
        devices_formatted = []
        for d in bt_devices:
            devices_formatted.append({'mac': d.get('mac','unknown'), 'name': d.get('name','unknown'), 'rssi': int(d.get('rssi',-60)), 'distance': round(d.get('distance',0),2)})
        if devices_formatted and hasattr(sess.symbiosis.gps, '_is_gps_quality_valid'):
            if sess.symbiosis.gps._is_gps_quality_valid():
                lat, lon, acc, _ = sess.symbiosis.gps.last_gps_quality_location
                for dev in devices_formatted:
                    mac = dev['mac']
                    if mac not in sess.symbiosis.gps.bluetooth.known_beacons:
                        sess.symbiosis.gps.bluetooth.learn_beacon_automatically(mac, lat, lon, confidence=0.6)
        return jsonify({"success": True, "devices": devices_formatted, "count": len(devices_formatted)}), 200
    except Exception as e:
        return jsonify({"success": False, "error": str(e)}), 500"""
# Insertar justo después de la función anterior (antes de la siguiente ruta)
c = c.replace("return jsonify({\"error\": str(e)}), 500\n\n@app.route", 
              "return jsonify({\"error\": str(e)}), 500\n" + bt_endpoint + "\n\n@app.route")

# 7. Agregar llamada a solicitar_permisos_termux() en el main
c = c.replace('print("=" * 70)', 'print("=" * 70)\n    solicitar_permisos_termux()   # <--- AGREGADO')

with open(ARCHIVO, "w") as f:
    f.write(c)

print("✅ ¡Parches aplicados exitosamente!")
print("   Backup: sistema_uber_unificado.py.backup")
print("   Reinicia el servidor: python3 sistema_uber_unificado.py")
