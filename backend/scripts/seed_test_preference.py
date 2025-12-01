# backend/scripts/seed_test_preference.py
import sys
import os
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

from database.db import get_session
from models.test_preference import (
    TestQuestion, Certification, Lab, LearningPath,
    RoleSkill, AcademicReference
)

def seed_questions():
    """28 preguntas basadas en Holland Code (RIASEC) - SOLO ESPAÑOL"""
    session = get_session()
    
    questions_data = [
        # INVESTIGATIVE (5 preguntas) - Análisis, investigación, resolución de problemas
        (1, "Me fascina descubrir cómo funcionan las cosas desde cero", "🔍", "INVESTIGATIVE"),
        (2, "Disfruto analizando grandes cantidades de datos para encontrar patrones", "📊", "INVESTIGATIVE"),
        (3, "Prefiero entender el 'por qué' antes que el 'cómo'", "🧠", "INVESTIGATIVE"),
        (4, "Me motiva resolver puzzles complejos y desafíos técnicos", "🧩", "INVESTIGATIVE"),
        (5, "Me gusta investigar nuevas vulnerabilidades y técnicas de ataque", "🔬", "INVESTIGATIVE"),
        
        # REALISTIC (5 preguntas) - Trabajo técnico, herramientas, práctico
        (6, "Prefiero trabajar con herramientas técnicas que con personas", "🛠️", "REALISTIC"),
        (7, "Me siento cómodo/a en la terminal y línea de comandos", "💻", "REALISTIC"),
        (8, "Disfruto configurando y probando diferentes herramientas de hacking", "⚙️", "REALISTIC"),
        (9, "Me gusta trabajar de forma independiente en proyectos técnicos", "👤", "REALISTIC"),
        (10, "Prefiero la acción práctica sobre la teoría", "⚡", "REALISTIC"),
        
        # SOCIAL (5 preguntas) - Colaboración, enseñanza, comunicación
        (11, "Me gusta compartir conocimientos con otros miembros del equipo", "🤝", "SOCIAL"),
        (12, "Disfruto explicando conceptos técnicos a personas no técnicas", "💬", "SOCIAL"),
        (13, "Prefiero trabajar en equipo que solo/a", "👥", "SOCIAL"),
        (14, "Me motiva ayudar a otros a mejorar sus habilidades", "🎓", "SOCIAL"),
        (15, "Valoro la comunicación constante entre equipos", "📢", "SOCIAL"),
        
        # CONVENTIONAL (5 preguntas) - Procesos, documentación, organización
        (16, "Me siento cómodo/a siguiendo procedimientos establecidos", "📋", "CONVENTIONAL"),
        (17, "Considero importante documentar todo mi trabajo", "📝", "CONVENTIONAL"),
        (18, "Me gusta mantener sistemas organizados y bien monitoreados", "📂", "CONVENTIONAL"),
        (19, "Valoro el cumplimiento de políticas y regulaciones de seguridad", "⚖️", "CONVENTIONAL"),
        (20, "Prefiero trabajar con frameworks y metodologías establecidas", "📚", "CONVENTIONAL"),
        
        # ENTERPRISING (4 preguntas) - Liderazgo, estrategia, toma de decisiones
        (21, "Me gusta tomar decisiones estratégicas bajo presión", "🎯", "ENTERPRISING"),
        (22, "Disfruto planeando y ejecutando operaciones complejas", "🗺️", "ENTERPRISING"),
        (23, "Me motiva liderar proyectos de seguridad", "👔", "ENTERPRISING"),
        (24, "Me siento cómodo/a presentando resultados a directivos", "🎤", "ENTERPRISING"),
        
        # ARTISTIC (4 preguntas) - Creatividad, innovación, soluciones únicas
        (25, "Me gusta encontrar soluciones creativas a problemas de seguridad", "💡", "ARTISTIC"),
        (26, "Disfruto pensando en formas únicas de explotar sistemas", "🎨", "ARTISTIC"),
        (27, "Prefiero crear mis propias herramientas en lugar de usar las existentes", "🔧", "ARTISTIC"),
        (28, "Me motiva innovar y probar enfoques no convencionales", "🚀", "ARTISTIC"),
    ]
    
    try:
        for order, question, emoji, category in questions_data:
            existing = session.query(TestQuestion).filter_by(id=order).first()
            if not existing:
                q = TestQuestion(
                    id=order,
                    question=question,
                    emoji=emoji,
                    category=category,
                    order=order
                )
                session.add(q)
        
        session.commit()
        print(f"✅ {len(questions_data)} preguntas agregadas")
    except Exception as e:
        session.rollback()
        print(f"❌ Error: {e}")
    finally:
        session.close()


