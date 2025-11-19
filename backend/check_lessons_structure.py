#!/usr/bin/env python3
# check_lessons_structure.py

from database.db import db, Session
from sqlalchemy import text

def check_lessons_structure():
    print("🔍 VERIFICANDO ESTRUCTURA DE LESSONS")
    print("=" * 40)
    
    try:
        session = Session()
        
        # 1. Verificar estructura de la tabla lessons
        print("1. 📋 ESTRUCTURA DE LESSONS:")
        result = session.execute(text("""
            SELECT column_name, data_type, is_nullable
            FROM information_schema.columns 
            WHERE table_name = 'lessons'
            ORDER BY ordinal_position
        """))
        
        lesson_columns = []
        for row in result:
            lesson_columns.append(row[0])
            print(f"   - {row[0]} ({row[1]}, nullable: {row[2]})")
        
        # Verificar columnas críticas
        critical_columns = ['id', 'course_id', 'title', 'created_at']
        missing_columns = [col for col in critical_columns if col not in lesson_columns]
        
        if missing_columns:
            print(f"   ❌ FALTAN COLUMNAS: {missing_columns}")
        else:
            print("   ✅ Todas las columnas críticas existen")
        
        # Verificar updated_at
        if 'updated_at' not in lesson_columns:
            print("   ℹ️  updated_at NO existe en la tabla")
        else:
            print("   ✅ updated_at existe")
        
        # 2. Verificar datos de ejemplo
        print("\n2. 📊 DATOS EN LESSONS:")
        result = session.execute(text("SELECT COUNT(*) FROM lessons"))
        total_lessons = result.scalar()
        print(f"   Total lecciones: {total_lessons}")
        
        if total_lessons > 0:
            print("   Primeras 3 lecciones:")
            result = session.execute(text("""
                SELECT id, course_id, title, type, duration_minutes 
                FROM lessons 
                LIMIT 3
            """))
            
            for i, row in enumerate(result):
                print(f"     {i+1}. ID: {row[0]}, Curso: {row[1]}, Título: {row[2]}, Tipo: {row[3]}, Duración: {row[4]}min")
        
        session.close()
        
        print(f"\n💡 RECOMENDACIÓN: {'Actualizar el modelo Lesson' if 'updated_at' not in lesson_columns else 'Estructura correcta'}")
        return True
        
    except Exception as e:
        print(f"❌ ERROR: {e}")
        import traceback
        traceback.print_exc()
        return False

if __name__ == "__main__":
    check_lessons_structure()