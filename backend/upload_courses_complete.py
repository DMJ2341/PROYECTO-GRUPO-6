# scripts/upload_courses_complete.py
import psycopg2
import json

def upload_courses():
    conn = psycopg2.connect(
        host="localhost",
        port=5432,
        database="cyberlearn_db",
        user="cyberlearn_user",
        password="CyberLearn2024!"
    )
    cursor = conn.cursor()
    
    # Cursos reales completos
    courses = [
        {
            "title": "Ciberseguridad para Emprendedores",
            "description": "Aprende a proteger tu negocio digital desde cero. Cubre aspectos básicos de seguridad, contraseñas, phishing y protección de datos.",
            "category": "Seguridad Básica",
            "difficulty": "Principiante",
            "duration_hours": 8,
            "image_url": "https://images.unsplash.com/photo-1563206767-5b18f218e8de?w=500",
            "instructor": "Ana García",
            "rating": 4.8,
            "students_count": 1250,
            "price": 0.00,
            "language": "Español",
            "requirements": ["Conocimientos básicos de informática", "Correo electrónico"],
            "learning_objectives": [
                "Identificar amenazas comunes en línea",
                "Crear contraseñas seguras",
                "Reconocer intentos de phishing",
                "Proteger información personal"
            ]
        },
        {
            "title": "Hacking Ético y Penetration Testing",
            "description": "Curso completo de hacking ético desde cero. Aprende a encontrar vulnerabilidades y mejorar la seguridad de sistemas.",
            "category": "Hacking Ético",
            "difficulty": "Avanzado",
            "duration_hours": 40,
            "image_url": "https://images.unsplash.com/photo-1550751827-4bd374c3f58b?w=500",
            "instructor": "Carlos Rodríguez",
            "rating": 4.9,
            "students_count": 890,
            "price": 199.99,
            "language": "Español",
            "requirements": [
                "Conocimientos de redes y Linux",
                "Sistema Kali Linux",
                "Comprensión de vulnerabilidades web"
            ],
            "learning_objectives": [
                "Realizar pruebas de penetración",
                "Usar herramientas de hacking ético",
                "Documentar hallazgos de seguridad",
                "Aplicar metodologías OWASP"
            ]
        }
    ]
    
    for course in courses:
        cursor.execute("""
            INSERT INTO courses (
                title, description, category, difficulty, duration_hours,
                image_url, instructor, rating, students_count, price,
                language, requirements, learning_objectives, created_at, updated_at
            ) VALUES (
                %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, NOW(), NOW()
            )
        """, (
            course["title"], course["description"], course["category"],
            course["difficulty"], course["duration_hours"], course["image_url"],
            course["instructor"], course["rating"], course["students_count"],
            course["price"], course["language"], 
            json.dumps(course["requirements"]), json.dumps(course["learning_objectives"])
        ))
        
        print(f"✅ Curso creado: {course['title']}")
    
    conn.commit()
    cursor.close()
    conn.close()
    print("🎉 Todos los cursos han sido cargados exitosamente!")

if __name__ == "__main__":
    upload_courses()