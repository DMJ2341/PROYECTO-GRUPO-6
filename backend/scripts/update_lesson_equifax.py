# backend/scripts/update_lesson_equifax.py
import json
import sys, os, json, datetime
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from database.db import db
from models.lesson import Lesson

def update_equifax_lesson():
    content = {
        "title": "Ingeniería Social y Engaño – El Caso Equifax",
        "description": "Aprende a detectar y neutralizar la ingeniería social que comprometió 147 M de personas.",
        "type": "interactive",
        "duration_minutes": 25,
        "xp": 10,
        "badge": "Cazador de Engaños",
        "story": {
            "hook": "📅 7 de marzo 2017 – Oficina de Atlanta.\nCarlos, analista de nómina, recibe un correo: “URGENTE: Actualice sus datos antes del viernes”. Un clic… y 44 % de la población adulta de EE. UU. quedó expuesta.",
            "impact": {
                "personas": "147 000 000",
                "tarjetas": "209 000",
                "multa": "$700 M",
                "tiempo": "76 días sin detectar"
            },
            "resolution": "El parche existía… pero nunca se aplicó. La puerta de entrada no fue técnica: fue humana."
        },
        "screens": [
            {
                "id": 1,
                "type": "story_hook",
                "title": "📱 El Mensaje que Paralizó a Equifax",
                "content": "Toca cada tarjeta para ver el impacto real →",
                "cards": [
                    {"emoji": "👤", "value": "147 M", "label": "Personas", "detail": "44 % de adultos en EE. UU."},
                    {"emoji": "💳", "value": "209 K", "label": "Tarjetas", "detail": "Números expuestos"},
                    {"emoji": "💰", "value": "$700 M", "label": "Multa", "detail": "Récord histórico"}
                ],
                "cta": "🎯 ANALIZAR EL ATAQUE"
            },
            {
                "id": 2,
                "type": "phishing_simulator",
                "title": "🔍 Simulador: Encuentra los 3 errores",
                "email": {
                    "from": "rh@equifax-com.tk",
                    "subject": "URGENTE: Actualización de nómina",
                    "body": "Hola Carlos,\nNecesitamos que verifique sus datos antes del viernes.\n👇 Haga clic aquí:\nhttp://equifax-payroll.tk/update",
                    "logo": False
                },
                "errors": [
                    {"text": "Dominio .tk (no .com)", "correct": True},
                    {"text": "Falta logo oficial", "correct": True},
                    {"text": "URL sospechosa", "correct": True},
                    {"text": "Urgencia artificial", "correct": True, "bonus": True}
                ],
                "feedback": "✅ ¡3/3 correctas! El dominio .tk, la URL falsa y la urgencia son señales de phishing."
            },
            {
                "id": 3,
                "type": "drag_classifier",
                "title": "🎯 Clasificador de Tácticas",
                "instruction": "Arrastra cada caso a su técnica de ingeniería social:",
                "cases": [
                    {"text": "Mantenimiento pide acceso al servidor", "technique": "Pretexting"},
                    {"text": "Llamada del 'banco' pidiendo clave", "technique": "Vishing"},
                    {"text": "Email de CEO pidiendo transferencia", "technique": "Phishing"},
                    {"text": "Persona mirando tu pantalla en café", "technique": "Shoulder Surfing"}
                ],
                "techniques": [
                    {"id": "phishing", "name": "📧 Phishing", "desc": "Email fraudulento"},
                    {"id": "pretexting", "name": "🎭 Pretexting", "desc": "Fingir un rol"},
                    {"id": "vishing", "name": "📞 Vishing", "desc": "Llamada fraudulenta"},
                    {"id": "shoulder", "name": "👀 Shoulder", "desc": "Mirar secretos"}
                ]
            },
            {
                "id": 4,
                "type": "shoulder_survey",
                "title": "👀 ¿Quién está espiando tu pantalla?",
                "scenario": "Cafetería 2:30 PM - estás revisando nómina",
                "people": [
                    {"emoji": "😊", "role": "Mujer leyendo", "suspicious": False, "reason": "Distraída con su libro"},
                    {"emoji": "😐", "role": "Hombre con café", "suspicious": True, "reason": "Mira tu pantalla cada vez que tipeas"},
                    {"emoji": "👮", "role": "Guardia de seguridad", "suspicious": False, "reason": "Está de espaldas"},
                    {"emoji": "🎒", "role": "Estudiante con audífonos", "suspicious": False, "reason": "Concentrado en su música"}
                ],
                "correct_feedback": "✅ Correcto: el hombre con café está haciendo shoulder surfing."
            },
            {
                "id": 5,
                "type": "challenge",
                "title": "🧠 Mini-desafío final",
                "questions": [
                    {
                        "question": "¿Qué técnica usaron los atacantes de Equifax?",
                        "options": ["Phishing", "Pretexting", "Vishing", "Shoulder Surfing"],
                        "correct": 0,
                        "explanation": "Phishing por email fue la puerta de entrada."
                    },
                    {
                        "question": "¿Cuál es la mejor defensa contra el shoulder surfing?",
                        "options": ["Pantalla polarizada", "No trabajar en público", "Ambas", "Ninguna"],
                        "correct": 2,
                        "explanation": "Combinar pantalla polarizada + evitar trabajo sensible en público."
                    },
                    {
                        "question": "¿Qué hacer si recibes un email urgente de RRHH?",
                        "options": ["Clicar de inmediato", "Verificar remitente", "Ignorar siempre", "Reenviar a todos"],
                        "correct": 1,
                        "explanation": "Siempre verifica el remitente y llama por teléfono para confirmar."
                    }
                ]
            }
        ],
        "summary": "La ingeniería social ataca la mente, no la máquina. Verifica, cuestiona y nunca actúes por urgencia."
    }

    try:
        lesson = db.session.query(Lesson).filter_by(id=2).first()
        if not lesson:
            print("❌ Lección 2 no encontrada")
            return

        lesson.title = content["title"]
        lesson.description = content["description"]
        lesson.content = json.dumps(content) 
        lesson.type = content["type"]
        lesson.duration_minutes = content["duration_minutes"]
        lesson.updated_at = datetime.datetime.utcnow()
        db.session.commit()
        print("✅ Lección 2 –‘Ingeniería Social y Engaño’– actualizada con diseño mobile-first y simuladores interactivos.")
    except Exception as e:
        db.session.rollback()
        print("❌ Error:", e)
    finally:
        db.session.close()

if __name__ == "__main__":
    update_equifax_lesson()