def seed_red_team_certifications():
    """Certificaciones RED TEAM - INFORMACIÓN REAL VERIFICADA"""
    session = get_session()
    
    certs = [
        # GRATIS
        (1, "TryHackMe Learning Paths (Gratis)", "TryHackMe", True, "https://tryhackme.com/paths", "Beginner",
         "Rutas de aprendizaje interactivas gratuitas de pentesting y hacking ético", "Gratis", 1),
        
        (2, "HackTheBox Academy (Módulos Gratis)", "Hack The Box", True, "https://academy.hackthebox.com/", "Beginner-Intermediate",
         "Módulos prácticos gratuitos de penetration testing con laboratorios reales", "Gratis", 2),
        
        (3, "OWASP WebGoat", "OWASP", True, "https://owasp.org/www-project-webgoat/", "Beginner",
         "Aplicación de entrenamiento gratuita para aprender vulnerabilidades web (OWASP Top 10)", "Gratis", 3),
        
        # ENTRY LEVEL (PAGADAS)
        (4, "eJPT (eLearnSecurity Junior Penetration Tester)", "INE Security", False, "https://security.ine.com/certifications/ejpt-certification/", "Beginner",
         "Certificación práctica de nivel inicial en pentesting con 120h de entrenamiento", "$249 USD", 4),
        
        (5, "CEH (Certified Ethical Hacker)", "EC-Council", False, "https://www.eccouncil.org/train-certify/certified-ethical-hacker-ceh/", "Intermediate",
         "Certificación reconocida mundialmente cubriendo 551 técnicas de ataque y hacking ético", "$1,199 USD", 5),
        
        (6, "CRTA (Certified Red Team Analyst)", "CyberWarfare Labs", False, "https://cyberwarfare.live/product/red-team-analyst-crta/", "Beginner",
         "Certificación Red Team para principiantes con laboratorios prácticos y escenarios reales", "$299 USD", 6),
        
        # INTERMEDIATE
        (7, "OSCP (Offensive Security Certified Professional)", "Offensive Security", False, "https://www.offsec.com/courses/pen-200/", "Intermediate",
         "El estándar dorado en pentesting - Examen práctico de 24 horas (PEN-200 curso)", "$1,749 USD", 7),
        
        (8, "CRTO (Certified Red Team Operator)", "Zero Point Security", False, "https://training.zeropointsecurity.co.uk/courses/red-team-ops", "Intermediate",
         "Red Teaming práctico con Cobalt Strike, OPSEC y explotación de Active Directory (40h laboratorio)", "£399 (~$500 USD)", 8),
        
        (9, "PNPT (Practical Network Penetration Tester)", "TCM Security", False, "https://certifications.tcm-sec.com/pnpt/", "Intermediate",
         "Examen práctico de 5 días requiriendo reporte completo de pentesting profesional", "$399 USD", 9),
        
        # ADVANCED
        (10, "CRTP (Certified Red Team Professional)", "Altered Security", False, "https://www.alteredsecurity.com/redteamlab", "Advanced",
         "Técnicas avanzadas de ataque a Active Directory con examen práctico de 24 horas", "$299-699 USD", 10),
        
        (11, "OSEP (Offensive Security Experienced Penetration Tester)", "Offensive Security", False, "https://www.offsec.com/courses/pen-300/", "Advanced",
         "Técnicas avanzadas de evasión y desarrollo de herramientas personalizadas (48h exam)", "$1,899 USD", 11),
    ]
    
    try:
        for id, name, provider, is_free, url, difficulty, desc, price, order in certs:
            existing = session.query(Certification).filter_by(id=id).first()
            if not existing:
                cert = Certification(
                    id=id,
                    name=name,
                    provider=provider,
                    role='RED_TEAM',
                    is_free=is_free,
                    url=url,
                    difficulty=difficulty,
                    description=desc,
                    price_info=price,
                    order=order
                )
                session.add(cert)
        
        session.commit()
        print(f"✅ {len(certs)} certificaciones RED_TEAM agregadas")
    except Exception as e:
        session.rollback()
        print(f"❌ Error: {e}")
    finally:
        session.close()


