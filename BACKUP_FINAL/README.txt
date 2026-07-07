BACKUP FINAL - Sistema Completo
===============================

Puertos:
- 9876 HTTP: RadarBridge (BLE Advertise + Scan)
- 9877 TCP:  GPS_Bridge (GPS + WiFi + Bluetooth + Cell)
- 8080 Web:  Dashboard + Mapa

Archivos principales:
- sistema_uber_unificado.py: Sistema principal con todos los parches
- uber_bridge_client.py: Cliente TCP para puerto 9877
- v.py: BLE Broadcast

Parches aplicados:
- _load_learned_data DESACTIVADO (evita corrupción)
- GPS forzado desde puente 9877
- WiFi real desde puente 9877
- Bluetooth real desde puente 9877
- Cell/Torres reales desde puente 9877
- _send_to_bridge con curl (probado 19/19)

Fecha: Julio 2026
