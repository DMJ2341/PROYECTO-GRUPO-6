# scripts/upload_fundamentos_complete.py
import psycopg2
import json

def upload_fundamentos_complete():
    conn = psycopg2.connect(
        host="172.232.188.183",
        port=5432,
        database="cyberlearn_db",
        user="app_cyberlearn",
        password="CyberLearn2025*"
    )
    cursor = conn.cursor()
    
    # Obtener ID del curso "Fundamentos y Concientización"
    cursor.execute("SELECT id FROM course WHERE title = 'Fundamentos y Concientización'")
    result = cursor.fetchone()
    
    if not result:
        print("❌ Curso 'Fundamentos y Concientización' no encontrado")
        cursor.close()
        conn.close()
        return
    
    course_id = result[0]
    print(f"📚 Curso ID: {course_id}")
    
    # ========== LECCIÓN 1: WANNACRY ==========
    lesson_1 = {
        "id": "fundamentos_leccion_1",
        "title": "Introducción a las Amenazas Cibernéticas - WannaCry",
        "description": "Aprende sobre ransomware a través del caso real que paralizó hospitales en 150 países",
        "content": "Lección interactiva sobre WannaCry - el ransomware que conmocionó al mundo",
        "order_index": 1,
        "type": "interactive", 
        "duration_minutes": 25,
        "xp_reward": 30,
        "total_screens": 6,
        "screens": json.dumps([
            {
                "screen_number": 1,
                "type": "story_hook",
                "title": "🚨 CASO REAL: WANNACRY (2017)",
                "content": {
                    "hook": "12 de mayo de 2017 - Hospitales británicos comienzan a colapsar. Pantallas rojas muestran 'OOOPS, your files have been encrypted'",
                    "impact_cards": [
                        {"icon": "💻", "value": "200,000", "label": "Computadoras", "detail": "infectadas en 150 países"},
                        {"icon": "🏥", "value": "600+", "label": "Cirugías", "detail": "canceladas por el ataque"},
                        {"icon": "💰", "value": "$4B", "label": "Pérdidas", "detail": "económicas globales"}
                    ]
                },
                "cta_button": "🎯 ANALIZAR LA AMENAZA"
            },
            {
                "screen_number": 2,
                "type": "interactive_map", 
                "title": "🌍 MAPA DE INFECCIÓN GLOBAL",
                "content": {
                    "description": "WannaCry demostró 3 verdades sobre las amenazas modernas:",
                    "key_facts": [
                        "📈 VELOCIDAD: 150 países en 24 horas",
                        "🎯 IMPACTO: Desde hospitales hasta empresas", 
                        "⚡ FACILIDAD: Se propagó sola sin intervención humana"
                    ],
                    "user_action": "Identificar el tipo de amenaza"
                }
            },
            {
                "screen_number": 3,
                "type": "classification_game",
                "title": "🎮 CLASIFICADOR DE AMENAZAS",
                "content": {
                    "instruction": "WannaCry fue MALWARE. Clasifica estos casos:",
                    "cases": [
                        {
                            "text": "Empleado recibe SMS: 'Su paquete no se entregó. Confirme datos: bit.ly/paquete123'",
                            "correct_category": "Social",
                            "feedback": "✅ CORRECTO! SMiShing - Engaño por SMS"
                        }
                    ]
                }
            },
            {
                "screen_number": 4,
                "type": "hero_story",
                "title": "🦸 EL HÉROE DE WANNACRY",
                "content": {
                    "hero": "Marcus Hutchins (22 años)",
                    "discovery": "Descubrió el 'KILL SWITCH': El virus consultaba un dominio web. Si el dominio existía, se detenía.",
                    "action": "Marcus registró el dominio por $10.69 y salvó miles de sistemas",
                    "moral": "Un solo analista puede cambiar el curso de un ataque global. Tu conocimiento importa."
                }
            },
            {
                "screen_number": 5,
                "type": "mini_challenge",
                "title": "🧠 TEST RÁPIDO",
                "content": {
                    "questions": [
                        {
                            "question": "¿Qué tipo de amenaza fue WannaCry?",
                            "options": ["Virus", "Ransomware", "Phishing", "DDoS"],
                            "correct": 1,
                            "explanation": "WannaCry fue ransomware - encriptaba archivos y pedía rescate en Bitcoin"
                        },
                        {
                            "question": "¿Cómo se detuvo WannaCry?",
                            "options": ["Actualización de Windows", "Antivirus", "Kill switch", "Desconectar internet"],
                            "correct": 2,
                            "explanation": "Se descubrió un 'kill switch' - un dominio que cuando existía, detenía el virus"
                        }
                    ]
                }
            },
            {
                "screen_number": 6,
                "type": "completion",
                "title": "🏆 LECCIÓN COMPLETADA",
                "content": {
                    "xp_earned": 30,
                    "badge_unlocked": "Primer Respondedor",
                    "summary": "✅ Aprendiste sobre Malware (WannaCry)\n✅ Conociste amenazas sociales\n✅ Descubriste tu papel como defensor",
                    "next_lesson": "Continuar con Ingeniería Social"
                }
            }
        ])
    }

    # ========== LECCIÓN 2: EQUIFAX (INGENIERÍA SOCIAL) ==========
    lesson_2 = {
        "id": "fundamentos_leccion_2",
        "title": "Ingeniería Social y Engaño - El Caso Equifax",
        "description": "Aprende a detectar y neutralizar la ingeniería social que comprometió 147 millones de personas",
        "content": "Lección interactiva sobre phishing y técnicas de engaño",
        "order_index": 2,
        "type": "interactive", 
        "duration_minutes": 20,
        "xp_reward": 35,
        "total_screens": 5,
        "screens": json.dumps([
            {
                "screen_number": 1,
                "type": "story_hook",
                "title": "📱 El Mensaje que Paralizó a Equifax",
                "content": {
                    "hook": "7 de marzo 2017 - Un empleado recibe un correo urgente para 'actualizar datos'. Un clic... y 147 millones de personas quedaron expuestas.",
                    "impact_cards": [
                        {"icon": "👤", "value": "147M", "label": "Personas", "detail": "44% de adultos en EE.UU."},
                        {"icon": "💳", "value": "209K", "label": "Tarjetas", "detail": "números expuestos"},
                        {"icon": "💰", "value": "$700M", "label": "Multa", "detail": "récord histórico"}
                    ]
                },
                "cta_button": "🔍 ANALIZAR EL ATAQUE"
            },
            {
                "screen_number": 2,
                "type": "phishing_simulator", 
                "title": "📧 Simulador: Encuentra los 3 errores",
                "content": {
                    "email": {
                        "from": "rh@equifax-com.tk",
                        "subject": "URGENTE: Actualización de nómina",
                        "body": "Hola Carlos, Necesitamos que verifique sus datos antes del viernes. 👇 Haga clic aquí: http://equifax-payroll.tk/update"
                    },
                    "errors_to_find": [
                        "Dominio .tk (no .com oficial)",
                        "URL sospechosa y no segura", 
                        "Urgencia artificial en el mensaje"
                    ]
                }
            },
            {
                "screen_number": 3,
                "type": "classification_game",
                "title": "🎯 Clasificador de Tácticas",
                "content": {
                    "instruction": "Arrastra cada caso a su técnica de ingeniería social:",
                    "cases": [
                        {"text": "Mantenimiento pide acceso al servidor", "technique": "Pretexting"},
                        {"text": "Llamada del 'banco' pidiendo clave", "technique": "Vishing"},
                        {"text": "Email de CEO pidiendo transferencia", "technique": "Phishing"}
                    ]
                }
            },
            {
                "screen_number": 4,
                "type": "mini_challenge",
                "title": "🧠 TEST RÁPIDO",
                "content": {
                    "questions": [
                        {
                            "question": "¿Qué técnica usaron en Equifax?",
                            "options": ["Phishing", "Pretexting", "Vishing", "Shoulder Surfing"],
                            "correct": 0,
                            "explanation": "Phishing por email fue la puerta de entrada al sistema"
                        }
                    ]
                }
            },
            {
                "screen_number": 5,
                "type": "completion",
                "title": "🏆 LECCIÓN COMPLETADA", 
                "content": {
                    "xp_earned": 35,
                    "badge_unlocked": "Cazador de Phishing",
                    "summary": "✅ Aprendiste sobre Ingeniería Social\n✅ Identificaste señales de phishing\n✅ Conociste técnicas de pretexting y vishing",
                    "next_lesson": "Continuar con Ataques Cibernéticos"
                }
            }
        ])
    }

    # ========== LECCIÓN 3: COLONIAL PIPELINE (RANSOMWARE) ==========
    lesson_3 = {
        "id": "fundamentos_leccion_3", 
        "title": "Ataques Cibernéticos Básicos - Colonial Pipeline",
        "description": "Desde ransomware hasta DDoS: entiende los ataques que paralizan empresas y naciones",
        "content": "Lección interactiva sobre ransomware y ataques DDoS",
        "order_index": 3,
        "type": "interactive",
        "duration_minutes": 30, 
        "xp_reward": 40,
        "total_screens": 5,
        "screens": json.dumps([
            {
                "screen_number": 1,
                "type": "story_hook",
                "title": "⛽ El Clic que Paralizó un País",
                "content": {
                    "hook": "7 mayo 2021 - La mayor tubería de combustible de EE.UU. se detiene. 5 días después: 45% suba de precio y $4.4M pagados en Bitcoin.",
                    "impact_cards": [
                        {"icon": "⛽", "value": "5", "label": "Días", "detail": "paralizada la Costa Este"},
                        {"icon": "🚗", "value": "45%", "label": "Precio", "detail": "aumento en gasolineras"}, 
                        {"icon": "💰", "value": "$4.4M", "label": "Rescate", "detail": "pagado en Bitcoin"}
                    ]
                },
                "cta_button": "🎯 ANALIZAR EL RANSOMWARE"
            },
            {
                "screen_number": 2,
                "type": "timeline",
                "title": "🔄 Cómo Funciona el Ransomware",
                "content": {
                    "steps": [
                        {"step": "1/4", "title": "INFECCIÓN", "desc": "Empleado clica en anuncio 'Actualizar Windows' → descarga DarkSide"},
                        {"step": "2/4", "title": "PROPAGACIÓN", "desc": "El malware escanea la red y se salta a otros sistemas"},
                        {"step": "3/4", "title": "CIFRADO", "desc": "Encripta 100 GB en 2h - archivos cambian a .locked"},
                        {"step": "4/4", "title": "EXTORSIÓN", "desc": "Pantalla: 'Pague $4.4M o borramos todo'"}
                    ]
                }
            },
            {
                "screen_number": 3,
                "type": "ddos_simulator",
                "title": "🌊 Simulador DDoS en Vivo", 
                "content": {
                    "scenario": "Servidor bajo ataque - 10,000 bots envían 1,000,000 req/segundo",
                    "defenses": ["Filtro de tráfico malicioso", "Servicio de mitigación CDN", "Límite de peticiones por IP"]
                }
            },
            {
                "screen_number": 4, 
                "type": "mini_challenge",
                "title": "🧠 TEST RÁPIDO",
                "content": {
                    "questions": [
                        {
                            "question": "¿Qué tipo de malware fue DarkSide?",
                            "options": ["Ransomware", "Troyano", "Spyware", "Adware"],
                            "correct": 0,
                            "explanation": "DarkSide es ransomware: cifra archivos y pide rescate"
                        }
                    ]
                }
            },
            {
                "screen_number": 5,
                "type": "completion", 
                "title": "🏆 LECCIÓN COMPLETADA",
                "content": {
                    "xp_earned": 40,
                    "badge_unlocked": "Contenedor de Ransomware", 
                    "summary": "✅ Comprendiste el ciclo del ransomware\n✅ Conociste ataques DDoS\n✅ Aprendiste defensas básicas",
                    "next_lesson": "Continuar con Seguridad Móvil"
                }
            }
        ])
    }

    # ========== LECCIÓN 4: DISPOSITIVOS MÓVILES ==========
    lesson_4 = {
        "id": "fundamentos_leccion_4",
        "title": "Dispositivos Móviles e Inalámbricos - Evil Twin & Smishing", 
        "description": "Protege tu celular y tu Wi-Fi: detecta redes falsas y SMS maliciosos antes de que sea tarde",
        "content": "Lección interactiva sobre seguridad móvil y redes inalámbricas",
        "order_index": 4,
        "type": "interactive",
        "duration_minutes": 25,
        "xp_reward": 35, 
        "total_screens": 5,
        "screens": json.dumps([
            {
                "screen_number": 1,
                "type": "story_hook",
                "title": "📱 El Wi-Fi que Robó Tarjetas",
                "content": {
                    "hook": "Diciembre 2022 - centro comercial. 2,300 clientes reportan cargos falsos. El origen: un router Wi-Fi falso llamado «Free_Mall_WiFi».",
                    "impact_cards": [
                        {"icon": "👤", "value": "2,300", "label": "Clientes", "detail": "con cargos no autorizados"},
                        {"icon": "💳", "value": "$1.2M", "label": "Fraudes", "detail": "detectados en 48h"},
                        {"icon": "📡", "value": "1", "label": "Router Falso", "detail": "Evil Twin en la pared"}
                    ]
                },
                "cta_button": "🎯 ANALIZAR REDES FALSAS"
            },
            {
                "screen_number": 2,
                "type": "wifi_simulator",
                "title": "📶 Simulador: Elige tu Red Segura",
                "content": {
                    "scenario": "Aeropuerto - redes disponibles:",
                    "networks": [
                        {"name": "Free_Airport_WiFi", "secure": False, "reason": "Evil Twin posible"},
                        {"name": "Airport_Official", "secure": True, "reason": "Red oficial con contraseña"},
                        {"name": "Starbucks_Free", "secure": False, "reason": "No requiere contraseña"}
                    ]
                }
            },
            {
                "screen_number": 3,
                "type": "smishing_detector",
                "title": "📱 Detector de Smishing", 
                "content": {
                    "sms": {
                        "from": "Banco-Ofiicial", 
                        "body": "ALERTA: Actividad sospechosa. Bloquearemos su tarjeta. Verifique: http://banco-ofiicial.com/secure"
                    },
                    "signals": [
                        "Ofiicial con doble 'f'",
                        "Enlace HTTP (no HTTPS)", 
                        "Urgencia artificial",
                        "Remitente no oficial"
                    ]
                }
            },
            {
                "screen_number": 4,
                "type": "mini_challenge",
                "title": "🧠 TEST RÁPIDO", 
                "content": {
                    "questions": [
                        {
                            "question": "¿Qué técnica usan los routers falsos?",
                            "options": ["Evil Twin", "DDoS", "Phishing", "Ransomware"],
                            "correct": 0, 
                            "explanation": "Evil Twin imita el nombre de una red legítima para engañar"
                        }
                    ]
                }
            },
            {
                "screen_number": 5,
                "type": "completion",
                "title": "🏆 LECCIÓN COMPLETADA",
                "content": {
                    "xp_earned": 35,
                    "badge_unlocked": "Guardián Móvil",
                    "summary": "✅ Aprendiste sobre Evil Twin attacks\n✅ Identificaste smishing\n✅ Conociste redes seguras vs peligrosas",
                    "next_lesson": "Continuar con Tríada CIA"
                }
            }
        ])
    }

    # ========== LECCIÓN 5: TRÍADA CIA ==========
    lesson_5 = {
        "id": "fundamentos_leccion_5",
        "title": "Principios de la Ciberseguridad - La Tríada CIA", 
        "description": "Descubre cómo Confidencialidad, Integridad y Disponibilidad protegen la información en cualquier escenario",
        "content": "Lección interactiva sobre los principios fundamentales de seguridad",
        "order_index": 5,
        "type": "interactive",
        "duration_minutes": 20,
        "xp_reward": 40,
        "total_screens": 5,
        "screens": json.dumps([
            {
                "screen_number": 1,
                "type": "story_hook", 
                "title": "🔓 El Password que Expuso a Target",
                "content": {
                    "hook": "Diciembre 2013 - Target descubre que 40 millones de tarjetas fueron expuestas. El origen: un contratista con contraseña 'password1234'.",
                    "impact_cards": [
                        {"icon": "💳", "value": "40M", "label": "Tarjetas", "detail": "números expuestos"},
                        {"icon": "👤", "value": "70M", "label": "Clientes", "detail": "datos personales robados"},
                        {"icon": "🔑", "value": "1", "label": "Password", "detail": "password1234 del contratista"}
                    ]
                },
                "cta_button": "🎯 ANALIZAR LA TRÍADA CIA"
            },
            {
                "screen_number": 2,
                "type": "cia_triangle", 
                "title": "🛡️ La Tríada CIA Interactiva",
                "content": {
                    "principles": [
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
                    ]
                }
            },
            {
                "screen_number": 3,
                "type": "scenario_analyzer",
                "title": "🎯 ¿Qué Principio Proteger?",
                "content": {
                    "scenarios": [
                        {
                            "place": "Hospital - historial clínico", 
                            "question": "¿Qué es MÁS crítico?",
                            "correct": "Integridad",
                            "reason": "Datos médicos incorrectos pueden ser mortales."
                        },
                        {
                            "place": "Banco - cuentas de clientes",
                            "question": "¿Qué es MÁS crítico?",
                            "correct": "Confidencialidad", 
                            "reason": "Solo el cliente y el banco deben ver el saldo."
                        }
                    ]
                }
            },
            {
                "screen_number": 4,
                "type": "mini_challenge",
                "title": "🧠 TEST RÁPIDO",
                "content": {
                    "questions": [
                        {
                            "question": "¿Qué principio se violó en Target 2013?",
                            "options": ["Confidencialidad", "Integridad", "Disponibilidad", "Todas"],
                            "correct": 0,
                            "explanation": "Los atacantes accedieron y copiaron datos privados"
                        }
                    ]
                }
            },
            {
                "screen_number": 5,
                "type": "completion",
                "title": "🏆 LECCIÓN COMPLETADA",
                "content": {
                    "xp_earned": 40,
                    "badge_unlocked": "Guardián CIA",
                    "summary": "✅ Dominaste la Tríada CIA\n✅ Aprendiste Confidencialidad, Integridad, Disponibilidad\n✅ Aplicaste principios en casos reales",
                    "next_lesson": "Evaluación Final del Curso"
                }
            }
        ])
    }

    # ========== LECCIÓN 6: EVALUACIÓN FINAL ==========
    lesson_6 = {
        "id": "fundamentos_leccion_6",
        "title": "Operación Escudo Ciudadano - Evaluación Final", 
        "description": "Pon en práctica todo lo aprendido protegiendo a una PYME real en 24h con presupuesto limitado",
        "content": "Evaluación final interactiva del curso de fundamentos",
        "order_index": 6,
        "type": "interactive",
        "duration_minutes": 45,
        "xp_reward": 50,
        "total_screens": 6,
        "screens": json.dumps([
            {
                "screen_number": 1,
                "type": "mission_brief",
                "title": "🚀 Briefing de la Misión", 
                "content": {
                    "client": "María González, TecnoShop",
                    "objective": "Proteger la PYME en 24h",
                    "budget": 2000,
                    "time": 45,
                    "evidences": [
                        "Emails sospechosos a empleados",
                        "Wi-Fi público sin seguridad", 
                        "Sistema lento últimas 48h"
                    ]
                },
                "cta_button": "🚀 ACEPTAR MISIÓN"
            },
            {
                "screen_number": 2,
                "type": "phishing_analysis",
                "title": "🔍 Fase 1: Investigación de Emails",
                "content": {
                    "email": {
                        "from": "soporte@tecno-shop.com",
                        "subject": "URGENTE: Actualizar credenciales", 
                        "body": "Estimado empleado, Por seguridad, actualice sus credenciales: http://tecno-shop-update.com/login Tiene 24h o será suspendido."
                    },
                    "signals": [
                        "Dominio diferente (tecno-shop-update.com)",
                        "HTTP en lugar de HTTPS",
                        "Amenaza de suspensión"
                    ]
                }
            },
            {
                "screen_number": 3,
                "type": "network_audit",
                "title": "🌐 Fase 2: Auditoría de Red", 
                "content": {
                    "findings": [
                        "Red Wi-Fi: 'TecnoShop_Free' (Abierta)",
                        "3 dispositivos desconocidos conectados",
                        "Tráfico anormal: 2 GB/hora",
                        "Puerto 3389 (Remote Desktop) ABIERTO"
                    ]
                }
            },
            {
                "screen_number": 4,
                "type": "ransomware_response",
                "title": "🦠 Fase 3: Respuesta a Ransomware",
                "content": {
                    "alert": "Patrón de ransomware detectado",
                    "evidences": [
                        "1,200 archivos .txt siendo encriptados",
                        "Mensaje: 'Pague 0.5 BTC en 24h'",
                        "Origen: Dispositivo infectado vía Wi-Fi"
                    ]
                }
            },
            {
                "screen_number": 5,
                "type": "budget_defense",
                "title": "🛡️ Fase 4: Fortificación de Defensas",
                "content": {
                    "budget_left": 1500,
                    "options": [
                        {"name": "Antivirus Empresarial", "cost": 400, "benefit": "Protección endpoints"},
                        {"name": "Capacitación Empleados", "cost": 600, "benefit": "Concientización"},
                        {"name": "Backup Automático", "cost": 150, "benefit": "Recuperación"}
                    ]
                }
            },
            {
                "screen_number": 6,
                "type": "final_certificate",
                "title": "🎓 CURSO COMPLETADO",
                "content": {
                    "xp_earned": 50,
                    "badge_unlocked": "Escudo Ciudadano",
                    "total_xp": 230,
                    "badges_earned": [
                        "Primer Respondedor", "Cazador de Phishing", "Contenedor de Ransomware",
                        "Guardián Móvil", "Guardián CIA", "Escudo Ciudadano"
                    ],
                    "message": "¡Excelente trabajo! Has completado el curso de Fundamentos de Ciberseguridad y estás listo para proteger organizaciones reales."
                }
            }
        ])
    }

    # ========== INSERTAR TODAS LAS LECCIONES ==========
    lessons = [lesson_1, lesson_2, lesson_3, lesson_4, lesson_5, lesson_6]
    
    for lesson in lessons:
        cursor.execute("""
            INSERT INTO lesson (
                id, course_id, title, description, content, order_index, 
                type, duration_minutes, xp_reward, total_screens, screens, created_at
            ) VALUES (
                %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, NOW()
            )
        """, (
            lesson["id"], course_id, lesson["title"], lesson["description"],
            lesson["content"], lesson["order_index"], lesson["type"],
            lesson["duration_minutes"], lesson["xp_reward"], lesson["total_screens"],
            lesson["screens"]
        ))
        
        print(f"✅ Lección {lesson['order_index']} creada: {lesson['title']}")
        print(f"   📍 ID: {lesson['id']}")
        print(f"   ⭐ XP: {lesson['xp_reward']}")
        print(f"   ⏱️  Duración: {lesson['duration_minutes']} min")
        print()
    
    conn.commit()
    cursor.close()
    conn.close()
    
    print("🎉 TODAS LAS 6 LECCIONES DE FUNDAMENTOS CARGADAS EXITOSAMENTE!")
    print("=" * 60)
    print("📊 RESUMEN DEL CURSO:")
    print(f"• 6 lecciones interactivas creadas")
    print(f"• Total XP del curso: 230 XP")
    print(f"• 6 badges desbloqueables")
    print(f"• Duración total: ~3.5 horas")
    print("=" * 60)
    print("🚀 Próximo paso: Probar las lecciones en el frontend Android")

if __name__ == "__main__":
    upload_fundamentos_complete()