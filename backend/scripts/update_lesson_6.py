# backend/scripts/update_lesson_6.py
import sys, os, json, datetime
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from database.db import db
from models.lesson import Lesson

# --------- CONTENIDO DE LA LECCIÓN ---------
content = {
    "title": "Operación Escudo Ciudadano – Evaluación Final",
    "description": "Pon en práctica todo lo aprendido protegiendo a una PYME real en 24 h y con presupuesto limitado.",
    "type": "interactive",
    "duration_minutes": 45,
    "xp": 50,
    "badge": "Escudo Ciudadano",
    "story": {
        "hook": "🚨 María González, dueña de TecnoShop (15 empleados), alerta: emails raros, Wi-Fi lento y sistema pesado. Tu misión: protegerla en 24 h con $2 000.",
        "impact": {"budget": "$2 000", "time": "24 h", "team": "15 empleados"},
        "resolution": "Cada decisión cuenta: gasta bien, actúa rápido y comunica bien."
    },
    "screens": [
        {
            "id": 1, "type": "mission_brief",
            "title": "🚀 Briefing de la Misión",
            "client": "María González, TecnoShop",
            "objective": "Proteger la PYME en 24 h",
            "budget": 2000,
            "time": 45,
            "evidences": [
                "Emails sospechosos a empleados",
                "Wi-Fi público sin seguridad",
                "Sistema lento últimas 48 h"
            ],
            "cta": "🚀 ACEPTAR MISIÓN"
        },
        {
            "id": 2, "type": "phishing_analysis",
            "title": "🔍 Fase 1: Investigación de Emails",
            "email": {
                "from": "soporte@tecno-shop.com",
                "subject": "URGENTE: Actualizar credenciales",
                "body": "Estimado empleado,\nPor seguridad, debe actualizar sus credenciales en el siguiente enlace:\nhttp://tecno-shop-update.com/login\nTiene 24 h o será suspendido."
            },
            "signals": [
                {"text": "Dominio diferente (tecno-shop-update.com)", "type": "dominio", "found": True},
                {"text": "HTTP en lugar de HTTPS", "type": "protocolo", "found": True},
                {"text": "Amenaza de suspensión", "type": "urgencia", "found": True},
                {"text": "Falta de información específica", "type": "generic", "found": True}
            ],
            "time_left": 40,
            "next": "📨 ANALIZAR SIGUIENTE EMAIL"
        },
        {
            "id": 3, "type": "network_audit",
            "title": "🌐 Fase 2: Auditoría de Red",
            "findings": [
                {"icon": "📶", "text": "Red Wi-Fi: 'TecnoShop_Free' (Abierta)"},
                {"icon": "💻", "text": "3 dispositivos desconocidos conectados"},
                {"icon": "📊", "text": "Tráfico anormal: 2 GB/hora (Normal: 200 MB)"},
                {"icon": "⚠️", "text": "Puerto 3389 (Remote Desktop) ABIERTO"}
            ],
            "correct_actions": [
                "Cambiar a WPA3 con contraseña fuerte",
                "Desconectar dispositivos no autorizados",
                "Cerrar puerto 3389"
            ],
            "budget_cost": 0,
            "time_left": 35,
            "next": "🔍 INVESTIGAR TRÁFICO ANORMAL"
        },
        {
            "id": 4, "type": "ransomware_contention",
            "title": "🦠 Fase 3: Análisis del Sistema",
            "alert": "Patrón de ransomware detectado",
            "evidences": [
                {"icon": "🔍", "text": "1 200 archivos .txt siendo encriptados"},
                {"icon": "💰", "text": "Mensaje: 'Pague 0.5 BTC en 24 h'"},
                {"icon": "📍", "text": "Origen: Dispositivo infectado vía Wi-Fi"},
                {"icon": "⏰", "text": "Tiempo de infección: Hace 2 horas"}
            ],
            "correct_sequence": [
                "Aislar dispositivo infectado de la red",
                "Activar copias de seguridad",
                "Notificar a autoridades"
            ],
            "budget_cost": 0,
            "time_left": 25,
            "next": "🚨 EJECUTAR CONTENCIÓN"
        },
        {
            "id": 5, "type": "budget_defense",
            "title": "🛡️ Fase 4: Fortificación de Defensas",
            "budget_left": 1500,
            "options": [
                {"name": "Antivirus Empresarial", "cost": 400, "type": "year", "pillar": "Confidencialidad"},
                {"name": "Firewall Básico", "cost": 300, "type": "monthly", "pillar": "Disponibilidad"},
                {"name": "Filtro Anti-phishing", "cost": 200, "type": "monthly", "pillar": "Confidencialidad"},
                {"name": "Capacitación Empleados", "cost": 600, "type": "once", "pillar": "Integridad"},
                {"name": "Backup Automático", "cost": 150, "type": "monthly", "pillar": "Disponibilidad"}
            ],
            "correct_combo": ["Antivirus", "Capacitación", "Backup"],
            "total_cost": 1150,
            "time_left": 15,
            "next": "💾 IMPLEMENTAR SOLUCIONES"
        },
        {
            "id": 6, "type": "final_report",
            "title": "📊 Fase 5: Reporte Final al Cliente",
            "report": [
                "✅ Email phishing identificado y bloqueado",
                "✅ Wi-Fi asegurado con WPA3",
                "✅ Ransomware contenido sin pagar rescate",
                "✅ Sistema de backup implementado",
                "✅ Empleados serán capacitados"
            ],
            "question": "¿Qué principio CIA se vio más afectado?",
            "options": ["Confidencialidad", "Integridad", "Disponibilidad"],
            "correct": 0,
            "feedback": "Los datos de clientes estaban en riesgo de ser expuestos.",
            "time_left": 5,
            "next": "📨 ENVIAR REPORTE FINAL"
        },
        {
            "id": 7, "type": "mission_results",
            "title": "🏆 Operación Completada - Resultados",
            "score": 92,
            "breakdown": [
                {"phase": "Fase 1 (Phishing)", "points": 25, "max": 25},
                {"phase": "Fase 2 (Red)", "points": 23, "max": 25},
                {"phase": "Fase 3 (Ransomware)", "points": 24, "max": 25},
                {"phase": "Fase 4 (Defensas)", "points": 20, "max": 25}
            ],
            "strengths": [
                "Detección temprana de phishing",
                "Contención efectiva del ransomware",
                "Comunicación clara con el cliente"
            ],
            "improvements": ["Podrías haber detectado el tráfico anormal antes"],
            "time_left": 0,
            "next": "🎓 VER CERTIFICACIÓN"
        },
        {
            "id": 8, "type": "course_certificate",
            "title": "🎓 Curso Completado - Fundamentos de Ciberseguridad",
            "badges": [
                {"name": "Primer Respondedor", "icon": "🛡️"},
                {"name": "Cazador de Phishing", "icon": "🔍"},
                {"name": "Contenedor de Ransomware", "icon": "🦠"},
                {"name": "Estratega CIA", "icon": "🎯"},
                {"name": "Guardián Móvil", "icon": "📱"},
                {"name": "Escudo Ciudadano", "icon": "🏆"}
            ],
            "stats": {
                "time": "8.5 h",
                "accuracy": "92 %",
                "lessons": "6/6",
                "xp": 300
            },
            "rewards": [
                "+300 XP Totales",
                "Certificado de Fundamentos",
                "Acceso al Curso 2: Seguridad de Redes"
            ],
            "message": "Excelente trabajo. Estás listo para proteger organizaciones reales contra amenazas digitales.",
            "next": ["🚀 CONTINUAR AL CURSO 2", "📊 VER ESTADÍSTICAS DETALLADAS"]
        }
    ],
    "summary": "Has protegido a TecnoShop sin pagar rescates y dentro del presupuesto. María puede dormir tranquila."
}

# --------- BUSCAR O CREAR ---------
def update_lesson_6():
    try:
        lesson = db.session.query(Lesson).filter_by(id=6).first()
        if lesson is None:
            lesson = Lesson(
                id=6,
                course_id=1,
                order_index=6,
                title=content["title"],
                description=content["description"],
                content=json.dumps(content),
                type=content["type"],
                duration_minutes=content["duration_minutes"],
                created_at=datetime.datetime.utcnow(),
                updated_at=datetime.datetime.utcnow()
            )
            db.session.add(lesson)
            print("✅ Lección 6 (EVALUACIÓN FINAL) creada por primera vez.")
        else:
            lesson.title = content["title"]
            lesson.description = content["description"]
            lesson.content = json.dumps(content)
            lesson.type = content["type"]
            lesson.duration_minutes = content["duration_minutes"]
            lesson.course_id = 1
            lesson.order_index = 6
            lesson.updated_at = datetime.datetime.utcnow()
            print("✅ Lección 6 (EVALUACIÓN FINAL) actualizada.")
        db.session.commit()
    except Exception as e:
        db.session.rollback()
        print("❌ Error:", e)
    finally:
        db.session.close()

if __name__ == "__main__":
    update_lesson_6()