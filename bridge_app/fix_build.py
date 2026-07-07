#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
SCRIPT DE REPARACIÓN AUTOMÁTICA - BLE BRIDGE APP
===================================================
Diagnostica y repara automáticamente problemas de compilación en Termux.
Uso: python fix_build.py
"""
import os
import sys
import shutil
import re
from pathlib import Path
from typing import List

class BuildFixer:
    """Repara automáticamente problemas de compilación."""
    
    def __init__(self, project_dir: str = "."):
        self.project_dir = Path(project_dir).resolve()
        self.app_dir = self.project_dir / "app"
        self.issues_found: List[str] = []
        self.fixes_applied: List[str] = []
    
    def diagnose(self) -> bool:
        """Diagnostica problemas comunes."""
        print("=" * 60)
        print("🔍 DIAGNÓSTICO DE PROBLEMAS DE COMPILACIÓN")
        print("=" * 60)
        
        # Verificar estructura del proyecto
        if not (self.project_dir / "build.gradle").exists():
            print("❌ No se encontró build.gradle en la raíz")
            self.issues_found.append("build.gradle faltante")
            return False
        
        if not self.app_dir.exists():
            print("❌ No se encontró directorio app/")
            self.issues_found.append("Directorio app/ faltante")
            return False
        
        print("✅ Estructura del proyecto correcta")
        
        # Verificar gradle.properties global
        global_props = Path.home() / ".gradle" / "gradle.properties"
        if global_props.exists():
            content = global_props.read_text()
            if "aapt2FromMavenOverride" in content:
                print("⚠️  Configuración problemática en ~/.gradle/gradle.properties")
                self.issues_found.append("aapt2FromMavenOverride en gradle.properties global")
            else:
                print("✅ gradle.properties global limpio")
        else:
            print("✅ No existe gradle.properties global")
        
        # Verificar build.gradle raíz
        root_build = self.project_dir / "build.gradle"
        if root_build.exists():
            content = root_build.read_text()
            if "gradle:8.2.0" in content:
                print("⚠️  AGP 8.2.0 detectado (incompatible con Java 21)")
                self.issues_found.append("AGP 8.2.0 incompatible")
            elif "gradle:8.3" in content or "gradle:8.4" in content:
                print("✅ AGP compatible detectado")
            else:
                print("⚠️  Versión de AGP no reconocida")
        
        # Verificar app/build.gradle
        app_build = self.app_dir / "build.gradle"
        if app_build.exists():
            content = app_build.read_text()
            if "compileSdk 33" in content:
                print("⚠️  compileSdk 33 detectado (recomendado: 34)")
                self.issues_found.append("compileSdk 33")
            elif "compileSdk 34" in content:
                print("✅ compileSdk 34 correcto")
        
        # Verificar AndroidManifest.xml
        manifest = self.app_dir / "src" / "main" / "AndroidManifest.xml"
        if manifest.exists():
            content = manifest.read_text()
            if 'foregroundServiceType="connectedDevice"' not in content:
                print("❌ Falta foregroundServiceType en manifest")
                self.issues_found.append("foregroundServiceType faltante")
            else:
                print("✅ foregroundServiceType presente")
            
            if "FOREGROUND_SERVICE_CONNECTED_DEVICE" not in content:
                print("❌ Falta permiso FOREGROUND_SERVICE_CONNECTED_DEVICE")
                self.issues_found.append("Permiso FOREGROUND_SERVICE_CONNECTED_DEVICE faltante")
            else:
                print("✅ Permiso FOREGROUND_SERVICE_CONNECTED_DEVICE presente")
        
        # Verificar BridgeService.java
        bridge_service = self.app_dir / "src" / "main" / "java" / "com" / "radar" / "bridge" / "BridgeService.java"
        if bridge_service.exists():
            content = bridge_service.read_text()
            if "ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE" not in content:
                print("❌ Falta ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE en BridgeService")
                self.issues_found.append("ServiceInfo.FOREGROUND_SERVICE_TYPE faltante")
            else:
                print("✅ ServiceInfo.FOREGROUND_SERVICE_TYPE presente")
        
        # Verificar MainActivity.java
        main_activity = self.app_dir / "src" / "main" / "java" / "com" / "radar" / "bridge" / "MainActivity.java"
        if main_activity.exists():
            content = main_activity.read_text()
            if "BLUETOOTH_SCAN" not in content:
                print("❌ Falta permiso BLUETOOTH_SCAN en MainActivity")
                self.issues_found.append("Permiso BLUETOOTH_SCAN faltante")
            else:
                print("✅ Permiso BLUETOOTH_SCAN presente")
        
        print("\n" + "=" * 60)
        if self.issues_found:
            print(f"⚠️  Se encontraron {len(self.issues_found)} problema(s)")
            for i, issue in enumerate(self.issues_found, 1):
                print(f"   {i}. {issue}")
            return False
        else:
            print("✅ No se encontraron problemas")
            return True
    
    def fix_all(self) -> bool:
        """Aplica todas las correcciones necesarias."""
        print("\n" + "=" * 60)
        print("🔧 APLICANDO CORRECCIONES AUTOMÁTICAS")
        print("=" * 60)
        
        try:
            # 1. Limpiar gradle.properties global
            self._fix_global_gradle_properties()
            
            # 2. Actualizar AGP a 8.3.2
            self._fix_agp_version()
            
            # 3. Actualizar compileSdk a 34
            self._fix_compile_sdk()
            
            # 4. Aplicar parches de Android 14
            self._fix_android_14_patches()
            
            # 5. Limpiar caché
            self._clean_cache()
            
            print("\n" + "=" * 60)
            print(f"✅ {len(self.fixes_applied)} corrección(es) aplicada(s)")
            for i, fix in enumerate(self.fixes_applied, 1):
                print(f"   {i}. {fix}")
            return True
            
        except Exception as e:
            print(f"\n❌ Error aplicando correcciones: {e}")
            import traceback
            traceback.print_exc()
            return False
    
    def _fix_global_gradle_properties(self):
        """Elimina configuración problemática de aapt2."""
        global_props = Path.home() / ".gradle" / "gradle.properties"
        if global_props.exists():
            content = global_props.read_text()
            if "aapt2FromMavenOverride" in content:
                lines = [line for line in content.split('\n') if 'aapt2FromMavenOverride' not in line]
                global_props.write_text('\n'.join(lines))
                self.fixes_applied.append("Eliminado aapt2FromMavenOverride de ~/.gradle/gradle.properties")
                print("✅ Corregido: ~/.gradle/gradle.properties")
    
    def _fix_agp_version(self):
        """Actualiza AGP de 8.2.0 a 8.3.2."""
        root_build = self.project_dir / "build.gradle"
        if root_build.exists():
            content = root_build.read_text()
            if "gradle:8.2.0" in content:
                content = content.replace("gradle:8.2.0", "gradle:8.3.2")
                root_build.write_text(content)
                self.fixes_applied.append("Actualizado AGP de 8.2.0 a 8.3.2")
                print("✅ Corregido: AGP actualizado a 8.3.2")
    
    def _fix_compile_sdk(self):
        """Actualiza compileSdk y targetSdk de 33 a 34."""
        app_build = self.app_dir / "build.gradle"
        if app_build.exists():
            content = app_build.read_text()
            modified = False
            
            if "compileSdk 33" in content:
                content = content.replace("compileSdk 33", "compileSdk 34")
                modified = True
            
            if "targetSdk 33" in content:
                content = content.replace("targetSdk 33", "targetSdk 34")
                modified = True
            
            if modified:
                app_build.write_text(content)
                self.fixes_applied.append("Actualizado compileSdk/targetSdk a 34")
                print("✅ Corregido: compileSdk/targetSdk actualizados a 34")
    
    def _fix_android_14_patches(self):
        """Aplica parches críticos para Android 14."""
        # Parche 1: AndroidManifest.xml
        manifest = self.app_dir / "src" / "main" / "AndroidManifest.xml"
        if manifest.exists():
            content = manifest.read_text()
            modified = False
            
            # Añadir foregroundServiceType
            if 'foregroundServiceType="connectedDevice"' not in content:
                content = content.replace(
                    '<service\n            android:name=".BridgeService"\n            android:exported="false" />',
                    '<service\n            android:name=".BridgeService"\n            android:exported="false"\n            android:foregroundServiceType="connectedDevice" />'
                )
                modified = True
            
            # Añadir permiso FOREGROUND_SERVICE_CONNECTED_DEVICE
            if "FOREGROUND_SERVICE_CONNECTED_DEVICE" not in content:
                content = content.replace(
                    '<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />',
                    '<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />\n    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_CONNECTED_DEVICE" />'
                )
                modified = True
            
            if modified:
                manifest.write_text(content)
                self.fixes_applied.append("Aplicados parches a AndroidManifest.xml")
                print("✅ Corregido: AndroidManifest.xml")
        
        # Parche 2: BridgeService.java
        bridge_service = self.app_dir / "src" / "main" / "java" / "com" / "radar" / "bridge" / "BridgeService.java"
        if bridge_service.exists():
            content = bridge_service.read_text()
            modified = False
            
            # Añadir import ServiceInfo
            if "import android.content.pm.ServiceInfo;" not in content:
                content = content.replace(
                    "import android.content.Intent;",
                    "import android.content.Intent;\nimport android.content.pm.ServiceInfo;"
                )
                modified = True
            
            # Actualizar startForeground
            if "ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE" not in content:
                old_code = "startForeground(FOREGROUND_ID, buildNotification());"
                new_code = """if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(FOREGROUND_ID, buildNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE);
        } else {
            startForeground(FOREGROUND_ID, buildNotification());
        }"""
                content = content.replace(old_code, new_code)
                modified = True
            
            if modified:
                bridge_service.write_text(content)
                self.fixes_applied.append("Aplicados parches a BridgeService.java")
                print("✅ Corregido: BridgeService.java")
        
        # Parche 3: MainActivity.java
        main_activity = self.app_dir / "src" / "main" / "java" / "com" / "radar" / "bridge" / "MainActivity.java"
        if main_activity.exists():
            content = main_activity.read_text()
            
            # Reemplazar método requestLocationPermission
            if "BLUETOOTH_SCAN" not in content:
                old_method = """private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                LOCATION_PERMISSION_REQUEST);
    }"""
                
                new_method = """private void requestLocationPermission() {
        java.util.List<String> permissions = new java.util.ArrayList<>();
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN);
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT);
        }
        ActivityCompat.requestPermissions(this,
                permissions.toArray(new String[0]),
                LOCATION_PERMISSION_REQUEST);
    }"""
                
                content = content.replace(old_method, new_method)
                main_activity.write_text(content)
                self.fixes_applied.append("Aplicados parches a MainActivity.java")
                print("✅ Corregido: MainActivity.java")
    
    def _clean_cache(self):
        """Limpia caché de Gradle."""
        cache_dirs = [
            Path.home() / ".gradle" / "caches",
            self.project_dir / ".gradle",
            self.project_dir / "build",
            self.app_dir / "build"
        ]
        
        for cache_dir in cache_dirs:
            if cache_dir.exists():
                shutil.rmtree(cache_dir, ignore_errors=True)
        
        self.fixes_applied.append("Limpiada caché de Gradle")
        print("✅ Corregido: Caché limpiada")

def main():
    """Punto de entrada principal."""
    print("\n" + "=" * 60)
    print("🚀 SCRIPT DE REPARACIÓN AUTOMÁTICA - BLE BRIDGE APP")
    print("=" * 60)
    
    fixer = BuildFixer(".")
    
    # Diagnosticar
    is_clean = fixer.diagnose()
    
    if is_clean:
        print("\n✅ El proyecto está listo para compilar")
        print("\n📌 Próximos pasos:")
        print("   1. Ejecuta: chmod +x gradlew && ./gradlew assembleDebug")
        print("   2. Instala el APK: termux-open app/build/outputs/apk/debug/app-debug.apk")
        return 0
    
    # Aplicar correcciones automáticamente
    print("\n🔧 Aplicando correcciones automáticamente...")
    
    if fixer.fix_all():
        print("\n" + "=" * 60)
        print("✅ REPARACIÓN COMPLETADA")
        print("=" * 60)
        print("\n📌 Próximos pasos:")
        print("   1. Ejecuta: chmod +x gradlew && ./gradlew assembleDebug")
        print("   2. Instala el APK: termux-open app/build/outputs/apk/debug/app-debug.apk")
        return 0
    else:
        print("\n❌ Error durante la reparación")
        return 1

if __name__ == "__main__":
    sys.exit(main())
