# backend/config.py
import os
from dotenv import load_dotenv

load_dotenv()

class Config:
    """Configuración oficial de CyberLearn."""
    
    # PostgreSQL real vía Docker
    SQLALCHEMY_DATABASE_URI = os.environ.get('DATABASE_URL')
    if not SQLALCHEMY_DATABASE_URI:
        raise ValueError("❌ DATABASE_URL no está en variables de entorno. Docker debe setearla.")
    
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    
    # Seguridad oficial
    SECRET_KEY = os.environ.get('SECRET_KEY')
    if not SECRET_KEY:
        raise ValueError("❌ SECRET_KEY no está en variables de entorno.")
    
    JWT_EXPIRATION_HOURS = int(os.environ.get('JWT_EXPIRATION_HOURS', 24))
    PASSWORD_MIN_LENGTH = int(os.environ.get('PASSWORD_MIN_LENGTH', 8))