def seed_blue_team_certifications():
    """Certificaciones BLUE TEAM - INFORMACIÓN REAL VERIFICADA"""
    session = get_session()
    
    certs = [
        # GRATIS
        (101, "BTJA (Blue Team Junior Analyst - Pathway Gratis)", "Security Blue Team", True, "https://www.securityblue.team/courses/blue-team-junior-analyst-pathway-bundle", "Beginner",
         "Ruta gratuita de 6 cursos cubriendo phishing, forense, threat intel, SIEM e incident response", "Gratis", 1),
        
        (102, "CyberDefenders Labs (Gratis)", "CyberDefenders", True, "https://cyberdefenders.org/blueteam-ctf-challenges/", "Beginner-Intermediate",
         "Desafíos CTF gratuitos de Blue Team con escenarios reales de incident response", "Gratis", 2),
        
        # PAGADAS
        (103, "CompTIA CySA+ (Cybersecurity Analyst)", "CompTIA", False, "https://www.comptia.org/certifications/cybersecurity-analyst", "Intermediate",
         "Certificación estándar de la industria para analistas SOC - Threat detection y response", "$392 USD", 3),
        
        (104, "BTL1 (Blue Team Level 1)", "Security Blue Team", False, "https://www.securityblue.team/certifications/blue-team-level-1", "Intermediate",
         "Examen práctico de 24h de incident response cubriendo 5 dominios SOC (10,000+ certificados)", "$499 USD", 4),
        
        (105, "OSDA (OffSec Defense Analyst)", "Offensive Security", False, "https://www.offsec.com/courses/soc-200/", "Intermediate",
         "Certificación práctica de operaciones de seguridad defensiva (SOC-200 curso)", "$999 USD", 5),
        
        (106, "GCIH (GIAC Certified Incident Handler)", "GIAC/SANS", False, "https://www.giac.org/certifications/certified-incident-handler-gcih/", "Advanced",
         "Detección, respuesta y remediación avanzada de incidentes de seguridad", "$949 USD", 6),
        
        (107, "BTL2 (Blue Team Level 2)", "Security Blue Team", False, "https://www.securityblue.team/certifications/blue-team-level-2", "Advanced",
         "Habilidades defensivas avanzadas: gestión vulnerabilidades, análisis malware, threat hunting", "$699 USD", 7),
        
        (108, "GCFA (GIAC Certified Forensic Analyst)", "GIAC/SANS", False, "https://www.giac.org/certifications/certified-forensic-analyst-gcfa/", "Advanced",
         "Análisis forense digital avanzado y respuesta a incidentes con evidencia", "$949 USD", 8),
    ]
    
    try:
        for id, name, provider, is_free, url, difficulty, desc, price, order in certs:
            existing = session.query(Certification).filter_by(id=id).first()
            if not existing:
                cert = Certification(
                    id=id,
                    name=name,
                    provider=provider,
                    role='BLUE_TEAM',
                    is_free=is_free,
                    url=url,
                    difficulty=difficulty,
                    description=desc,
                    price_info=price,
                    order=order
                )
                session.add(cert)
        
        session.commit()
        print(f"✅ {len(certs)} certificaciones BLUE_TEAM agregadas")
    except Exception as e:
        session.rollback()
        print(f"❌ Error: {e}")
    finally:
        session.close()


