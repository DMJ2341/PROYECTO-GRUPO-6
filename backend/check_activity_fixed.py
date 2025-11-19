#!/usr/bin/env python3
# check_activity_fixed.py

from database.db import db, Session
from sqlalchemy import text
import sys
import os

def check_activity_fixed():
    print("🔍 VERIFICACIÓN ACTIVITY SERVICE (SIN RECARGA)")
    print("=" * 50)
    
    try:
        # 1. Verificar estructura de la tabla en BD (sin cargar modelos)
        print("1. 🗄️ ESTRUCTURA DE LA TABLA EN BD...")
        session = Session()
        
        result = session.execute(text("""
            SELECT column_name, data_type, is_nullable
            FROM information_schema.columns 
            WHERE table_name = 'activities'
            ORDER BY ordinal_position
        """))
        
        db_columns = []
        for row in result:
            db_columns.append(row[0])
            print(f"   - {row[0]} ({row[1]}, nullable: {row[2]})")
        
        # Verificar columnas críticas
        critical_columns = ['activity_type', 'xp_earned']
        missing_columns = [col for col in critical_columns if col not in db_columns]
        
        if missing_columns:
            print(f"   ❌ FALTAN COLUMNAS: {missing_columns}")
            print(f"   💡 Las columnas necesarias son: {critical_columns}")
            return False
        else:
            print("   ✅ Todas las columnas críticas existen")
        
        # 2. Verificar datos
        print("\n2. 📊 DATOS EN TABLA ACTIVITIES...")
        result = session.execute(text("SELECT COUNT(*) FROM activities"))
        total_records = result.scalar()
        print(f"   Total registros: {total_records}")
        
        if total_records > 0:
            print("   Primeros 3 registros:")
            result = session.execute(text("SELECT * FROM activities LIMIT 3"))
            for i, row in enumerate(result):
                row_dict = dict(row)
                # Mostrar solo columnas importantes
                important_cols = {k: v for k, v in row_dict.items() if k in ['id', 'user_id', 'activity_type', 'xp_earned']}
                print(f"     {i+1}. {important_cols}")
        
        session.close()
        
        # 3. Verificar si el modelo está cargado correctamente
        print("\n3. 🔍 VERIFICANDO SI EL MODELO ESTÁ CARGADO...")
        try:
            # Intentar importar sin forzar recarga
            from models.activity import Activity
            
            print("   ✅ Modelo Activity importado")
            print("   Columnas del modelo:")
            for column in Activity.__table__.columns:
                print(f"     - {column.name} ({column.type})")
                
        except Exception as e:
            print(f"   ❌ Error importando modelo: {e}")
            return False
        
        # 4. Probar consultas SQL directas
        print("\n4. 🧪 PROBANDO CONSULTAS SQL...")
        session = Session()
        try:
            # Probar usuario específico (cambia el ID si es necesario)
            test_user_id = 2  # Usa el ID de tu usuario recién creado
            
            # Suma de XP
            result = session.execute(text(f"""
                SELECT SUM(xp_earned) as total_xp 
                FROM activities 
                WHERE user_id = {test_user_id}
            """))
            total_xp = result.scalar() or 0
            print(f"   ✅ Total XP usuario {test_user_id}: {total_xp}")
            
            # Conteo por tipo de actividad
            result = session.execute(text(f"""
                SELECT activity_type, COUNT(*) as count 
                FROM activities 
                WHERE user_id = {test_user_id}
                GROUP BY activity_type
            """))
            
            activity_counts = {}
            for row in result:
                activity_counts[row[0]] = row[1]
                print(f"   ✅ {row[0]}: {row[1]} actividades")
            
            if not activity_counts:
                print("   ℹ️  El usuario no tiene actividades aún")
                
        finally:
            session.close()
        
        print("\n🎉 VERIFICACIÓN COMPLETADA")
        return True
        
    except Exception as e:
        print(f"❌ ERROR: {e}")
        import traceback
        traceback.print_exc()
        return False

if __name__ == "__main__":
    success = check_activity_fixed()
    
    if success:
        print("\n✅ RECOMENDACIÓN: Ahora prueba el dashboard en Android")
    else:
        print("\n❌ PROBLEMA: Revisa la estructura de la tabla 'activities'")
    
    sys.exit(0 if success else 1)