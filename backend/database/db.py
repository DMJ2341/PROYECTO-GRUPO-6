# backend/database/db.py
import os
from dotenv import load_dotenv
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, scoped_session
from sqlalchemy.ext.declarative import declarative_base

load_dotenv()

DB_URL = os.getenv('DATABASE_URL')

if not DB_URL:
    raise ValueError("❌ DATABASE_URL no está configurada. Revisa tu archivo .env")

engine = create_engine(DB_URL, pool_pre_ping=True)

SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Session = scoped_session(SessionLocal) # <-- ESTA ES LA SCOPED SESSION QUE USA .remove() y .close()
Base = declarative_base()

def get_session():
    # Devuelve la scoped session
    return Session() 

def create_all():
    Base.metadata.create_all(engine)