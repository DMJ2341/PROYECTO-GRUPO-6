#!/usr/bin/env python3
# diagnose_user_model.py

from database.db import db, Session
from sqlalchemy import text

def diagnose_user_model():
    print("🔍 DIAGNOSTICANDO MODELO USER")
    print("=" * 50)
    
    try:
        session = Session()
        
        # 1. Ver estructura REAL de la tabla
        print("📋 Estructura REAL de la tabla 'users':")
        result = session.execute(text("""
            SELECT column_name, data_type, is_nullable, column_default
            FROM information_schema.columns 
            WHERE table_name = 'users'
            ORDER BY ordinal_position
        """))
        
        real_columns = []
        for row in result:
            print(f"   - {row[0]} ({row[1]}, nullable: {row[2]})")
            real_columns.append(row[0])
        
        # 2. Columnas que Flask está buscando (según el error)
        flask_columns = [
            'id', 'email', 'password_hash', 'name', 
            'created_at', 'updated_at'
        ]
        
        print(f"\n🎯 Columnas que Flask está buscando:")
        for col in flask_columns:
            if col in real_columns:
                print(f"   ✅ {col} - EXISTE")
            else:
                print(f"   ❌ {col} - FALTANTE")
        
        # 3. Columnas extra en la tabla real
        extra_columns = set(real_columns) - set(flask_columns)
        if extra_columns:
            print(f"\n📊 Columnas EXTRA en la tabla real:")
            for col in extra_columns:
                print(f"   📦 {col}")
        
        session.close()
        
    except Exception as e:
        print(f"❌ Error: {e}")
        import traceback
        traceback.print_exc()

if __name__ == "__main__":
    diagnose_user_model()