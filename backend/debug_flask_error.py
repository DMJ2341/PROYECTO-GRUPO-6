#!/usr/bin/env python3
"""
🐛 SCRIPT DE DEPURACIÓN - ERROR EN APLICACIÓN FLASK
Identifica exactamente dónde falla la creación de la app
"""

import sys
import os
import traceback

sys.path.append(os.path.dirname(os.path.abspath(__file__)))

def debug_flask_creation():
    """Depurar paso a paso la creación de Flask"""
    print("🐛 DEPURANDO CREACIÓN DE FLASK")
    print("=" * 50)
    
    try:
        print("1. ✅ Importando Flask...")
        from flask import Flask
        print("   ✅ Flask importado correctamente")
        
        print("2. ✅ Creando aplicación Flask...")
        app = Flask(__name__)
        print("   ✅ Aplicación Flask creada")
        
        print("3. ✅ Configurando CORS...")
        from flask_cors import CORS
        CORS(app)
        print("   ✅ CORS configurado")
        
        print("4. ✅ Configurando secret key...")
        app.config['SECRET_KEY'] = 'cyberlearn_super_secret_key_2024_change_in_production'
        print("   ✅ Secret key configurada")
        
        print("5. ✅ Probando import de database...")
        from database.db import db
        print("   ✅ Database importada")
        
        print("6. ✅ Probando import de modelos...")
        from models.user import User, UserBadge
        from models.course import Course
        from models.lesson import Lesson
        from models.activity import Activity
        from models.badge import Badge
        print("   ✅ Modelos importados")
        
        print("7. ✅ Probando import de servicios...")
        from services.activity_service import ActivityService
        from services.course_service import CourseService
        from services.badge_service import BadgeService
        print("   ✅ Servicios importados")
        
        print("8. ✅ Probando conexión a BD en contexto...")
        from sqlalchemy import text
        with app.app_context():
            db.session.execute(text("SELECT 1"))
            print("   ✅ Conexión a BD funciona en contexto")
        
        print("9. ✅ Probando creación de app completa...")
        # Esto simula lo que hace app.py
        from app import app as full_app
        print("   ✅ App completa importada")
        
        print("\n🎉 ¡TODOS LOS COMPONENTES FUNCIONAN!")
        return True
        
    except Exception as e:
        print(f"\n❌ ERROR ENCONTRADO:")
        print(f"   💥 Tipo: {type(e).__name__}")
        print(f"   📝 Mensaje: {e}")
        print(f"\n🔍 TRAZA COMPLETA:")
        traceback.print_exc()
        return False

def check_specific_imports():
    """Verificar imports específicos que podrían fallar"""
    print("\n🔍 VERIFICANDO IMPORTS ESPECÍFICOS")
    print("=" * 50)
    
    imports_to_check = [
        ("database.db", ["db", "Session"]),
        ("models.user", ["User", "UserBadge"]),
        ("models.course", ["Course"]),
        ("models.lesson", ["Lesson"]),
        ("models.activity", ["Activity"]),
        ("models.badge", ["Badge"]),
        ("services.activity_service", ["ActivityService"]),
        ("services.course_service", ["CourseService"]),
        ("services.badge_service", ["BadgeService"]),
        ("services.auth_service", ["AuthService"]),
        ("services.streak_service", ["StreakService"]),
    ]
    
    for module_name, attributes in imports_to_check:
        try:
            module = __import__(module_name, fromlist=attributes)
            for attr in attributes:
                if hasattr(module, attr):
                    print(f"   ✅ {module_name}.{attr}")
                else:
                    print(f"   ❌ {module_name}.{attr} - NO EXISTE")
        except Exception as e:
            print(f"   💥 {module_name} - ERROR: {e}")

def main():
    print("🚀 INICIANDO DEPURACIÓN COMPLETA")
    print("=" * 60)
    
    # 1. Depurar Flask
    flask_ok = debug_flask_creation()
    
    # 2. Verificar imports
    check_specific_imports()
    
    print("\n" + "=" * 60)
    if flask_ok:
        print("🎉 ¡EL PROBLEMA ESTÁ RESUELTO!")
        print("La aplicación Flask ahora debería funcionar")
    else:
        print("💥 HAY ERRORES QUE NECESITAN ATENCIÓN")
        print("Revisa la traza de error arriba")

if __name__ == "__main__":
    main()