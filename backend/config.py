# backend/config.py
import os

class Config:
    """Configuración oficial de CyberLearn para servidor real."""
    
    # CONEXIÓN DIRECTA AL SERVIDOR
    SQLALCHEMY_DATABASE_URI = 'postgresql://app_cyberlearn:CyberLearn2025*@172.232.188.183:5432/cyberlearn_db'
    
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    
    # CLAVE SECRETA DIRECTA (para desarrollo)
    SECRET_KEY = 'cyberlearn_super_secret_key_2024_change_in_production'
    
    JWT_EXPIRATION_HOURS = 24
    PASSWORD_MIN_LENGTH = 8

# Configuración global
config = Config()