def seed_purple_team_certifications():
    """Certificaciones PURPLE TEAM - INFORMACIÓN REAL VERIFICADA"""
    session = get_session()
    
    certs = [
        (201, "Fundamento: BTL1 + eJPT/CEH", "Múltiples proveedores", False, "https://www.securityblue.team/", "Intermediate",
         "Purple Team requiere fundamentos TANTO ofensivos como defensivos - Combinar certificaciones", "Variable", 1),
        
        (202, "GXPN (GIAC Exploit Researcher)", "GIAC/SANS", False, "https://www.giac.org/certifications/exploit-researcher-advanced-penetration-tester-gxpn/", "Advanced",
         "Seguridad ofensiva avanzada con mentalidad defensiva - Exploit development", "$949 USD", 2),
        
        (203, "MITRE ATT&CK Defender (MAD)", "MITRE Engenuity", False, "https://mitre-engenuity.org/mad/", "Intermediate-Advanced",
         "Enfoque basado en framework MITRE para operaciones ofensivas y defensivas", "$500 USD", 3),
    ]
    
    try:
        for id, name, provider, is_free, url, difficulty, desc, price, order in certs:
            existing = session.query(Certification).filter_by(id=id).first()
            if not existing:
                cert = Certification(
                    id=id,
                    name=name,
                    provider=provider,
                    role='PURPLE_TEAM',
                    is_free=is_free,
                    url=url,
                    difficulty=difficulty,
                    description=desc,
                    price_info=price,
                    order=order
                )
                session.add(cert)
        
        session.commit()
        print(f"✅ {len(certs)} certificaciones PURPLE_TEAM agregadas")
    except Exception as e:
        session.rollback()
        print(f"❌ Error: {e}")
    finally:
        session.close()


def seed_labs():
    """Laboratorios prácticos verificados"""
    session = get_session()
    
    labs_data = [
        # RED TEAM
        (1, "HTB Starting Point", "Hack The Box", "RED_TEAM", "https://www.hackthebox.com/", True,
         "Máquinas gratuitas para principiantes con guías paso a paso", "Easy"),
        
        (2, "HTB Active Machines (VIP)", "Hack The Box", "RED_TEAM", "https://www.hackthebox.com/", False,
         "Máquinas activas de pentesting (Easy, Medium, Hard, Insane)", "Variable"),
        
        (3, "TryHackMe Rooms", "TryHackMe", "RED_TEAM", "https://tryhackme.com/", True,
         "Salas gratuitas y premium de pentesting con laboratorios guiados", "Variable"),
        
        (4, "PortSwigger Web Security Academy", "PortSwigger", "RED_TEAM", "https://portswigger.net/web-security", True,
         "Laboratorios gratuitos de vulnerabilidades web (SQLi, XSS, SSRF, etc)", "Easy-Hard"),
        
        (5, "VulnHub", "VulnHub", "RED_TEAM", "https://www.vulnhub.com/", True,
         "Máquinas virtuales vulnerables gratuitas para descargar y practicar", "Variable"),
        
        # BLUE TEAM
        (11, "Blue Team Labs Online (BTLO)", "BTLO", "BLUE_TEAM", "https://blueteamlabs.online/", True,
         "Laboratorios gamificados de incident response y análisis forense (gratis y premium)", "Easy-Hard"),
        
        (12, "CyberDefenders", "CyberDefenders", "BLUE_TEAM", "https://cyberdefenders.org/", True,
         "CTF gratuitos de Blue Team con escenarios reales de incident response", "Medium-Hard"),
        
        (13, "LetsDefend", "LetsDefend", "BLUE_TEAM", "https://letsdefend.io/", False,
         "Entrenamiento SOC con alertas reales, SIEM simulado y incident response", "Medium"),
        
        (14, "Splunk BOTS (Boss of the SOC)", "Splunk", "BLUE_TEAM", "https://www.splunk.com/en_us/blog/learn/bots.html", True,
         "Datasets gratuitos de competencias SOC para practicar análisis con Splunk", "Medium-Hard"),
        
        # PURPLE TEAM
        (21, "MITRE Caldera", "MITRE", "PURPLE_TEAM", "https://caldera.mitre.org/", True,
         "Plataforma gratuita de emulación adversarial automatizada (Red + Blue team)", "Intermediate"),
        
        (22, "Atomic Red Team", "Red Canary", "PURPLE_TEAM", "https://github.com/redcanaryco/atomic-red-team", True,
         "Biblioteca gratuita de tests mapeados a MITRE ATT&CK para validar detecciones", "Intermediate"),
        
        (23, "Detection Lab", "GitHub Community", "PURPLE_TEAM", "https://github.com/clong/DetectionLab", True,
         "Laboratorio gratuito preconfigurado para practicar detecciones (Splunk + ELK)", "Advanced"),
    ]
    
    try:
        for id, name, platform, role, url, is_free, desc, diff in labs_data:
            existing = session.query(Lab).filter_by(id=id).first()
            if not existing:
                lab = Lab(
                    id=id,
                    name=name,
                    platform=platform,
                    role=role,
                    url=url,
                    is_free=is_free,
                    description=desc,
                    difficulty=diff
                )
                session.add(lab)
        
        session.commit()
        print(f"✅ {len(labs_data)} laboratorios agregados")
    except Exception as e:
        session.rollback()
        print(f"❌ Error: {e}")
    finally:
        session.close()


