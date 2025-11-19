#!/usr/bin/env python3
# test_lesson_model_fixed.py

from database.db import db, Session
from models.lesson import Lesson
from sqlalchemy import text

def test_lesson_model_fixed():
    print("🧪 VERIFICANDO MODELO LESSON CORREGIDO")
    print("=" * 45)
    
    try:
        # 1. Verificar que el modelo se carga correctamente
        print("1. 📋 MODELO LESSON CARGADO:")
        for column in Lesson.__table__.columns:
            print(f"   - {column.name} ({column.type})")
        
        # 2. Probar consultas con el modelo
        print("\n2. 🔍 PROBANDO CONSULTAS CON MODELO...")
        session = Session()
        
        # Contar lecciones por curso (esto debería funcionar ahora)
        result = session.execute(text("SELECT DISTINCT course_id FROM lessons"))
        course_ids = [row[0] for row in result if row[0] is not None]
        
        for course_id in course_ids[:3]:  # Probar primeros 3 cursos
            lesson_count = session.query(Lesson).filter_by(course_id=course_id).count()
            print(f"   ✅ Curso {course_id}: {lesson_count} lecciones")
        
        # 3. Probar acceso a datos
        print("\n3. 📊 PROBANDO ACCESO A DATOS...")
        lessons = session.query(Lesson).limit(2).all()
        for i, lesson in enumerate(lessons):
            print(f"   {i+1}. ID: {lesson.id}, Título: {lesson.title}, Tipo: {lesson.type}")
            if lesson.screens:
                screens_data = lesson.get_screens_data()
                print(f"      Screens: {len(screens_data)} pantallas")
        
        session.close()
        
        print("\n🎉 MODELO LESSON FUNCIONA CORRECTAMENTE")
        return True
        
    except Exception as e:
        print(f"❌ ERROR: {e}")
        import traceback
        traceback.print_exc()
        return False

if __name__ == "__main__":
    success = test_lesson_model_fixed()
    exit(0 if success else 1)