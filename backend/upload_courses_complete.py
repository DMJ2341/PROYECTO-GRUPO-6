# scripts/upload_courses_complete.py
import psycopg2
import json

def upload_courses():
    conn = psycopg2.connect(
        host="172.232.188.183",
        port=5432,
        database="cyberlearn_db",
        user="app_cyberlearn",
        password="CyberLearn2025*"
    )
    cursor = conn.cursor()
    
    # Cursos reales completos
    courses = [
        # --- NIVEL 1: FUNDAMENTOS Y CONCIENTIZACIÓN ---
        {
            "title": "Fundamentos y Concientización", 
            "description": "Aprende los principios básicos de ciberseguridad a través de casos reales. Domina la identificación de amenazas y conviértete en un primer respondedor digital.",
            "level": "Principiante",
            "xp_reward": 215,
            "image_url": "https://images.unsplash.com/photo-1563206767-5b18f218e8de?w=500",
            "category": "Fundamentos",
            "duration_hours": 6
        },
        {
            "title": "Higiene Digital Esencial",
            "description": "Protege tus cuentas y dispositivos con prácticas esenciales. Contraseñas seguras, 2FA, backups y navegación segura.",
            "level": "Principiante", 
            "xp_reward": 185,
            "image_url": "https://images.unsplash.com/photo-1550751827-4bd374c3f58b?w=500",
            "category": "Prácticas Seguras",
            "duration_hours": 5
        },
        {
            "title": "Fundamentos de Redes",
            "description": "Domina los fundamentos de redes que todo profesional de ciberseguridad necesita. TCP/IP, DNS, HTTP/HTTPS y firewalls.",
            "level": "Principiante",
            "xp_reward": 225,
            "image_url": "https://images.unsplash.com/photo-1544197150-b99a580bb7a8?w=500",
            "category": "Redes",
            "duration_hours": 7
        },
        {
            "title": "Sistemas Operativos Seguros",
            "description": "Seguridad en Windows y Linux. Gestión de permisos, usuarios, y la importancia de parches y actualizaciones.",
            "level": "Principiante",
            "xp_reward": 200,
            "image_url": "https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=500",
            "category": "Sistemas",
            "duration_hours": 6
        },
        
        # --- NIVEL 2: RED TEAM (OFENSIVA) ---
        {
            "title": "Reconocimiento y OSINT",
            "description": "Técnicas para recolectar información ética sobre objetivos usando fuentes públicas (OSINT).",
            "level": "Intermedio",
            "xp_reward": 280,
            "image_url": "https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=500",
            "category": "Red Team",
            "duration_hours": 8
        },
        {
            "title": "Hacking Web y API",
            "description": "Principales fallos de seguridad en aplicaciones web (OWASP Top 10). Inyecciones y fallos de autenticación.",
            "level": "Avanzado",
            "xp_reward": 320,
            "image_url": "https://images.unsplash.com/photo-1550751827-4bd374c3f58b?w=500",
            "category": "Red Team",
            "duration_hours": 10
        },
        
        # --- NIVEL 3: BLUE TEAM (DEFENSIVA) ---
        {
            "title": "Monitoreo y Análisis de Logs",
            "description": "Leer y comprender logs de sistemas y red. Rol de Analista SOC y uso de SIEM para detección temprana.",
            "level": "Intermedio",
            "xp_reward": 300,
            "image_url": "https://images.unsplash.com/photo-1544197150-b99a580bb7a8?w=500",
            "category": "Blue Team",
            "duration_hours": 9
        },
        {
            "title": "Respuesta a Incidentes y Forense",
            "description": "Fases de respuesta a ciberataques. Fundamentos de análisis forense para recolección de pruebas digitales.",
            "level": "Avanzado",
            "xp_reward": 350,
            "image_url": "https://images.unsplash.com/photo-1563206767-5b18f218e8de?w=500",
            "category": "Blue Team",
            "duration_hours": 11
        },
        
        # --- CURSOS ADICIONALES ---
        {
            "title": "Hacking Ético y Pentesting",
            "description": "Curso completo de hacking ético y pruebas de penetración en entornos controlados.",
            "level": "Avanzado",
            "xp_reward": 400,
            "image_url": "https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=500",
            "category": "Red Team",
            "duration_hours": 12
        },
        {
            "title": "Ciberseguridad para Emprendedores",
            "description": "Protege tu negocio digital desde cero. Seguridad para startups y pequeños negocios.",
            "level": "Principiante",
            "xp_reward": 180,
            "image_url": "https://images.unsplash.com/photo-1550751827-4bd374c3f58b?w=500",
            "category": "Negocios",
            "duration_hours": 4
        }
    ]

    course_ids = []
    
    for course in courses:
        cursor.execute("""
            INSERT INTO course (
                title, description, level, xp_reward, image_url, 
                category, duration_hours, created_at
            ) VALUES (
                %s, %s, %s, %s, %s, %s, %s, NOW()
            ) RETURNING id
        """, (
            course["title"], course["description"], course["level"],
            course["xp_reward"], course["image_url"], course["category"],
            course["duration_hours"]
        ))
        
        course_id = cursor.fetchone()[0]
        course_ids.append(course_id)
        print(f"✅ Curso creado: {course['title']} (ID: {course_id})")
    
    conn.commit()
    cursor.close()
    conn.close()
    print("🎉 Todos los cursos han sido cargados exitosamente!")

    return course_ids

if __name__ == "__main__":
    upload_courses()