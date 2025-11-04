from database.db import db
from app import app
from sqlalchemy import text
import sys

LESSONS_DATA = {
    'fundamentos_ciberseguridad': [
        {
            'lesson_id': 'fund_ciber_1',
            'title': 'Qué es ciberseguridad y por qué importa',
            'lesson_order': 1,
            'content': '''📚 LECCIÓN 1: QUÉ ES CIBERSEGURIDAD

🔐 DEFINICIÓN

La ciberseguridad es la práctica de proteger sistemas, redes, dispositivos y datos de ataques digitales, accesos no autorizados, daños o robos.

💡 POR QUÉ IMPORTA EN TU VIDA DIARIA

Cada día usas tecnología para:
- Hacer pagos con tu banco móvil
- Enviar mensajes por WhatsApp
- Publicar fotos en Instagram
- Comprar en línea
- Estudiar o trabajar remotamente

⚠️ RIESGOS REALES

🔸 Robo de identidad
→ Alguien se hace pasar por ti

🔸 Fraude financiero
→ Cargos no autorizados en tu cuenta

🔸 Pérdida de privacidad
→ Datos personales expuestos

🔸 Chantaje digital
→ Amenazas con información comprometedora

🔸 Suplantación
→ Cuentas hackeadas

📊 DATOS IMPORTANTES

- En 2024, Perú registró más de 1.2 millones de intentos de ciberataques
- El 60% de latinoamericanos ha sido víctima de fraude digital
- Costo promedio por incidente: USD 500
- El 95% de incidentes involucran error humano

🎯 LA CIBERSEGURIDAD ES PARA TODOS

❌ MITO: Solo para hackers o expertos en IT
✅ REALIDAD: Responsabilidad de TODOS

Tú eres la primera línea de defensa de tus propios datos.

✨ BUENA NOTICIA

La mayoría de ataques se previenen con:
- Conocimiento básico (este curso)
- Hábitos digitales seguros
- Herramientas simples y gratuitas
- Sentido común digital

📖 QUÉ VAS A APRENDER

✓ Identificar amenazas comunes
✓ Proteger tu información personal
✓ Reconocer intentos de ataque
✓ Tomar decisiones seguras en línea
✓ Responder ante incidentes

💭 RECUERDA

No necesitas ser experto técnico para estar seguro en línea. Solo necesitas estar INFORMADO y ALERTA.

🛡️ La ciberseguridad comienza contigo.''',
            'xp_reward': 25,
            'duration_minutes': 10
        },
        {
            'lesson_id': 'fund_ciber_2',
            'title': 'La tríada CIA',
            'lesson_order': 2,
            'content': '''📚 LECCIÓN 2: LA TRÍADA CIA

Los tres pilares de la seguridad de la información:

🔒 CONFIDENCIALIDAD (C)

Solo personas autorizadas acceden a la información.

📱 EJEMPLOS EN TU VIDA:
- Contraseña de banco → Solo TÚ
- Mensajes privados → Solo destinatario
- Historial médico → Solo tú y doctor

⚠️ SE VIOLA CUANDO:
❌ Alguien lee tus mensajes sin permiso
❌ Hacker accede a tu cuenta bancaria
❌ Fotos privadas se filtran

🛡️ CÓMO PROTEGERLA:
✓ Contraseñas fuertes y únicas
✓ Cifrado extremo a extremo (WhatsApp)
✓ No usar WiFi público para info sensible
✓ Configurar privacidad en redes

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

✅ INTEGRIDAD (I)

Información precisa y no modificada sin autorización.

📱 EJEMPLOS EN TU VIDA:
- Calificaciones → Solo profesor cambia
- Contrato digital → Permanece igual
- Saldo bancario → Refleja transacciones reales

⚠️ SE VIOLA CUANDO:
❌ Modifican un correo que enviaste
❌ Cambian número de cuenta en transferencia
❌ Alteran tu expediente académico

🛡️ CÓMO PROTEGERLA:
✓ Firmas digitales en documentos
✓ Verificar URLs antes de ingresar datos
✓ Respaldos de archivos importantes
✓ Verificación en dos pasos

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🚀 DISPONIBILIDAD (A)

Información accesible cuando la necesites.

📱 EJEMPLOS EN TU VIDA:
- Acceder a tu correo cuando quieras
- Archivos en la nube disponibles
- App bancaria funcionando

⚠️ SE VIOLA CUANDO:
❌ Ataque DDoS tumba el servidor
❌ Ransomware bloquea archivos
❌ Cuenta suspendida maliciosamente

🛡️ CÓMO PROTEGERLA:
✓ Respaldos actualizados
✓ Servicios confiables
✓ Planes de contingencia

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

💡 CASO REAL: GOOGLE DRIVE

Compartes tarea con tu grupo:

🔒 CONFIDENCIALIDAD → Solo el grupo puede verla
✅ INTEGRIDAD → Cambios se registran (historial)
🚀 DISPONIBILIDAD → Accesible 24/7 desde cualquier lugar

⚖️ EQUILIBRIO

A veces hay tensión entre los principios:
- Más seguridad ⟷ Menos accesibilidad
- Más verificaciones ⟷ Más lentitud

El reto es encontrar el BALANCE correcto.

🎯 PUNTO CLAVE

Evalúa cualquier sistema preguntando:
❓ ¿Quién puede ver esto? (C)
❓ ¿Puede ser alterado? (I)
❓ ¿Estará disponible cuando lo necesite? (A)''',
            'xp_reward': 30,
            'duration_minutes': 12
        }
    ],
    'phishing_ingenieria_social': [
        {
            'lesson_id': 'phishing_1',
            'title': 'Introducción al Phishing',
            'lesson_order': 1,
            'content': '''🎣 LECCIÓN 1: QUÉ ES PHISHING

⚠️ LA AMENAZA MÁS COMÚN

Phishing: Técnica donde criminales se hacen pasar por entidades confiables para robar tu información.

🎣 Viene de "fishing" (pescar)
→ Los atacantes "pescan" víctimas

📋 CÓMO FUNCIONA

1️⃣ Recibes un mensaje sospechoso
2️⃣ Parece de alguien confiable
3️⃣ Te pide algo URGENTE
4️⃣ Link a sitio web falso
5️⃣ Ingresas tus datos
6️⃣ ❌ Criminales roban tu información

🎭 TIPOS DE PHISHING

📧 EMAIL PHISHING - El más común, correos falsos de bancos/empresas
📱 SMS PHISHING - Mensajes de texto maliciosos
💬 WHATSAPP PHISHING - Números desconocidos, cuentas hackeadas
📲 REDES SOCIALES - Perfiles falsos
☎️ VOZ (Vishing) - Llamadas de "soporte técnico"

🇵🇪 CASO REAL PERÚ (2024)

Ataque masivo contra usuarios BCP:
- Correos falsos con logo del banco
- "Su cuenta será bloqueada"
- Link a página falsa idéntica
- 5,000+ personas afectadas
- Pérdidas: S/ 2.5 millones

💥 IMPACTO DEL PHISHING

📱 A NIVEL PERSONAL:
❌ Robo de dinero
❌ Pérdida de control de redes sociales
❌ Robo de identidad
❌ Compras fraudulentas
❌ Daño a tu reputación

🏢 A NIVEL EMPRESARIAL:
- 90% de brechas comienzan con phishing
- Costo promedio: USD 4.9 millones
- Pérdida de datos de clientes

📊 ESTADÍSTICAS CLAVE

- 3.4 mil millones de correos phishing se envían DIARIAMENTE
- 1 de cada 4 latinoamericanos ha caído en phishing
- 96% de ataques phishing llegan por correo

🧠 POR QUÉ FUNCIONA

Explotan la PSICOLOGÍA humana:

⏰ URGENCIA - "Tu cuenta será cerrada en 24h"
😱 MIEDO - "Detectamos actividad sospechosa"
🤔 CURIOSIDAD - "Mira quién vio tu perfil"
💰 CODICIA - "Ganaste un premio, reclámalo"
👔 AUTORIDAD - "Mensaje del gerente general"
✅ CONFIANZA - Usan logos y diseños oficiales

✨ BUENA NOTICIA

El phishing es PREVENIBLE si sabes identificarlo.

📚 PRÓXIMAS LECCIONES:
✓ Señales de alerta
✓ Cómo verificar links
✓ Qué hacer si caes
✓ Herramientas de protección

🔑 REGLA DE ORO

Si algo parece demasiado:
- Urgente
- Bueno
- Alarmante

→ DETENTE Y VERIFICA

Tu primera defensa es la DUDA ❓''',
            'xp_reward': 25,
            'duration_minutes': 15
        },
        {
            'lesson_id': 'phishing_2',
            'title': 'Anatomía de un correo de phishing',
            'lesson_order': 2,
            'content': '''🔍 LECCIÓN 2: ANATOMÍA DEL PHISHING

Aprende a identificar correos maliciosos analizando sus componentes.

🚨 LAS 8 SEÑALES DE ALERTA

1️⃣ REMITENTE SOSPECHOSO

✅ LEGÍTIMO: servicios@bcp.com.pe
❌ PHISHING: servicios@bcp-seguridad.com
❌ PHISHING: notificaciones@bcp.verify.tk

🔍 CÓMO VERIFICAR:
→ Revisa el dominio después del @
→ Bancos usan dominios oficiales (.pe, .com)
→ Desconfía de .tk, .ru, .info

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

2️⃣ SALUDO GENÉRICO

✅ LEGÍTIMO: "Estimado Juan Pérez"
❌ PHISHING: "Estimado cliente"
❌ PHISHING: "Estimado usuario"

💡 Empresas legítimas te llaman por nombre

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

3️⃣ URGENCIA O AMENAZA

⚠️ FRASES TÍPICAS:
❌ "Cuenta bloqueada en 24 horas"
❌ "Actividad sospechosa - AHORA"
❌ "Última oportunidad"
❌ "Acción requerida inmediatamente"

🔐 Bancos NUNCA presionan por correo

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

4️⃣ ERRORES ORTOGRÁFICOS

❌ EJEMPLO PHISHING:
"Estimado cliente, hemos detectado actividades sospechoza en su cuénta."

✅ Correos legítimos son profesionales

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

5️⃣ LINKS SOSPECHOSOS

🖱️ ANTES DE HACER CLIC:
1. Pasa el mouse sobre el link
2. Ve la URL real (abajo)
3. Verifica que coincida

🚩 SEÑALES DE URL MALICIOSA:
❌ IP en lugar de dominio (192.168.1.1)
❌ Guiones extras (bcp-security.com)
❌ Dominios raros (.tk, .ml, .ga)
❌ Errores sutiles (rnicrosoft.com)

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

6️⃣ ARCHIVOS ADJUNTOS SOSPECHOSOS

⚠️ PELIGROSOS:
❌ .exe (ejecutables)
❌ .zip con contraseña
❌ .docm o .xlsm (macros)
❌ Facturas.pdf.exe

🔑 REGLA: Si no esperabas archivo, NO ABRIR

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

7️⃣ SOLICITUD DE INFO SENSIBLE

🚫 NUNCA te pedirán por correo:
❌ Contraseña completa
❌ Número de tarjeta completo
❌ PIN o CVV
❌ Código de token

🏦 Bancos JAMÁS piden esto por correo

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

8️⃣ DISEÑO DESCUIDADO

🔍 Compara con correo legítimo:
- ¿Colores iguales?
- ¿Logo nítido?
- ¿Formato profesional?

🛡️ QUÉ HACER SI DUDAS

1️⃣ NO hagas clic en el correo
2️⃣ Abre navegador manualmente
3️⃣ Ve al sitio oficial directamente
4️⃣ Inicia sesión desde ahí
5️⃣ Verifica notificaciones reales

💡 RECUERDA

La mejor defensa es la ATENCIÓN

⏱️ Toma 30 seg para analizar antes de hacer clic''',
            'xp_reward': 30,
            'duration_minutes': 18
        }
    ]
}

