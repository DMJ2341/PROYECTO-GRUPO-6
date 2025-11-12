# backend/scripts/update_lesson_5.py
import sys, os, json, datetime
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from database.db import db
from models.lesson import Lesson

# --------- CONTENIDO DE LA LECCIÓN ---------
content = {
    "title": "Principios de la Ciberseguridad – La Tríada CIA",
    "description": "Descubre cómo Confidencialidad, Integridad y Disponibilidad protegen la información en cualquier escenario.",
    "type": "interactive",
    "duration_minutes": 20,
    "xp": 15,
    "badge": "Guardián CIA",
    "story": {
        "hook": "🎯 Diciembre 2013 – temporada navideña. Target descubre que 40 millones de tarjetas y 70 millones de clientes fueron expuestos. El origen: un contratista de aire acondicionado con contraseña 'password1234'.",
        "impact": {"cards": "40 M", "customers": "70 M", "cost": "$162 M en multas"},
        "resolution": "Una sola clave débil rompió la cadena: falló la Confidencialidad."
    },
    "screens": [
        {
            "id": 1, "type": "story_hook",
            "title": "🔓 El Password que Expuso a Target",
            "content": "Toca cada tarjeta para ver qué principio se violó →",
            "cards": [
                {"emoji": "💳", "value": "40 M", "label": "Tarjetas", "detail": "Números expuestos"},
                {"emoji": "👤", "value": "70 M", "label": "Clientes", "detail": "Datos personales robados"},
                {"emoji": "🔑", "value": "1", "label": "Password", "detail": "password1234 del contratista"}
            ],
            "cta": "🎯 ANALIZAR LA TRÍADA CIA"
        },
        {
            "id": 2, "type": "cia_triangle",
            "title": "🛡️ La Tríada CIA Interactiva",
            "triangle": [
                {
                    "letter": "C", "name": "Confidencialidad", "icon": "🔒",
                    "definition": "Solo autorizados pueden ver la información.",
                    "example": "Target: hackers leyeron datos privados → Confidencialidad ROTA."
                },
                {
                    "letter": "I", "name": "Integridad", "icon": "📊",
                    "definition": "Los datos son exactos y no han sido alterados.",
                    "example": "Target: no modificaron datos, solo los copiaron → Integridad OK."
                },
                {
                    "letter": "A", "name": "Disponibilidad", "icon": "⏰",
                    "definition": "La información está accesible cuando se necesita.",
                    "example": "Target: sistemas funcionaban durante el robo → Disponibilidad OK."
                }
            ],
            "summary": "En Target falló 🔒 Confidencialidad; los otros dos pilares resistieron."
        },
        {
            "id": 3, "type": "cia_simulator",
            "title": "🎯 Simulador: ¿Qué Principio Proteger?",
            "scenarios": [
                {
                    "place": "Hospital – historial clínico",
                    "question": "¿Qué es MÁS crítico?",
                    "correct": "Integridad",
                    "reason": "Datos médicos incorrectos pueden ser mortales."
                },
                {
                    "place": "Banco – cuentas de clientes",
                    "question": "¿Qué es MÁS crítico?",
                    "correct": "Confidencialidad",
                    "reason": "Solo el cliente y el banco deben ver el saldo."
                },
                {
                    "place": "Central nuclear – panel de control",
                    "question": "¿Qué es MÁS crítico?",
                    "correct": "Disponibilidad",
                    "reason": "Si el sistema no responde, el reactor puede fallar."
                }
            ],
            "feedback": {
                "correct": "✅ Correcto: {principle} es prioritario en {place}.",
                "wrong": "❌ Revisa: en {place} el punto crítico es {principle}."
            }
        },
        {
            "id": 4, "type": "cia_balance_game",
            "title": "⚖️ Equilibra la Tríada",
            "challenge": "Ajusta los controles para que ningún pilar baje del 80 %",
            "controls": [
                {"name": "Cifrado + NDA", "pillar": "Confidencialidad", "boost": 20},
                {"name": "Hashes + Firmas", "pillar": "Integridad", "boost": 20},
                {"name": "Redundancia + UPS", "pillar": "Disponibilidad", "boost": 20}
            ],
            "goal": "Mantén los tres pilares ≥ 80 % simultáneamente.",
            "tip": "Sube dos pilares 20 % cada vez, pero nunca dejes uno por debajo del umbral."
        },
        {
            "id": 5, "type": "challenge",
            "title": "🧠 Mini-desafío final",
            "questions": [
                {
                    "question": "¿Qué principio se violó en Target 2013?",
                    "options": ["Confidencialidad", "Integridad", "Disponibilidad", "Todas"],
                    "correct": 0,
                    "explanation": "Los atacantes accedieron y copiaron datos privados."
                },
                {
                    "question": "En un hospital, ¿qué es más crítico?",
                    "options": ["Que el dato sea exacto", "Que sea secreto", "Que esté siempre online", "Que sea rápido"],
                    "correct": 0,
                    "explanation": "Un dato médico erróneo puede ser letal (Integridad)."
                },
                {
                    "question": "¿Cuál NO es parte de la tríada CIA?",
                    "options": ["Confidencialidad", "Autenticidad", "Integridad", "Disponibilidad"],
                    "correct": 1,
                    "explanation": "La tríada es Confidencialidad, Integridad y Disponibilidad (CIA)."
                }
            ]
        }
    ],
    "summary": "Cuando entiendes la tríada CIA puedes diseñar defensas que nunca dependan de un solo pilar."
}

# --------- BUSCAR O CREAR ---------
def update_lesson_5():
    try:
        lesson = db.session.query(Lesson).filter_by(id=5).first()
        if lesson is None:
            lesson = Lesson(
                id=5,
                course_id=1,
                order_index=5,
                title=content["title"],
                description=content["description"],
                content=json.dumps(content),
                type=content["type"],
                duration_minutes=content["duration_minutes"],
                created_at=datetime.datetime.utcnow(),
                updated_at=datetime.datetime.utcnow()
            )
            db.session.add(lesson)
            print("✅ Lección 5 creada por primera vez.")
        else:
            lesson.title = content["title"]
            lesson.description = content["description"]
            lesson.content = json.dumps(content)
            lesson.type = content["type"]
            lesson.duration_minutes = content["duration_minutes"]
            lesson.course_id = 1
            lesson.order_index = 5
            lesson.updated_at = datetime.datetime.utcnow()
            print("✅ Lección 5 actualizada.")
        db.session.commit()
    except Exception as e:
        db.session.rollback()
        print("❌ Error:", e)
    finally:
        db.session.close()

if __name__ == "__main__":
    update_lesson_5()