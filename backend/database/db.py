# backend/database/db.py - CORRECCIÓN FINAL PARA ATTRIBUTEERROR 'SESSION'
import os
from dotenv import load_dotenv
from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker, scoped_session

load_dotenv()  # Carga .env automáticamente

# Conexión desde .env (seguro para servidor)
DB_URL = os.getenv('DATABASE_URL', 'postgresql://app_cyberlearn:CyberLearn2025*@172.232.188.183:5432/cyberlearn_db')  # Fallback local si no .env

engine = create_engine(DB_URL)
pool_pre_ping=True,  # <--- AGREGA ESTO
SessionLocal = sessionmaker(bind=engine)  # Añadido para compatibilidad si otros códigos lo usan
Session = scoped_session(SessionLocal)
Base = declarative_base()

def get_session():
    return Session()

def create_all():
    Base.metadata.create_all(engine)