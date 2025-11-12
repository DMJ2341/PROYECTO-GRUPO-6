# scripts/upload_lessons_complete.py
import psycopg2
import json

def upload_lessons():
    conn = psycopg2.connect(
        host="localhost",
        port=5432,
        database="cyberlearn_db",
        user="cyberlearn_user",
        password="CyberLearn2024!"
    )
    cursor = conn.cursor()
    
    # Obtener IDs de cursos
    cursor.execute("SELECT id, title FROM courses")
    courses = {title: id for id, title in cursor.fetchall()}
    
    # Lecciones detalladas para cada curso
    lessons = [
        # Curso 1: Ciberseguridad para Emprendedores
        {
            "course_title": "Ciberseguridad para Emprendedores",
            "title": "Introducción a la Ciberseguridad",
            "description": "Conceptos fundamentales de ciberseguridad y por qué es crucial para emprendedores",
            "content": json.dumps({
                "video_url": "https://example.com/video1.mp4",
                "duration": "15:30",
                "resources": [
                    {"name": "Guía de Conceptos Básicos", "url": "/resources/cyber-basics.pdf"},
                    {"name": "Checklist de Seguridad", "url": "/resources/security-checklist.pdf"}
                ]
            }),
            "order_index": 1,
            "type": "video",
            "duration_minutes": 16
        },
        {
            "course_title": "Ciberseguridad para Emprendedores",
            "title": "Gestión de Contraseñas Seguras",
            "description": "Cómo crear y gestionar contraseñas fuertes y únicas para tu negocio",
            "content": json.dumps({
                "video_url": "https://example.com/video2.mp4",
                "duration": "20:45",
                "resources": [
                    {"name": "Plantilla de Política de Contraseñas", "url": "/resources/password-policy.docx"},
                    {"name": "Comparativa de Gestores de Contraseñas", "url": "/resources/password-managers.pdf"}
                ]
            }),
            "order_index": 2,
            "type": "video",
            "duration_minutes": 21
        },
        # Curso 2: Hacking Ético
        {
            "course_title": "Hacking Ético y Penetration Testing",
            "title": "Fundamentos de Hacking Ético",
            "description": "Introducción al hacking ético, metodologías y marco legal",
            "content": json.dumps({
                "video_url": "https://example.com/ethical1.mp4",
                "duration": "25:15",
                "resources": [
                    {"name": "Código Ético del Hacker", "url": "/resources/ethical-code.pdf"},
                    {"name": "Marco Legal Español", "url": "/resources/legal-framework.pdf"}
                ]
            }),
            "order_index": 1,
            "type": "video",
            "duration_minutes": 25
        }
    ]
    
    for lesson in lessons:
        course_id = courses.get(lesson["course_title"])
        if course_id:
            cursor.execute("""
                INSERT INTO lessons (
                    course_id, title, description, content, order_index, 
                    type, duration_minutes, created_at, updated_at
                ) VALUES (
                    %s, %s, %s, %s, %s, %s, %s, NOW(), NOW()
                )
            """, (
                course_id, lesson["title"], lesson["description"],
                lesson["content"], lesson["order_index"], 
                lesson["type"], lesson["duration_minutes"]
            ))
            
            print(f"✅ Lección creada: {lesson['title']} (Curso: {lesson['course_title']})")
        else:
            print(f"❌ Curso no encontrado: {lesson['course_title']}")
    
    conn.commit()
    cursor.close()
    conn.close()
    print("🎉 Todas las lecciones han sido cargadas exitosamente!")

if __name__ == "__main__":
    upload_lessons()