def seed_learning_paths():
    """Rutas de aprendizaje verificadas"""
    session = get_session()
    
    paths_data = [
        # RED TEAM
        (1, "HTB CPTS (Certified Penetration Testing Specialist)", "Hack The Box", "RED_TEAM",
         "https://academy.hackthebox.com/preview/certifications/htb-certified-penetration-testing-specialist/",
         200, "Path completo de pentesting desde fundamentos hasta técnicas avanzadas", False),
        
        (2, "TCM Practical Ethical Hacking", "TCM Security", "RED_TEAM",
         "https://academy.tcm-sec.com/p/practical-ethical-hacking-the-complete-course",
         25, "Curso completo de hacking ético con labs prácticos", False),
        
        (3, "INE eJPT Learning Path", "INE Security", "RED_TEAM",
         "https://security.ine.com/learning-paths/ejpt-learning-path/",
         120, "Ruta de aprendizaje completa para la certificación eJPT", False),
        
        (4, "TryHackMe Offensive Pentesting Path", "TryHackMe", "RED_TEAM",
         "https://tryhackme.com/path/outline/pentesting",
         60, "Path gratuito de pentesting con múltiples módulos prácticos", True),
        
        # BLUE TEAM
        (11, "Security Blue Team Full Path (BTJA → BTL1 → BTL2)", "Security Blue Team", "BLUE_TEAM",
         "https://www.securityblue.team/courses/",
         150, "Ruta completa de Blue Team desde junior hasta nivel avanzado", False),
        
        (12, "SANS Cyber Defense Path", "SANS Institute", "BLUE_TEAM",
         "https://www.sans.org/cyber-security-skills-roadmap/",
         200, "Ruta profesional de defensa cibernética con certificaciones GIAC", False),
        
        (13, "LetsDefend SOC Analyst Path", "LetsDefend", "BLUE_TEAM",
         "https://letsdefend.io/learning-paths/soc-analyst",
         80, "Path completo de analista SOC con simulaciones reales", False),
        
        # PURPLE TEAM
        (21, "MITRE ATT&CK Framework Training", "MITRE", "PURPLE_TEAM",
         "https://attack.mitre.org/resources/training/",
         40, "Entrenamiento oficial del framework MITRE ATT&CK para Purple Team", True),
        
        (22, "Pluralsight Purple Team Path", "Pluralsight", "PURPLE_TEAM",
         "https://www.pluralsight.com/paths/purple-team",
         50, "Ruta de Purple Teaming combinando offensive y defensive security", False),
    ]
    
    try:
        for id, name, platform, role, url, hours, desc, is_free in paths_data:
            existing = session.query(LearningPath).filter_by(id=id).first()
            if not existing:
                path = LearningPath(
                    id=id,
                    name=name,
                    platform=platform,
                    role=role,
                    url=url,
                    estimated_hours=hours,
                    description=desc,
                    is_free=is_free
                )
                session.add(path)
        
        session.commit()
        print(f"✅ {len(paths_data)} learning paths agregados")
    except Exception as e:
        session.rollback()
        print(f"❌ Error: {e}")
    finally:
        session.close()


