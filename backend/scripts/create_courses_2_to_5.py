# backend/scripts/create_courses_2_to_5.py
import sys, os, datetime
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from database.db import db
from models.course import Course
from models.lesson import Lesson

# ---------- DATOS DE CURSOS ----------
courses = [
    {
        "id": 2,
        "title": "Seguridad de Redes y Comunicaciones",
        "description": "Domina firewalls, IDS/IPS, VPNs y arquitectura Zero-Trust. Laboratorios con pfSense y Wireshark.",
        "category": "Ciberseguridad",
        "difficulty": "intermediate",
        "duration_hours": 12,
        "instructor": "Equipo CyberLearn",
        "price": 0.00,
        "rating": 4.9,
        "students_count": 0,
        "language": "es",
        "image_url": "",
        "requirements": ["Curso 1 completado", "Conocimientos básicos de redes"],
        "learning_objectives": [
            "Configurar firewalls y reglas de acceso",
            "Detectar ataques TCP/UDP y ARP poisoning",
            "Implementar VPNs seguras",
            "Auditar servicios empresariales HTTP/HTTPS",
            "Diseñar infraestructura Zero-Trust"
        ]
    },
    {
        "id": 3,
        "title": "Seguridad de Sistemas Operativos",
        "description": "Asegura Windows y Linux: hardening, antimalware, parches y monitoreo.",
        "category": "Ciberseguridad",
        "difficulty": "intermediate",
        "duration_hours": 10,
        "instructor": "Equipo CyberLearn",
        "price": 0.00,
        "rating": 4.8,
        "students_count": 0,
        "language": "es",
        "image_url": "",
        "requirements": ["Curso 1 completado", "Conocimientos básicos de SO"],
        "learning_objectives": [
            "Aplicar hardening en Windows y Linux",
            "Gestionar parches y actualizaciones",
            "Configurar antimalware host-based",
            "Implementar protección de endpoints",
            "Realizar evaluaciones de seguridad"
        ]
    },
    {
        "id": 4,
        "title": "Ciberseguridad Avanzada y Cloud",
        "description": "Defiende entornos cloud: IAM, contenedores, serverless y SIEM moderno.",
        "category": "Ciberseguridad",
        "difficulty": "advanced",
        "duration_hours": 14,
        "instructor": "Equipo CyberLearn",
        "price": 0.00,
        "rating": 4.9,
        "students_count": 0,
        "language": "es",
        "image_url": "",
        "requirements": ["Cursos 1-3 completados", "Conocimientos de cloud básicos"],
        "learning_objectives": [
            "Diseñar arquitecturas cloud seguras",
            "Implementar Zero-Trust en cloud",
            "Proteger contenedores y serverless",
            "Operar SIEM moderno",
            "Responder a incidentes en cloud"
        ]
    },
    {
        "id": 5,
        "title": "Operaciones de Ciberseguridad",
        "description": "Gestiona un SOC real: monitoreo 24/7, playbooks, forensics y compliance.",
        "category": "Ciberseguridad",
        "difficulty": "expert",
        "duration_hours": 16,
        "instructor": "Equipo CyberLearn",
        "price": 0.00,
        "rating": 5.0,
        "students_count": 0,
        "language": "es",
        "image_url": "",
        "requirements": ["Cursos 1-4 completados", "Experiencia laboral en TI"],
        "learning_objectives": [
            "Operar un SOC 24/7",
            "Crear playbooks de respuesta",
            "Realizar forensics digital",
            "Cumplir normativas ISO 27001",
            "Gestionar crisis de seguridad"
        ]
    }
]

