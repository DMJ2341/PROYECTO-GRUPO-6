# backend/scripts/generate_full_glossary.py
import json
import os

CYBERLEARN_GLOSSARY = [
    {"id": 1, "term_en": "Cybersecurity", "term_es": "Ciberseguridad", "acronym": None,
     "definition_en": "The practice of protecting systems, networks, programs, and data from digital attacks, unauthorized access, damage, or theft.",
     "definition_es": "Práctica de proteger sistemas, redes, programas y datos de ataques digitales, acceso no autorizado, daño o robo.",
     "category": "Fundamentals", "difficulty": "beginner", "sources": "NIST SP 800-12 Rev.1 / ISO 27000:2018 / INCIBE"},

    {"id": 2, "term_en": "Information Security", "term_es": "Seguridad de la Información", "acronym": "InfoSec",
     "definition_en": "Preservation of confidentiality, integrity and availability of information.",
     "definition_es": "Preservación de la confidencialidad, integridad y disponibilidad de la información.",
     "category": "Fundamentals", "difficulty": "beginner", "sources": "ISO 27000:2018 / NIST SP 800-12"},

    {"id": 3, "term_en": "Confidentiality", "term_es": "Confidencialidad", "acronym": None,
     "definition_en": "Property that information is not made available or disclosed to unauthorized individuals, entities, or processes.",
     "definition_es": "Propiedad por la que la información no se divulga a personas, entidades o procesos no autorizados.",
     "category": "Fundamentals", "difficulty": "beginner", "sources": "ISO 27000:2018 / NIST FIPS 199"},

    {"id": 4, "term_en": "Integrity", "term_es": "Integridad", "acronym": None,
     "definition_en": "Property of accuracy and completeness; guarding against improper information modification or destruction.",
     "definition_es": "Propiedad de exactitud y completitud; protección contra modificación o destrucción indebida.",
     "category": "Fundamentals", "difficulty": "beginner", "sources": "ISO 27000:2018 / NIST FIPS 199"},

    {"id": 5, "term_en": "Availability", "term_es": "Disponibilidad", "acronym": None,
     "definition_en": "Property of being accessible and usable upon demand by an authorized entity.",
     "definition_es": "Propiedad de ser accesible y utilizable bajo demanda por una entidad autorizada.",
     "category": "Fundamentals", "difficulty": "beginner", "sources": "ISO 27000:2018 / NIST FIPS 199"},

    {"id": 6, "term_en": "CIA Triad", "term_es": "Triada CIA", "acronym": "CIA",
     "definition_en": "The three core principles of information security: Confidentiality, Integrity, and Availability.",
     "definition_es": "Los tres principios fundamentales de la seguridad de la información: Confidencialidad, Integridad y Disponibilidad.",
     "category": "Fundamentals", "difficulty": "beginner", "sources": "NIST / ISO 27001 / SANS"},

    {"id": 7, "term_en": "Asset", "term_es": "Activo", "acronym": None,
     "definition_en": "Anything that has value to the organization and therefore requires protection.",
     "definition_es": "Todo aquello que tiene valor para la organización y por tanto requiere protección.",
     "category": "Fundamentals", "difficulty": "beginner", "sources": "ISO 27001 / NIST SP 800-30"},

    {"id": 8, "term_en": "Vulnerability", "term_es": "Vulnerabilidad", "acronym": None,
     "definition_en": "Weakness in a system, procedure, or implementation that could be exploited by a threat.",
     "definition_es": "Debilidad en un sistema, procedimiento o implementación que podría ser explotada por una amenaza.",
     "category": "Fundamentals", "difficulty": "beginner", "sources": "NIST SP 800-30 / CWE / ISO 27005"},

    {"id": 9, "term_en": "Threat", "term_es": "Amenaza", "acronym": None,
     "definition_en": "Any circumstance or event with the potential to adversely affect organizational operations or assets.",
     "definition_es": "Cualquier circunstancia o evento con potencial de afectar adversamente operaciones o activos.",
     "category": "Fundamentals", "difficulty": "beginner", "sources": "NIST SP 800-30 / ENISA"},

    {"id": 10, "term_en": "Risk", "term_es": "Riesgo", "acronym": None,
     "definition_en": "The potential for loss or damage when a threat exploits a vulnerability.",
     "definition_es": "El potencial de pérdida o daño cuando una amenaza explota una vulnerabilidad.",
     "category": "Fundamentals", "difficulty": "beginner", "sources": "ISO 27005 / NIST SP 800-30"},

    {"id": 11, "term_en": "Risk Assessment", "term_es": "Evaluación de Riesgos", "acronym": None,
     "definition_en": "Process of identifying, analyzing, and evaluating risk.",
     "definition_es": "Proceso de identificar, analizar y evaluar el riesgo.",
     "category": "Fundamentals", "difficulty": "intermediate", "sources": "NIST SP 800-30 / ISO 27005"},

    {"id": 12, "term_en": "Threat Actor", "term_es": "Actor de Amenaza", "acronym": None,
     "definition_en": "Individual or group that can cause or contribute to an information security incident.",
     "definition_es": "Individuo o grupo que puede causar o contribuir a un incidente de seguridad de la información.",
     "category": "Fundamentals", "difficulty": "beginner", "sources": "MITRE ATT&CK / NIST SP 800-30"},

    {"id": 13, "term_en": "Exploit", "term_es": "Exploit", "acronym": None,
     "definition_en": "A piece of software or technique that takes advantage of a vulnerability.",
     "definition_es": "Pieza de software o técnica que aprovecha una vulnerabilidad.",
     "category": "Fundamentals", "difficulty": "intermediate", "sources": "NIST / MITRE ATT&CK"},

    {"id": 14, "term_en": "Security Control", "term_es": "Control de Seguridad", "acronym": None,
     "definition_en": "Safeguard or countermeasure to avoid, detect, counteract or minimize security risks.",
     "definition_es": "Salvaguarda o contramedida para evitar, detectar, contrarrestar o minimizar riesgos de seguridad.",
     "category": "Fundamentals", "difficulty": "intermediate", "sources": "NIST SP 800-53 / ISO 27001"},

    {"id": 15, "term_en": "Defense in Depth", "term_es": "Defensa en Profundidad", "acronym": "DiD",
     "definition_en": "Layered security strategy using multiple controls to protect assets.",
     "definition_es": "Estrategia de seguridad en capas usando múltiples controles para proteger activos.",
     "category": "Fundamentals", "difficulty": "intermediate", "sources": "NIST SP 800-53 / NSA"},

    {"id": 16, "term_en": "Least Privilege", "term_es": "Mínimo Privilegio", "acronym": None,
     "definition_en": "Principle that users should only have the access needed to perform their job.",
     "definition_es": "Principio de que los usuarios solo deben tener el acceso necesario para realizar su trabajo.",
     "category": "Fundamentals", "difficulty": "intermediate", "sources": "NIST SP 800-53 / ISO 27001"},

    {"id": 17, "term_en": "Zero Trust", "term_es": "Confianza Cero", "acronym": "ZT",
     "definition_en": "Security model that assumes no trust and verifies every access request.",
     "definition_es": "Modelo de seguridad que no asume confianza y verifica cada solicitud de acceso.",
     "category": "Fundamentals", "difficulty": "advanced", "sources": "NIST SP 800-207 / Forrester"},

    {"id": 18, "term_en": "Non-repudiation", "term_es": "No Repudio", "acronym": None,
     "definition_en": "Assurance that someone cannot deny the validity of something (e.g., a digital signature).",
     "definition_es": "Garantía de que alguien no puede negar la validez de algo (ej. firma digital).",
     "category": "Fundamentals", "difficulty": "intermediate", "sources": "ISO 27000 / NIST SP 800-57"},

    {"id": 19, "term_en": "Attack Surface", "term_es": "Superficie de Ataque", "acronym": None,
     "definition_en": "All points where an unauthorized user can try to enter or extract data from a system.",
     "definition_es": "Todos los puntos donde un usuario no autorizado puede intentar entrar o extraer datos.",
     "category": "Fundamentals", "difficulty": "intermediate", "sources": "OWASP / NIST"},

    {"id": 20, "term_en": "Security Policy", "term_es": "Política de Seguridad", "acronym": None,
     "definition_en": "High-level document that defines the organization's security objectives and requirements.",
     "definition_es": "Documento de alto nivel que define los objetivos y requisitos de seguridad de la organización.",
     "category": "Fundamentals", "difficulty": "intermediate", "sources": "ISO 27001 / NIST SP 800-12"},
    
    {"id": 21, "term_en": "Patch", "term_es": "Parche", "acronym": None,
     "definition_en": "Software update designed to fix security vulnerabilities, bugs, or improve functionality.",
     "definition_es": "Actualización de software diseñada para corregir vulnerabilidades de seguridad, errores o mejorar funcionalidad.",
     "category": "Fundamentals", "difficulty": "beginner", "sources": "NIST SP 800-40 Rev.4 / INCIBE"},

    {"id": 22, "term_en": "Exploit Kit", "term_es": "Kit de Exploits", "acronym": None,
     "definition_en": "Automated toolset that exploits known vulnerabilities in browsers and plugins to deliver malware.",
     "definition_es": "Conjunto automatizado de herramientas que explota vulnerabilidades conocidas en navegadores y plugins para entregar malware.",
     "category": "Fundamentals", "difficulty": "intermediate", "sources": "MITRE ATT&CK T1587 / CISA"},

    {"id": 23, "term_en": "Kill Chain", "term_es": "Cadena de Ataque", "acronym": None,
     "definition_en": "Model describing the stages of a cyberattack from reconnaissance to actions on objectives.",
     "definition_es": "Modelo que describe las fases de un ciberataque desde el reconocimiento hasta las acciones sobre objetivos.",
     "category": "Fundamentals", "difficulty": "intermediate", "sources": "Lockheed Martin Cyber Kill Chain / MITRE ATT&CK"},

    {"id": 24, "term_en": "Adversary", "term_es": "Adversario", "acronym": None,
     "definition_en": "Entity (person, group, or organization) that opposes or attacks an organization’s security.",
     "definition_es": "Entidad (persona, grupo u organización) que se opone o ataca la seguridad de una organización.",
     "category": "Fundamentals", "difficulty": "beginner", "sources": "MITRE ATT&CK / NIST"},

    {"id": 25, "term_en": "Indicator of Compromise", "term_es": "Indicador de Compromiso", "acronym": "IoC",
     "definition_en": "Artifact observed on a network or system that indicates a potential intrusion.",
     "definition_es": "Artefacto observado en red o sistema que indica una posible intrusión.",
     "category": "Fundamentals", "difficulty": "intermediate", "sources": "NIST SP 800-61 Rev.2 / MITRE"},

    {"id": 26, "term_en": "TTP", "term_es": "TTP", "acronym": "TTP",
     "definition_en": "Tactics, Techniques, and Procedures used by threat actors.",
     "definition_es": "Tácticas, Técnicas y Procedimientos utilizados por actores de amenaza.",
     "category": "Fundamentals", "difficulty": "intermediate", "sources": "MITRE ATT&CK Framework"},

    {"id": 27, "term_en": "Red Team", "term_es": "Equipo Rojo", "acronym": None,
     "definition_en": "Group that simulates real adversaries to test an organization’s detection and response capabilities.",
     "definition_es": "Grupo que simula adversarios reales para probar capacidades de detección y respuesta de una organización.",
     "category": "Fundamentals", "difficulty": "intermediate", "sources": "NIST SP 800-115 / MITRE"},

    {"id": 28, "term_en": "Blue Team", "term_es": "Equipo Azul", "acronym": None,
     "definition_en": "Internal cybersecurity team responsible for defending against attacks.",
     "definition_es": "Equipo interno de ciberseguridad responsable de defender contra ataques.",
     "category": "Fundamentals", "difficulty": "intermediate", "sources": "SANS / NIST"},

    {"id": 29, "term_en": "Purple Team", "term_es": "Equipo Púrpura", "acronym": None,
     "definition_en": "Collaborative approach combining Red and Blue teams to improve overall security.",
     "definition_es": "Enfoque colaborativo que combina equipos Rojo y Azul para mejorar la seguridad global.",
     "category": "Fundamentals", "difficulty": "intermediate", "sources": "SANS / MITRE"},

    {"id": 30, "term_en": "Security Posture", "term_es": "Postura de Seguridad", "acronym": None,
     "definition_en": "Overall cybersecurity strength of an organization at a given point in time.",
     "definition_es": "Fortaleza global de ciberseguridad de una organización en un momento dado momento.",
     "category": "Fundamentals", "difficulty": "intermediate", "sources": "NIST / ENISA"},

    {"id": 31, "term_en": "Malware", "term_es": "Malware", "acronym": None,
     "definition_en": "Malicious software designed to harm, disrupt, or gain unauthorized access to systems.",
     "definition_es": "Software malicioso diseñado para dañar, interrumpir o acceder sin autorización a sistemas.",
     "category": "Threats", "difficulty": "beginner", "sources": "NIST SP 800-83 / INCIBE"},

    {"id": 32, "term_en": "Virus", "term_es": "Virus", "acronym": None,
     "definition_en": "Malware that attaches to legitimate files and spreads when executed.",
     "definition_es": "Malware que se adjunta a archivos legítimos y se propaga al ejecutarse.",
     "category": "Threats", "difficulty": "beginner", "sources": "NIST SP 800-83 / INCIBE"},

    {"id": 33, "term_en": "Worm", "term_es": "Gusano", "acronym": None,
     "definition_en": "Self-replicating malware that spreads across networks without user interaction.",
     "definition_es": "Malware autorreplicante que se propaga por redes sin interacción del usuario.",
     "category": "Threats", "difficulty": "beginner", "sources": "NIST SP 800-83 / MITRE ATT&CK"},

    {"id": 34, "term_en": "Trojan Horse", "term_es": "Troyano", "acronym": None,
     "definition_en": "Malware disguised as legitimate software to trick users into installing it.",
     "definition_es": "Malware disfrazado de software legítimo para engañar al usuario y que lo instale.",
     "category": "Threats", "difficulty": "beginner", "sources": "NIST SP 800-83 / INCIBE"},

    {"id": 35, "term_en": "Ransomware", "term_es": "Ransomware", "acronym": None,
     "definition_en": "Malware that encrypts victim data and demands ransom for decryption.",
     "definition_es": "Malware que cifra datos de la víctima y exige rescate para descifrarlos.",
     "category": "Threats", "difficulty": "beginner", "sources": "CISA / INCIBE / MITRE ATT&CK T1486"},

    {"id": 36, "term_en": "Spyware", "term_es": "Spyware", "acronym": None,
     "definition_en": "Malware that secretly gathers information about a person or organization.",
     "definition_es": "Malware que recopila información sobre una persona u organización de forma secreta.",
     "category": "Threats", "difficulty": "beginner", "sources": "NIST SP 800-83 / INCIBE"},

    {"id": 37, "term_en": "Adware", "term_es": "Adware", "acronym": None,
     "definition_en": "Software that automatically displays or downloads advertising.",
     "definition_es": "Software que muestra o descarga publicidad automáticamente.",
     "category": "Threats", "difficulty": "beginner", "sources": "INCIBE / CISA"},

    {"id": 38, "term_en": "Rootkit", "term_es": "Rootkit", "acronym": None,
     "definition_en": "Stealthy malware designed to hide the existence of certain processes or programs.",
     "definition_es": "Malware sigiloso diseñado para ocultar la existencia de procesos o programas.",
     "category": "Threats", "difficulty": "intermediate", "sources": "NIST SP 800-83 / MITRE ATT&CK T1014"},

    {"id": 39, "term_en": "Keylogger", "term_es": "Keylogger", "acronym": None,
     "definition_en": "Software or hardware that records every keystroke made by a user.",
     "definition_es": "Software o hardware que registra cada pulsación de tecla realizada por un usuario.",
     "category": "Threats", "difficulty": "beginner", "sources": "INCIBE / MITRE ATT&CK T1056"},

    {"id": 40, "term_en": "Botnet", "term_es": "Botnet", "acronym": None,
     "definition_en": "Network of infected devices controlled by an attacker to perform coordinated attacks.",
     "definition_es": "Red de dispositivos infectados controlados por un atacante para realizar ataques coordinados.",
     "category": "Threats", "difficulty": "intermediate", "sources": "NIST SP 800-83 / MITRE ATT&CK"},

    {"id": 41, "term_en": "Phishing", "term_es": "Phishing", "acronym": None,
     "definition_en": "Social engineering attack using fraudulent emails or messages to steal credentials.",
     "definition_es": "Ataque de ingeniería social mediante correos o mensajes fraudulentos para robar credenciales.",
     "category": "Threats", "difficulty": "beginner", "sources": "NIST SP 800-177 / INCIBE / MITRE ATT&CK T1566"},

    {"id": 42, "term_en": "Spear Phishing", "term_es": "Phishing Dirigido", "acronym": None,
     "definition_en": "Targeted phishing attack against specific individuals or organizations.",
     "definition_es": "Ataque de phishing dirigido a individuos u organizaciones específicas.",
     "category": "Threats", "difficulty": "intermediate", "sources": "MITRE ATT&CK T1566.001 / CISA"},

    {"id": 43, "term_en": "Whaling", "term_es": "Whaling", "acronym": None,
     "definition_en": "Spear phishing targeting high-profile individuals (executives, VIPs).",
     "definition_es": "Phishing dirigido a personas de alto perfil (directivos, VIP).",
     "category": "Threats", "difficulty": "intermediate", "sources": "INCIBE / CISA"},

    {"id": 44, "term_en": "Vishing", "term_es": "Vishing", "acronym": None,
     "definition_en": "Voice phishing – social engineering over the phone.",
     "definition_es": "Phishing por voz – ingeniería social telefónica.",
     "category": "Threats", "difficulty": "beginner", "sources": "INCIBE / CISA"},

    {"id": 45, "term_en": "Smishing", "term_es": "Smishing", "acronym": None,
     "definition_en": "SMS phishing – phishing via text messages.",
     "definition_es": "Phishing por SMS – phishing mediante mensajes de texto.",
     "category": "Threats", "difficulty": "beginner", "sources": "INCIBE / CISA"},

    {"id": 46, "term_en": "Social Engineering", "term_es": "Ingeniería Social", "acronym": None,
     "definition_en": "Psychological manipulation to trick people into divulging confidential information.",
     "definition_es": "Manipulación psicológica para engañar a personas y obtener información confidencial.",
     "category": "Threats", "difficulty": "beginner", "sources": "NIST SP 800-61 / INCIBE"},

    {"id": 47, "term_en": "Denial of Service", "term_es": "Denegación de Servicio", "acronym": "DoS",
     "definition_en": "Attack that makes a system or network resource unavailable to intended users.",
     "definition_es": "Ataque que impide que un sistema o recurso esté disponible para usuarios legítimos.",
     "category": "Threats", "difficulty": "beginner", "sources": "NIST SP 800-61 / MITRE ATT&CK T1499"},

    {"id": 48, "term_en": "Distributed Denial of Service", "term_es": "Denegación de Servicio Distribuida", "acronym": "DDoS",
     "definition_en": "DoS attack launched from multiple compromised systems simultaneously.",
     "definition_es": "Ataque DoS lanzado desde múltiples sistemas comprometidos simultáneamente.",
     "category": "Threats", "difficulty": "intermediate", "sources": "NIST / MITRE ATT&CK T1498"},

    {"id": 49, "term_en": "Man-in-the-Middle Attack", "term_es": "Ataque de Hombre en el Medio", "acronym": "MitM",
     "definition_en": "Attacker secretly intercepts and possibly alters communication between two parties.",
     "definition_es": "Atacante intercepta y posiblemente altera la comunicación entre dos partes.",
     "category": "Threats", "difficulty": "intermediate", "sources": "NIST SP 800-113 / OWASP"},

    {"id": 50, "term_en": "SQL Injection", "term_es": "Inyección SQL", "acronym": "SQLi",
     "definition_en": "Code injection technique that exploits improper input validation to manipulate databases.",
     "definition_es": "Técnica de inyección de código que explota validación incorrecta de entrada para manipular bases de datos.",
     "category": "Threats", "difficulty": "intermediate", "sources": "OWASP Top 10 A03:2021 / CWE-89"},
    
    {"id": 51, "term_en": "Cross-Site Scripting", "term_es": "Secuencias de Comandos en Sitios Cruzados", "acronym": "XSS",
     "definition_en": "Vulnerability that allows attackers to inject malicious scripts into web pages viewed by other users.",
     "definition_es": "Vulnerabilidad que permite a atacantes inyectar scripts maliciosos en páginas vistas por otros usuarios.",
     "category": "Threats", "difficulty": "intermediate", "sources": "OWASP Top 10 A07:2021 / CWE-79"},

    {"id": 52, "term_en": "Cross-Site Request Forgery", "term_es": "Falsificación de Petición en Sitios Cruzados", "acronym": "CSRF",
     "definition_en": "Attack that tricks an authenticated user into executing unwanted actions on a web application.",
     "definition_es": "Ataque que engaña a un usuario autenticado para ejecutar acciones no deseadas en una aplicación web.",
     "category": "Threats", "difficulty": "intermediate", "sources": "OWASP Top 10 A01:2021 / CWE-352"},

    {"id": 53, "term_en": "Buffer Overflow", "term_es": "Desbordamiento de Búfer", "acronym": None,
     "definition_en": "Vulnerability occurring when a program writes more data to a buffer than it can hold.",
     "definition_es": "Vulnerabilidad que ocurre cuando un programa escribe más datos de los que puede contener un búfer.",
     "category": "Threats", "difficulty": "advanced", "sources": "CWE-120 / NIST SP 800-28"},

    {"id": 54, "term_en": "Zero-Day Exploit", "term_es": "Exploit de Día Cero", "acronym": "0-day",
     "definition_en": "Attack that exploits a previously unknown vulnerability before a patch is available.",
     "definition_es": "Ataque que explota una vulnerabilidad previamente desconocida antes de que exista parche.",
     "category": "Threats", "difficulty": "advanced", "sources": "NIST IR 8011 / MITRE ATT&CK T1190"},

    {"id": 55, "term_en": "Advanced Persistent Threat", "term_es": "Amenaza Persistente Avanzada", "acronym": "APT",
     "definition_en": "Sophisticated, long-term attack usually conducted by nation-states or organized groups.",
     "definition_es": "Ataque sofisticado y prolongado generalmente realizado por estados-nación o grupos organizados.",
     "category": "Threats", "difficulty": "advanced", "sources": "NIST IR 7298 / MITRE ATT&CK"},

    {"id": 56, "term_en": "Brute Force Attack", "term_es": "Ataque de Fuerza Bruta", "acronym": None,
     "definition_en": "Method of trying all possible combinations to crack passwords or keys.",
     "definition_es": "Método de probar todas las combinaciones posibles para descifrar contraseñas o claves.",
     "category": "Threats", "difficulty": "beginner", "sources": "NIST SP 800-63B / MITRE ATT&CK T1110"},

    {"id": 57, "term_en": "Dictionary Attack", "term_es": "Ataque de Diccionario", "acronym": None,
     "definition_en": "Brute force variant using a list of common words and passwords.",
     "definition_es": "Variante de fuerza bruta usando una lista de palabras y contraseñas comunes.",
     "category": "Threats", "difficulty": "beginner", "sources": "NIST SP 800-63B / OWASP"},

    {"id": 58, "term_en": "Credential Stuffing", "term_es": "Relleno de Credenciales", "acronym": None,
     "definition_en": "Automated attack using stolen username/password pairs on multiple sites.",
     "definition_es": "Ataque automatizado usando pares usuario/contraseña robados en múltiples sitios.",
     "category": "Threats", "difficulty": "intermediate", "sources": "OWASP / MITRE ATT&CK T1110.004"},

    {"id": 59, "term_en": "Session Hijacking", "term_es": "Secuestro de Sesión", "acronym": None,
     "definition_en": "Attacker steals a valid session token to gain unauthorized access.",
     "definition_es": "Atacante roba un token de sesión válido para obtener acceso no autorizado.",
     "category": "Threats", "difficulty": "intermediate", "sources": "OWASP / MITRE ATT&CK T1559"},

    {"id": 60, "term_en": "Drive-by Download", "term_es": "Descarga Involuntaria", "acronym": None,
     "definition_en": "Malware infection triggered simply by visiting a compromised website.",
     "definition_es": "Infección por malware al visitar una web comprometida sin interacción adicional.",
     "category": "Threats", "difficulty": "intermediate", "sources": "INCIBE / MITRE ATT&CK T1189"},

    {"id": 61, "term_en": "Watering Hole Attack", "term_es": "Ataque de Abrevadero", "acronym": None,
     "definition_en": "Compromising websites frequented by the target group to deliver malware.",
     "definition_es": "Compromiso de sitios frecuentados por el grupo objetivo para entregar malware.",
     "category": "Threats", "difficulty": "advanced", "sources": "MITRE ATT&CK T1189"},

    {"id": 62, "term_en": "Supply Chain Attack", "term_es": "Ataque a la Cadena de Suministro", "acronym": None,
     "definition_en": "Attack targeting a trusted third-party vendor to compromise the final target.",
     "definition_es": "Ataque dirigido a un proveedor de confianza para comprometer al objetivo final.",
     "category": "Threats", "difficulty": "advanced", "sources": "NIST SP 800-161 / MITRE ATT&CK T1195"},

    {"id": 63, "term_en": "Backdoor", "term_es": "Puerta Trasera", "acronym": None,
     "definition_en": "Hidden method to bypass normal authentication and regain access.",
     "definition_es": "Método oculto para evadir autenticación normal y recuperar acceso.",
     "category": "Threats", "difficulty": "intermediate", "sources": "MITRE ATT&CK T1055"},

    {"id": 64, "term_en": "Logic Bomb", "term_es": "Bomba Lógica", "acronym": None,
     "definition_en": "Malicious code that triggers under specific conditions or dates, events).",
     "definition_es": "Código malicioso que se activa bajo condiciones específicas (fechas, eventos).",
     "category": "Threats", "difficulty": "intermediate", "sources": "INCIBE / SANS"},

    {"id": 65, "term_en": "Fileless Malware", "term_es": "Malware sin Archivo", "acronym": None,
     "definition_en": "Malware that operates in memory without writing files to disk.",
     "definition_es": "Malware que opera en memoria sin escribir archivos en disco.",
     "category": "Threats", "difficulty": "advanced", "sources": "MITRE ATT&CK T1055 / CrowdStrike"},

    {"id": 66, "term_en": "Living off the Land", "term_es": "Vivir de la Tierra", "acronym": "LotL",
     "definition_en": "Attack technique using legitimate system tools to avoid detection.",
     "definition_es": "Técnica de ataque que usa herramientas legítimas del sistema para evitar detección.",
     "category": "Threats", "difficulty": "advanced", "sources": "MITRE ATT&CK / SANS"},

    {"id": 67, "term_en": "Command and Control", "term_es": "Comando y Control", "acronym": "C2",
     "definition_en": "Communication channel between compromised systems and attacker infrastructure.",
     "definition_es": "Canal de comunicación entre sistemas comprometidos e infraestructura del atacante.",
     "category": "Threats", "difficulty": "intermediate", "sources": "MITRE ATT&CK TA0011"},

    {"id": 68, "term_en": "Exfiltration", "term_es": "Exfiltración", "acronym": None,
     "definition_en": "Unauthorized transfer of data from a compromised system.",
     "definition_es": "Transferencia no autorizada de datos desde un sistema comprometido.",
     "category": "Threats", "difficulty": "intermediate", "sources": "MITRE ATT&CK TA0010"},

    {"id": 69, "term_en": "Lateral Movement", "term_es": "Movimiento Lateral", "acronym": None,
     "definition_en": "Techniques attackers use to move through a network seeking key assets.",
     "definition_es": "Técnicas que usan atacantes para desplazarse por una red buscando activos clave.",
     "category": "Threats", "difficulty": "advanced", "sources": "MITRE ATT&CK TA0008"},

    {"id": 70, "term_en": "Privilege Escalation", "term_es": "Escalada de Privilegios", "acronym": None,
     "definition_en": "Act of exploiting a bug or design flaw to gain elevated access.",
     "definition_es": "Explotación de un fallo para obtener acceso elevado en un sistema.",
     "category": "Threats", "difficulty": "intermediate", "sources": "MITRE ATT&CK TA0004"},

    {"id": 71, "term_en": "Authentication", "term_es": "Autenticación", "acronym": None,
     "definition_en": "Process of verifying the identity of a user, process, or device.",
     "definition_es": "Proceso de verificar la identidad de un usuario, proceso o dispositivo.",
     "category": "Authentication", "difficulty": "beginner", "sources": "NIST SP 800-63-3"},

    {"id": 72, "term_en": "Authorization", "term_es": "Autorización", "acronym": None,
     "definition_en": "Process of determining what an authenticated entity is allowed to do.",
     "definition_es": "Proceso de determinar qué puede hacer una entidad autenticada.",
     "category": "Authentication", "difficulty": "beginner", "sources": "NIST SP 800-162"},

    {"id": 73, "term_en": "Multi-Factor Authentication", "term_es": "Autenticación Multifactor", "acronym": "MFA",
     "definition_en": "Authentication requiring two or more verification factors.",
     "definition_es": "Autenticación que requiere dos o más factores de verificación.",
     "category": "Authentication", "difficulty": "beginner", "sources": "NIST SP 800-63B / INCIBE"},

    {"id": 74, "term_en": "Two-Factor Authentication", "term_es": "Autenticación de Dos Factores", "acronym": "2FA",
     "definition_en": "Specific type of MFA using exactly two different factors.",
     "definition_es": "Tipo específico de MFA que usa exactamente dos factores diferentes.",
     "category": "Authentication", "difficulty": "beginner", "sources": "NIST SP 800-63B"},

    {"id": 75, "term_en": "Biometrics", "term_es": "Biometría", "acronym": None,
     "definition_en": "Authentication based on unique physical or behavioral characteristics.",
     "definition_es": "Autenticación basada en características físicas o de comportamiento únicas.",
     "category": "Authentication", "difficulty": "beginner", "sources": "NIST SP 800-76-2"},

    {"id": 76, "term_en": "Password", "term_es": "Contraseña", "acronym": None,
     "definition_en": "Secret string of characters used as a knowledge factor for authentication.",
     "definition_es": "Cadena secreta de caracteres usada como factor de conocimiento en autenticación.",
     "category": "Authentication", "difficulty": "beginner", "sources": "NIST SP 800-63B"},

    {"id": 77, "term_en": "Passphrase", "term_es": "Frase de Contraseña", "acronym": None,
     "definition_en": "Longer, more secure alternative to passwords using multiple words.",
     "definition_es": "Alternativa más larga y segura a las contraseñas usando múltiples palabras.",
     "category": "Authentication", "difficulty": "beginner", "sources": "NIST SP 800-63B"},

    {"id": 78, "term_en": "One-Time Password", "term_es": "Contraseña de Un Solo Uso", "acronym": "OTP",
     "definition_en": "Password valid for only one login session or transaction.",
     "definition_es": "Contraseña válida solo para una sesión o transacción.",
     "category": "Authentication", "difficulty": "beginner", "sources": "NIST SP 800-63B"},

    {"id": 79, "term_en": "Single Sign-On", "term_es": "Inicio de Sesión Único", "acronym": "SSO",
     "definition_en": "Authentication scheme allowing a user to log in once and gain access to multiple systems.",
     "definition_es": "Esquema que permite iniciar sesión una vez y acceder a múltiples sistemas.",
     "category": "Authentication", "difficulty": "intermediate", "sources": "NIST SP 800-63C"},

    {"id": 80, "term_en": "Role-Based Access Control", "term_es": "Control de Acceso Basado en Roles", "acronym": "RBAC",
     "definition_en": "Access control method where permissions are assigned to roles, not individuals.",
     "definition_es": "Método donde los permisos se asignan a roles, no a individuos.",
     "category": "Authentication", "difficulty": "intermediate", "sources": "NIST SP 800-53 AC-6"},
    
    {"id": 81, "term_en": "Attribute-Based Access Control", "term_es": "Control de Acceso Basado en Atributos", "acronym": "ABAC",
     "definition_en": "Access control paradigm whereby access rights are granted based on attributes (user, resource, environment).",
     "definition_es": "Paradigma de control de acceso donde los derechos se conceden según atributos (usuario, recurso, entorno).",
     "category": "Authentication", "difficulty": "advanced", "sources": "NIST SP 800-162 / ISO 27001"},

    {"id": 82, "term_en": "Identity Provider", "term_es": "Proveedor de Identidad", "acronym": "IdP",
     "definition_en": "Service that creates, maintains, and manages identity information and provides authentication to other services.",
     "definition_es": "Servicio que crea, mantiene y gestiona información de identidad y proporciona autenticación a otros servicios.",
     "category": "Authentication", "difficulty": "intermediate", "sources": "NIST SP 800-63C"},

    {"id": 83, "term_en": "OAuth", "term_es": "OAuth", "acronym": "OAuth",
     "definition_en": "Open standard for token-based authentication and authorization on the Internet.",
     "definition_es": "Estándar abierto para autenticación y autorización basada en tokens en Internet.",
     "category": "Authentication", "difficulty": "intermediate", "sources": "RFC 6749 / OWASP"},

    {"id": 84, "term_en": "OpenID Connect", "term_es": "OpenID Connect", "acronym": "OIDC",
     "definition_en": "Identity layer on top of OAuth 2.0 that allows clients to verify the identity of the end-user.",
     "definition_es": "Capa de identidad sobre OAuth 2.0 que permite a clientes verificar la identidad del usuario final.",
     "category": "Authentication", "difficulty": "intermediate", "sources": "OpenID Foundation / NIST"},

    {"id": 85, "term_en": "Kerberos", "term_es": "Kerberos", "acronym": None,
     "definition_en": "Network authentication protocol using tickets to allow nodes to prove their identity securely.",
     "definition_es": "Protocolo de autenticación de red que usa tickets para que los nodos prueben su identidad de forma segura.",
     "category": "Authentication", "difficulty": "advanced", "sources": "RFC 4120 / MIT"},

    {"id": 86, "term_en": "Password Hashing", "term_es": "Hash de Contraseñas", "acronym": None,
     "definition_en": "One-way transformation of passwords using algorithms like bcrypt, Argon2, or PBKDF2.",
     "definition_es": "Transformación unidireccional de contraseñas usando algoritmos como bcrypt, Argon2 o PBKDF2.",
     "category": "Authentication", "difficulty": "intermediate", "sources": "NIST SP 800-63B / OWASP"},

    {"id": 87, "term_en": "Rainbow Table", "term_es": "Tabla Arcoíris", "acronym": None,
     "definition_en": "Precomputed table of hash values used to reverse cryptographic hash functions.",
     "definition_es": "Tabla precalculada de valores hash usada para revertir funciones hash criptográficas.",
     "category": "Authentication", "difficulty": "advanced", "sources": "OWASP / Philippe Oechslin"},

    {"id": 88, "term_en": "Token", "term_es": "Token", "acronym": None,
     "definition_en": "Physical or digital object used as a possession factor in authentication.",
     "definition_es": "Objeto físico o digital usado como factor de posesión en autenticación.",
     "category": "Authentication", "difficulty": "beginner", "sources": "NIST SP 800-63B"},

    {"id": 89, "term_en": "FIDO2", "term_es": "FIDO2", "acronym": "FIDO2",
     "definition_en": "Set of standards for passwordless authentication using public-key cryptography.",
     "definition_es": "Conjunto de estándares para autenticación sin contraseña usando criptografía de clave pública.",
     "category": "Authentication", "difficulty": "intermediate", "sources": "FIDO Alliance / W3C"},

    {"id": 90, "term_en": "Passkey", "term_es": "Clave de Acceso", "acronym": None,
     "definition_en": "Passwordless credential based on FIDO2 using device-bound public/private key pairs.",
     "definition_es": "Credencial sin contraseña basada en FIDO2 que usa pares de claves pública/privada ligadas al dispositivo.",
     "category": "Authentication", "difficulty": "intermediate", "sources": "FIDO Alliance / Google, Apple, Microsoft"},

    {"id": 91, "term_en": "Cryptography", "term_es": "Criptografía", "acronym": None,
     "definition_en": "Practice and study of techniques for secure communication in the presence of adversaries.",
     "definition_es": "Práctica y estudio de técnicas para comunicación segura en presencia de adversarios.",
     "category": "Cryptography", "difficulty": "beginner", "sources": "NIST SP 800-175B"},

    {"id": 92, "term_en": "Encryption", "term_es": "Cifrado", "acronym": None,
     "definition_en": "Process of converting plaintext into ciphertext to prevent unauthorized access.",
     "definition_es": "Proceso de convertir texto plano en texto cifrado para evitar acceso no autorizado.",
     "category": "Cryptography", "difficulty": "beginner", "sources": "NIST FIPS 140-3"},

    {"id": 93, "term_en": "Decryption", "term_es": "Descifrado", "acronym": None,
     "definition_en": "Reverse process of encryption – converting ciphertext back to plaintext.",
     "definition_es": "Proceso inverso al cifrado – convertir texto cifrado a texto plano.",
     "category": "Cryptography", "difficulty": "beginner", "sources": "NIST FIPS 140-3"},

    {"id": 94, "term_en": "Symmetric Encryption", "term_es": "Cifrado Simétrico", "acronym": None,
     "definition_en": "Encryption using the same key for both encryption and decryption (e.g., AES).",
     "definition_es": "Cifrado que usa la misma clave para cifrar y descifrar (ej. AES).",
     "category": "Cryptography", "difficulty": "intermediate", "sources": "NIST SP 800-57"},

    {"id": 95, "term_en": "Asymmetric Encryption", "term_es": "Cifrado Asimétrico", "acronym": None,
     "definition_en": "Encryption using a public/private key pair (e.g., RSA, ECC).",
     "definition_es": "Cifrado que usa un par de claves pública/privada (ej. RSA, ECC).",
     "category": "Cryptography", "difficulty": "intermediate", "sources": "NIST SP 800-57"},

    {"id": 96, "term_en": "Public Key", "term_es": "Clave Pública", "acronym": None,
     "definition_en": "Key that can be freely distributed and is used to encrypt data or verify signatures.",
     "definition_es": "Clave que se puede distribuir libremente y se usa para cifrar datos o verificar firmas.",
     "category": "Cryptography", "difficulty": "intermediate", "sources": "NIST SP 800-57"},

    {"id": 97, "term_en": "Private Key", "term_es": "Clave Privada", "acronym": None,
     "definition_en": "Secret key that must never be shared and is used to decrypt data or create signatures.",
     "definition_es": "Clave secreta que nunca debe compartirse y se usa para descifrar o crear firmas.",
     "category": "Cryptography", "difficulty": "intermediate", "sources": "NIST SP 800-57"},

    {"id": 98, "term_en": "Hash Function", "term_es": "Función Hash", "acronym": None,
     "definition_en": "One-way function that converts input of arbitrary size into fixed-size output.",
     "definition_es": "Función unidireccional que convierte entrada de tamaño arbitrario en salida de tamaño fijo.",
     "category": "Cryptography", "difficulty": "intermediate", "sources": "NIST SP 800-107"},

    {"id": 99, "term_en": "Digital Signature", "term_es": "Firma Digital", "acronym": None,
     "definition_en": "Cryptographic mechanism that provides authenticity, integrity, and non-repudiation.",
     "definition_es": "Mecanismo criptográfico que proporciona autenticidad, integridad y no repudio.",
     "category": "Cryptography", "difficulty": "intermediate", "sources": "NIST SP 800-89"},

    {"id": 100, "term_en": "Digital Certificate", "term_es": "Certificado Digital", "acronym": None,
     "definition_en": "Electronic document used to prove ownership of a public key.",
     "definition_es": "Documento electrónico que prueba la propiedad de una clave pública.",
     "category": "Cryptography", "difficulty": "intermediate", "sources": "NIST SP 800-32 / RFC 5280"},

    {"id": 101, "term_en": "Certificate Authority", "term_es": "Autoridad de Certificación", "acronym": "CA",
     "definition_en": "Trusted entity that issues and revokes digital certificates.",
     "definition_es": "Entidad de confianza que emite y revoca certificados digitales.",
     "category": "Cryptography", "difficulty": "intermediate", "sources": "NIST / IETF"},

    {"id": 102, "term_en": "Public Key Infrastructure", "term_es": "Infraestructura de Clave Pública", "acronym": "PKI",
     "definition_en": "Set of roles, policies, hardware, software, and procedures needed to manage digital certificates.",
     "definition_es": "Conjunto de roles, políticas, hardware, software y procedimientos para gestionar certificados digitales.",
     "category": "Cryptography", "difficulty": "advanced", "sources": "NIST SP 800-32"},

    {"id": 103, "term_en": "Advanced Encryption Standard", "term_es": "Estándar de Cifrado Avanzado", "acronym": "AES",
     "definition_en": "Symmetric block cipher chosen as U.S. federal standard (128/192/256-bit keys).",
     "definition_es": "Cifrado de bloques simétrico elegido como estándar federal de EE.UU. (claves 128/192/256 bits).",
     "category": "Cryptography", "difficulty": "intermediate", "sources": "FIPS 197"},

    {"id": 104, "term_en": "RSA", "term_es": "RSA", "acronym": "RSA",
     "definition_en": "Asymmetric algorithm based on the difficulty of factoring large prime numbers.",
     "definition_es": "Algoritmo asimétrico basado en la dificultad de factorizar grandes números primos.",
     "category": "Cryptography", "difficulty": "advanced", "sources": "RFC 8017"},

    {"id": 105, "term_en": "Elliptic Curve Cryptography", "term_es": "Criptografía de Curva Elíptica", "acronym": "ECC",
     "definition_en": "Public-key cryptography based on elliptic curves over finite fields.",
     "definition_es": "Criptografía de clave pública basada en curvas elípticas sobre campos finitos.",
     "category": "Cryptography", "difficulty": "advanced", "sources": "NIST SP 800-56A"},

    {"id": 106, "term_en": "SHA-256", "term_es": "SHA-256", "acronym": None,
     "definition_en": "Cryptographic hash function producing a 256-bit hash value.",
     "definition_es": "Función hash criptográfica que produce un valor hash de 256 bits.",
     "category": "Cryptography", "difficulty": "intermediate", "sources": "FIPS 180-4"},

    {"id": 107, "term_en": "End-to-End Encryption", "term_es": "Cifrado de Extremo a Extremo", "acronym": "E2EE",
     "definition_en": "Encryption where only the communicating users can read the messages.",
     "definition_es": "Cifrado donde solo los usuarios que se comunican pueden leer los mensajes.",
     "category": "Cryptography", "difficulty": "intermediate", "sources": "Signal Protocol / WhatsApp"},

    {"id": 108, "term_en": "Perfect Forward Secrecy", "term_es": "Secreto Perfecto hacia Adelante", "acronym": "PFS",
     "definition_en": "Property ensuring that session keys are not compromised even if long-term keys are.",
     "definition_es": "Propiedad que garantiza que las claves de sesión no se comprometan aunque se comprometan las claves a largo plazo.",
     "category": "Cryptography", "difficulty": "advanced", "sources": "RFC 7627"},

    {"id": 109, "term_en": "Homomorphic Encryption", "term_es": "Cifrado Homomórfico", "acronym": None,
     "definition_en": "Encryption allowing computation on ciphertext, generating encrypted results.",
     "definition_es": "Cifrado que permite cálculo sobre texto cifrado generando resultados cifrados.",
     "category": "Cryptography", "difficulty": "advanced", "sources": "ENISA / IBM Research"},

    {"id": 110, "term_en": "Post-Quantum Cryptography", "term_es": "Criptografía Post-Cuántica", "acronym": "PQC",
     "definition_en": "Cryptographic algorithms believed to be secure against quantum computer attacks.",
     "definition_es": "Algoritmos criptográficos considerados seguros frente a ataques de computadoras cuánticas.",
     "category": "Cryptography", "difficulty": "advanced", "sources": "NIST PQC Project"},

    {"id": 111, "term_en": "Firewall", "term_es": "Cortafuegos", "acronym": None,
     "definition_en": "Security system that monitors and controls incoming and outgoing network traffic.",
     "definition_es": "Sistema de seguridad que monitorea y controla el tráfico de red entrante y saliente.",
     "category": "Network Security", "difficulty": "beginner", "sources": "NIST SP 800-41"},

    {"id": 112, "term_en": "Next-Generation Firewall", "term_es": "Firewall de Nueva Generación", "acronym": "NGFW",
     "definition_en": "Advanced firewall with application awareness, user identity, and threat prevention.",
     "definition_es": "Firewall avanzado con conocimiento de aplicaciones, identidad de usuario y prevención de amenazas.",
     "category": "Network Security", "difficulty": "intermediate", "sources": "Gartner / Palo Alto Networks"},

    {"id": 113, "term_en": "Intrusion Detection System", "term_es": "Sistema de Detección de Intrusiones", "acronym": "IDS",
     "definition_en": "Device or software that monitors network/system activities for malicious activities.",
     "definition_es": "Dispositivo o software que monitorea actividades de red/sistema en busca de actividades maliciosas.",
     "category": "Network Security", "difficulty": "intermediate", "sources": "NIST SP 800-94"},

    {"id": 114, "term_en": "Intrusion Prevention System", "term_es": "Sistema de Prevención de Intrusiones", "acronym": "IPS",
     "definition_en": "Network security appliance that can detect and block malicious traffic in real-time.",
     "definition_es": "Dispositivo de seguridad que detecta y bloquea tráfico malicioso en tiempo real.",
     "category": "Network Security", "difficulty": "intermediate", "sources": "NIST SP 800-94"},

    {"id": 115, "term_en": "Virtual Private Network", "term_es": "Red Privada Virtual", "acronym": "VPN",
     "definition_en": "Secure tunnel between a user and a private network over the public Internet.",
     "definition_es": "Túnel seguro entre un usuario y una red privada a través de Internet público.",
     "category": "Network Security", "difficulty": "beginner", "sources": "NIST SP 800-77"},

    {"id": 116, "term_en": "Demilitarized Zone", "term_es": "Zona Desmilitarizada", "acronym": "DMZ",
     "definition_en": "Network segment that exposes external-facing services to an untrusted network.",
     "definition_es": "Segmento de red que expone servicios externos a una red no confiable.",
     "category": "Network Security", "difficulty": "intermediate", "sources": "NIST SP 800-41"},

    {"id": 117, "term_en": "Transport Layer Security", "term_es": "Seguridad de la Capa de Transporte", "acronym": "TLS",
     "definition_en": "Cryptographic protocol that provides privacy and data integrity for Internet communications.",
     "definition_es": "Protocolo criptográfico que proporciona privacidad e integridad de datos en comunicaciones de Internet.",
     "category": "Network Security", "difficulty": "intermediate", "sources": "RFC 8446"},

    {"id": 118, "term_en": "HTTPS", "term_es": "HTTPS", "acronym": "HTTPS",
     "definition_en": "HTTP over TLS/SSL – secure version of HTTP.",
     "definition_es": "HTTP sobre TLS/SSL – versión segura de HTTP.",
     "category": "Network Security", "difficulty": "beginner", "sources": "RFC 2818"},

    {"id": 119, "term_en": "IPsec", "term_es": "IPsec", "acronym": "IPsec",
     "definition_en": "Suite of protocols for securing IP communications by authenticating and encrypting each IP packet.",
     "definition_es": "Conjunto de protocolos para asegurar comunicaciones IP autenticando y cifrando cada paquete.",
     "category": "Network Security", "difficulty": "intermediate", "sources": "RFC 4301"},

    {"id": 120, "term_en": "Zero Trust Network Access", "term_es": "Acceso a Red de Confianza Cero", "acronym": "ZTNA",
     "definition_en": "Security model that verifies every access request regardless of location.",
     "definition_es": "Modelo de seguridad que verifica cada solicitud de acceso independientemente de la ubicación.",
     "category": "Network Security", "difficulty": "intermediate", "sources": "NIST SP 800-207"},

    {"id": 121, "term_en": "Software-Defined Perimeter", "term_es": "Perímetro Definido por Software", "acronym": "SDP",
     "definition_en": "Security framework that hides infrastructure and grants access only after authentication.",
     "definition_es": "Marco de seguridad que oculta la infraestructura y concede acceso solo tras autenticación.",
     "category": "Network Security", "difficulty": "advanced", "sources": "Cloud Security Alliance"},

    {"id": 122, "term_en": "Network Segmentation", "term_es": "Segmentación de Red", "acronym": None,
     "definition_en": "Practice of dividing a network into subnetworks to improve performance and security.",
     "definition_es": "Práctica de dividir una red en subredes para mejorar rendimiento y seguridad.",
     "category": "Network Security", "difficulty": "intermediate", "sources": "NIST SP 800-207"},

    {"id": 123, "term_en": "Microsegmentation", "term_es": "Microsegmentación", "acronym": None,
     "definition_en": "Granular network segmentation, often at the workload or application level.",
     "definition_es": "Segmentación granular de red, a menudo a nivel de carga de trabajo o aplicación.",
     "category": "Network Security", "difficulty": "advanced", "sources": "VMware NSX / Gartner"},

    {"id": 124, "term_en": "DNS Security Extensions", "term_es": "Extensiones de Seguridad DNS", "acronym": "DNSSEC",
     "definition_en": "Suite of extensions that add data origin authentication and data integrity to DNS.",
     "definition_es": "Conjunto de extensiones que añaden autenticación de origen e integridad a DNS.",
     "category": "Network Security", "difficulty": "intermediate", "sources": "RFC 4033-4035"},

    {"id": 125, "term_en": "Secure Access Service Edge", "term_es": "Borde de Servicio de Acceso Seguro", "acronym": "SASE",
     "definition_en": "Cloud-native architecture combining network security functions with WAN capabilities.",
     "definition_es": "Arquitectura nativa en la nube que combina funciones de seguridad de red con capacidades WAN.",
     "category": "Network Security", "difficulty": "advanced", "sources": "Gartner"},

    {"id": 126, "term_en": "Web Application Firewall", "term_es": "Firewall de Aplicaciones Web", "acronym": "WAF",
     "definition_en": "Security solution that monitors and blocks HTTP traffic to and from a web application.",
     "definition_es": "Solución de seguridad que monitorea y bloquea tráfico HTTP hacia/desde aplicaciones web.",
     "category": "Network Security", "difficulty": "intermediate", "sources": "OWASP / NIST"},

    {"id": 127, "term_en": "SIEM", "term_es": "SIEM", "acronym": "SIEM",
     "definition_en": "Security Information and Event Management – tool for real-time analysis of security alerts.",
     "definition_es": "Gestión de Información y Eventos de Seguridad – herramienta para análisis en tiempo real de alertas.",
     "category": "Network Security", "difficulty": "intermediate", "sources": "Gartner / NIST"},

    {"id": 128, "term_en": "SOAR", "term_es": "SOAR", "acronym": "SOAR",
     "definition_en": "Security Orchestration, Automation and Response – platform that automates security operations.",
     "definition_es": "Orquestación, Automatización y Respuesta de Seguridad – plataforma que automatiza operaciones de seguridad.",
     "category": "Network Security", "difficulty": "intermediate", "sources": "Gartner"},

    {"id": 129, "term_en": "XDR", "term_es": "XDR", "acronym": "XDR",
     "definition_en": "Extended Detection and Response – unified security platform across endpoints, network, cloud.",
     "definition_es": "Detección y Respuesta Extendida – plataforma unificada de seguridad entre endpoints, red y nube.",
     "category": "Network Security", "difficulty": "intermediate", "sources": "Palo Alto Networks / Gartner"},

    {"id": 130, "term_en": "Threat Intelligence", "term_es": "Inteligencia de Amenazas", "acronym": None,
     "definition_en": "Evidence-based knowledge about existing or emerging threats to assets.",
     "definition_es": "Conocimiento basado en evidencia sobre amenazas existentes o emergentes a activos.",
     "category": "Network Security", "difficulty": "intermediate", "sources": "ENISA / MITRE"},
    
    {"id": 121, "term_en": "Software-Defined Perimeter", "term_es": "Perímetro Definido por Software", "acronym": "SDP",
     "definition_en": "Security framework that hides infrastructure and grants access only after authentication and authorization.",
     "definition_es": "Marco de seguridad que oculta la infraestructura y concede acceso solo tras autenticación y autorización.",
     "category": "Network Security", "difficulty": "advanced", "sources": "Cloud Security Alliance / NIST"},

    {"id": 122, "term_en": "Network Segmentation", "term_es": "Segmentación de Red", "acronym": None,
     "definition_en": "Practice of dividing a network into subnetworks to improve performance and security.",
     "definition_es": "Práctica de dividir una red en subredes para mejorar rendimiento y seguridad.",
     "category": "Network Security", "difficulty": "intermediate", "sources": "NIST SP 800-207 / SANS"},

    {"id": 123, "term_en": "Microsegmentation", "term_es": "Microsegmentación", "acronym": None,
     "definition_en": "Granular network segmentation applied at the workload or application level.",
     "definition_es": "Segmentación granular de red aplicada a nivel de carga de trabajo o aplicación.",
     "category": "Network Security", "difficulty": "advanced", "sources": "NIST SP 800-207 / VMware"},

    {"id": 124, "term_en": "DNS Security Extensions", "term_es": "Extensiones de Seguridad DNS", "acronym": "DNSSEC",
     "definition_en": "Suite of extensions that add origin authentication and data integrity to DNS.",
     "definition_es": "Conjunto de extensiones que añaden autenticación de origen e integridad de datos a DNS.",
     "category": "Network Security", "difficulty": "intermediate", "sources": "RFC 4033–4035 / ICANN"},

    {"id": 125, "term_en": "Secure Access Service Edge", "term_es": "Borde de Servicio de Acceso Seguro", "acronym": "SASE",
     "definition_en": "Converged cloud-native architecture combining networking and security functions.",
     "definition_es": "Arquitectura convergente nativa en la nube que combina funciones de red y seguridad.",
     "category": "Network Security", "difficulty": "advanced", "sources": "Gartner 2019 / NIST"},

    {"id": 126, "term_en": "Web Application Firewall", "term_es": "Firewall de Aplicaciones Web", "acronym": "WAF",
     "definition_en": "Security solution that monitors, filters, and blocks HTTP traffic to/from web applications.",
     "definition_es": "Solución que monitorea, filtra y bloquea tráfico HTTP hacia/desde aplicaciones web.",
     "category": "Network Security", "difficulty": "intermediate", "sources": "OWASP / NIST SP 800-94"},

    {"id": 127, "term_en": "SIEM", "term_es": "SIEM", "acronym": "SIEM",
     "definition_en": "Security Information and Event Management – centralized logging and real-time analysis platform.",
     "definition_es": "Gestión de Información y Eventos de Seguridad – plataforma centralizada de logs y análisis en tiempo real.",
     "category": "Network Security", "difficulty": "intermediate", "sources": "Gartner / NIST SP 800-137"},

    {"id": 128, "term_en": "SOAR", "term_es": "SOAR", "acronym": "SOAR",
     "definition_en": "Security Orchestration, Automation and Response – automates and orchestrates security processes.",
     "definition_es": "Orquestación, Automatización y Respuesta de Seguridad – automatiza y orquesta procesos de seguridad.",
     "category": "Network Security", "difficulty": "intermediate", "sources": "Gartner"},

    {"id": 129, "term_en": "XDR", "term_es": "XDR", "acronym": "XDR",
     "definition_en": "Extended Detection and Response – integrated platform across endpoints, network, cloud, and email.",
     "definition_es": "Detección y Respuesta Extendida – plataforma integrada entre endpoints, red, nube y correo.",
     "category": "Network Security", "difficulty": "intermediate", "sources": "Gartner / Palo Alto Networks"},

    {"id": 130, "term_en": "Threat Intelligence", "term_es": "Inteligencia de Amenazas", "acronym": None,
     "definition_en": "Evidence-based knowledge about threats used to inform defensive measures.",
     "definition_es": "Conocimiento basado en evidencia sobre amenazas usado para informar medidas defensivas.",
     "category": "Network Security", "difficulty": "intermediate", "sources": "ENISA / MITRE ATT&CK"},

    {"id": 131, "term_en": "Vulnerability Scanning", "term_es": "Escaneo de Vulnerabilidades", "acronym": None,
     "definition_en": "Automated process of identifying security weaknesses in systems and applications.",
     "definition_es": "Proceso automatizado de identificación de debilidades de seguridad en sistemas y aplicaciones.",
     "category": "Vulnerability Management", "difficulty": "intermediate", "sources": "NIST SP 800-115"},

    {"id": 132, "term_en": "Patch Management", "term_es": "Gestión de Parches", "acronym": None,
     "definition_en": "Process of acquiring, testing, and installing code changes to fix vulnerabilities.",
     "definition_es": "Proceso de adquisición, prueba e instalación de cambios de código para corregir vulnerabilidades.",
     "category": "Vulnerability Management", "difficulty": "intermediate", "sources": "NIST SP 800-40 Rev.4"},

    {"id": 133, "term_en": "CVE", "term_es": "CVE", "acronym": "CVE",
     "definition_en": "Common Vulnerabilities and Exposures – public list of known cybersecurity vulnerabilities.",
     "definition_es": "Vulnerabilidades y Exposiciones Comunes – lista pública de vulnerabilidades de ciberseguridad conocidas.",
     "category": "Vulnerability Management", "difficulty": "beginner", "sources": "MITRE / cve.org"},

    {"id": 134, "term_en": "CVSS", "term_es": "CVSS", "acronym": "CVSS",
     "definition_en": "Common Vulnerability Scoring System – standardized method to rate vulnerability severity.",
     "definition_es": "Sistema Común de Puntuación de Vulnerabilidades – método estandarizado para calificar gravedad.",
     "category": "Vulnerability Management", "difficulty": "intermediate", "sources": "FIRST.org"},

    {"id": 135, "term_en": "Penetration Testing", "term_es": "Prueba de Penetración", "acronym": "Pentest",
     "definition_en": "Authorized simulated attack on a system to evaluate its security.",
     "definition_es": "Ataque simulado autorizado a un sistema para evaluar su seguridad.",
     "category": "Vulnerability Management", "difficulty": "intermediate", "sources": "NIST SP 800-115 / OWASP"},

    {"id": 136, "term_en": "Red Teaming", "term_es": "Red Teaming", "acronym": None,
     "definition_en": "Advanced adversary simulation that tests an organization’s overall security posture.",
     "definition_es": "Simulación avanzada de adversario que prueba la postura de seguridad global de una organización.",
     "category": "Vulnerability Management", "difficulty": "advanced", "sources": "MITRE / SANS"},

    {"id": 137, "term_en": "Bug Bounty", "term_es": "Programa de Recompensas por Errores", "acronym": None,
     "definition_en": "Program that rewards ethical hackers for discovering and reporting vulnerabilities.",
     "definition_es": "Programa que recompensa a hackers éticos por descubrir e informar vulnerabilidades.",
     "category": "Vulnerability Management", "difficulty": "intermediate", "sources": "HackerOne / Bugcrowd"},

    {"id": 138, "term_en": "Incident Response", "term_es": "Respuesta a Incidentes", "acronym": "IR",
     "definition_en": "Organized approach to addressing and managing the aftermath of a security breach.",
     "definition_es": "Enfoque organizado para abordar y gestionar las consecuencias de una brecha de seguridad.",
     "category": "Incident Response", "difficulty": "intermediate", "sources": "NIST SP 800-61 Rev.2"},

    {"id": 139, "term_en": "Incident", "term_es": "Incidente", "acronym": None,
     "definition_en": "Security event that compromises CIA of an asset or violates policy.",
     "definition_es": "Evento de seguridad que compromete la CIA de un activo o viola políticas.",
     "category": "Incident Response", "difficulty": "beginner", "sources": "NIST SP 800-61"},

    {"id": 140, "term_en": "Breach", "term_es": "Brecha", "acronym": None,
     "definition_en": "Confirmed incident in which sensitive data has been accessed or exfiltrated.",
     "definition_es": "Incidente confirmado en el que datos sensibles han sido accedidos o exfiltrados.",
     "category": "Incident Response", "difficulty": "beginner", "sources": "GDPR / CISA"},

    {"id": 141, "term_en": "Containment", "term_es": "Contención", "acronym": None,
     "definition_en": "Phase of incident response focused on stopping the spread of an attack.",
     "definition_es": "Fase de respuesta a incidentes enfocada en detener la propagación del ataque.",
     "category": "Incident Response", "difficulty": "intermediate", "sources": "NIST SP 800-61"},

    {"id": 142, "term_en": "Eradication", "term_es": "Erradicación", "acronym": None,
     "definition_en": "Phase where the root cause and all traces of the attacker are removed.",
     "definition_es": "Fase donde se elimina la causa raíz y todos los rastros del atacante.",
     "category": "Incident Response", "difficulty": "intermediate", "sources": "NIST SP 800-61"},

    {"id": 143, "term_en": "Recovery", "term_es": "Recuperación", "acronym": None,
     "definition_en": "Phase of restoring and returning affected systems to normal operation.",
     "definition_es": "Fase de restauración y retorno de sistemas afectados a operación normal.",
     "category": "Incident Response", "difficulty": "intermediate", "sources": "NIST SP 800-61"},

    {"id": 144, "term_en": "Digital Forensics", "term_es": "Forense Digital", "acronym": None,
     "definition_en": "Application of science to identify, preserve, recover, analyze digital evidence.",
     "definition_es": "Aplicación de la ciencia para identificar, preservar, recuperar y analizar evidencia digital.",
     "category": "Incident Response", "difficulty": "advanced", "sources": "NIST SP 800-86"},

    {"id": 145, "term_en": "Chain of Custody", "term_es": "Cadena de Custodia", "acronym": None,
     "definition_en": "Documented process ensuring evidence integrity from collection to presentation.",
     "definition_es": "Proceso documentado que garantiza la integridad de la evidencia desde su recolección hasta presentación.",
     "category": "Incident Response", "difficulty": "intermediate", "sources": "NIST SP 800-86"},

    {"id": 146, "term_en": "Memory Forensics", "term_es": "Forense de Memoria", "acronym": None,
     "definition_en": "Analysis of volatile data in a computer's memory dump.",
     "definition_es": "Análisis de datos volátiles en un volcado de memoria de computadora.",
     "category": "Incident Response", "difficulty": "advanced", "sources": "SANS FOR508"},

    {"id": 147, "term_en": "Timeline Analysis", "term_es": "Análisis de Línea de Tiempo", "acronym": None,
     "definition_en": "Reconstruction of events based on filesystem and log timestamps.",
     "definition_es": "Reconstrucción de eventos basada en marcas de tiempo de sistema de archivos y logs.",
     "category": "Incident Response", "difficulty": "advanced", "sources": "SANS FOR508"},

    {"id": 148, "term_en": "Ransomware Negotiation", "term_es": "Negociación con Ransomware", "acronym": None,
     "definition_en": "Controlled communication with attackers to reduce ransom or buy time.",
     "definition_es": "Comunicación controlada con atacantes para reducir rescate o ganar tiempo.",
     "category": "Incident Response", "difficulty": "advanced", "sources": "Coveware / FBI"},

    {"id": 149, "term_en": "Tabletop Exercise", "term_es": "Ejercicio de Mesa", "acronym": None,
     "definition_en": "Discussion-based simulation of an incident response scenario.",
     "definition_es": "Simulación basada en discusión de un escenario de respuesta a incidentes.",
     "category": "Incident Response", "difficulty": "intermediate", "sources": "NIST SP 800-84"},

    {"id": 150, "term_en": "Purple Teaming", "term_es": "Equipo Púrpura", "acronym": None,
     "definition_en": "Collaborative exercise combining red and blue teams to improve detection and response.",
     "definition_es": "Ejercicio colaborativo que combina equipos rojo y azul para mejorar detección y respuesta.",
     "category": "Incident Response", "difficulty": "intermediate", "sources": "SANS / MITRE"},

    {"id": 151, "term_en": "Cloud Security", "term_es": "Seguridad en la Nube", "acronym": None,
     "definition_en": "Set of policies, controls, and technologies to protect cloud-based systems and data.",
     "definition_es": "Conjunto de políticas, controles y tecnologías para proteger sistemas y datos en la nube.",
     "category": "Cloud Security", "difficulty": "intermediate", "sources": "CSA CCM / NIST"},

    {"id": 152, "term_en": "Shared Responsibility Model", "term_es": "Modelo de Responsabilidad Compartida", "acronym": None,
     "definition_en": "Division of security responsibilities between cloud provider and customer.",
     "definition_es": "División de responsabilidades de seguridad entre proveedor de nube y cliente.",
     "category": "Cloud Security", "difficulty": "beginner", "sources": "AWS / Azure / Google Cloud"},

    {"id": 153, "term_en": "CASB", "term_es": "CASB", "acronym": "CASB",
     "definition_en": "Cloud Access Security Broker – security policy enforcement point between users and cloud services.",
     "definition_es": "Intermediario de Seguridad de Acceso a la Nube – punto de aplicación de políticas entre usuarios y servicios en la nube.",
     "category": "Cloud Security", "difficulty": "intermediate", "sources": "Gartner"},

    {"id": 154, "term_en": "Serverless Security", "term_es": "Seguridad Serverless", "acronym": None,
     "definition_en": "Security considerations for functions-as-a-service (FaaS) environments.",
     "definition_es": "Consideraciones de seguridad para entornos de funciones como servicio (FaaS).",
     "category": "Cloud Security", "difficulty": "advanced", "sources": "OWASP / OWASP Serverless Top 10"},

    {"id": 155, "term_en": "Container Security", "term_es": "Seguridad de Contenedores", "acronym": None,
     "definition_en": "Practices to secure containerized applications (Docker, Kubernetes).",
     "definition_es": "Prácticas para asegurar aplicaciones contenerizadas (Docker, Kubernetes).",
     "category": "Cloud Security", "difficulty": "intermediate", "sources": "NIST SP 800-190"},

    {"id": 156, "term_en": "Kubernetes Security", "term_es": "Seguridad de Kubernetes", "acronym": None,
     "definition_en": "Best practices for securing Kubernetes clusters and workloads.",
     "definition_es": "Mejores prácticas para asegurar clústeres y cargas de trabajo de Kubernetes.",
     "category": "Cloud Security", "difficulty": "advanced", "sources": "CNCF / NSA-CISA Kubernetes Hardening Guide"},

    {"id": 157, "term_en": "IoT Security", "term_es": "Seguridad IoT", "acronym": None,
     "definition_en": "Security challenges and controls for Internet of Things devices and ecosystems.",
     "definition_es": "Desafíos y controles de seguridad para dispositivos y ecosistemas del Internet de las Cosas.",
     "category": "Cloud Security", "difficulty": "intermediate", "sources": "ENISA / OWASP IoT Project"},

    {"id": 158, "term_en": "OT Security", "term_es": "Seguridad OT", "acronym": "OT",
     "definition_en": "Operational Technology security – protecting industrial control systems (ICS, SCADA).",
     "definition_es": "Seguridad de Tecnología Operativa – protección de sistemas de control industrial (ICS, SCADA).",
     "category": "Cloud Security", "difficulty": "intermediate", "sources": "NIST SP 800-82"},

    {"id": 159, "term_en": "AI Security", "term_es": "Seguridad de IA", "acronym": None,
     "definition_en": "Security of artificial intelligence systems against attacks like data poisoning or model theft.",
     "definition_es": "Seguridad de sistemas de inteligencia artificial contra ataques como envenenamiento de datos o robo de modelo.",
     "category": "Cloud Security", "difficulty": "advanced", "sources": "MITRE ATLAS / ENISA"},

    {"id": 160, "term_en": "Adversarial Machine Learning", "term_es": "Aprendizaje Automático Adversario", "acronym": None,
     "definition_en": "Attacks that manipulate AI/ML models by feeding them malicious inputs.",
     "definition_es": "Ataques que manipulan modelos de IA/ML alimentándolos con entradas maliciosas.",
     "category": "Cloud Security", "difficulty": "advanced", "sources": "MITRE ATLAS"},

    {"id": 161, "term_en": "Deepfake", "term_es": "Deepfake", "acronym": None,
     "definition_en": "Synthetic media where a person’s likeness is replaced using artificial neural networks.",
     "definition_es": "Medio sintético donde la apariencia de una persona se reemplaza usando redes neuronales artificiales.",
     "category": "Threats", "difficulty": "intermediate", "sources": "NIST IR 8356"},

    {"id": 162, "term_en": "Blockchain Security", "term_es": "Seguridad Blockchain", "acronym": None,
     "definition_en": "Security considerations for blockchain networks and smart contracts.",
     "definition_es": "Consideraciones de seguridad para redes blockchain y contratos inteligentes.",
     "category": "Cloud Security", "difficulty": "advanced", "sources": "OWASP / ConsenSys"},

    {"id": 163, "term_en": "Smart Contract Vulnerability", "term_es": "Vulnerabilidad en Contrato Inteligente", "acronym": None,
     "definition_en": "Security flaw in blockchain smart contract code that can be exploited.",
     "definition_es": "Fallo de seguridad en código de contrato inteligente que puede ser explotado.",
     "category": "Cloud Security", "difficulty": "advanced", "sources": "ConsenSys Diligence"},

    {"id": 164, "term_en": "Zero-Knowledge Proof", "term_es": "Prueba de Conocimiento Cero", "acronym": "ZKP",
     "definition_en": "Cryptographic method allowing one party to prove knowledge without revealing the information.",
     "definition_es": "Método criptográfico que permite probar conocimiento sin revelar la información.",
     "category": "Cryptography", "difficulty": "advanced", "sources": "Zcash / Ethereum"},

    {"id": 165, "term_en": "Homomorphic Encryption", "term_es": "Cifrado Homomórfico", "acronym": None,
     "definition_en": "Encryption allowing computation on encrypted data without decrypting it.",
     "definition_es": "Cifrado que permite cálculo sobre datos cifrados sin descifrarlos.",
     "category": "Cryptography", "difficulty": "advanced", "sources": "ENISA / IBM"},

    {"id": 166, "term_en": "Quantum Key Distribution", "term_es": "Distribución Cuántica de Claves", "acronym": "QKD",
     "definition_en": "Method to securely distribute encryption keys using quantum mechanics.",
     "definition_es": "Método para distribuir claves de cifrado de forma segura usando mecánica cuántica.",
     "category": "Cryptography", "difficulty": "advanced", "sources": "NIST / ETSI"},

    {"id": 167, "term_en": "Side-Channel Attack", "term_es": "Ataque por Canal Lateral", "acronym": None,
     "definition_en": "Attack based on information gained from physical implementation (timing, power consumption).",
     "definition_es": "Ataque basado en información obtenida de la implementación física (tiempo, consumo de energía).",
     "category": "Cryptography", "difficulty": "advanced", "sources": "NIST / Kocher 1996"},

    {"id": 168, "term_en": "Meltdown", "term_es": "Meltdown", "acronym": None,
     "definition_en": "Hardware vulnerability allowing unauthorized reading of kernel memory.",
     "definition_es": "Vulnerabilidad hardware que permite lectura no autorizada de memoria del kernel.",
     "category": "Threats", "difficulty": "advanced", "sources": "MeltdownAttack.com"},

    {"id": 169, "term_en": "Spectre", "term_es": "Spectre", "acronym": None,
     "definition_en": "Class of speculative execution vulnerabilities in modern processors.",
     "definition_es": "Clase de vulnerabilidades de ejecución especulativa en procesadores modernos.",
     "category": "Threats", "difficulty": "advanced", "sources": "SpectreAttack.com"},

    {"id": 170, "term_en": "Rowhammer", "term_es": "Rowhammer", "acronym": None,
     "definition_en": "Hardware vulnerability where repeated memory access can flip bits in adjacent rows.",
     "definition_es": "Vulnerabilidad hardware donde acceso repetido a memoria puede invertir bits en filas adyacentes.",
     "category": "Threats", "difficulty": "advanced", "sources": "Google Project Zero"},
    
    {"id": 170, "term_en": "Rowhammer", "term_es": "Rowhammer", "acronym": None,
     "definition_en": "Hardware vulnerability where repeated memory access can flip bits in adjacent rows.",
     "definition_es": "Vulnerabilidad hardware donde acceso repetido a memoria puede invertir bits en filas adyacentes.",
     "category": "Threats", "difficulty": "advanced", "sources": "Google Project Zero / CVE-2015-0565"},

    {"id": 171, "term_en": "Log4Shell", "term_es": "Log4Shell", "acronym": None,
     "definition_en": "Critical remote code execution vulnerability in Apache Log4j 2 (CVE-2021-44228).",
     "definition_es": "Vulnerabilidad crítica de ejecución remota de código en Apache Log4j 2 (CVE-2021-44228).",
     "category": "Threats", "difficulty": "intermediate", "sources": "CVE-2021-44228 / CISA"},

    {"id": 172, "term_en": "ProxyLogon", "term_es": "ProxyLogon", "acronym": None,
     "definition_en": "Chain of vulnerabilities in Microsoft Exchange Server exploited in 2021 (CVE-2021-26855 etc.).",
     "definition_es": "Cadena de vulnerabilidades en Microsoft Exchange Server explotadas en 2021 (CVE-2021-26855 y otras).",
     "category": "Threats", "difficulty": "intermediate", "sources": "Microsoft / CISA"},

    {"id": 173, "term_en": "SolarWinds Supply Chain Attack", "term_es": "Ataque Cadena de Suministro SolarWinds", "acronym": None,
     "definition_en": "State-sponsored supply chain attack via compromised Orion software updates (2020).",
     "definition_es": "Ataque patrocinado por estado a través de actualizaciones comprometidas de software Orion (2020).",
     "category": "Threats", "difficulty": "advanced", "sources": "FireEye / Microsoft / CISA"},

    {"id": 174, "term_en": "Kaseya VSA Attack", "term_es": "Ataque Kaseya VSA", "acronym": None,
     "definition_en": "REvil ransomware supply chain attack via Kaseya VSA patch (July 2021).",
     "definition_es": "Ataque ransomware REvil a través de parche de Kaseya VSA (julio 2021).",
     "category": "Threats", "difficulty": "intermediate", "sources": "CISA / REvil"},

    {"id": 175, "term_en": "Business Email Compromise", "term_es": "Compromiso de Correo Empresarial", "acronym": "BEC",
     "definition_en": "Sophisticated scam targeting organizations via email to conduct unauthorized transfers.",
     "definition_es": "Estafa sofisticada dirigida a organizaciones vía correo para realizar transferencias no autorizadas.",
     "category": "Threats", "difficulty": "intermediate", "sources": "FBI / INCIBE"},

    {"id": 176, "term_en": "CEO Fraud", "term_es": "Fraude del CEO", "acronym": None,
     "definition_en": "Type of BEC where attacker impersonates an executive to request urgent payments.",
     "definition_es": "Tipo de BEC donde el atacante se hace pasar por un directivo para solicitar pagos urgentes.",
     "category": "Threats", "difficulty": "beginner", "sources": "INCIBE / FBI"},

    {"id": 177, "term_en": "Cryptojacking", "term_es": "Cryptojacking", "acronym": None,
     "definition_en": "Unauthorized use of victim's computing resources to mine cryptocurrency.",
     "definition_es": "Uso no autorizado de recursos informáticos de la víctima para minar criptomonedas.",
     "category": "Threats", "difficulty": "intermediate", "sources": "MITRE ATT&CK T1496 / CISA"},

    {"id": 178, "term_en": "Typosquatting", "term_es": "Typosquatting", "acronym": None,
     "definition_en": "Registering domains with intentional misspellings of popular domains to deceive users.",
     "definition_es": "Registro de dominios con errores tipográficos intencionales de dominios populares para engañar usuarios.",
     "category": "Threats", "difficulty": "intermediate", "sources": "ENISA / Microsoft"},

    {"id": 179, "term_en": "Domain Generation Algorithm", "term_es": "Algoritmo de Generación de Dominios", "acronym": "DGA",
     "definition_en": "Technique used by malware to generate numerous domain names for C2 communication.",
     "definition_es": "Técnica usada por malware para generar numerosos nombres de dominio para comunicación C2.",
     "category": "Threats", "difficulty": "advanced", "sources": "MITRE ATT&CK T1483"},

    {"id": 180, "term_en": "Fast Flux", "term_es": "Fast Flux", "acronym": None,
     "definition_en": "Technique to hide malicious servers by rapidly changing DNS records.",
     "definition_es": "Técnica para ocultar servidores maliciosos cambiando rápidamente registros DNS.",
     "category": "Threats", "difficulty": "advanced", "sources": "ICANN / SANS"},

    {"id": 181, "term_en": "Governance, Risk and Compliance", "term_es": "Gobierno, Riesgo y Cumplimiento", "acronym": "GRC",
     "definition_en": "Integrated approach to align IT with business objectives while managing risk and meeting regulations.",
     "definition_es": "Enfoque integrado para alinear TI con objetivos de negocio gestionando riesgo y cumpliendo regulaciones.",
     "category": "GRC", "difficulty": "intermediate", "sources": "ISACA / OCEG"},

    {"id": 182, "term_en": "ISO 27001", "term_es": "ISO 27001", "acronym": None,
     "definition_en": "International standard for information security management systems (ISMS).",
     "definition_es": "Estándar internacional para sistemas de gestión de seguridad de la información (SGSI).",
     "category": "GRC", "difficulty": "intermediate", "sources": "ISO/IEC 27001:2022"},

    {"id": 183, "term_en": "NIST Cybersecurity Framework", "term_es": "Marco de Ciberseguridad NIST", "acronym": "CSF",
     "definition_en": "Voluntary framework with standards, guidelines, and practices to manage cybersecurity risk.",
     "definition_es": "Marco voluntario con estándares, directrices y prácticas para gestionar riesgo de ciberseguridad.",
     "category": "GRC", "difficulty": "intermediate", "sources": "NIST CSF v1.1 / v2.0"},

    {"id": 184, "term_en": "GDPR", "term_es": "RGPD", "acronym": "GDPR",
     "definition_en": "General Data Protection Regulation – EU regulation on data protection and privacy.",
     "definition_es": "Reglamento General de Protección de Datos – regulación europea sobre protección y privacidad de datos.",
     "category": "GRC", "difficulty": "intermediate", "sources": "Regulation (EU) 2016/679"},

    {"id": 185, "term_en": "CCPA", "term_es": "CCPA", "acronym": "CCPA",
     "definition_en": "California Consumer Privacy Act – state statute enhancing privacy rights.",
     "definition_es": "Ley de Privacidad del Consumidor de California – estatuto que mejora derechos de privacidad.",
     "category": "GRC", "difficulty": "intermediate", "sources": "California Civil Code"},

    {"id": 186, "term_en": "HIPAA", "term_es": "HIPAA", "acronym": "HIPAA",
     "definition_en": "Health Insurance Portability and Accountability Act – U.S. law for health data protection.",
     "definition_es": "Ley de Portabilidad y Responsabilidad de Seguro Médico – ley estadounidense para protección de datos sanitarios.",
     "category": "GRC", "difficulty": "intermediate", "sources": "U.S. Congress 1996"},

    {"id": 187, "term_en": "PCI DSS", "term_es": "PCI DSS", "acronym": "PCI DSS",
     "definition_en": "Payment Card Industry Data Security Standard – security standard for cardholder data.",
     "definition_es": "Estándar de Seguridad de Datos de la Industria de Tarjetas de Pago – estándar para datos de titulares de tarjetas.",
     "category": "GRC", "difficulty": "intermediate", "sources": "PCI SSC v4.0"},

    {"id": 188, "term_en": "SOC 2", "term_es": "SOC 2", "acronym": "SOC 2",
     "definition_en": "Service Organization Control 2 – report on controls at service organizations (Trust Services Criteria).",
     "definition_es": "Control de Organización de Servicio 2 – informe sobre controles en organizaciones de servicio (Criterios de Servicios de Confianza).",
     "category": "GRC", "difficulty": "intermediate", "sources": "AICPA"},

    {"id": 189, "term_en": "Risk Appetite", "term_es": "Apetito de Riesgo", "acronym": None,
     "definition_en": "Amount and type of risk an organization is willing to accept.",
     "definition_es": "Cantidad y tipo de riesgo que una organización está dispuesta a aceptar.",
     "category": "GRC", "difficulty": "intermediate", "sources": "ISO 31000 / COSO"},

    {"id": 190, "term_en": "Risk Tolerance", "term_es": "Tolerancia al Riesgo", "acronym": None,
     "definition_en": "Acceptable level of variation relative to achievement of objectives.",
     "definition_es": "Nivel aceptable de variación respecto al logro de objetivos.",
     "category": "GRC", "difficulty": "intermediate", "sources": "COSO ERM"},

    {"id": 191, "term_en": "Physical Security", "term_es": "Seguridad Física", "acronym": None,
     "definition_en": "Protection of personnel, hardware, software, networks from physical actions and events.",
     "definition_es": "Protección de personal, hardware, software y redes contra acciones y eventos físicos.",
     "category": "Physical Security", "difficulty": "beginner", "sources": "NIST SP 800-53 PE family"},

    {"id": 192, "term_en": "Tailgating", "term_es": "Cola de Cerdo", "acronym": None,
     "definition_en": "Unauthorized person following an authorized person into a restricted area.",
     "definition_es": "Persona no autorizada que sigue a una persona autorizada para entrar en área restringida.",
     "category": "Physical Security", "difficulty": "beginner", "sources": "INCIBE / SANS"},

    {"id": 193, "term_en": "Security Awareness Training", "term_es": "Formación en Concienciación de Seguridad", "acronym": None,
     "definition_en": "Educational program to teach employees about security risks and best practices.",
     "definition_es": "Programa educativo para enseñar a empleados sobre riesgos de seguridad y mejores prácticas.",
     "category": "Human Security", "difficulty": "beginner", "sources": "NIST SP 800-50"},

    {"id": 194, "term_en": "Phishing Simulation", "term_es": "Simulación de Phishing", "acronym": None,
     "definition_en": "Controlled phishing campaigns to test and improve employee awareness.",
     "definition_es": "Campañas controladas de phishing para probar y mejorar concienciación de empleados.",
     "category": "Human Security", "difficulty": "intermediate", "sources": "KnowBe4 / Proofpoint"},

    {"id": 195, "term_en": "Insider Threat Program", "term_es": "Programa de Amenaza Interna", "acronym": None,
     "definition_en": "Framework to detect, prevent, and respond to threats from within the organization.",
     "definition_es": "Marco para detectar, prevenir y responder a amenazas provenientes del interior de la organización.",
     "category": "Human Security", "difficulty": "intermediate", "sources": "CISA Insider Threat Mitigation Guide"},

    {"id": 196, "term_en": "Endpoint Detection and Response", "term_es": "Detección y Respuesta en Endpoints", "acronym": "EDR",
     "definition_en": "Security solution that continuously monitors endpoints to detect and respond to threats.",
     "definition_es": "Solución de seguridad que monitorea continuamente endpoints para detectar y responder a amenazas.",
     "category": "Network Security", "difficulty": "intermediate", "sources": "Gartner / CrowdStrike"},

    {"id": 197, "term_en": "Managed Detection and Response", "term_es": "Detección y Respuesta Gestionada", "acronym": "MDR",
     "definition_en": "Outsourced cybersecurity service providing 24/7 threat detection and response.",
     "definition_es": "Servicio de ciberseguridad externalizado que ofrece detección y respuesta de amenazas 24/7.",
     "category": "Network Security", "difficulty": "intermediate", "sources": "Gartner"},

    {"id": 198, "term_en": "Deception Technology", "term_es": "Tecnología de Engaño", "acronym": None,
     "definition_en": "Security tools that use decoys (honeypots, honeytokens) to detect attackers.",
     "definition_es": "Herramientas que usan señuelos (honeypots, honeytokens) para detectar atacantes.",
     "category": "Network Security", "difficulty": "advanced", "sources": "Gartner / Attivo Networks"},

    {"id": 199, "term_en": "Data Loss Prevention", "term_es": "Prevención de Pérdida de Datos", "acronym": "DLP",
     "definition_en": "Strategy and tools to prevent sensitive data from leaving the organization.",
     "definition_es": "Estrategia y herramientas para evitar que datos sensibles salgan de la organización.",
     "category": "Network Security", "difficulty": "intermediate", "sources": "NIST SP 800-53 SC-28"},

    {"id": 200, "term_en": "Sandboxing", "term_es": "Sandboxing", "acronym": None,
     "definition_en": "Isolated environment to safely execute and analyze suspicious files or code.",
     "definition_es": "Entorno aislado para ejecutar y analizar archivos o código sospechosos de forma segura.",
     "category": "Network Security", "difficulty": "intermediate", "sources": "NIST / Cuckoo Sandbox"},

    {"id": 201, "term_en": "YARA", "term_es": "YARA", "acronym": "YARA",
     "definition_en": "Tool for identifying and classifying malware based on textual or binary patterns.",
     "definition_es": "Herramienta para identificar y clasificar malware basada en patrones textuales o binarios.",
     "category": "Forensics", "difficulty": "advanced", "sources": "VirusTotal / Google"},

    {"id": 202, "term_en": "STIX/TAXII", "term_es": "STIX/TAXII", "acronym": None,
     "definition_en": "Standards for exchanging cyber threat intelligence (Structured Threat Information eXpression / Trusted Automated eXchange of Indicator Information).",
     "definition_es": "Estándares para intercambio de inteligencia de amenazas (Structured Threat Information eXpression / Trusted Automated eXchange).",
     "category": "Threat Intelligence", "difficulty": "advanced", "sources": "OASIS"},

    {"id": 203, "term_en": "MITRE ATT&CK Navigator", "term_es": "MITRE ATT&CK Navigator", "acronym": None,
     "definition_en": "Web tool to explore and visualize the MITRE ATT&CK knowledge base.",
     "definition_es": "Herramienta web para explorar y visualizar la base de conocimiento MITRE ATT&CK.",
     "category": "Threat Intelligence", "difficulty": "intermediate", "sources": "MITRE"},

    {"id": 204, "term_en": "Cyber Threat Hunting", "term_es": "Caza de Amenazas Cibernéticas", "acronym": None,
     "definition_en": "Proactive search for threats that evade existing security controls.",
     "definition_es": "Búsqueda proactiva de amenazas que evaden los controles de seguridad existentes.",
     "category": "Threat Intelligence", "difficulty": "advanced", "sources": "Sqrrl / CrowdStrike"},

    {"id": 205, "term_en": "OSINT", "term_es": "OSINT", "acronym": "OSINT",
     "definition_en": "Open-Source Intelligence – intelligence collected from publicly available sources.",
     "definition_es": "Inteligencia de Fuentes Abiertas – inteligencia recolectada de fuentes públicas.",
     "category": "Threat Intelligence", "difficulty": "intermediate", "sources": "ENISA"},

    {"id": 206, "term_en": "Dark Web Monitoring", "term_es": "Monitoreo de la Dark Web", "acronym": None,
     "definition_en": "Service that monitors dark web forums and markets for leaked company data.",
     "definition_es": "Servicio que monitorea foros y mercados de la dark web en busca de datos filtrados de la empresa.",
     "category": "Threat Intelligence", "difficulty": "intermediate", "sources": "Recorded Future / Flashpoint"},

    {"id": 207, "term_en": "Purple Hat", "term_es": "Sombrero Púrpura", "acronym": None,
     "definition_en": "Ethical hacker who uses both offensive and defensive techniques for maximum security improvement.",
     "definition_es": "Hacker ético que combina técnicas ofensivas y defensivas para máxima mejora de seguridad.",
     "category": "Fundamentals", "difficulty": "intermediate", "sources": "Comunidad"},

    {"id": 208, "term_en": "Bugcrowd", "term_es": "Bugcrowd", "acronym": None,
     "definition_en": "Leading bug bounty and vulnerability disclosure platform.",
     "definition_es": "Plataforma líder en bug bounty y divulgación de vulnerabilidades.",
     "category": "Vulnerability Management", "difficulty": "intermediate", "sources": "Bugcrowd Inc."},

    {"id": 209, "term_en": "HackerOne", "term_es": "HackerOne", "acronym": None,
     "definition_en": "Popular platform connecting organizations with ethical hackers.",
     "definition_es": "Plataforma popular que conecta organizaciones con hackers éticos.",
     "category": "Vulnerability Management", "difficulty": "intermediate", "sources": "HackerOne Inc."},

    {"id": 210, "term_en": "CVSS v4.0", "term_es": "CVSS v4.0", "acronym": "CVSS",
     "definition_en": "Latest version of the Common Vulnerability Scoring System (released November 2023).",
     "definition_es": "Versión más reciente del Sistema Común de Puntuación de Vulnerabilidades (noviembre 2023).",
     "category": "Vulnerability Management", "difficulty": "intermediate", "sources": "FIRST.org"},

    {"id": 211, "term_en": "EPSS", "term_es": "EPSS", "acronym": "EPSS",
     "definition_en": "Exploit Prediction Scoring System – predicts likelihood of CVE exploitation.",
     "definition_es": "Sistema de Puntuación de Predicción de Explotación – predice probabilidad de explotación de CVE.",
     "category": "Vulnerability Management", "difficulty": "intermediate", "sources": "FIRST.org"},

    {"id": 212, "term_en": "CWE Top 25", "term_es": "CWE Top 25", "acronym": None,
     "definition_en": "Annual list of the 25 most dangerous software weaknesses.",
     "definition_es": "Lista anual de las 25 debilidades de software más peligrosas.",
     "category": "Vulnerability Management", "difficulty": "intermediate", "sources": "MITRE"},

    {"id": 213, "term_en": "SBOM", "term_es": "SBOM", "acronym": "SBOM",
     "definition_en": "Software Bill of Materials – formal record of components in a piece of software.",
     "definition_es": "Lista de Materiales de Software – registro formal de componentes en un software.",
     "category": "GRC", "difficulty": "intermediate", "sources": "NTIA / Executive Order 14028"},

    {"id": 214, "term_en": "DevSecOps", "term_es": "DevSecOps", "acronym": None,
     "definition_en": "Integration of security practices into DevOps processes.",
     "definition_es": "Integración de prácticas de seguridad en procesos DevOps.",
     "category": "GRC", "difficulty": "intermediate", "sources": "Gartner / Gene Kim"},

    {"id": 215, "term_en": "Shift Left Security", "term_es": "Seguridad Shift Left", "acronym": None,
     "definition_en": "Practice of performing security testing earlier in the development lifecycle.",
     "definition_es": "Práctica de realizar pruebas de seguridad más temprano en el ciclo de desarrollo.",
     "category": "GRC", "difficulty": "intermediate", "sources": "Microsoft / Gartner"},

    {"id": 216, "term_en": "SAST", "term_es": "SAST", "acronym": "SAST",
     "definition_en": "Static Application Security Testing – analyzes source code without execution.",
     "definition_es": "Pruebas de Seguridad de Aplicaciones Estáticas – analiza código fuente sin ejecución.",
     "category": "Application Security", "difficulty": "intermediate", "sources": "OWASP"},

    {"id": 217, "term_en": "DAST", "term_es": "DAST", "acronym": "DAST",
     "definition_en": "Dynamic Application Security Testing – tests running applications from the outside.",
     "definition_es": "Pruebas de Seguridad de Aplicaciones Dinámicas – prueba aplicaciones en ejecución desde fuera.",
     "category": "Application Security", "difficulty": "intermediate", "sources": "OWASP"},

    {"id": 218, "term_en": "IAST", "term_es": "IAST", "acronym": "IAST",
     "definition_en": "Interactive Application Security Testing – combines SAST and DAST in runtime.",
     "definition_es": "Pruebas de Seguridad de Aplicaciones Interactivas – combina SAST y DAST en tiempo de ejecución.",
     "category": "Application Security", "difficulty": "advanced", "sources": "Gartner"},

    {"id": 219, "term_en": "RASP", "term_es": "RASP", "acronym": "RASP",
     "definition_en": "Runtime Application Self-Protection – security technology embedded in applications.",
     "definition_es": "Protección de Aplicaciones en Tiempo de Ejecución – tecnología integrada en aplicaciones.",
     "category": "Application Security", "difficulty": "advanced", "sources": "Gartner"},

    {"id": 220, "term_en": "API Security", "term_es": "Seguridad de APIs", "acronym": None,
     "definition_en": "Practices and tools to secure application programming interfaces.",
     "definition_es": "Prácticas y herramientas para asegurar interfaces de programación de aplicaciones.",
     "category": "Application Security", "difficulty": "intermediate", "sources": "OWASP API Security Top 10"},
    
    {"id": 220, "term_en": "API Security", "term_es": "Seguridad de APIs", "acronym": None,
     "definition_en": "Practices and tools to secure application programming interfaces against abuse and attacks.",
     "definition_es": "Prácticas y herramientas para proteger las interfaces de programación de aplicaciones contra abuso y ataques.",
     "category": "Application Security", "difficulty": "intermediate", "sources": "OWASP API Security Top 10 2023"},

    {"id": 221, "term_en": "Broken Access Control", "term_es": "Control de Acceso Roto", "acronym": None,
     "definition_en": "Failure to properly restrict what authenticated users are allowed to do.",
     "definition_es": "Fallo al restringir adecuadamente lo que los usuarios autenticados pueden hacer.",
     "category": "Application Security", "difficulty": "intermediate", "sources": "OWASP Top 10 A01:2021"},

    {"id": 222, "term_en": "Cryptographic Failures", "term_es": "Fallos Criptográficos", "acronym": None,
     "definition_en": "Weaknesses related to cryptography that lead to sensitive data exposure.",
     "definition_es": "Debilidades relacionadas con criptografía que provocan exposición de datos sensibles.",
     "category": "Application Security", "difficulty": "intermediate", "sources": "OWASP Top 10 A02:2021"},

    {"id": 223, "term_en": "Security Misconfiguration", "term_es": "Configuración Incorrecta de Seguridad", "acronym": None,
     "definition_en": "Improperly configured security settings that expose systems to attack.",
     "definition_es": "Configuraciones de seguridad mal aplicadas que exponen sistemas a ataques.",
     "category": "Application Security", "difficulty": "intermediate", "sources": "OWASP Top 10 A05:2021"},

    {"id": 224, "term_en": "Insecure Deserialization", "term_es": "Deserialización Insegura", "acronym": None,
     "definition_en": "Vulnerability allowing attackers to execute code via manipulated serialized objects.",
     "definition_es": "Vulnerabilidad que permite ejecutar código mediante objetos serializados manipulados.",
     "category": "Application Security", "difficulty": "advanced", "sources": "OWASP Top 10 A08:2021"},

    {"id": 225, "term_en": "Software and Data Integrity Failures", "term_es": "Fallos de Integridad de Software y Datos", "acronym": None,
     "definition_en": "Failures related to code and infrastructure that do not protect against integrity violations.",
     "definition_es": "Fallos relacionados con código e infraestructura que no protegen contra violaciones de integridad.",
     "category": "Application Security", "difficulty": "intermediate", "sources": "OWASP Top 10 A08:2021"},

    {"id": 226, "term_en": "Secure by Design", "term_es": "Seguro por Diseño", "acronym": None,
     "definition_en": "Approach that integrates security from the beginning of the development lifecycle.",
     "definition_es": "Enfoque que integra la seguridad desde el inicio del ciclo de vida de desarrollo.",
     "category": "Application Security", "difficulty": "intermediate", "sources": "Microsoft SDL / OWASP SAMM"},

    {"id": 227, "term_en": "Threat Modeling", "term_es": "Modelado de Amenazas", "acronym": None,
     "definition_en": "Structured approach to identify and prioritize potential threats to a system.",
     "definition_es": "Enfoque estructurado para identificar y priorizar amenazas potenciales a un sistema.",
     "category": "Application Security", "difficulty": "intermediate", "sources": "Microsoft / OWASP"},

    {"id": 228, "term_en": "STRIDE", "term_es": "STRIDE", "acronym": "STRIDE",
     "definition_en": "Threat modeling methodology: Spoofing, Tampering, Repudiation, Information disclosure, Denial of service, Elevation of privilege.",
     "definition_es": "Metodología de modelado de amenazas: Suplantación, Manipulación, Repudio, Divulgación, Denegación, Elevación.",
     "category": "Application Security", "difficulty": "intermediate", "sources": "Microsoft"},

    {"id": 229, "term_en": "DREAD", "term_es": "DREAD", "acronym": "DREAD",
     "definition_en": "Risk assessment model: Damage, Reproducibility, Exploitability, Affected users, Discoverability.",
     "definition_es": "Modelo de evaluación de riesgo: Daño, Reproducibilidad, Explotabilidad, Usuarios afectados, Descubribilidad.",
     "category": "Application Security", "difficulty": "intermediate", "sources": "Microsoft"},

    {"id": 230, "term_en": "Secure Coding", "term_es": "Codificación Segura", "acronym": None,
     "definition_en": "Practices and guidelines to prevent vulnerabilities during software development.",
     "definition_es": "Prácticas y directrices para prevenir vulnerabilidades durante el desarrollo de software.",
     "category": "Application Security", "difficulty": "intermediate", "sources": "OWASP Secure Coding Practices"},

    {"id": 231, "term_en": "Input Validation", "term_es": "Validación de Entrada", "acronym": None,
     "definition_en": "Process of ensuring that application input conforms to expected format and range.",
     "definition_es": "Proceso de asegurar que la entrada de la aplicación cumpla con formato y rango esperados.",
     "category": "Application Security", "difficulty": "intermediate", "sources": "OWASP"},

    {"id": 232, "term_en": "Output Encoding", "term_es": "Codificación de Salida", "acronym": None,
     "definition_en": "Technique to prevent XSS by encoding data before displaying it to users.",
     "definition_es": "Técnica para prevenir XSS codificando datos antes de mostrarlos a usuarios.",
     "category": "Application Security", "difficulty": "intermediate", "sources": "OWASP XSS Prevention Cheat Sheet"},

    {"id": 233, "term_en": "Content Security Policy", "term_es": "Política de Seguridad de Contenido", "acronym": "CSP",
     "definition_en": "HTTP header that helps mitigate XSS and data injection attacks.",
     "definition_es": "Cabecera HTTP que ayuda a mitigar ataques XSS e inyección de datos.",
     "category": "Application Security", "difficulty": "intermediate", "sources": "W3C / OWASP"},

    {"id": 234, "term_en": "Clickjacking", "term_es": "Clickjacking", "acronym": None,
     "definition_en": "Malicious technique of tricking users into clicking on something different from what they perceive.",
     "definition_es": "Técnica maliciosa de engañar al usuario para que haga clic en algo distinto a lo que percibe.",
     "category": "Threats", "difficulty": "intermediate", "sources": "OWASP / CWE-2008-7240"},

    {"id": 235, "term_en": "HTTP Security Headers", "term_es": "Cabeceras de Seguridad HTTP", "acronym": None,
     "definition_en": "Set of HTTP response headers that improve web application security (HSTS, X-Frame-Content-Type-Options, etc.).",
     "definition_es": "Conjunto de cabeceras HTTP que mejoran la seguridad de aplicaciones web (HSTS, X-Frame-Options, etc.).",
     "category": "Application Security", "difficulty": "intermediate", "sources": "OWASP Secure Headers Project"},

    {"id": 236, "term_en": "HSTS", "term_es": "HSTS", "acronym": "HSTS",
     "definition_en": "HTTP Strict Transport Security – forces browsers to use HTTPS only.",
     "definition_es": "HTTP Strict Transport Security – fuerza a los navegadores a usar solo HTTPS.",
     "category": "Application Security", "difficulty": "intermediate", "sources": "RFC 6797"},

    {"id": 237, "term_en": "CORS", "term_es": "CORS", "acronym": "CORS",
     "definition_en": "Cross-Origin Resource Sharing – mechanism that allows restricted resources to be requested from another domain.",
     "definition_es": "Intercambio de Recursos de Origen Cruzado – mecanismo que permite solicitar recursos restringidos desde otro dominio.",
     "category": "Application Security", "difficulty": "intermediate", "sources": "W3C / Mozilla"},

    {"id": 238, "term_en": "Server-Side Request Forgery", "term_es": "Falsificación de Petición del Lado del Servidor", "acronym": "SSRF",
     "definition_en": "Vulnerability allowing attacker to make server send requests to unintended locations.",
     "definition_es": "Vulnerabilidad que permite al atacante hacer que el servidor envíe peticiones a ubicaciones no deseadas.",
     "category": "Application Security", "difficulty": "advanced", "sources": "OWASP Top 10 A10:2021"},

    {"id": 239, "term_en": "XML External Entity", "term_es": "Entidad Externa XML", "acronym": "XXE",
     "definition_en": "Attack against applications parsing XML input with improperly configured parsers.",
     "definition_es": "Ataque contra aplicaciones que analizan entrada XML con parsers mal configurados.",
     "category": "Application Security", "difficulty": "advanced", "sources": "OWASP / CWE-611"},

    {"id": 240, "term_en": "Command Injection", "term_es": "Inyección de Comandos", "acronym": None,
     "definition_en": "Attack allowing execution of arbitrary commands on the host OS via vulnerable application.",
     "definition_es": "Ataque que permite ejecutar comandos arbitrarios en el SO host a través de aplicación vulnerable.",
     "category": "Threats", "difficulty": "intermediate", "sources": "OWASP / CWE-78"},

    {"id": 241, "term_en": "Directory Traversal", "term_es": "Traversión de Directorios", "acronym": None,
     "definition_en": "Attack technique used to access files outside the web root directory.",
     "definition_es": "Técnica de ataque para acceder a archivos fuera del directorio raíz web.",
     "category": "Threats", "difficulty": "intermediate", "sources": "OWASP / CWE-22"},

    {"id": 242, "term_en": "Local File Inclusion", "term_es": "Inclusión Local de Archivos", "acronym": "LFI",
     "definition_en": "Vulnerability allowing inclusion and execution of local files on the server.",
     "definition_es": "Vulnerabilidad que permite incluir y ejecutar archivos locales en el servidor.",
     "category": "Threats", "difficulty": "intermediate", "sources": "OWASP / PortSwigger"},

    {"id": 243, "term_en": "Remote File Inclusion", "term_es": "Inclusión Remota de Archivos", "acronym": "RFI",
     "definition_en": "Vulnerability allowing inclusion of remote files, often leading to remote code execution.",
     "definition_es": "Vulnerabilidad que permite incluir archivos remotos, llevando frecuentemente a ejecución remota de código.",
     "category": "Threats", "difficulty": "advanced", "sources": "OWASP"},

    {"id": 244, "term_en": "Open Redirect", "term_es": "Redirección Abierta", "acronym": None,
     "definition_en": "Vulnerability allowing attackers to redirect users to arbitrary external websites.",
     "definition_es": "Vulnerabilidad que permite a atacantes redirigir usuarios a sitios web externos arbitrarios.",
     "category": "Threats", "difficulty": "intermediate", "sources": "OWASP"},

    {"id": 245, "term_en": "Race Condition", "term_es": "Condición de Carrera", "acronym": None,
     "definition_en": "Vulnerability occurring when multiple processes access shared resources without proper synchronization.",
     "definition_es": "Vulnerabilidad que ocurre cuando múltiples procesos acceden a recursos compartidos sin sincronización adecuada.",
     "category": "Application Security", "difficulty": "advanced", "sources": "CWE-362"},

    {"id": 246, "term_en": "Time-of-Check to Time-of-Use", "term_es": "TOCTOU", "acronym": "TOCTOU",
     "definition_en": "Race condition where a resource is checked and then used, allowing changes in between.",
     "definition_es": "Condición de carrera donde se comprueba un recurso y luego se usa, permitiendo cambios entre ambas acciones.",
     "category": "Application Security", "difficulty": "advanced", "sources": "MITRE CWE-367"},

    {"id": 247, "term_en": "Insecure Direct Object Reference", "term_es": "Referencia Directa Insegura a Objetos", "acronym": "IDOR",
     "definition_en": "Vulnerability where application exposes internal object references allowing unauthorized access.",
     "definition_es": "Vulnerabilidad donde la aplicación expone referencias internas permitiendo acceso no autorizado.",
     "category": "Application Security", "difficulty": "intermediate", "sources": "OWASP Top 10 2017 A5"},

    {"id": 248, "term_en": "Mass Assignment", "term_es": "Asignación Masiva", "acronym": None,
     "definition_en": "Vulnerability in web frameworks where user input is automatically bound to object properties.",
     "definition_es": "Vulnerabilidad en frameworks web donde la entrada del usuario se asigna automáticamente a propiedades de objetos.",
     "category": "Application Security", "difficulty": "intermediate", "sources": "OWASP / Rails"},

    {"id": 249, "term_en": "Rate Limiting", "term_es": "Limitación de Tasa", "acronym": None,
     "definition_en": "Technique to control the number of requests a user can make in a given time period.",
     "definition_es": "Técnica para controlar el número de peticiones que un usuario puede hacer en un período dado.",
     "category": "Application Security", "difficulty": "intermediate", "sources": "OWASP / Cloudflare"},

    {"id": 250, "term_en": "Cyber Kill Chain", "term_es": "Cadena de Ciberataque", "acronym": None,
     "definition_en": "Seven-stage model developed by Lockheed Martin describing the phases of a cyberattack.",
     "definition_es": "Modelo de siete fases desarrollado por Lockheed Martin que describe las fases de un ciberataque.",
     "category": "Fundamentals", "difficulty": "intermediate", "sources": "Lockheed Martin / MITRE ATT&CK"},
]
    
output_path = os.path.join(os.path.dirname(__file__), '..', 'data', 'glossary_data.json')

os.makedirs(os.path.dirname(output_path), exist_ok=True)

with open(output_path, 'w', encoding='utf-8') as f:
    json.dump(CYBERLEARN_GLOSSARY, f, indent=2, ensure_ascii=False)

print("GLOSARIO COMPLETO GENERADO CON ÉXITO")
print(f"Archivo guardado en: {output_path}")
print(f"Total de términos: {len(CYBERLEARN_GLOSSARY)}")