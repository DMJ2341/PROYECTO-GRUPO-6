# scripts/upload_lessons_simple.py - ✅ VERSIÓN INICIAL SIMPLE
import psycopg2
import json

def upload_lessons_simple():
    conn = psycopg2.connect(
        host="192.192.192.192",
        port=5432,
        database="cyberlearn_db",
        user="app_cyberlearn",
        password="CyberLearn2024!"
    )
    cursor = conn.cursor()
    
    # Obtener IDs de cursos REALES
    cursor.execute("SELECT id, title FROM course")
    courses = {title: id for id, title in cursor.fetchall()}
    print("📚 Cursos disponibles:", courses)
    
    # Lecciones SIMPLES para probar - solo 2 lecciones
    lessons = [
        {
            "lesson_id": "fundamentos_leccion_1",
            "course_title": "Fundamentos y Concientización",  # ✅ Título exacto
            "title": "Introducción a las Amenazas Cibernéticas",
            "content": "Aprende sobre los principales tipos de amenazas digitales",
            "lesson_order": 1,
            "xp_reward": 30,
            "duration_minutes": 10,
            "lesson_type": "interactive",
            "total_screens": 3,
            "screens": json.dumps([
                {
                    "screen_number": 1,
                    "type": "intro",
                    "title": "🚨 Bienvenido a Ciberseguridad",
                    "content": "Aprende a protegerte en el mundo digital"
                }
            ])
        },
        {
            "lesson_id": "fundamentos_leccion_2", 
            "course_title": "Fundamentos y Concientización",
            "title": "Tipos de Malware",
            "content": "Conoce los diferentes tipos de software malicioso",
            "lesson_order": 2,
            "xp_reward": 35,
            "duration_minutes": 15,
            "lesson_type": "interactive",
            "total_screens": 4,
            "screens": json.dumps([
                {
                    "screen_number": 1, 
                    "type": "intro",
                    "title": "🦠 ¿Qué es el Malware?",
                    "content": "Software diseñado para dañar sistemas"
                }
            ])
        }
    ]
    
    for lesson in lessons:
        course_id = courses.get(lesson["course_title"])
        if course_id:
            cursor.execute("""
                INSERT INTO lesson (
                    id, course_id, title, description, content, order_index, 
                    type, duration_minutes, xp_reward, total_screens, screens, created_at
                ) VALUES (
                    %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, NOW()
                )
            """, (
                lesson["lesson_id"], course_id, lesson["title"], lesson.get("description", ""),
                lesson["content"], lesson["lesson_order"], lesson["lesson_type"],
                lesson["duration_minutes"], lesson["xp_reward"], lesson["total_screens"], 
                lesson["screens"]
            ))
            
            print(f"✅ Lección creada: {lesson['title']}")
        else:
            print(f"❌ Curso no encontrado: '{lesson['course_title']}'")
            print(f"   Cursos disponibles: {list(courses.keys())}")
    
    conn.commit()
    cursor.close()
    conn.close()
    print("🎉 Lecciones básicas cargadas exitosamente!")

if __name__ == "__main__":
    upload_lessons_simple()