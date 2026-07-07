#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
SCRIPT DE VERIFICACIÓN DE PARCHES - BLE BRIDGE APP
===================================================
Verifica que todos los parches críticos estén aplicados.
Uso: python verify_patches.py
"""
import sys
from pathlib import Path

class PatchVerifier:
    """Verifica que los parches estén aplicados."""
    
    def __init__(self, project_dir: str = "."):
        self.project_dir = Path(project_dir).resolve()
        self.app_dir = self.project_dir / "app"
        self.checks_passed = 0
        self.checks_failed = 0
    
    def check(self, name: str, condition: bool, details: str = ""):
        """Registra el resultado de una verificación."""
        if condition:
            print(f"✅ {name}")
            self.checks_passed += 1
        else:
            print(f"❌ {name}")
            if details:
                print(f"   └─ {details}")
            self.checks_failed += 1
    
    def verify_all(self) -> bool:
        """Verifica todos los parches críticos."""
        print("=" * 60)
        print("🔍 VERIFICACIÓN DE PARCHES CRÍTICOS")
        print("=" * 60)
        
        # 1. Verificar build.gradle raíz (AGP)
        root_build = self.project_dir / "build.gradle"
        if root_build.exists():
            content = root_build.read_text()
            self.check(
                "AGP >= 8.3.0",
                "gradle:8.3" in content or "gradle:8.4" in content,
                "Se requiere AGP 8.3+ para Java 21"
            )
        else:
            self.check("build.gradle existe", False)
        
        # 2. Verificar app/build.gradle (compileSdk)
        app_build = self.app_dir / "build.gradle"
        if app_build.exists():
            content = app_build.read_text()
            self.check(
                "compileSdk = 34",
                "compileSdk 34" in content,
                "Se requiere compileSdk 34 para Android 14"
            )
            self.check(
                "targetSdk = 34",
                "targetSdk 34" in content,
                "Se requiere targetSdk 34"
            )
        else:
            self.check("app/build.gradle existe", False)
        
        # 3. Verificar AndroidManifest.xml
        manifest = self.app_dir / "src" / "main" / "AndroidManifest.xml"
        if manifest.exists():
            content = manifest.read_text()
            self.check(
                "foregroundServiceType presente",
                'foregroundServiceType="connectedDevice"' in content,
                "Requerido para Foreground Service en Android 14"
            )
            self.check(
                "Permiso FOREGROUND_SERVICE_CONNECTED_DEVICE",
                "FOREGROUND_SERVICE_CONNECTED_DEVICE" in content,
                "Permiso necesario para el tipo de servicio"
            )
            self.check(
                "Permiso BLUETOOTH_SCAN",
                "BLUETOOTH_SCAN" in content,
                "Requerido para escaneo BLE en Android 12+"
            )
            self.check(
                "Permiso BLUETOOTH_CONNECT",
                "BLUETOOTH_CONNECT" in content,
                "Requerido para conexión BLE en Android 12+"
            )
        else:
            self.check("AndroidManifest.xml existe", False)
        
        # 4. Verificar BridgeService.java
        bridge_service = self.app_dir / "src" / "main" / "java" / "com" / "radar" / "bridge" / "BridgeService.java"
        if bridge_service.exists():
            content = bridge_service.read_text()
            self.check(
                "Import ServiceInfo",
                "import android.content.pm.ServiceInfo;" in content,
                "Necesario para FOREGROUND_SERVICE_TYPE"
            )
            self.check(
                "startForeground con tipo",
                "ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE" in content,
                "Requerido para Android 10+"
            )
        else:
            self.check("BridgeService.java existe", False)
        
        # 5. Verificar MainActivity.java
        main_activity = self.app_dir / "src" / "main" / "java" / "com" / "radar" / "bridge" / "MainActivity.java"
        if main_activity.exists():
            content = main_activity.read_text()
            self.check(
                "Solicitud dinámica BLUETOOTH_SCAN",
                "BLUETOOTH_SCAN" in content and "Build.VERSION_CODES.S" in content,
                "Requerido para Android 12+ (API 31+)"
            )
        else:
            self.check("MainActivity.java existe", False)
        
        # 6. Verificar gradle.properties global
        global_props = Path.home() / ".gradle" / "gradle.properties"
        if global_props.exists():
            content = global_props.read_text()
            self.check(
                "gradle.properties limpio",
                "aapt2FromMavenOverride" not in content,
                "Configuración problemática de aapt2 detectada"
            )
        else:
            self.check("gradle.properties global", True, "No existe (OK)")
        
        return self.checks_failed == 0

def main():
    """Punto de entrada principal."""
    print("\n" + "=" * 60)
    print("🔍 VERIFICADOR DE PARCHES - BLE BRIDGE APP")
    print("=" * 60)
    
    verifier = PatchVerifier(".")
    all_ok = verifier.verify_all()
    
    print("\n" + "=" * 60)
    print("📊 RESUMEN")
    print("=" * 60)
    print(f"✅ Verificaciones pasadas: {verifier.checks_passed}")
    print(f"❌ Verificaciones fallidas: {verifier.checks_failed}")
    
    if all_ok:
        print("\n✅ TODOS LOS PARCHES ESTÁN APLICADOS")
        print("\n📌 Puedes compilar con:")
        print("   python build_bridge.py")
        return 0
    else:
        print("\n❌ FALTAN PARCHES CRÍTICOS")
        print("\n💡 Ejecuta para reparar automáticamente:")
        print("   python fix_build.py")
        return 1

if __name__ == "__main__":
    sys.exit(main())
