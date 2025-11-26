# backend/database/db.py
import os
from dotenv import load_dotenv
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, scoped_session
from sqlalchemy.ext.declarative import declarative_base

load_dotenv()

# Es mejor fallar si no hay URL en producción que dejar una contraseña hardcodeada,
# pero si mantienes el fallback, asegúrate de que sea solo para desarrollo.
DB_URL = os.getenv('DATABASE_URL', 'postgresql://app_cyberlearn:CyberLearn2025*@172.232.188.183:5432/cyberlearn_db')

# ✅ CORRECCIÓN: pool_pre_ping dentro de los paréntesis
engine = create_engine(DB_URL, pool_pre_ping=True)

SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Session = scoped_session(SessionLocal)
Base = declarative_base()

def get_session():
    return Session()

def create_all():
    Base.metadata.create_all(engine)