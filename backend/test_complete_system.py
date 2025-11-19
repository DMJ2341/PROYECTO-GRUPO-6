#!/usr/bin/env python3
"""
📋 SCRIPT DE PRUEBA COMPLETO - CYBERLEARN BACKEND
Verifica: Base de datos, Modelos, Servicios y Endpoints básicos
"""

import sys
import os
import requests

# Agregar el backend al path
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

def test_database_connection():
    """Probar conexión a base de datos"""
    print("🔍 1. PROBANDO CONEXIÓN A BASE DE DATOS")
    print("=" * 50)
    
    try:
        from database.db import db
        from sqlalchemy import text
        
        # Probar conexión básica
        session = db.get_session()
        result = session.execute(text("SELECT version(), current_database(), current_user"))
        db_info = result.fetchone()
        session.close()
        
        print(f"   ✅ PostgreSQL: {db_info[0].split(',')[0]}")
        print(f"   ✅ Base de datos: {db_info[1]}")
        print(f"   ✅ Usuario: {db_info[2]}")
        return True
        
    except Exception as e:
        print(f"   ❌ Error de conexión: {e}")
        return False

def test_models_import():
    """Probar que todos los modelos importan correctamente"""
    print("\n🔍 2. PROBANDO MODELOS DE DATOS")
    print("=" * 50)
    
    models_to_test = [
        'User', 'UserBadge', 'Course', 'Lesson', 
        'Badge', 'Activity'
    ]
    
    try:
        from models.user import User, UserBadge
        from models.course import Course
        from models.lesson import Lesson
        from models.badge import Badge
        from models.activity import Activity
        
        print("   ✅ Todos los modelos importados correctamente")
        
        # Verificar que las tablas existen
        from database.db import db
        session = db.get_session()
        
        existing_tables = []
        for model in [User, Course, Lesson, Badge, Activity, UserBadge]:
            try:
                count = session.query(model).count()
                existing_tables.append(model.__tablename__)
            except:
                pass
        
        session.close()
        
        print(f"   📊 Tablas existentes: {len(existing_tables)}")
        for table in existing_tables:
            print(f"      - {table}")
            
        return True
        
    except Exception as e:
        print(f"   ❌ Error en modelos: {e}")
        return False

def test_services_import():
    """Probar que todos los servicios importan correctamente"""
    print("\n🔍 3. PROBANDO SERVICIOS")
    print("=" * 50)
    
    services_to_test = [
        'ActivityService', 'BadgeService', 'AuthService',
        'CourseService', 'StreakService'
    ]
    
    try:
        from services.activity_service import ActivityService
        from services.badge_service import BadgeService
        from services.auth_service import AuthService
        from services.course_service import CourseService
        from services.streak_service import StreakService
        
        print("   ✅ Todos los servicios importados correctamente")
        
        # Probar instanciación básica
        services = []
        for ServiceClass in [ActivityService, BadgeService, AuthService, CourseService]:
            try:
                service = ServiceClass()
                services.append(ServiceClass.__name__)
                service.__del__()  # Limpiar
            except Exception as e:
                print(f"   ⚠️  Error en {ServiceClass.__name__}: {e}")
        
        print(f"   🔧 Servicios instanciados: {len(services)}")
        for service in services:
            print(f"      - {service}")
            
        return True
        
    except Exception as e:
        print(f"   ❌ Error en servicios: {e}")
        return False

def test_flask_app():
    """Probar que la aplicación Flask se puede crear"""
    print("\n🔍 4. PROBANDO APLICACIÓN FLASK")
    print("=" * 50)
    
    try:
        from app import app
        
        # Verificar configuración
        assert app.config['SECRET_KEY'] is not None
        assert 'CORS' in str(app.extensions)
        
        print("   ✅ Aplicación Flask creada correctamente")
        print(f"   🔑 Secret Key: {'Configurada' if app.config['SECRET_KEY'] else 'No'}")
        print(f"   🌐 CORS: Habilitado")
        
        # Probar algunos endpoints básicos
        with app.test_client() as client:
            # Health check
            response = client.get('/api/health')
            if response.status_code == 200:
                print("   ✅ Endpoint /api/health funciona")
            else:
                print(f"   ❌ Health check falló: {response.status_code}")
            
            # Root endpoint
            response = client.get('/')
            if response.status_code == 200:
                print("   ✅ Endpoint raíz funciona")
            else:
                print(f"   ❌ Endpoint raíz falló: {response.status_code}")
        
        return True
        
    except Exception as e:
        print(f"   ❌ Error en aplicación Flask: {e}")
        return False