# ---------- LECCIONES POR CURSO ----------
lessons = [
    # CURSO 2: 8 lecciones
    {"course_id": 2, "lessons": [
        {"title": "Fundamentos de Redes", "desc": "IPv4 vs IPv6, Ataques ICMP", "duration": 120, "xp": 40},
        {"title": "Ataques TCP/UDP", "desc": "SYN Flood, Spoofing", "duration": 90, "xp": 45},
        {"title": "Servicios de Red Vulnerables", "desc": "ARP Poisoning, DNS Spoofing", "duration": 120, "xp": 50},
        {"title": "Servicios Empresariales", "desc": "HTTP/HTTPS, SQL Injection", "duration": 120, "xp": 55},
        {"title": "Comunicación Inalámbrica", "desc": "WLAN vs Cableada, KRACK", "duration": 120, "xp": 60},
        {"title": "Infraestructura de Seguridad", "desc": "Firewalls, IDS/IPS, VPN", "duration": 120, "xp": 65},
        {"title": "Listas de Control de Acceso", "desc": "Configuración ACL, Mitigación", "duration": 120, "xp": 70},
        {"title": "Evaluación Final Redes", "desc": "Configuración Firewall, Análisis Tráfico", "duration": 180, "xp": 65}
    ]},
    # CURSO 3: 8 lecciones
    {"course_id": 3, "lessons": [
        {"title": "Windows Security", "desc": "Arquitectura, Configuración, Monitoreo", "duration": 90, "xp": 55},
        {"title": "Fundamentos Linux", "desc": "Shell, Comandos, Sistema Archivos", "duration": 60, "xp": 50},
        {"title": "Servidores Linux", "desc": "Cliente-Servidor, Hardening", "duration": 60, "xp": 45},
        {"title": "Protección de Endpoints", "desc": "Gestión Parches, Cifrado Host", "duration": 90, "xp": 60},
        {"title": "Protección Antimalware", "desc": "HIDS/HIPS, Protección Basada en Host", "duration": 60, "xp": 50},
        {"title": "Seguridad de Aplicaciones", "desc": "Sandboxing, Application Whitelisting", "duration": 60, "xp": 45},
        {"title": "Hardening de Sistemas", "desc": "Windows/Linux, Recuperación Contraseñas", "duration": 120, "xp": 60},
        {"title": "Evaluación Final SO", "desc": "Caso Práctico Integral", "duration": 120, "xp": 35}
    ]},
    # CURSO 4: 8 lecciones
    {"course_id": 4, "lessons": [
        {"title": "Arquitectura Cloud Segura", "desc": "Modelos de Responsabilidad, CSPM", "duration": 90, "xp": 60},
        {"title": "Identity and Access Management", "desc": "IAM, MFA, PAM", "duration": 90, "xp": 65},
        {"title": "Protección de Datos en Cloud", "desc": "Cifrado, Tokenización, KMS", "duration": 90, "xp": 70},
        {"title": "Seguridad en Contenedores", "desc": "Docker, Kubernetes, Runtime Security", "duration": 120, "xp": 75},
        {"title": "Serverless Security", "desc": "Funciones, API Gateway, Eventos", "duration": 90, "xp": 70},
        {"title": "Monitoreo y Detección", "desc": "CloudTrail, GuardDuty, SIEM", "duration": 90, "xp": 65},
        {"title": "Cumplimiento y Auditoría", "desc": "PCI-DSS, ISO 27001, SOC 2", "duration": 90, "xp": 60},
        {"title": "Evaluación Final Cloud", "desc": "Caso Práctico Cloud", "duration": 180, "xp": 65}
    ]},
    # CURSO 5: 8 lecciones
    {"course_id": 5, "lessons": [
        {"title": "Fundamentos del SOC", "desc": "Roles, Herramientas, Procesos", "duration": 90, "xp": 65},
        {"title": "Monitoreo 24/7", "desc": "SIEM, SOAR, Threat Intelligence", "duration": 120, "xp": 70},
        {"title": "Respuesta a Incidentes", "desc": "Playbooks, CSIRT, Comunicación", "duration": 120, "xp": 75},
        {"title": "Forensics Digital", "desc": "Análisis de Discos, Redes, Memoria", "duration": 120, "xp": 80},
        {"title": "Threat Hunting", "desc": "Búsqueda Proactiva, Hipótesis, IOCs", "duration": 120, "xp": 85},
        {"title": "Automatización y SOAR", "desc": "Playbooks Automatizados, APIs", "duration": 90, "xp": 70},
        {"title": "Cumplimiento y Normativas", "desc": "ISO 27001, GDPR, SOC 2", "duration": 90, "xp": 65},
        {"title": "Evaluación Final SOC", "desc": "Caso Práctico SOC", "duration": 240, "xp": 70}
    ]}
]

# ---------- FUNCIÓN PRINCIPAL ----------
def create_courses_2_to_5():
    session = db.session
    try:
        # 1. Crear cursos
        for course_data in courses:
            course = session.query(Course).filter_by(id=course_data["id"]).first()
            if course:
                # Actualizar
                for key, value in course_data.items():
                    if key != "id":
                        setattr(course, key, value)
                print(f"✅ Curso {course_data['id']} actualizado.")
            else:
                course = Course(**course_data)
                session.add(course)
                print(f"✅ Curso {course_data['id']} creado.")

        # 2. Crear lecciones vacías
        for course_lesson in lessons:
            course_id = course_lesson["course_id"]
            for idx, lesson_data in enumerate(course_lesson["lessons"], start=1):
                lesson = session.query(Lesson).filter_by(course_id=course_id, order_index=idx).first()
                if lesson:
                    # Actualizar
                    lesson.title = lesson_data["title"]
                    lesson.description = lesson_data["desc"]
                    lesson.duration_minutes = lesson_data["duration"]
                    lesson.type = "interactive"
                    lesson.content = ""  # Vacío por ahora
                    lesson.updated_at = datetime.datetime.utcnow()
                    print(f"✅ Lección {idx} del curso {course_id} actualizada.")
                else:
                    lesson = Lesson(
                        course_id=course_id,
                        order_index=idx,
                        title=lesson_data["title"],
                        description=lesson_data["desc"],
                        content="",
                        type="interactive",
                        duration_minutes=lesson_data["duration"],
                        created_at=datetime.datetime.utcnow(),
                        updated_at=datetime.datetime.utcnow()
                    )
                    session.add(lesson)
                    print(f"✅ Lección {idx} del curso {course_id} creada.")

        session.commit()
        print("🏁 Cursos 2-5 y lecciones vacías listos.")
    except Exception as e:
        session.rollback()
        print("❌ Error:", e)
    finally:
        session.close()

if __name__ == "__main__":
    create_courses_2_to_5()