#backend/scripts/seed_courses.py
import sys
import os
import json

# Ajustar path para importar desde backend
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

from database.db import get_session
from models.course import Course
from models.lesson import Lesson

# Ruta base donde están los JSONs: backend/data/courses
DATA_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), '../data/courses'))

def load_lesson_content(course_num, lesson_num):
    """Intenta cargar el JSON específico para una lección."""
    # Estructura esperada: backend/data/courses/course_1/lesson_1_1.json
    file_path = os.path.join(DATA_DIR, f"course_{course_num}", f"lesson_{course_num}_{lesson_num}.json")
    
    if os.path.exists(file_path):
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                print(f"   📂 Cargando contenido desde: {os.path.basename(file_path)}")
                return json.load(f)
        except Exception as e:
            print(f"   ⚠️ Error leyendo {file_path}: {e}")
    
    return None

def seed_courses():
    session = get_session()
    print("🚀 Iniciando carga de Cursos y Lecciones (Modo Archivos JSON)...")

    # Definición de la estructura académica (Metadatos de los 5 Cursos)
    courses_struct = [
        {
            "id": 1,
            "title": "Fundamentos de Seguridad de la Información",
            "description": "Comprender principios fundamentales de seguridad según estándares NIST.",
            "level": "Principiante", "xp": 140, "bg": "0D8ABC",
            "lessons": [
                {"id": "c1_l1", "t": "Principios Fundamentales (CIA Triad)", "d": "Confidencialidad, Integridad y Disponibilidad."},
                {"id": "c1_l2", "t": "Taxonomía de Amenazas Cibernéticas", "d": "Threat actors y MITRE ATT&CK®."},
                {"id": "c1_l3", "t": "Vulnerabilidades y CVE", "d": "Sistema CVE y ciclo de vida."},
                {"id": "c1_l4", "t": "Malware y Técnicas de Ataque", "d": "Malware, WannaCry y C2."},
                {"id": "c1_l5", "t": "Ingeniería Social", "d": "Phishing y caso Equifax."},
                {"id": "c1_l6", "t": "Controles de Seguridad", "d": "Defense in Depth y Zero Trust."},
                {"id": "c1_l7", "t": "Evaluación Final Curso 1", "d": "Escenario 'Operación Defensa Digital'."}
            ]
        },
        {
            "id": 2,
            "title": "Seguridad de Redes",
            "description": "Arquitecturas de red seguras, TCP/IP y Firewalls.",
            "level": "Intermedio", "xp": 140, "bg": "555555",
            "lessons": [
                {"id": "c2_l1", "t": "Fundamentos TCP/IP", "d": "Modelo OSI y puertos."},
                {"id": "c2_l2", "t": "Dispositivos de Seguridad", "d": "Firewalls, IDS/IPS y VPNs."},
                {"id": "c2_l3", "t": "Seguridad Wireless", "d": "WPA3 y ataques WiFi."},
                {"id": "c2_l4", "t": "Arquitecturas Seguras", "d": "Segmentación y NAC."},
                {"id": "c2_l5", "t": "Ataques de Red", "d": "DoS, MITM y Spoofing."},
                {"id": "c2_l6", "t": "Monitoreo de Red", "d": "SIEM y Threat Hunting."},
                {"id": "c2_l7", "t": "Evaluación Final Curso 2", "d": "Escenario 'Arquitecto de Red'."}
            ]
        },
        {
            "id": 3,
            "title": "Gestión de Identidades y Accesos (IAM)",
            "description": "Implementar y gestionar sistemas de control de acceso (MFA, RBAC).",
            "level": "Intermedio", "xp": 120, "bg": "6200EA",
            "lessons": [
                {"id": "c3_l1", "t": "Principios de Control de Acceso", "d": "AAA, ACLs y principios."},
                {"id": "c3_l2", "t": "Autenticación y Factores", "d": "MFA, Biometría y Passwordless."},
                {"id": "c3_l3", "t": "Modelos de Control de Acceso", "d": "DAC, MAC, RBAC, ABAC."},
                {"id": "c3_l4", "t": "Sistemas IAM", "d": "SSO, Federación y PAM."},
                {"id": "c3_l5", "t": "Controles de Acceso Físico", "d": "Seguridad perimetral."},
                {"id": "c3_l6", "t": "Evaluación Final Curso 3", "d": "Escenario 'Arquitecto IAM'."}
            ]
        },
        {
            "id": 4,
            "title": "Respuesta a Incidentes y Forense Digital",
            "description": "Ejecutar procesos de IR según NIST y preservar evidencia.",
            "level": "Avanzado", "xp": 140, "bg": "C62828",
            "lessons": [
                {"id": "c4_l1", "t": "Fundamentos de Respuesta a Incidentes", "d": "Ciclo de vida NIST y CSIRT."},
                {"id": "c4_l2", "t": "Detección y Análisis", "d": "Triage e IOCs."},
                {"id": "c4_l3", "t": "Contención, Erradicación y Recuperación", "d": "Estrategias ante Ransomware."},
                {"id": "c4_l4", "t": "Forense Digital - Fundamentos", "d": "Cadena de custodia y adquisición."},
                {"id": "c4_l5", "t": "Análisis Forense - Disco y Memoria", "d": "Artifacts y File Systems."},
                {"id": "c4_l6", "t": "Actividad Post-Incidente", "d": "Lecciones aprendidas."},
                {"id": "c4_l7", "t": "Evaluación Final Curso 4", "d": "Escenario 'Ransomware APT'."}
            ]
        },
        {
            "id": 5,
            "title": "Gestión de Riesgos y Cumplimiento",
            "description": "Aplicar frameworks de gestión de riesgos (RMF) y cumplimiento.",
            "level": "Avanzado", "xp": 120, "bg": "F9A825",
            "lessons": [
                {"id": "c5_l1", "t": "Fundamentos de Gestión de Riesgos", "d": "Conceptos y cálculo de riesgo."},
                {"id": "c5_l2", "t": "Risk Management Framework (RMF)", "d": "Los 6 pasos del NIST RMF."},
                {"id": "c5_l3", "t": "Evaluación y Análisis de Riesgos", "d": "Cualitativo vs Cuantitativo."},
                {"id": "c5_l4", "t": "Tratamiento y Controles de Riesgo", "d": "Mitigación y Transferencia."},
                {"id": "c5_l5", "t": "Frameworks y Cumplimiento", "d": "NIST CSF, ISO 27001, GDPR."},
                {"id": "c5_l6", "t": "Evaluación Final Curso 5", "d": "Escenario 'Risk Manager'."}
            ]
        }
    ]

    try:
        for c_data in courses_struct:
            course_num = c_data["id"]
            
            # 1. Crear Curso
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
                print(f"✅ Curso {course_num}: {c_data['title']}")
            
            # 2. Crear Lecciones
            for l_idx, l_data in enumerate(c_data["lessons"]):
                lesson_num = l_idx + 1
                lesson_id = l_data["id"]
                
                # Intentar cargar contenido real del JSON
                screens_content = load_lesson_content(course_num, lesson_num)
                
                # Si no hay archivo JSON, usar un placeholder para que no falle
                if not screens_content:
                    screens_content = [{
                        "screen_id": 1,
                        "type": "theory",
                        "title": l_data["t"],
                        "content": {"text": l_data["d"] + "\n\n(Contenido próximamente...)"}
                    }]

                lesson = session.query(Lesson).filter_by(id=lesson_id).first()
                
                if not lesson:
                    lesson = Lesson(
                        id=lesson_id,
                        course_id=course_num,
                        title=l_data["t"],
                        description=l_data["d"],
                        type="interactive",
                        content={"intro": l_data["d"]}, 
                        screens=screens_content,       # <--- AQUÍ SE GUARDA EL JSON
                        total_screens=len(screens_content),
                        duration_minutes=15,
                        xp_reward=20,
                        order_index=lesson_num
                    )
                    session.add(lesson)
                else:
                    # Actualizar siempre el contenido por si editaste el JSON
                    lesson.screens = screens_content
                    lesson.total_screens = len(screens_content)
                    session.add(lesson)
        
        session.commit()
        print("\n✨ Base de datos actualizada con éxito.")

    except Exception as e:
        session.rollback()
        print(f"\n❌ Error: {e}")
    finally:
        session.close()

if __name__ == "__main__":
    seed_courses()