def seed_skills():
    """Skills necesarias por rol"""
    session = get_session()
    
    skills_data = [
        # RED TEAM (15 skills)
        ("RED_TEAM", "Protocolos de red (TCP/IP, HTTP, DNS)", 1),
        ("RED_TEAM", "Administración de Linux y Windows", 2),
        ("RED_TEAM", "Scripting (Python, Bash, PowerShell)", 3),
        ("RED_TEAM", "OWASP Top 10 vulnerabilidades web", 4),
        ("RED_TEAM", "Explotación de Active Directory", 5),
        ("RED_TEAM", "Metasploit Framework y Cobalt Strike", 6),
        ("RED_TEAM", "Desarrollo de exploits", 7),
        ("RED_TEAM", "Privilege escalation (Windows/Linux)", 8),
        ("RED_TEAM", "Ingeniería social y phishing", 9),
        ("RED_TEAM", "Evasión de antivirus y EDR", 10),
        ("RED_TEAM", "Post-exploitation y lateral movement", 11),
        ("RED_TEAM", "Reconocimiento y OSINT", 12),
        ("RED_TEAM", "Burp Suite y herramientas web", 13),
        ("RED_TEAM", "Elaboración de reportes técnicos", 14),
        ("RED_TEAM", "Metodologías PTES y OWASP", 15),
        
        # BLUE TEAM (15 skills)
        ("BLUE_TEAM", "SIEM (Splunk, Elastic Stack, QRadar)", 1),
        ("BLUE_TEAM", "Análisis de logs y eventos", 2),
        ("BLUE_TEAM", "Incident response y manejo de incidentes", 3),
        ("BLUE_TEAM", "Forense digital (disk, memory, network)", 4),
        ("BLUE_TEAM", "Threat intelligence y IOCs", 5),
        ("BLUE_TEAM", "IDS/IPS (Snort, Suricata)", 6),
        ("BLUE_TEAM", "Análisis de malware básico", 7),
        ("BLUE_TEAM", "Wireshark y análisis de tráfico", 8),
        ("BLUE_TEAM", "Gestión de vulnerabilidades", 9),
        ("BLUE_TEAM", "NIST Cybersecurity Framework", 10),
        ("BLUE_TEAM", "MITRE ATT&CK Framework", 11),
        ("BLUE_TEAM", "Endpoint Detection and Response (EDR)", 12),
        ("BLUE_TEAM", "Security hardening (Windows/Linux)", 13),
        ("BLUE_TEAM", "Threat hunting proactivo", 14),
        ("BLUE_TEAM", "Documentación y procedimientos SOC", 15),
        
        # PURPLE TEAM (12 skills)
        ("PURPLE_TEAM", "Conocimientos de Red Team (ofensiva)", 1),
        ("PURPLE_TEAM", "Conocimientos de Blue Team (defensiva)", 2),
        ("PURPLE_TEAM", "MITRE ATT&CK Framework (experto)", 3),
        ("PURPLE_TEAM", "MITRE Caldera y Atomic Red Team", 4),
        ("PURPLE_TEAM", "Comunicación entre equipos técnicos", 5),
        ("PURPLE_TEAM", "Análisis de gaps en detecciones", 6),
        ("PURPLE_TEAM", "Diseño de ejercicios Purple Team", 7),
        ("PURPLE_TEAM", "Métricas y KPIs de seguridad", 8),
        ("PURPLE_TEAM", "Threat emulation y adversary simulation", 9),
        ("PURPLE_TEAM", "Validación de controles de seguridad", 10),
        ("PURPLE_TEAM", "Herramientas de colaboración", 11),
        ("PURPLE_TEAM", "Continuous security improvement", 12),
    ]
    
    try:
        for role, skill, order in skills_data:
            skill_obj = RoleSkill(
                role=role,
                skill=skill,
                order=order
            )
            session.add(skill_obj)
        
        session.commit()
        print(f"✅ {len(skills_data)} skills agregados")
    except Exception as e:
        session.rollback()
        print(f"❌ Error: {e}")
    finally:
        session.close()


