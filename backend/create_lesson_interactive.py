import json
from database.db import db
from app import app
from sqlalchemy import text

def insert_phishing_lesson():
    """Insertar la lección completa de phishing en PostgreSQL"""
    
    print("=" * 60)
    print("📝 INSERTANDO LECCIÓN COMPLETA DE PHISHING")
    print("=" * 60)
    
    # Tu JSON exacto CORREGIDO (True/False con mayúscula)
    lesson_data = {
        "lesson_id": "phishing_anatomia_interactivo",
        "course_id": "phishing_ingenieria_social",
        "title": "Anatomía de un correo de phishing",
        "lesson_order": 3,
        "xp_reward": 35,
        "duration_minutes": 4,
        "lesson_type": "interactive",
        "total_screens": 5,
        "screens": [
            {
                "screen_number": 1,
                "type": "story_hook",
                "title": "⚠️ ALERTA: CASO REAL EN LIMA",
                "content": {
                    "story": "María, 28 años, perdió S/ 8,500 en 10 minutos por un correo falso",
                    "quote": "Pensé que era real... tenía el logo y todo",
                    "hook_question": "¿Podrías haber detectado la trampa? Descúbrelo ahora..."
                },
                "cta_button": "Analizar el correo →"
            },
            {
                "screen_number": 2,
                "type": "interactive_email",
                "title": "🔍 ANALIZA ESTE CORREO",
                "subtitle": "Toca las partes sospechosas",
                "email_data": {
                    "from": "servicios@bcp-seguridad.tk",
                    "to": "maria.gomez@gmail.com",
                    "subject": "⚠️ Cuenta suspendida",
                    "body": "Estimado cliente,\n\nHemos detectado actividad sospechoza en su cuenta.\n\nSu cuenta será bloqueada en 24 horas si no verifica sus datos inmediatamente.\n\nHaga clic aquí para verificar:\nhttp://bcp-verify.tk/login\n\nAtentamente,\nEquipo de Seguridad BCP"
                },
                "signals": [
                    {
                        "id": 1,
                        "name": "Dominio falso",
                        "element": "from",
                        "correct_value": "servicios@bcp.com.pe",
                        "explanation": "Los bancos NUNCA usan dominios .tk, .ml, .ga (gratuitos)",
                        "xp": 5
                    },
                    {
                        "id": 2,
                        "name": "Urgencia falsa",
                        "element": "subject",
                        "explanation": "Tácticas de presión como 'inmediatamente', 'urgente', 'última oportunidad'",
                        "xp": 5
                    },
                    {
                        "id": 3,
                        "name": "Saludo genérico",
                        "element": "greeting",
                        "correct_value": "Estimada María Gómez",
                        "explanation": "Tu banco te conoce por tu nombre real, no 'cliente'",
                        "xp": 5
                    },
                    {
                        "id": 4,
                        "name": "Error ortográfico",
                        "element": "typo",
                        "correct_value": "sospechosa",
                        "explanation": "Correos profesionales NO tienen errores",
                        "xp": 5
                    },
                    {
                        "id": 5,
                        "name": "Amenaza de bloqueo",
                        "element": "threat",
                        "explanation": "Objetivo: Que actúes SIN PENSAR",
                        "xp": 5
                    },
                    {
                        "id": 6,
                        "name": "URL sospechosa",
                        "element": "url",
                        "correct_value": "https://www.bcp.com.pe",
                        "explanation": "Sin HTTPS, dominio extraño (.tk), subdominios sospechosos",
                        "xp": 5
                    }
                ],
                "total_signals": 6,  # CORREGIDO: era 8 pero solo hay 6 señales
                "hint": "💡 Pista: Empieza por el remitente"
            },
            {
                "screen_number": 3,
                "type": "checklist",
                "title": "🎯 LAS 6 SEÑALES DE PHISHING",  # CORREGIDO: eran 8 pero solo hay 6
                "items": [
                    {"id": 1, "name": "REMITENTE FALSO", "description": "Dominio sospechoso (.tk, .ml)"},
                    {"id": 2, "name": "URGENCIA/AMENAZA", "description": "'En 24h', 'inmediatamente'"},
                    {"id": 3, "name": "SALUDO GENÉRICO", "description": "'Estimado cliente' sin nombre"},
                    {"id": 4, "name": "ERRORES ORTOGRÁFICOS", "description": "Faltas de tildes, letras"},
                    {"id": 5, "name": "PRESIÓN PSICOLÓGICA", "description": "'Bloqueo', 'suspensión'"},
                    {"id": 6, "name": "URL SOSPECHOSA", "description": "Sin HTTPS, dominio raro"}
                ],
                "tip": "💡 Con 2+ señales → Es PHISHING"
            },
            {
                "screen_number": 4,
                "type": "action_plan",
                "title": "🛡️ TU PLAN ANTI-PHISHING",
                "steps": [
                    {
                        "number": 1,
                        "icon": "🛑",
                        "title": "DETENTE",
                        "actions": ["NO hagas clic en nada", "NO respondas el correo", "NO descargues archivos"],
                        "type": "dont"
                    },
                    {
                        "number": 2,
                        "icon": "🔍",
                        "title": "VERIFICA",
                        "actions": ["Abre tu navegador manualmente", "Ve al sitio oficial: www.bcp.com.pe", "Inicia sesión desde ahí", "Revisa si hay alertas reales"],
                        "type": "do"
                    },
                    {
                        "number": 3,
                        "icon": "☎️",
                        "title": "CONFIRMA",
                        "actions": ["Llama al número oficial del banco", "Pregunta si el correo es real", "NO uses números del correo"],
                        "type": "do"
                    },
                    {
                        "number": 4,
                        "icon": "📢",
                        "title": "REPORTA",
                        "actions": ["Marca como spam/phishing", "Reenvía a: phishing@bcp.com.pe", "Alerta a tus contactos"],
                        "type": "do"
                    }
                ],
                "reminder": "⚡ 5 segundos de duda pueden salvarte miles de soles"
            },
            {
                "screen_number": 5,
                "type": "quiz",
                "title": "🎯 DESAFÍO FINAL",
                "questions": [
                    {
                        "id": 1,
                        "scenario": "📧 Recibes este correo de 'Netflix':\n\nDe: no-reply@netflix-support.com\n\nTu pago falló. Actualiza tu método de pago en 24h o tu cuenta será cancelada.",
                        "question": "¿Qué haces?",
                        "options": [
                            {"id": "A", "text": "Hago clic para actualizar", "correct": False},  # CORREGIDO
                            {"id": "B", "text": "Voy a netflix.com manualmente e inicio sesión", "correct": True},   # CORREGIDO
                            {"id": "C", "text": "Respondo el correo pidiendo más información", "correct": False}  # CORREGIDO
                        ],
                        "correct_answer": "B",
                        "explanation": "Siempre verifica manualmente yendo al sitio oficial. Nunca hagas clic en links de correos sospechosos",
                        "xp": 10
                    },
                    {
                        "id": 2,
                        "scenario": "📱 Un amigo te envía por WhatsApp:\n\n'Mira este link, están regalando iPhones 😱'\n\nhttp://apple-regalo.tk/iphone15",
                        "question": "¿Es legítimo?",
                        "options": [
                            {"id": "A", "text": "Sí, mi amigo me lo envió", "correct": False},  # CORREGIDO
                            {"id": "B", "text": "No, probablemente phishing", "correct": True},   # CORREGIDO
                            {"id": "C", "text": "Hago clic para verificar", "correct": False}  # CORREGIDO
                        ],
                        "correct_answer": "B",
                        "explanation": "La cuenta de tu amigo puede estar hackeada. Señales claras: dominio .tk, urgencia falsa",
                        "xp": 10
                    }
                ]
            }
        ]
    }
    
    with app.app_context():
        try:
            print(f"\n🔹 Título: {lesson_data['title']}")
            print(f"🔹 ID: {lesson_data['lesson_id']}")
            print(f"🔹 Curso: {lesson_data['course_id']}")
            print(f"🔹 Pantallas: {lesson_data['total_screens']}")
            print(f"🔹 XP: {lesson_data['xp_reward']}")
            
            # Convertir screens a JSON string
            screens_json = json.dumps(lesson_data['screens'])
            
            # Insertar en PostgreSQL
            db.session.execute(
                text("""
                    INSERT INTO lessons 
                    (lesson_id, course_id, title, lesson_order, xp_reward, 
                     duration_minutes, lesson_type, screens, total_screens, content)
                    VALUES 
                    (:lesson_id, :course_id, :title, :lesson_order, :xp_reward,
                     :duration_minutes, :lesson_type, :screens, :total_screens, :content)
                    ON CONFLICT (lesson_id) DO UPDATE SET
                        screens = EXCLUDED.screens,
                        total_screens = EXCLUDED.total_screens,
                        lesson_type = EXCLUDED.lesson_type,
                        title = EXCLUDED.title,
                        xp_reward = EXCLUDED.xp_reward
                """),
                {
                    'lesson_id': lesson_data['lesson_id'],
                    'course_id': lesson_data['course_id'],
                    'title': lesson_data['title'],
                    'lesson_order': lesson_data['lesson_order'],
                    'xp_reward': lesson_data['xp_reward'],
                    'duration_minutes': lesson_data['duration_minutes'],
                    'lesson_type': lesson_data['lesson_type'],
                    'screens': screens_json,
                    'total_screens': lesson_data['total_screens'],
                    'content': 'Lección interactiva completa sobre phishing - ver campo screens'
                }
            )
            
            db.session.commit()
            print("✅ Lección completa insertada exitosamente en PostgreSQL")
            
            # Verificar que se insertó
            result = db.session.execute(
                text("""
                    SELECT lesson_id, title, lesson_type, total_screens 
                    FROM lessons 
                    WHERE lesson_id = :lesson_id
                """),
                {"lesson_id": lesson_data['lesson_id']}
            ).fetchone()
            
            if result:
                print(f"✅ Verificado: {result.lesson_id} - {result.title}")
                print(f"   Tipo: {result.lesson_type} - Pantallas: {result.total_screens}")
            else:
                print("❌ No se pudo verificar la inserción")
                
        except Exception as e:
            db.session.rollback()
            print(f"❌ ERROR: {str(e)}")
            import traceback
            traceback.print_exc()

if __name__ == '__main__':
    insert_phishing_lesson()