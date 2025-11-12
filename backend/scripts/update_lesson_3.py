# backend/scripts/update_lesson_3.py
import sys, os, json, datetime
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from database.db import db
from models.lesson import Lesson

def update_lesson_3():
    content = {
        "title": "Ataques Cibernéticos Básicos – Colonial Pipeline",
        "description": "Desde ransomware hasta DDoS: entiende los ataques que paralizan empresas y naciones.",
        "type": "interactive",
        "duration_minutes": 30,
        "xp": 15,
        "badge": "Defensor de Redes",
        "story": {
            "hook": "⛽ 7 mayo 2021 – 17:00 ET. La mayor tubería de combustible de EE. UU. se detiene. 5 días después: 45 % suba de precio y $4.4 M pagados en Bitcoin.",
            "impact": {
                "days": "5",
                "price_hike": "45 %",
                "ransom": "$4.4 M",
                "currency": "Bitcoin"
            },
            "resolution": "Un solo clic en un anuncio falso encendió el efecto dominó."
        },
        "screens": [
            {
                "id": 1,
                "type": "story_hook",
                "title": "📱 El Clic que Paralizó un País",
                "content": "Toca cada tarjeta para sentir el impacto →",
                "cards": [
                    {"emoji": "⛽", "value": "5 Días", "label": "Parálisis", "detail": "Sin gasolina en Costa Este"},
                    {"emoji": "🚗", "value": "45 %", "label": "Precio", "detail": "Aumento en estaciones"},
                    {"emoji": "💰", "value": "$4.4 M", "label": "Rescate", "detail": "Pagado en Bitcoin"}
                ],
                "cta": "🎯 ANALIZAR EL RANSOMWARE"
            },
            {
                "id": 2,
                "type": "timeline_ransomware",
                "title": "🔄 Cómo Funciona el Ransomware",
                "steps": [
                    {
                        "name": "PASO 1/4: INFECCIÓN",
                        "desc": "Empleado clic en anuncio 'Actualizar Windows' → descarga DarkSide",
                        "visual": "anuncio_falso.png",
                        "action": "continuar"
                    },
                    {
                        "name": "PASO 2/4: PROPAGACIÓN",
                        "desc": "El malware escanea la red y se salta a otros sistemas",
                        "visual": "red_onda.gif",
                        "action": "continuar"
                    },
                    {
                        "name": "PASO 3/4: CIFRADO",
                        "desc": "Encripta 100 GB en 2 h – archivos cambian a .locked",
                        "visual": "candado_cayendo.json",
                        "action": "continuar"
                    },
                    {
                        "name": "PASO 4/4: EXTORSIÓN",
                        "desc": "Pantalla: 'Pague $4.4 M o borramos todo'",
                        "visual": "nota_rescate.png",
                        "action": "finalizar"
                    }
                ],
                "summary": "4 pasos: Infección → Propagación → Cifrado → Extorsión"
            },
            {
                "id": 3,
                "type": "ddos_simulator",
                "title": "🌊 Simulador DDoS en Vivo",
                "server": {
                    "status": "🟢 OPERATIVO",
                    "load": "50 req/min",
                    "latency": "45 ms"
                },
                "attack": {
                    "bots": 10000,
                    "traffic": "1 000 000 req/s",
                    "bandwidth": "100 % saturado"
                },
                "controls": [
                    {"label": "🚨 INICIAR ATAQUE", "action": "start"},
                    {"label": "🛡️ ACTIVAR DEFENSA", "action": "mitigate"},
                    {"label": "🔄 REINICIAR", "action": "reset"}
                ],
                "defense": {
                    "waf": "Filtro de tráfico malicioso",
                    "cdn": "Servicio de mitigación (CloudFlare, Akamai)",
                    "rate_limit": "Límite de peticiones por IP"
                }
            },
            {
                "id": 4,
                "type": "challenge",
                "title": "🧠 Mini-desafío final",
                "questions": [
                    {
                        "question": "¿Qué tipo de malware fue DarkSide?",
                        "options": ["Ransomware", "Troyano", "Spyware", "Adware"],
                        "correct": 0,
                        "explanation": "DarkSide es ransomware: cifra archivos y pide rescate."
                    },
                    {
                        "question": "¿Cuál es la mejor defensa inicial contra DDoS?",
                        "options": ["CDN con mitigación", "Apagar servidor", "Pagar atacantes", "Ignorar"],
                        "correct": 0,
                        "explanation": "Los servicios de CDN absorben y filtran el tráfico malicioso."
                    },
                    {
                        "question": "¿Por qué pagaron el rescate en Colonial Pipeline?",
                        "options": ["Backup dañado", "Presión temporal", "Sin planes de recuperación", "Todas anteriores"],
                        "correct": 3,
                        "explanation": "Backup incompleto + urgencia operativa + falta de plan = pago."
                    }
                ]
            }
        ],
        "summary": "Entender el ciclo de un ataque es el primer paso para detenerlo antes de que cause daño."
    }

    try:
        lesson = db.session.query(Lesson).filter_by(id=3).first()
        if not lesson:
            print("❌ Lección 3 no encontrada")
            return

        lesson.title = content["title"]
        lesson.description = content["description"]
        lesson.content = json.dumps(content)   # ← serializa
        lesson.type = content["type"]
        lesson.duration_minutes = content["duration_minutes"]
        lesson.updated_at = datetime.datetime.utcnow()
        lesson.course_id = 1
        lesson.order_index = 3
        db.session.commit()
        print("✅ Lección 3 –‘Ataques Cibernéticos Básicos’– cargada con simuladores de ransomware y DDoS.")
    except Exception as e:
        db.session.rollback()
        print("❌ Error:", e)
    finally:
        db.session.close()

if __name__ == "__main__":
    update_lesson_3()