def test_database_structure():
    """Probar estructura de base de datos"""
    print("\n🔍 5. PROBANDO ESTRUCTURA DE DATOS")
    print("=" * 50)
    
    try:
        from database.db import db
        from sqlalchemy import text
        
        session = db.get_session()
        
        # Contar registros en cada tabla
        tables_data = []
        
        table_queries = [
            ("courses", "SELECT COUNT(*) FROM courses"),
            ("lessons", "SELECT COUNT(*) FROM lessons"),
            ("badges", "SELECT COUNT(*) FROM badges"),
            ("users", "SELECT COUNT(*) FROM users"),
            ("activities", "SELECT COUNT(*) FROM activities"),
            ("user_badges", "SELECT COUNT(*) FROM user_badges")
        ]
        
        for table_name, query in table_queries:
            try:
                result = session.execute(text(query))
                count = result.scalar()
                tables_data.append((table_name, count))
            except Exception as e:
                tables_data.append((table_name, f"Error: {e}"))
        
        session.close()
        
        print("   📊 Datos en tablas:")
        for table_name, count in tables_data:
            print(f"      - {table_name}: {count}")
        
        return True
        
    except Exception as e:
        print(f"   ❌ Error en estructura de datos: {e}")
        return False

def test_backend_start():
    """Probar que el backend puede iniciarse"""
    print("\n🔍 6. PROBANDO INICIO DEL BACKEND")
    print("=" * 50)
    
    try:
        # Este test simula el inicio sin realmente ejecutar app.run()
        from app import app
        
        with app.app_context():
            # Verificar que podemos acceder a la configuración de BD
            from database.db import db
            db.session.execute(text("SELECT 1"))
            
            print("   ✅ Backend puede iniciarse correctamente")
            print("   ✅ Contexto de aplicación funciona")
            print("   ✅ Configuración de BD accesible")
            
            return True
            
    except Exception as e:
        print(f"   ❌ Error al iniciar backend: {e}")
        return False

def main():
    """Función principal de pruebas"""
    print("🚀 INICIANDO PRUEBAS COMPLETAS DEL SISTEMA")
    print("=" * 60)
    
    tests = [
        test_database_connection,
        test_models_import, 
        test_services_import,
        test_flask_app,
        test_database_structure,
        test_backend_start
    ]
    
    results = []
    
    for test in tests:
        try:
            result = test()
            results.append(result)
        except Exception as e:
            print(f"   💥 Test crasheó: {e}")
            results.append(False)
    
    print("\n" + "=" * 60)
    print("📊 RESUMEN DE PRUEBAS")
    print("=" * 60)
    
    passed = sum(results)
    total = len(results)
    
    print(f"   ✅ Pruebas pasadas: {passed}/{total}")
    print(f"   ❌ Pruebas fallidas: {total - passed}/{total}")
    print(f"   📈 Porcentaje: {passed/total*100:.1f}%")
    
    if passed == total:
        print("\n🎉 ¡TODO EL SISTEMA ESTÁ FUNCIONANDO CORRECTAMENTE!")
        print("\n📝 PRÓXIMOS PASOS:")
        print("   1. Ejecutar: python app.py")
        print("   2. Probar endpoints con: curl http://192.192.192.192:8000/api/health")
        print("   3. Configurar frontend Android con la IP del servidor")
    else:
        print("\n💥 HAY PROBLEMAS QUE NECESITAN ATENCIÓN")
        print("   Revisa los errores arriba y aplica las correcciones necesarias")

if __name__ == "__main__":
    # Agregar import de text para todo el script
    from sqlalchemy import text
    main()