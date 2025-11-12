# backend/scripts/update_lesson_wannacry.py - versión corregida
import psycopg2
import json
from datetime import datetime

def update_wannacry_lesson():
    conn = psycopg2.connect(
        host="localhost",
        port=5432,
        database="cyberlearn_db",
        user="cyberlearn_user",
        password="CyberLearn2024!"
    )
    cursor = conn.cursor()
    
    # Contenido completo de la Lección 1: WannaCry
    lesson_content = {
        "title": "Introducción a las Amenazas Cibernéticas - WannaCry",
        "story": {
            "hook": "12 de mayo de 2017, 10:00 AM. Hospitales británicos comienzan a colapsar. Pantallas rojas muestran 'OOOPS, your files have been encrypted'. En 24 horas, 200,000 computadoras en 150 países están infectadas. Cirugías se cancelan, ambulancias se desvían. El NHS pierde £92 millones. Todo por un virus que se propagó solo.",
            "impact": {
                "computers": "200,000",
                "countries": "150", 
                "surgeries": "600+ canceladas",
                "losses": "$4B globales",
                "time": "24 horas"
            },
            "resolution": "Marcus Hutchins, analista de 22 años, descubre el 'kill switch' registrando un dominio por $10.69 y detiene el virus global."
        },
        "simulator": {
            "type": "interactive_map",
            "title": "Mapa de Infección Global",
            "description": "Simula la propagación de WannaCry en tiempo real",
            "steps": [
                {
                    "step": 1,
                    "title": "Infección Inicial",
                    "description": "Un empleado hace clic en un archivo infectado",
                    "action": "Descarga de actualización falsa",
                    "impact": "1 computadora infectada"
                },
                {
                    "step": 2, 
                    "title": "Propagación Local",
                    "description": "El virus busca conexiones de red y se expande",
                    "action": "Explota vulnerabilidad EternalBlue",
                    "impact": "10 computadoras infectadas"
                },
                {
                    "step": 3,
                    "title": "Expansión Global", 
                    "description": "Se propaga por todo el mundo",
                    "action": "Se replica automáticamente",
                    "impact": "10,000 computadoras infectadas"
                },
                {
                    "step": 4,
                    "title": "Descubrimiento del Kill Switch",
                    "description": "Marcus Hutchins encuentra la solución",
                    "action": "Registra dominio iuqerfsodp9ifjaposdfjhgosurijfaewrwergwea.com",
                    "impact": "Virus detenido globalmente"
                }
            ],
            "user_actions": [
                "Identificar el tipo de amenaza",
                "Descubrir el kill switch",
                "Ver el impacto global"
            ]
        },
        "mini_challenge": {
            "questions": [
                {
                    "question": "¿Qué tipo de amenaza fue WannaCry principalmente?",
                    "options": ["Virus", "Ransomware", "Phishing", "DDoS"],
                    "correct": 1,
                    "explanation": "WannaCry fue ransomware que encriptaba archivos y pedía rescate."
                },
                {
                    "question": "¿Cómo se detuvo principalmente WannaCry?",
                    "options": ["Actualización de Windows", "Antivirus actualizado", "Kill switch descubierto", "Desconectar internet"],
                    "correct": 2,
                    "explanation": "Marcus Hutchins descubrió un 'kill switch' - un dominio que cuando existía, detenía el virus."
                },
                {
                    "question": "¿Qué principio de seguridad se violó principalmente con WannaCry?",
                    "options": ["Confidencialidad", "Integridad", "Disponibilidad", "Autenticidad"],
                    "correct": 2,
                    "explanation": "El ransomware violó la disponibilidad - los sistemas no estaban accesibles."
                }
            ],
            "time_limit": 60,
            "xp_reward": 30
        },
        "rewards": {
            "xp": 30,
            "badge": "Primer Respondedor",
            "description": "Completaste tu primera lección sobre amenazas reales",
            "unlock_content": "Historia bonus: Otras variantes de ransomware"
        }
    }
    
    # Actualizar lección 1 del curso 1
    cursor.execute("""
        UPDATE lessons 
        SET 
            title = %s,
            description = %s,
            content = %s,
            type = 'interactive',
            duration_minutes = %s,
            updated_at = NOW()
        WHERE id = 1 AND course_id = 1
    """, (
        lesson_content["title"],
        "Introducción completa a las amenazas cibernéticas usando el caso real de WannaCry",
        json.dumps(lesson_content),
        20
    ))
    
    print(f"✅ Lección 1 actualizada: {lesson_content['title']}")
    print(f"✅ XP otorgada: {lesson_content['rewards']['xp']} XP")
    print(f"✅ Insignia desbloqueada: {lesson_content['rewards']['badge']}")
    
    conn.commit()
    cursor.close()
    conn.close()
    print("🎉 Lección 1: WannaCry actualizada exitosamente!")

if __name__ == "__main__":
    update_wannacry_lesson()