#!/usr/bin/env python3
# fix_user_badges_complete.py

from database.db import db, Session
from sqlalchemy import text

def fix_user_badges_complete():
    print("🔧 CORRECCIÓN COMPLETA - USER_BADGES")
    print("=" * 45)
    
    try:
        session = Session()
        
        # 1. Verificar estructura actual
        print("1. 📋 ESTRUCTURA ACTUAL DE USER_BADGES:")
        result = session.execute(text("""
            SELECT column_name, data_type, is_nullable
            FROM information_schema.columns 
            WHERE table_name = 'user_badges'
            ORDER BY ordinal_position
        """))
        
        current_columns = {}
        for row in result:
            current_columns[row[0]] = row[1]
            print(f"   - {row[0]} ({row[1]}, nullable: {row[2]})")
        
        # 2. Añadir earned_value si no existe
        if 'earned_value' not in current_columns:
            print("\n2. ➕ AÑADIENDO COLUMNA EARNED_VALUE...")
            session.execute(text("ALTER TABLE user_badges ADD COLUMN earned_value INTEGER DEFAULT 1"))
            session.commit()
            print("   ✅ Columna 'earned_value' añadida")
        else:
            print("\n2. ✅ COLUMNA EARNED_VALUE YA EXISTE")
        
        # 3. Verificar tipo de badge_id
        print("\n3. 🔍 VERIFICANDO TIPO DE BADGE_ID...")
        if current_columns.get('badge_id') == 'character varying':
            print("   ⚠️  badge_id es VARCHAR, pero el modelo espera INTEGER")
            
            # Verificar si se puede convertir
            result = session.execute(text("SELECT badge_id FROM user_badges WHERE badge_id !~ '^\\d+$' LIMIT 5"))
            non_numeric = [row[0] for row in result]
            
            if non_numeric:
                print(f"   ❌ No se puede convertir - valores no numéricos: {non_numeric}")
                print("   💡 Solución: Actualizar el modelo para usar String o limpiar datos")
            else:
                print("   ✅ Todos los badge_id son numéricos - se puede convertir")
                # session.execute(text("ALTER TABLE user_badges ALTER COLUMN badge_id TYPE INTEGER USING badge_id::integer"))
                # session.commit()
                # print("   ✅ badge_id convertido a INTEGER")
        
        # 4. Verificar datos actuales
        print("\n4. 📊 DATOS ACTUALES EN USER_BADGES:")
        result = session.execute(text("SELECT COUNT(*) FROM user_badges"))
        total_records = result.scalar()
        print(f"   Total registros: {total_records}")
        
        if total_records > 0:
            print("   Primeros 3 registros:")
            result = session.execute(text("""
                SELECT ub.id, ub.user_id, ub.badge_id, ub.earned_at, 
                       COALESCE(ub.earned_value, 1) as earned_value,
                       u.email
                FROM user_badges ub
                LEFT JOIN users u ON ub.user_id = u.id
                LIMIT 3
            """))
            
            for i, row in enumerate(result):
                print(f"     {i+1}. User: {row[5]}, Badge ID: {row[2]}, Earned: {row[3]}, Value: {row[4]}")
        
        session.close()
        
        print("\n🎉 CORRECCIÓN COMPLETADA")
        return True
        
    except Exception as e:
        print(f"❌ ERROR: {e}")
        import traceback
        traceback.print_exc()
        return False

if __name__ == "__main__":
    success = fix_user_badges_complete()
    exit(0 if success else 1)