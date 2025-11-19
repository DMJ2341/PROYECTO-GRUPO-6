#!/usr/bin/env python3
# check_database.py

from database.db import db, Session
from sqlalchemy import text

def check_database():
    print("🔍 VERIFICANDO BASE DE DATOS")
    print("=" * 40)
    
    try:
        session = Session()
        
        # Ver tablas existentes
        result = session.execute(text("""
            SELECT table_name 
            FROM information_schema.tables 
            WHERE table_schema = 'public'
            ORDER BY table_name
        """))
        
        tables = [row[0] for row in result]
        print(f"📊 Tablas existentes ({len(tables)}):")
        for table in tables:
            print(f"   - {table}")
        
        # Ver datos en cada tabla
        for table in tables:
            print(f"\n📋 Contenido de {table}:")
            try:
                result = session.execute(text(f"SELECT COUNT(*) FROM {table}"))
                count = result.scalar()
                print(f"   Registros: {count}")
                
                if count > 0:
                    result = session.execute(text(f"SELECT * FROM {table} LIMIT 3"))
                    for row in result:
                        print(f"   {row}")
            except Exception as e:
                print(f"   ❌ Error leyendo tabla: {e}")
        
        session.close()
        print("\n✅ Verificación completada")
        
    except Exception as e:
        print(f"❌ Error general: {e}")

if __name__ == "__main__":
    check_database()