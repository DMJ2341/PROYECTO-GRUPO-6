#!/usr/bin/env python3
# check_badges_structure.py

from database.db import db, Session
from sqlalchemy import text

def check_badges_structure():
    print("🔍 VERIFICANDO ESTRUCTURA DE BADGES")
    print("=" * 40)
    
    try:
        session = Session()
        
        # 1. Verificar estructura de la tabla badges
        print("1. 📋 ESTRUCTURA REAL DE BADGES:")
        result = session.execute(text("""
            SELECT column_name, data_type, is_nullable
            FROM information_schema.columns 
            WHERE table_name = 'badges'
            ORDER BY ordinal_position
        """))
        
        badge_columns = []
        for row in result:
            badge_columns.append(row[0])
            print(f"   - {row[0]} ({row[1]}, nullable: {row[2]})")
        
        # 2. Verificar datos de ejemplo
        print("\n2. 📊 DATOS EN BADGES:")
        result = session.execute(text("SELECT COUNT(*) FROM badges"))
        total_badges = result.scalar()
        print(f"   Total badges: {total_badges}")
        
        if total_badges > 0:
            print("   Primeros 3 badges:")
            result = session.execute(text("""
                SELECT id, name, description, icon, points_required
                FROM badges 
                LIMIT 3
            """))
            
            for i, row in enumerate(result):
                print(f"     {i+1}. {row[1]} ({row[4]} pts) - {row[2]}")
        
        session.close()
        
        print(f"\n💡 COLUMNAS QUE EXISTEN: {', '.join(badge_columns)}")
        return badge_columns
        
    except Exception as e:
        print(f"❌ ERROR: {e}")
        import traceback
        traceback.print_exc()
        return []

if __name__ == "__main__":
    check_badges_structure()