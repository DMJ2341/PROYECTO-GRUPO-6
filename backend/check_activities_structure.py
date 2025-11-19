#!/usr/bin/env python3
# check_activities_structure.py

from database.db import db, Session
from sqlalchemy import text

def check_activities_structure():
    print("🔍 VERIFICANDO ESTRUCTURA DE ACTIVITIES")
    print("=" * 40)
    
    try:
        session = Session()
        
        # 1. Verificar estructura de la tabla activities
        print("1. 📋 ESTRUCTURA DE ACTIVITIES:")
        result = session.execute(text("""
            SELECT column_name, data_type, is_nullable
            FROM information_schema.columns 
            WHERE table_name = 'activities'
            ORDER BY ordinal_position
        """))
        
        activity_columns = []
        for row in result:
            activity_columns.append(row[0])
            print(f"   - {row[0]} ({row[1]}, nullable: {row[2]})")
        
        # Verificar columnas críticas
        critical_columns = ['id', 'user_id', 'activity_type', 'xp_earned', 'created_at']
        missing_columns = [col for col in critical_columns if col not in activity_columns]
        
        if missing_columns:
            print(f"   ❌ FALTAN COLUMNAS: {missing_columns}")
        else:
            print("   ✅ Todas las columnas críticas existen")
        
        # Verificar description
        if 'description' not in activity_columns:
            print("   ℹ️  description NO existe en la tabla")
        else:
            print("   ✅ description existe")
        
        # 2. Verificar datos de ejemplo
        print("\n2. 📊 DATOS EN ACTIVITIES:")
        result = session.execute(text("SELECT COUNT(*) FROM activities"))
        total_activities = result.scalar()
        print(f"   Total actividades: {total_activities}")
        
        if total_activities > 0:
            print("   Primeras 3 actividades:")
            result = session.execute(text("""
                SELECT id, user_id, activity_type, xp_earned, lesson_id, created_at 
                FROM activities 
                LIMIT 3
            """))
            
            for i, row in enumerate(result):
                print(f"     {i+1}. User: {row[1]}, Tipo: {row[2]}, XP: {row[3]}, Lección: {row[4]}, Fecha: {row[5]}")
        
        session.close()
        
        print(f"\n💡 RECOMENDACIÓN: {'Actualizar el modelo Activity' if 'description' not in activity_columns else 'Estructura correcta'}")
        return True
        
    except Exception as e:
        print(f"❌ ERROR: {e}")
        import traceback
        traceback.print_exc()
        return False

if __name__ == "__main__":
    check_activities_structure()