#!/usr/bin/env python3
from database.db import db, Session
from sqlalchemy import text

def fix_dashboard():
    print("🔍 DIAGNOSTICANDO ERROR DEL DASHBOARD")
    print("=" * 50)
    
    session = Session()
    
    try:
        # 1. Verificar estructura de activities
        print("\n1. Verificando tabla 'activities':")
        result = session.execute(text("""
            SELECT column_name, data_type 
            FROM information_schema.columns 
            WHERE table_name = 'activities'
        """))
        
        activities_cols = {row[0]: row[1] for row in result}
        print(f"   Columnas: {list(activities_cols.keys())}")
        
        # 2. Verificar estructura de badges
        print("\n2. Verificando tabla 'badges':")
        result = session.execute(text("""
            SELECT column_name, data_type 
            FROM information_schema.columns 
            WHERE table_name = 'badges'
        """))
        
        badges_cols = {row[0]: row[1] for row in result}
        print(f"   Columnas: {list(badges_cols.keys())}")
        print(f"   Tipo de 'id': {badges_cols.get('id')}")
        
        # 3. Verificar estructura de courses
        print("\n3. Verificando tabla 'courses':")
        result = session.execute(text("""
            SELECT column_name, data_type 
            FROM information_schema.columns 
            WHERE table_name = 'courses'
        """))
        
        courses_cols = {row[0]: row[1] for row in result}
        print(f"   Columnas: {list(courses_cols.keys())}")
        
        # 4. Test query del dashboard
        print("\n4. Probando query del dashboard:")
        test_user_id = 2  # Usuario de prueba
        
        result = session.execute(text(f"""
            SELECT COUNT(*) FROM activities WHERE user_id = {test_user_id}
        """))
        print(f"   ✅ Activities count: {result.scalar()}")
        
        result = session.execute(text(f"""
            SELECT COUNT(*) FROM user_badges WHERE user_id = {test_user_id}
        """))
        print(f"   ✅ User badges count: {result.scalar()}")
        
    except Exception as e:
        print(f"\n❌ ERROR: {e}")
        import traceback
        traceback.print_exc()
    finally:
        session.close()

if __name__ == "__main__":
    fix_dashboard()