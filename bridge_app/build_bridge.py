#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
SCRIPT DE COMPILACIÓN AUTOMATIZADA - BLE BRIDGE APP
====================================================
Compila, instala y verifica la app puente automáticamente.
Uso: python build_bridge.py
"""
import os
import sys
import subprocess
import time
from pathlib import Path

class BridgeBuilder:
    """Compila y gestiona la app puente."""
    
    def __init__(self, project_dir: str = "."):
        self.project_dir = Path(project_dir).resolve()
        self.apk_path = self.project_dir / "app" / "build" / "outputs" / "apk" / "debug" / "app-debug.apk"
    
    def check_prerequisites(self) -> bool:
        """Verifica que los archivos necesarios existan."""
        print("=" * 60)
        print("🔍 VERIFICANDO PREREQUISITOS")
        print("=" * 60)
        
        # Verificar gradlew
        gradlew = self.project_dir / "gradlew"
        if not gradlew.exists():
            print("❌ No se encontró gradlew")
            return False
        print("✅ gradlew encontrado")
        
        # Verificar build.gradle
        if not (self.project_dir / "build.gradle").exists():
            print("❌ No se encontró build.gradle")
            return False
        print("✅ build.gradle encontrado")
        
        # Verificar app/build.gradle
        if not (self.project_dir / "app" / "build.gradle").exists():
            print("❌ No se encontró app/build.gradle")
            return False
        print("✅ app/build.gradle encontrado")
        
        # Verificar AndroidManifest.xml
        manifest = self.project_dir / "app" / "src" / "main" / "AndroidManifest.xml"
        if not manifest.exists():
            print("❌ No se encontró AndroidManifest.xml")
            return False
        print("✅ AndroidManifest.xml encontrado")
        
        return True
    
    def build(self) -> bool:
        """Compila el proyecto."""
        print("\n" + "=" * 60)
        print("🔨 COMPILANDO PROYECTO")
        print("=" * 60)
        
        # Dar permisos de ejecución a gradlew
        gradlew = self.project_dir / "gradlew"
        os.chmod(gradlew, 0o755)
        print("✅ Permisos de ejecución otorgados a gradlew")
        
        # Ejecutar compilación
        print("\n📦 Iniciando compilación (esto puede tardar varios minutos)...")
        result = subprocess.run(
            ["./gradlew", "clean", "assembleDebug", "--no-daemon"],
            cwd=self.project_dir,
            capture_output=True,
            text=True
        )
        
        if result.returncode != 0:
            print("\n❌ ERROR DURANTE LA COMPILACIÓN")
            print("\n--- STDOUT ---")
            print(result.stdout[-2000:] if len(result.stdout) > 2000 else result.stdout)
            print("\n--- STDERR ---")
            print(result.stderr[-2000:] if len(result.stderr) > 2000 else result.stderr)
            return False
        
        # Verificar que el APK se generó
        if not self.apk_path.exists():
            print("\n❌ El APK no se generó correctamente")
            return False
        
        print(f"\n✅ COMPILACIÓN EXITOSA")
        print(f"📱 APK generado: {self.apk_path}")
        return True
    
    def install(self) -> bool:
        """Instala el APK usando termux-open."""
        print("\n" + "=" * 60)
        print("📲 INSTALANDO APK")
        print("=" * 60)
        
        if not self.apk_path.exists():
            print("❌ APK no encontrado. Compila primero.")
            return False
        
        print(f"📱 Abriendo instalador para: {self.apk_path.name}")
        print("⚠️  ACEPTA TODOS LOS PERMISOS cuando te los pida")
        
        result = subprocess.run(
            ["termux-open", str(self.apk_path)],
            capture_output=True,
            text=True
        )
        
        if result.returncode != 0:
            print(f"⚠️  Error ejecutando termux-open: {result.stderr}")
            print("💡 Intenta instalar manualmente desde el explorador de archivos")
            return False
        
        print("✅ Instalador abierto. Completa la instalación en tu teléfono.")
        return True
    
    def verify_bridge(self) -> bool:
        """Verifica que el puente esté activo."""
        print("\n" + "=" * 60)
        print("🔌 VERIFICANDO PUENTE HTTP")
        print("=" * 60)
        
        try:
            import urllib.request
            print("📡 Probando conexión a http://127.0.0.1:9876/ping...")
            resp = urllib.request.urlopen("http://127.0.0.1:9876/ping", timeout=3)
            if resp.status == 200:
                print("✅ Puente activo y respondiendo correctamente")
                return True
        except Exception as e:
            print(f"⚠️  Puente no responde: {e}")
            print("\n💡 Posibles causas:")
            print("   1. La app no está instalada")
            print("   2. La app no se ha iniciado manualmente")
            print("   3. Los permisos no fueron aceptados")
            print("\n📌 Pasos a seguir:")
            print("   1. Abre la app 'RadarBridge' desde el launcher")
            print("   2. Acepta todos los permisos")
            print("   3. Vuelve a ejecutar este script")
            return False

def main():
    """Punto de entrada principal."""
    print("\n" + "=" * 60)
    print("🚀 COMPILACIÓN AUTOMATIZADA - BLE BRIDGE APP")
    print("=" * 60)
    
    builder = BridgeBuilder(".")
    
    # Verificar prerequisitos
    if not builder.check_prerequisites():
        print("\n❌ Prerequisitos no cumplidos")
        print("💡 Ejecuta primero: python v.py --prepare-bridge")
        return 1
    
    # Compilar
    if not builder.build():
        print("\n❌ Compilación fallida")
        print("💡 Ejecuta primero: python fix_build.py")
        return 1
    
    # Instalar
    builder.install()
    
    print("\n" + "=" * 60)
    print("⏳ ESPERANDO INSTALACIÓN...")
    print("=" * 60)
    print("\n📌 INSTRUCCIONES:")
    print("   1. Completa la instalación en tu teléfono")
    print("   2. Abre la app 'RadarBridge' desde el launcher")
    print("   3. ACEPTA TODOS LOS PERMISOS")
    print("   4. Presiona ENTER cuando hayas completado estos pasos")
    
    input("\n⏎ Presiona ENTER para verificar el puente...")
    
    # Verificar puente
    if builder.verify_bridge():
        print("\n" + "=" * 60)
        print("✅ TODO LISTO - PUENTE OPERATIVO")
        print("=" * 60)
        print("\n🎉 El sistema está completamente operativo")
        print("💡 Ahora puedes ejecutar: python v.py")
        return 0
    else:
        print("\n" + "=" * 60)
        print("⚠️  PUENTE NO DETECTADO")
        print("=" * 60)
        print("\n💡 El puente no está activo todavía.")
        print("   Asegúrate de haber:")
        print("   1. Instalado la app")
        print("   2. Abierto la app manualmente")
        print("   3. Aceptado todos los permisos")
        return 1

if __name__ == "__main__":
    sys.exit(main())
