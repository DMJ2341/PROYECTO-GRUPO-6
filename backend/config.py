# backend/config.py
import os
from dotenv import load_dotenv

# Carga las variables del archivo .env si existe
load_dotenv()

class Config:
    """Configuración segura de CyberLearn."""
    
    # ==========================================
    # 🗄️ BASE DE DATOS
    # ==========================================
    SQLALCHEMY_DATABASE_URI = os.getenv('DATABASE_URL')
    if not SQLALCHEMY_DATABASE_URI:
        raise ValueError("❌ Error Crítico: No se encontró DATABASE_URL en las variables de entorno.")
    
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    
    # ==========================================
    # 🔐 SEGURIDAD
    # ==========================================
    SECRET_KEY = os.getenv('SECRET_KEY')
    if not SECRET_KEY:
        raise ValueError("❌ Error Crítico: No se encontró SECRET_KEY en las variables de entorno.")
    
    JWT_EXPIRATION_HOURS = 24
    PASSWORD_MIN_LENGTH = 8
    
    # ==========================================
    # 📧 EMAIL (GMAIL SMTP)
    # ==========================================
    GMAIL_USER = os.getenv('GMAIL_USER')
    GMAIL_APP_PASSWORD = os.getenv('GMAIL_APP_PASSWORD')
    
    if not GMAIL_USER or not GMAIL_APP_PASSWORD:
        print("⚠️ Advertencia: Credenciales de Gmail no configuradas. El envío de emails estará deshabilitado.")
    
    # ==========================================
    # 🎓 DOMINIOS ACADÉMICOS PERMITIDOS
    # ==========================================
    ACADEMIC_DOMAINS = [
        '@uni.pe',
    ]
    
    # ==========================================
    # 🔒 LISTA NEGRA DE CONTRASEÑAS DÉBILES
    # ==========================================
    WEAK_PASSWORDS = [
        '12345678', 'password', 'qwerty123', 'abc12345',
        'password123', '11111111', '12341234', 'admin123',
        'peru2024', 'cyberlearn', 'hacker123', 'univers1',
        '00000000', 'pass1234', 'contraseña', 'miclave123'
    ]

# Configuración global
config = Config()