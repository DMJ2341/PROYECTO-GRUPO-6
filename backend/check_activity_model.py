#!/usr/bin/env python3
# check_activity_service.py

from database.db import db, Session
from models.activity import Activity
from services.activity_service import ActivityService
from sqlalchemy import text
import sys
import importlib

def check_activity_service():
    print("🔍 VERIFICACIÓN COMPLETA ACTIVITY SERVICE")
    print("=" * 50)
    
    try:
        # 1. Forzar recarga de módulos
        print("1. 🔄 RECARGANDO MÓDULOS...")
        modules_to_reload = ['models.activity', 'services.activity_service']
        for module in modules_to_reload:
            if module in sys.modules:
                del sys.modules[module]
                print(f"   ✅ {module} eliminado de cache")
        
        # Recargar módulos
        from models.activity import Activity
        from services.activity_service import ActivityService
        print("   ✅ Módulos recargados")
        
        # 2. Verificar modelo Activity
        print("\n2. 📋 VERIFICANDO MODELO ACTIVITY...")
        print("   Columnas definidas en el modelo:")
        for column in Activity.__table__.columns:
            print(f"     - {column.name} ({column.type})")
        
        # Verificar atributos específicos
        has_activity_type = hasattr(Activity, 'activity_type')
        has_xp_earned = hasattr(Activity, 'xp_earned')
        print(f"   ✅ activity_type: {'SÍ' if has_activity_type else 'NO'}")
        print(f"   ✅ xp_earned: {'SÍ' if has_xp_earned else 'NO'}")
        
        if not has_activity_type or not has_xp_earned:
            print("   ❌ ERROR: El modelo no tiene los atributos correctos")
            return False
        
        # 3. Verificar estructura REAL de la tabla en BD
        print("\n3. 🗄️ VERIFICANDO ESTRUCTURA DE LA TABLA EN BD...")
        session = Session()
        try:
            result = session.execute(text("""
                SELECT column_name, data_type, is_nullable
                FROM information_schema.columns 
                WHERE table_name = 'activities'
                ORDER BY ordinal_position
            """))
            
            db_columns = []
            for row in result:
                db_columns.append(row[0])
                print(f"     - {row[0]} ({row[1]}, nullable: {row[2]})")
            
            # Verificar coincidencia
            expected_columns = ['activity_type', 'xp_earned']
            missing_in_db = [col for col in expected_columns if col not in db_columns]
            
            if missing_in_db:
                print(f"   ❌ FALTAN COLUMNAS EN BD: {missing_in_db}")
                return False
            else:
                print("   ✅ Todas las columnas necesarias existen en BD")
        
        finally:
            session.close()
        
        # 4. Verificar datos en la tabla
        print("\n4. 📊 VERIFICANDO DATOS EN TABLA ACTIVITIES...")
        session = Session()
        try:
            # Contar registros
            result = session.execute(text("SELECT COUNT(*) FROM activities"))
            total_records = result.scalar()
            print(f"   Total registros: {total_records}")
            
            # Mostrar algunos registros de ejemplo
            if total_records > 0:
                print("   Primeros 3 registros:")
                result = session.execute(text("SELECT * FROM activities LIMIT 3"))
                for i, row in enumerate(result):
                    print(f"     {i+1}. {dict(row)}")
            else:
                print("   ℹ️  La tabla está vacía")
        
        finally:
            session.close()
        
        # 5. Probar ActivityService
        print("\n5. 🧪 PROBANDO ACTIVITY SERVICE...")
        try:
            activity_service = ActivityService()
            print("   ✅ ActivityService se instancia correctamente")
            
            # Probar método get_total_xp con usuario de prueba (ID 1)
            test_user_id = 1
            total_xp = activity_service.get_total_xp(test_user_id)
            print(f"   ✅ get_total_xp({test_user_id}) = {total_xp}")
            
            # Probar método get_user_activities
            activities = activity_service.get_user_activities(test_user_id, limit=2)
            print(f"   ✅ get_user_activities({test_user_id}) = {len(activities)} actividades")
            
            # Probar método get_user_progress
            progress = activity_service.get_user_progress(test_user_id)
            print(f"   ✅ get_user_progress({test_user_id}) = {progress['total_points']} puntos totales")
            
            print("   🎉 TODAS LAS PRUEBAS DEL ACTIVITY SERVICE PASARON")
            
        except Exception as e:
            print(f"   ❌ ERROR en ActivityService: {e}")
            import traceback
            traceback.print_exc()
            return False
        
        # 6. Probar consulta directa a la BD
        print("\n6. 🔍 PROBANDO CONSULTA DIRECTA A BD...")
        session = Session()
        try:
            # Probar suma de xp_earned
            result = session.execute(text(f"""
                SELECT SUM(xp_earned) as total_xp 
                FROM activities 
                WHERE user_id = {test_user_id}
            """))
            total_xp_db = result.scalar() or 0
            print(f"   ✅ Consulta SQL SUM(xp_earned) = {total_xp_db}")
            
            # Probar consulta con activity_type
            result = session.execute(text(f"""
                SELECT COUNT(*) as count 
                FROM activities 
                WHERE user_id = {test_user_id} AND activity_type = 'lesson_completed'
            """))
            lesson_completed_count = result.scalar() or 0
            print(f"   ✅ Lecciones completadas: {lesson_completed_count}")
            
        finally:
            session.close()
        
        print("\n🎉 VERIFICACIÓN COMPLETADA - TODO CORRECTO")
        return True
        
    except Exception as e:
        print(f"❌ ERROR GENERAL: {e}")
        import traceback
        traceback.print_exc()
        return False

if __name__ == "__main__":
    success = check_activity_service()
    sys.exit(0 if success else 1)