#!/usr/bin/env python3
"""
REPARA LA CONEXIÓN BLE EN V.PY
Ejecuta: python fix_ble.py
"""
import re
from pathlib import Path

v_py = Path("v.py")
if not v_py.exists():
    print("❌ No se encontró v.py")
    exit(1)

with open(v_py) as f:
    code = f.read()

# 1. Cambiar intervalo de 0.2 a 5.0
code = re.sub(
    r'self\.ble_broadcaster = BLEBroadcastService\(([^,]+),\s*interval\s*=\s*0\.2\)',
    r'self.ble_broadcaster = BLEBroadcastService(\1, interval=5.0)',
    code
)

# 2. Añadir _check_bridge y _send_via_bridge si no existen
if "_check_bridge" not in code:
    code = code.replace(
        "class BLEBroadcastService(threading.Thread):",
        '''class BLEBroadcastService(threading.Thread):
    def _check_bridge(self) -> bool:
        try:
            import urllib.request
            with urllib.request.urlopen('http://127.0.0.1:9876/ping', timeout=2) as resp:
                return resp.read().decode().strip() == 'pong'
        except:
            return False

    def _send_via_bridge(self, data: str):
        try:
            import urllib.request, base64
            payload = base64.b64encode(data.encode()).decode()
            url = f"http://127.0.0.1:9876/advertise?payload={payload}"
            with urllib.request.urlopen(url, timeout=3) as resp:
                if resp.getcode() == 200:
                    self._log("Fragmento enviado")
                else:
                    self._log("Error bridge: código {}".format(resp.getcode()))
        except Exception as e:
            self._log("Error: {}".format(e))
'''
    )

# 3. Parchear el método run() para que use _check_bridge
if "while self._running:" in code:
    # Buscar el bloque run y reemplazar su contenido
    run_pattern = r'(def run\(self\):.*?)(?=def \w+|\Z)'
    def replace_run(m):
        original = m.group(1)
        if "Puente no disponible" in original:
            return original
        nuevo = '''def run(self):
        self._running = True
        if self._termux_available:
            self._log("BLE Broadcast en modo nativo Termux.")
            while self._running:
                frag = self.broadcast.get_next_fragment()
                self._send_native(frag)
                time.sleep(self.interval)
        else:
            self._log("BLE Broadcast en modo puente (intervalo {}s)".format(self.interval))
            while self._running:
                if not self._check_bridge():
                    self._log("Puente no disponible, esperando...")
                    time.sleep(5)
                    continue
                frag = self.broadcast.get_next_fragment()
                self._send_via_bridge(frag)
                time.sleep(self.interval)
'''
        return nuevo
    code = re.sub(run_pattern, replace_run, code, flags=re.DOTALL)

# 4. Añadir verificación al inicio de main()
if "Verificando puente" not in code:
    code = code.replace(
        "def main():",
        '''def main():
    print("[MAIN] Verificando puente BLE...")
    import time, urllib.request
    for i in range(10):
        try:
            with urllib.request.urlopen('http://127.0.0.1:9876/ping', timeout=1) as r:
                if r.read().decode().strip() == 'pong':
                    print("[MAIN] Puente detectado!")
                    break
        except:
            pass
        time.sleep(1)
    else:
        print("[MAIN] Puente no detectado. Modo simulación.")
'''
    )

with open(v_py, "w") as f:
    f.write(code)

print("✅ Reparación completada. Ejecuta: python v.py --broadcast")
