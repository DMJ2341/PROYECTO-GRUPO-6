# backend/config.py
import os
from dotenv import load_dotenv

# Carga las variables del archivo .env si existe
load_dotenv()

class Config:
    """Configuración segura de CyberLearn."""
    
    # Obtenemos la URL de la base de datos de las variables de entorno
    # Si no existe, lanzamos un error para evitar arrancar mal configurado
    SQLALCHEMY_DATABASE_URI = os.getenv('DATABASE_URL')
    if not SQLALCHEMY_DATABASE_URI:
        raise ValueError("❌ Error Crítico: No se encontró DATABASE_URL en las variables de entorno.")
    
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    
    # Obtenemos la clave secreta
    SECRET_KEY = os.getenv('SECRET_KEY')
    if not SECRET_KEY:
        raise ValueError("❌ Error Crítico: No se encontró SECRET_KEY en las variables de entorno.")
    
    JWT_EXPIRATION_HOURS = 24
    PASSWORD_MIN_LENGTH = 8

# Configuración global
config = Config()