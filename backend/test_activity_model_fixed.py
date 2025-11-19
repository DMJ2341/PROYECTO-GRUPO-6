#!/usr/bin/env python3
# test_activity_model_fixed.py

from database.db import db, Session
from models.activity import Activity
from sqlalchemy import text

def test_activity_model_fixed():
    print("🧪 VERIFICANDO MODELO ACTIVITY CORREGIDO")
    print("=" * 45)
    
    try:
        # 1. Verificar que el modelo se carga correctamente
        print("1. 📋 MODELO ACTIVITY CARGADO:")
        for column in Activity.__table__.columns:
            print(f"   - {column.name} ({column.type})")
        
        # Verificar que NO tiene description
        has_description = any(column.name == 'description' for column in Activity.__table__.columns)
        if has_description:
            print("   ❌ ERROR: El modelo todavía tiene 'description'")
            return False
        else:
            print("   ✅ CORRECTO: El modelo NO tiene 'description'")
        
        # 2. Probar consultas con el modelo
        print("\n2. 🔍 PROBANDO CONSULTAS CON MODELO...")
        session = Session()
        
        # Probar contar actividades (esto debería funcionar ahora)
        activity_count = session.query(Activity).count()
        print(f"   ✅ Total actividades en BD: {activity_count}")
        
        # Probar filtrar por activity_type
        lesson_completed_count = session.query(Activity).filter(
            Activity.activity_type == 'lesson_completed'
        ).count()
        print(f"   ✅ Lecciones completadas: {lesson_completed_count}")
        
        # Probar suma de XP
        from sqlalchemy import func
        result = session.query(func.sum(Activity.xp_earned)).first()
        total_xp = result[0] or 0
        print(f"   ✅ Total XP en sistema: {total_xp}")
        
        session.close()
        
        print("\n3. 🎯 PROBANDO DASHBOARD QUERIES...")
        session = Session()
        
        # Simular la query que fallaba en el dashboard
        test_user_id = 2
        try:
            completed_count = session.query(Activity).filter(
                Activity.user_id == test_user_id,
                Activity.activity_type == 'lesson_completed',
                Activity.lesson_id.isnot(None)
            ).count()
            print(f"   ✅ Query del dashboard FUNCIONA: {completed_count} lecciones completadas")
        except Exception as e:
            print(f"   ❌ Query del dashboard FALLA: {e}")
            return False
        
        session.close()
        
        print("\n🎉 MODELO ACTIVITY 100% FUNCIONAL")
        print("💡 El dashboard debería cargar sin errores ahora")
        return True
        
    except Exception as e:
        print(f"❌ ERROR: {e}")
        import traceback
        traceback.print_exc()
        return False

if __name__ == "__main__":
    success = test_activity_model_fixed()
    exit(0 if success else 1)