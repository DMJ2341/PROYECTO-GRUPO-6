#!/usr/bin/env python3
# backend/verify_lessons_json.py
import psycopg2
import json
import sys

def verify_lessons_json():
    print("🔍 VERIFICANDO JSON DE LECCIONES")
    print("=" * 60)
    
    try:
        conn = psycopg2.connect(
            host="172.232.188.183",
            port=5432,
            database="cyberlearn_db",
            user="app_cyberlearn",
            password="CyberLearn2025*"
        )
        cursor = conn.cursor()
        
        # 1. Verificar tipo de columna screens
        print("\n1. 📋 TIPO DE COLUMNA 'screens':")
        cursor.execute("""
            SELECT data_type 
            FROM information_schema.columns 
            WHERE table_name = 'lessons' AND column_name = 'screens'
        """)
        
        data_type = cursor.fetchone()[0]
        print(f"   Tipo: {data_type}")
        
        if data_type != 'jsonb':
            print("   ⚠️  La columna NO es JSONB")
            print("   Ejecuta primero: python backend/migration_text_to_jsonb.py")
        
        # 2. Obtener todas las lecciones del Curso 1
        print("\n2. 📚 LECCIONES DEL CURSO 1:")
        cursor.execute("""
            SELECT id, title, type, total_screens, 
                   LENGTH(screens::text) as json_length,
                   screens
            FROM lessons 
            WHERE course_id = 1
            ORDER BY order_index
        """)
        
        lessons = cursor.fetchall()
        
        if not lessons:
            print("   ❌ No se encontraron lecciones del Curso 1")
            cursor.close()
            conn.close()
            return False
        
        print(f"   Total: {len(lessons)} lecciones\n")
        
        all_valid = True
        
        for idx, lesson in enumerate(lessons, 1):
            lesson_id, title, lesson_type, total_screens, json_length, screens_data = lesson
            
            print(f"   {idx}. {title}")
            print(f"      ID: {lesson_id}")
            print(f"      Tipo: {lesson_type}")
            print(f"      Total pantallas: {total_screens}")
            
            # Verificar que screens sea válido
            if screens_data is None:
                print(f"      ❌ screens es NULL")
                all_valid = False
                continue
            
            # Si es JSONB, viene como dict/list
            # Si es TEXT, viene como string
            try:
                if isinstance(screens_data, str):
                    screens_list = json.loads(screens_data)
                else:
                    screens_list = screens_data
                
                if isinstance(screens_list, list):
                    actual_screens = len(screens_list)
                    print(f"      ✅ JSON válido: {actual_screens} pantallas")
                    
                    # Verificar discrepancia
                    if actual_screens != total_screens:
                        print(f"      ⚠️  Discrepancia: total_screens={total_screens}, real={actual_screens}")
                    
                    # Mostrar tipos de pantallas
                    screen_types = [s.get('type', 'unknown') for s in screens_list]
                    print(f"      Tipos: {', '.join(screen_types)}")
                else:
                    print(f"      ❌ screens no es una lista, es: {type(screens_list)}")
                    all_valid = False
                    
            except json.JSONDecodeError as e:
                print(f"      ❌ JSON inválido: {e}")
                print(f"      Primeros 100 chars: {str(screens_data)[:100]}")
                all_valid = False
            except Exception as e:
                print(f"      ❌ Error: {e}")
                all_valid = False
            
            print()
        
        # 3. Resumen
        print("=" * 60)
        if all_valid:
            print("✅ TODAS LAS LECCIONES TIENEN JSON VÁLIDO")
        else:
            print("❌ ALGUNAS LECCIONES TIENEN PROBLEMAS")
            print("\nAcciones recomendadas:")
            print("1. Verifica la estructura de las lecciones problemáticas")
            print("2. Ejecuta el script de migración si screens es TEXT")
            print("3. Recrea las lecciones si el JSON está corrupto")
        
        cursor.close()
        conn.close()
        return all_valid
        
    except Exception as e:
        print(f"\n❌ ERROR: {e}")
        import traceback
        traceback.print_exc()
        
        if 'conn' in locals():
            conn.close()
        
        return False

if __name__ == "__main__":
    success = verify_lessons_json()
    sys.exit(0 if success else 1)