def seed_lessons():
    print("="*60)
    print("🌱 INICIANDO SCRIPT DE POBLACIÓN DE LECCIONES")
    print("="*60)
    
    try:
        with app.app_context():
            print("✅ Contexto de aplicación creado")
            print("🌱 Poblando base de datos con lecciones...")
            
            for course_id, lessons in LESSONS_DATA.items():
                print(f"\n📚 Agregando lecciones para: {course_id}")
                
                for lesson_data in lessons:
                    try:
                        # Verificar si ya existe
                        result = db.session.execute(
                            text("SELECT id FROM lessons WHERE lesson_id = :lesson_id"),
                            {"lesson_id": lesson_data['lesson_id']}
                        ).fetchone()
                        
                        if result:
                            print(f"  ⏭️  Lección '{lesson_data['title']}' ya existe")
                            continue
                        
                        # Insertar nueva lección
                        db.session.execute(
                            text("""
                                INSERT INTO lessons 
                                (lesson_id, course_id, title, content, lesson_order, xp_reward, duration_minutes)
                                VALUES 
                                (:lesson_id, :course_id, :title, :content, :lesson_order, :xp_reward, :duration_minutes)
                            """),
                            {
                                'lesson_id': lesson_data['lesson_id'],
                                'course_id': course_id,
                                'title': lesson_data['title'],
                                'content': lesson_data['content'],
                                'lesson_order': lesson_data['lesson_order'],
                                'xp_reward': lesson_data['xp_reward'],
                                'duration_minutes': lesson_data['duration_minutes']
                            }
                        )
                        print(f"  ✅ Agregada: {lesson_data['title']}")
                        
                    except Exception as e:
                        print(f"  ❌ ERROR: {str(e)}")
                        db.session.rollback()
                        continue
            
            db.session.commit()
            print("\n" + "="*60)
            print("✅ ¡Lecciones agregadas exitosamente!")
            print("="*60)
            
    except Exception as e:
        print(f"\n❌ ERROR FATAL: {str(e)}")
        import traceback
        traceback.print_exc()
        sys.exit(1)

if __name__ == '__main__':
    seed_lessons()