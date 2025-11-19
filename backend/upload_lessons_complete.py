# scripts/upload_lessons_complete.py
import psycopg2
import json

def upload_lessons():
    conn = psycopg2.connect(
        host="172.232.188.183",
        port=5432,
        database="cyberlearn_db",
        user="app_cyberlearn",
        password="CyberLearn2025*"
    )
    cursor = conn.cursor()
    
    # Obtener IDs de cursos
    cursor.execute("SELECT id, title FROM course")
    courses = {title: id for id, title in cursor.fetchall()}
    print("📚 Cursos disponibles:", courses)
    
    # Lecciones detalladas para cada curso
    lessons = [
        # Curso: Fundamentos de Ciberseguridad
        {
            "lesson_id": "fundamentos_intro_interactivo",
            "course_title": "Fundamentos de Ciberseguridad",
            "title": "¡Tu Primera Línea de Defensa Digital!",
            "content": "Lección interactiva completa - ver campo screens",
            "lesson_order": 1,
            "xp_reward": 30,
            "duration_minutes": 6,
            "lesson_type": "interactive",
            "total_screens": 4,
            "screens": json.dumps([
                {
                    "screen_number": 1,
                    "type": "hero_intro",
                    "title": "🎮 Conviértete en un Guardián Digital",
                    "content": {
                        "hero_image": "🛡️",
                        "subtitle": "Tu misión comienza ahora",
                        "stats": [
                            {"icon": "⏱️", "text": "2min para hackear una cuenta"},
                            {"icon": "💰", "text": "S/ 8,500 perdidos en Perú cada día"},
                            {"icon": "👥", "text": "60% de latinos afectados"}
                        ]
                    },
                    "cta_button": "Aceptar Misión →"
                },
                {
                    "screen_number": 2,
                    "type": "interactive_scenario", 
                    "title": "🔍 Caso Real: Ana y el Correo Sospechoso",
                    "content": {
                        "scenario": "Ana recibe un correo de su 'banco':\\n\\n'Su cuenta será BLOQUEADA en 24h. Verifique sus datos INMEDIATAMENTE.'",
                        "characters": [
                            {"name": "Ana", "role": "Usuario", "status": "preocupada"},
                            {"name": "Hacker", "role": "Atacante", "status": "oculto"}
                        ]
                    },
                    "choices": [
                        {
                            "id": "A",
                            "text": "Hacer clic y verificar",
                            "consequence": "❌ Cuenta hackeada - S/ 3,000 perdidos",
                            "correct": False,
                            "feedback": "¡Cuidado! La urgencia es una táctica común"
                        },
                        {
                            "id": "B", 
                            "text": "Llamar al banco directamente",
                            "consequence": "✅ Cuenta protegida - Fraude evitado", 
                            "correct": True,
                            "feedback": "¡Excelente! Verificar siempre con la fuente oficial"
                        }
                    ]
                }
            ])
        },
        {
            "lesson_id": "triada_cia_interactivo",
            "course_title": "Fundamentos de Ciberseguridad", 
            "title": "La Tríada CIA: Tus 3 Superpoderes Digitales",
            "content": "Lección interactiva sobre la Tríada CIA",
            "lesson_order": 2,
            "xp_reward": 45,
            "duration_minutes": 8,
            "lesson_type": "interactive", 
            "total_screens": 6,
            "screens": json.dumps([
                {
                    "screen_number": 1,
                    "type": "story_hook",
                    "title": "🦸 DESCUBRE TUS 3 SUPERPRODERES DIGITALES",
                    "content": {
                        "story": "Cada vez que usas tu celular, 3 superpoderes invisibles te protegen",
                        "quote": "95% de ciberataques violan al menos uno de estos principios", 
                        "hook_question": "¿Sabes cuáles son y cómo te protegen día a día?"
                    },
                    "cta_button": "Descubrir Mis Superpoderes →"
                },
                {
                    "screen_number": 2,
                    "type": "interactive_concept",
                    "title": "🔒 SUPERPODER #1: CONFIDENCIALIDAD",
                    "subtitle": "Solo personas AUTORIZADAS ven tu información",
                    "content": {
                        "definition": "Como un diario personal con candado - solo TÚ tienes la llave",
                        "examples": [
                            {
                                "text": "Tus mensajes de WhatsApp → Solo tú y tu amigo los ven",
                                "correct": True,
                                "icon": "💬"
                            }
                        ]
                    }
                }
            ])
        },
        # Curso: Hacking Ético
        {
            "lesson_id": "hacking_etico_intro",
            "course_title": "Hacking Ético y Pentesting", 
            "title": "Fundamentos de Hacking Ético",
            "content": "Introducción al hacking ético y marco legal",
            "lesson_order": 1,
            "xp_reward": 50,
            "duration_minutes": 20,
            "lesson_type": "video",
            "total_screens": 1,
            "screens": json.dumps([
                {
                    "screen_number": 1,
                    "type": "video_content",
                    "title": "🎥 Fundamentos de Hacking Ético",
                    "content": {
                        "video_url": "https://example.com/hacking-etico-intro.mp4",
                        "description": "Introducción al hacking ético, metodologías y marco legal",
                        "resources": [
                            {"name": "Código Ético del Hacker", "type": "pdf"},
                            {"name": "Marco Legal", "type": "pdf"}
                        ]
                    }
                }
            ])
        }
    ]
    
    for lesson in lessons:
        course_id = courses.get(lesson["course_title"])
        if course_id:
            cursor.execute("""
                INSERT INTO lessons (
                    lesson_id, course_id, title, content, lesson_order, 
                    xp_reward, duration_minutes, lesson_type, total_screens, screens,
                    created_at
                ) VALUES (
                    %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, NOW()
                )
            """, (
                lesson["lesson_id"], course_id, lesson["title"], lesson["content"],
                lesson["lesson_order"], lesson["xp_reward"], lesson["duration_minutes"],
                lesson["lesson_type"], lesson["total_screens"], lesson["screens"]
            ))
            
            print(f"✅ Lección creada: {lesson['title']}")
            print(f"   📍 ID: {lesson['lesson_id']}")
            print(f"   🎯 Curso: {lesson['course_title']}")
            print(f"   ⭐ XP: {lesson['xp_reward']}")
            print(f"   ⏱️  Duración: {lesson['duration_minutes']} min")
            print()
        else:
            print(f"❌ Curso no encontrado: {lesson['course_title']}")
    
    conn.commit()
    cursor.close()
    conn.close()
    print("🎉 Todas las lecciones han sido cargadas exitosamente!")

if __name__ == "__main__":
    upload_lessons()