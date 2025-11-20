# backend/migration_text_to_jsonb.py
import psycopg2
import sys

def migrate_screens_to_jsonb():
    print("🔄 MIGRANDO COLUMNA 'screens' DE TEXT A JSONB")
    print("=" * 60)
    
    try:
        # Conectar a PostgreSQL
        conn = psycopg2.connect(
            host="172.232.188.183",
            port=5432,
            database="cyberlearn_db",
            user="app_cyberlearn",
            password="CyberLearn2025*"
        )
        cursor = conn.cursor()
        
        # 1. Verificar tipo actual
        print("\n1. Verificando tipo actual de 'screens'...")
        cursor.execute("""
            SELECT data_type 
            FROM information_schema.columns 
            WHERE table_name = 'lessons' AND column_name = 'screens'
        """)
        
        result = cursor.fetchone()
        if result:
            current_type = result[0]
            print(f"   Tipo actual: {current_type}")
            
            if current_type == 'jsonb':
                print("   ✅ La columna ya es JSONB, no se requiere migración")
                cursor.close()
                conn.close()
                return True
        else:
            print("   ❌ Columna 'screens' no existe")
            cursor.close()
            conn.close()
            return False
        
        # 2. Backup de datos
        print("\n2. Creando backup temporal...")
        cursor.execute("""
            CREATE TABLE IF NOT EXISTS lessons_backup AS 
            SELECT * FROM lessons
        """)
        print("   ✅ Backup creado en tabla 'lessons_backup'")
        
        # 3. Verificar datos válidos JSON
        print("\n3. Verificando que todos los 'screens' sean JSON válido...")
        cursor.execute("""
            SELECT id, screens 
            FROM lessons 
            WHERE screens IS NOT NULL AND screens != ''
        """)
        
        invalid_lessons = []
        for row in cursor.fetchall():
            lesson_id, screens_text = row
            try:
                if screens_text:
                    import json
                    json.loads(screens_text)
            except:
                invalid_lessons.append(lesson_id)
        
        if invalid_lessons:
            print(f"   ⚠️  Lecciones con JSON inválido: {invalid_lessons}")
            print("   Corrige estos datos antes de continuar")
            cursor.close()
            conn.close()
            return False
        else:
            print("   ✅ Todos los datos son JSON válido")
        
        # 4. Migrar columna
        print("\n4. Convirtiendo columna TEXT → JSONB...")
        cursor.execute("""
            ALTER TABLE lessons 
            ALTER COLUMN screens TYPE JSONB 
            USING CASE 
                WHEN screens IS NULL OR screens = '' THEN NULL
                ELSE screens::jsonb 
            END
        """)
        print("   ✅ Columna convertida a JSONB")
        
        # 5. Verificar migración
        print("\n5. Verificando migración...")
        cursor.execute("""
            SELECT data_type 
            FROM information_schema.columns 
            WHERE table_name = 'lessons' AND column_name = 'screens'
        """)
        
        new_type = cursor.fetchone()[0]
        print(f"   Nuevo tipo: {new_type}")
        
        # 6. Contar registros
        cursor.execute("SELECT COUNT(*) FROM lessons WHERE screens IS NOT NULL")
        count = cursor.fetchone()[0]
        print(f"   Lecciones con screens: {count}")
        
        # Commit
        conn.commit()
        
        print("\n✅ MIGRACIÓN COMPLETADA EXITOSAMENTE")
        print("\n💡 Notas:")
        print("   - La tabla 'lessons_backup' contiene una copia de seguridad")
        print("   - Puedes eliminarla después de verificar que todo funciona:")
        print("     DROP TABLE lessons_backup;")
        
        cursor.close()
        conn.close()
        return True
        
    except Exception as e:
        print(f"\n❌ ERROR EN MIGRACIÓN: {e}")
        import traceback
        traceback.print_exc()
        
        if 'conn' in locals():
            conn.rollback()
            conn.close()
        
        return False

if __name__ == "__main__":
    success = migrate_screens_to_jsonb()
    sys.exit(0 if success else 1)