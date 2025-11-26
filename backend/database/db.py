# backend/database/db.py
import os
from dotenv import load_dotenv
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, scoped_session
from sqlalchemy.ext.declarative import declarative_base

load_dotenv()

# Obtenemos la URL de forma segura
DB_URL = os.getenv('DATABASE_URL')

if not DB_URL:
    raise ValueError("❌ DATABASE_URL no está configurada. Revisa tu archivo .env")

# Creamos el motor con la URL segura
# pool_pre_ping=True ayuda a reconectar si la base de datos cierra la conexión
engine = create_engine(DB_URL, pool_pre_ping=True)

SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Session = scoped_session(SessionLocal)
Base = declarative_base()

def get_session():
    return Session()

def create_all():
    Base.metadata.create_all(engine)