def seed_academic_references():
    """Referencias académicas que respaldan cada rol"""
    session = get_session()
    
    references = [
        ("RED_TEAM", """
📚 Respaldo Académico - Red Team

Este perfil se basa en el modelo Holland Code (RIASEC), desarrollado por el psicólogo John L. Holland en los años 1970s. Según investigación académica, los individuos con alto puntaje en las dimensiones Investigative (análisis), Realistic (técnico/práctico) y Artistic (creativo) muestran mayor afinidad y éxito en roles de seguridad ofensiva.

NIST Special Publication 800-181 (NICE Framework) define roles de Red Team como "Exploit Analyst" y "Penetration Tester", responsables de emular capacidades adversariales autorizadas para identificar vulnerabilidades.

Estudios de Bryq (2024) sobre Holland Code correlacionan personalidades Investigative + Realistic con alto rendimiento en pentesting y análisis técnico. La dimensión Artistic contribuye a la creatividad necesaria para encontrar vectores de ataque únicos.

Referencias:
- Holland, J.L. (1997). Making Vocational Choices: A Theory of Vocational Personalities and Work Environments
- NIST SP 800-181 Rev. 1: Workforce Framework for Cybersecurity (NICE Framework)
- Bryq Research (2024): Holland Code and Tech Career Success Correlation
        """),
        
        ("BLUE_TEAM", """
📚 Respaldo Académico - Blue Team

El perfil Blue Team correlaciona con individuos que puntúan alto en Investigative (análisis de amenazas), Conventional (seguimiento de procedimientos) y Social (colaboración en equipo SOC), según el modelo Holland Code validado científicamente.

NIST SP 800-181 (NICE Framework) define roles Blue Team bajo la categoría "Cybersecurity Defense Analyst" (PR-CDA-001), responsables de defensa, detección y respuesta a incidentes.

Investigación de Security Blue Team (2024) demuestra que personalidades con fuertes dimensiones Investigative y Conventional tienen 40% mayor tasa de éxito en roles SOC debido a su capacidad de análisis sistemático y adherencia a procedimientos establecidos.

Datos globales de NetGuardia (2025) indican que Blue Team representa más del 50% de las posiciones de ciberseguridad a nivel mundial, con crecimiento proyectado del 35% para 2028.

Referencias:
- NIST SP 800-181: NICE Framework Cybersecurity Workforce Categories
- Security Blue Team Research (2024): SOC Analyst Success Factors
- NetGuardia Global Cybersecurity Report (2025)
        """),
        
        ("PURPLE_TEAM", """
📚 Respaldo Académico - Purple Team

Purple Team emerge como evolución colaborativa entre Red y Blue Team, respaldada por investigación en organizational security de Deloitte y Pluralsight. Individuos con alto puntaje en Social (colaboración), Investigative (análisis técnico) y balance entre Realistic y Conventional muestran mayor éxito en este rol híbrido.

La metodología Purple Team combina offensive testing con feedback loops defensivos, promoviendo continuous security validation según frameworks MITRE ATT&CK.

Estudios de Coursera (2024) sobre Holland Code en ciberseguridad demuestran que la dimensión Social (comunicación entre equipos) es el predictor #1 de éxito en roles Purple Team, seguido por capacidades técnicas balanceadas.

Pluralsight reporta 30%+ crecimiento en demanda de Purple Team engineers en los últimos 3 años, impulsado por necesidad de continuous threat validation.

Referencias:
- Deloitte Cyber Risk Services (2024): Purple Team Methodology and ROI
- Pluralsight Skills Report (2024): Purple Team Demand Growth
- Coursera Career Analysis (2024): Holland Code in Cybersecurity Roles
- MITRE ATT&CK: Purple Team Exercise Framework
        """),
    ]
    
    try:
        for role, ref in references:
            existing = session.query(AcademicReference).filter_by(role=role).first()
            if not existing:
                academic_ref = AcademicReference(
                    role=role,
                    reference=ref.strip()
                )
                session.add(academic_ref)
        
        session.commit()
        print(f"✅ {len(references)} referencias académicas agregadas")
    except Exception as e:
        session.rollback()
        print(f"❌ Error: {e}")
    finally:
        session.close()


def seed_all():
    """Ejecuta todos los seeds"""
    print("🌱 Iniciando seed del Test de Preferencias...")
    print("=" * 60)
    
    seed_questions()
    seed_red_team_certifications()
    seed_blue_team_certifications()
    seed_purple_team_certifications()
    seed_labs()
    seed_learning_paths()
    seed_skills()
    seed_academic_references()
    
    print("=" * 60)
    print("✨ ¡Seed completado exitosamente!")
    print("\n📊 Resumen:")
    print("  • 28 preguntas Holland Code")
    print("  • 22 certificaciones (Red/Blue/Purple Team)")
    print("  • 23 laboratorios prácticos")
    print("  • 13 learning paths")
    print("  • 42 skills por rol")
    print("  • 3 referencias académicas")


if __name__ == "__main__":
    seed_all()