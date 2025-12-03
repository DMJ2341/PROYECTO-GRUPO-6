import sys
import os
import json

# Ajustar path para importar desde backend
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

from database.db import get_session
from models.course import Course
from models.lesson import Lesson

DATA_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), '../data/courses'))

def load_lesson_json(course_num, lesson_num):
    """Carga el archivo JSON completo de una lección."""
    # Ajuste: Buscar tanto 'lesson_X_Y.json' como 'c1_l1.json' por si acaso, 
    # pero tu estructura actual es lesson_1_1.json, así que mantenemos ese estándar.
    file_name = f"lesson_{course_num}_{lesson_num}.json"
    file_path = os.path.join(DATA_DIR, f"course_{course_num}", file_name)
    
    if os.path.exists(file_path):
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                print(f"    📂 Leyendo archivo: {file_name}")
                return json.load(f)
        except Exception as e:
            print(f"    ⚠️ Error leyendo {file_path}: {e}")
            return None
    return None

def seed_courses():
    session = get_session()
    print("🚀 Iniciando carga de Cursos y Lecciones (Black Edition)...")

    # Estructura base de Cursos (Los títulos y descripciones vienen de aquí, 
    # pero el contenido detallado viene de los JSONs)
    courses_struct = [
        {
            "id": 1,
            "title": "Fundamentos de Ciberdefensa",
            "description": "Comprender principios fundamentales de seguridad según estándares NIST.",
            "level": "Principiante", "xp": 440, "bg": "0D8ABC",
            "lessons_count": 7 # Usamos esto para iterar
        },
        {
            "id": 2,
            "title": "Seguridad y Defensa de Redes",
            "description": "Arquitecturas de red seguras, TCP/IP y Firewalls.",
            "level": "Intermedio", "xp": 415, "bg": "555555",
            "lessons_count": 7
        },
        {
            "id": 3,
            "title": "Criptografía y Gestión de Identidades (IAM)",
            "description": "Implementar y gestionar sistemas de control de acceso (MFA, RBAC).",
            "level": "Intermedio", "xp": 400, "bg": "6200EA",
            "lessons_count": 6
        },
        {
            "id": 4,
            "title": "Seguridad de Aplicaciones y DevSecOps",
            "description": "Ejecutar procesos de IR según NIST y preservar evidencia.",
            "level": "Avanzado", "xp": 615, "bg": "C62828",
            "lessons_count": 7
        },
        {
            "id": 5,
            "title": "Respuesta a Incidentes y Forense (DFIR)",
            "description": "Aplicar frameworks de gestión de riesgos (RMF) y cumplimiento.",
            "level": "Avanzado", "xp": 695, "bg": "F9A825",
            "lessons_count": 6
        }
    ]

    try:
        for c_data in courses_struct:
            course_num = c_data["id"]
            
            # 1. Crear/Actualizar Curso
            course = session.query(Course).filter_by(id=course_num).first()
            if not course:
                course = Course(
                    id=course_num,
                    title=c_data["title"],
                    description=c_data["description"],
                    level=c_data["level"],
                    xp_reward=c_data["xp"],
                    image_url=f"https://ui-avatars.com/api/?name={c_data['title'][:10]}&background={c_data['bg']}&color=fff"
                )
                session.add(course)
                print(f"\n✅ Curso {course_num}: {c_data['title']}")
            else:
                # Actualizar datos por si cambiaron los nombres
                course.title = c_data["title"]
                course.description = c_data["description"]
                print(f"\n♻️  Actualizando Curso {course_num}: {c_data['title']}")
            
            # 2. Crear/Actualizar Lecciones desde JSON
            for i in range(1, c_data["lessons_count"] + 1):
                json_data = load_lesson_json(course_num, i)
                
                if json_data:
                    lesson_id = json_data.get("id")
                    
                    # Extraer metadata crítica del JSON
                    screens = json_data.get("screens", [])
                    xp_reward = json_data.get("xp_reward", 20)
                    duration = json_data.get("duration_minutes", 15)
                    l_type = json_data.get("type", "interactive")
                    title = json_data.get("title", f"Lección {i}")
                    description = json_data.get("description", "")
                    
                    # IMPORTANTE: Guardamos el 'theme' dentro de 'content'
                    # para que el frontend pueda leer los colores.
                    content_data = {
                        "intro": description,
                        "theme": json_data.get("theme", {}) 
                    }

                    lesson = session.query(Lesson).filter_by(id=lesson_id).first()
                    
                    if not lesson:
                        lesson = Lesson(
                            id=lesson_id,
                            course_id=course_num,
                            title=title,
                            description=description,
                            type=l_type,
                            content=content_data, # Guardamos el theme aquí
                            screens=screens,
                            total_screens=len(screens),
                            duration_minutes=duration,
                            xp_reward=xp_reward,
                            order_index=i
                        )
                        session.add(lesson)
                        print(f"   ➕ Creada: {lesson_id} - {title}")
                    else:
                        # Actualizar campos
                        lesson.title = title
                        lesson.description = description
                        lesson.type = l_type
                        lesson.content = content_data
                        lesson.screens = screens
                        lesson.total_screens = len(screens)
                        lesson.duration_minutes = duration
                        lesson.xp_reward = xp_reward
                        session.add(lesson)
                        print(f"   ♻️  Actualizada: {lesson_id}")
                else:
                    print(f"   ❌ FALTA ARCHIVO JSON para lección {i} del curso {course_num}")
        
        session.commit()
        print("\n✨ Base de datos actualizada con éxito (Black Edition Ready).")

    except Exception as e:
        session.rollback()
        print(f"\n❌ Error crítico: {e}")
        import traceback
        traceback.print_exc()
    finally:
        session.close()

if __name__ == "__main__":
    seed_courses()