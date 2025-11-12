# backend/scripts/update_lesson_4.py
import sys, os, json, datetime
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from database.db import db
from models.lesson import Lesson

# --------- CONTENIDO DE LA LECCIÓN ---------
content = {
    "title": "Dispositivos Móviles e Inalámbricos – Evil Twin & Smishing",
    "description": "Protege tu celular y tu Wi-Fi: detecta redes falsas y SMS maliciosos antes de que sea tarde.",
    "type": "interactive",
    "duration_minutes": 25,
    "xp": 15,
    "badge": "Guardián Móvil",
    "story": {
        "hook": "🛍️ Diciembre 2022 – centro comercial. 2 300 clientes reportan cargos falsos en sus tarjetas. El origen: un solo router Wi-Fi falso llamado «Free_Mall_WiFi».",
        "impact": {"customers": "2 300", "fraud": "$1.2 M", "devices": "1 router falso"},
        "resolution": "El ataque más barato: 50 $ de router puede robar millones si no sabes detectarlo."
    },
    "screens": [
        {
            "id": 1, "type": "story_hook",
            "title": "📱 El Wi-Fi que Robó Tarjetas",
            "content": "Toca cada tarjeta para descubrir el impacto →",
            "cards": [
                {"emoji": "👤", "value": "2 300", "label": "Clientes", "detail": "Con cargos no autorizados"},
                {"emoji": "💳", "value": "$1.2 M", "label": "Fraudes", "detail": "Detectados en 48 h"},
                {"emoji": "📡", "value": "1", "label": "Router Falso", "detail": "Evil Twin colgado en la pared"}
            ], "cta": "🎯 ANALIZAR REDES FALSAS"
        },
        {
            "id": 2, "type": "wifi_simulator",
            "title": "📶 Simulador: Elige tu Red Segura",
            "scenario": "Aeropuerto – redes disponibles:",
            "networks": [
                {"ssid": "Free_Airport_WiFi", "lock": False, "safe": False, "reason": "Evil Twin posible"},
                {"ssid": "Airport_Official", "lock": True, "safe": True, "reason": "Red oficial con contraseña"},
                {"ssid": "Starbucks_Free", "lock": False, "safe": False, "reason": "No requiere contraseña"},
                {"ssid": "Guest_Airport", "lock": False, "safe": False, "reason": "Canal abierto sin cifrar"}
            ],
            "feedback": {
                "correct": "✅ Excelente: redes oficiales con contraseña son más seguras.",
                "wrong": "❌ Cuidado: redes abiertas pueden ser Evil Twins que capturan tus datos."
            }
        },
        {
            "id": 3, "type": "smishing_detector",
            "title": "📱 Detector de Smishing",
            "sms": {"from": "Banco-Ofiicial", "body": "ALERTA: Actividad sospechosa en su cuenta. Bloquearemos su tarjeta. Verifique ahora: http://banco-ofiicial.com/secure"},
            "signals": [
                {"text": "Ofiicial con doble 'f'", "type": "ortografía", "found": True},
                {"text": "Enlace HTTP (no HTTPS)", "type": "url", "found": True},
                {"text": "Urgencia artificial", "type": "urgencia", "found": True},
                {"text": "Remitente no oficial", "type": "remitente", "found": True}
            ],
            "score": 4, "action": "Reporta y elimina el mensaje."
        },
        {
            "id": 4, "type": "qr_code_trap",
            "title": "📲 Trampa QR – ¿Escaneas o no?",
            "scenario": "Encuentras este QR en tu mesa del café:",
            "qr_label": "Wi-Fi_Gratis_Aqui",
            "options": [
                {"text": "Escaneo", "result": "❌ Red Evil Twin – datos robados"},
                {"text": "Pregunto al personal", "result": "✅ Verificación antes de conectar"}
            ],
            "tip": "Nunca escanees QR de fuentes desconocidas; pregunta siempre al establecimiento."
        },
        {
            "id": 5, "type": "challenge",
            "title": "🧠 Mini-desafío final",
            "questions": [
                {
                    "question": "¿Qué técnica usan los routers falsos?",
                    "options": ["Evil Twin", "DDoS", "Phishing", "Ransomware"],
                    "correct": 0,
                    "explanation": "Evil Twin imita el nombre de una red legítima para engañar."
                },
                {
                    "question": "Señal segura de una red Wi-Fi:",
                    "options": ["Abierta y rápida", "Requiere contraseña y usa WPA2/WPA3", "Sin contraseña", "Nombre divertido"],
                    "correct": 1,
                    "explanation": "WPA2/WPA3 garantiza cifrado y autenticación."
                },
                {
                    "question": "¿Qué hacer ante un SMS sospechoso?",
                    "options": ["Clicar y verificar", "Eliminar y reportar", "Reenviar a amigos", "Ignorar"],
                    "correct": 1,
                    "explanation": "Eliminar y reportar evita que otros caigan en la trampa."
                }
            ]
        }
    ],
    "summary": "Tu celular y tu Wi-Fi son tu vida digital: verifica antes de conectar y nunca actúes por impulso."
}

# --------- BUSCAR O CREAR ---------
def update_lesson_4():
    try:
        lesson = db.session.query(Lesson).filter_by(id=4).first()
        if lesson is None:
            lesson = Lesson(
                id=4,
                course_id=1,
                order_index=4,
                title=content["title"],
                description=content["description"],
                content=json.dumps(content),
                type=content["type"],
                duration_minutes=content["duration_minutes"],
                created_at=datetime.datetime.utcnow(),
                updated_at=datetime.datetime.utcnow()
            )
            db.session.add(lesson)
            print("✅ Lección 4 creada por primera vez.")
        else:
            lesson.title = content["title"]
            lesson.description = content["description"]
            lesson.content = json.dumps(content)
            lesson.type = content["type"]
            lesson.duration_minutes = content["duration_minutes"]
            lesson.course_id = 1
            lesson.order_index = 4
            lesson.updated_at = datetime.datetime.utcnow()
            print("✅ Lección 4 actualizada.")
        db.session.commit()
    except Exception as e:
        db.session.rollback()
        print("❌ Error:", e)
    finally:
        db.session.close()

if __name__ == "__main__":
    update_lesson_4()