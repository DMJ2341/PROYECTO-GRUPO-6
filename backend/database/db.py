# backend/database/db.py
import os
from sqlalchemy import create_engine, Column, Integer, String, Text, Float, DateTime, ForeignKey, func
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker, scoped_session, relationship, backref

def get_database_url():
    # CONEXIÓN DIRECTA AL SERVIDOR
    return 'postgresql://app_cyberlearn:CyberLearn2025*@172.232.188.183:5432/cyberlearn_db'

SQLALCHEMY_DATABASE_URL = get_database_url()
print(f"🔗 Conectando a: {SQLALCHEMY_DATABASE_URL}")

try:
    engine = create_engine(
        SQLALCHEMY_DATABASE_URL,
        pool_size=5,
        max_overflow=10,
        pool_pre_ping=True,
        pool_recycle=3600,
    )
    
    SessionLocal = scoped_session(sessionmaker(autocommit=False, autoflush=False, bind=engine))
    Base = declarative_base()
    
    # Objeto Database con TODOS los atributos necesarios
    class Database:
        def __init__(self):
            self.Model = Base
            self.Column = Column
            self.Integer = Integer
            self.String = String
            self.Text = Text
            self.Float = Float
            self.DateTime = DateTime
            self.ForeignKey = ForeignKey
            self.relationship = relationship
            self.backref = backref
            self.func = func
            self.metadata = Base.metadata
            self.session = SessionLocal
            self.engine = engine
            
        def create_all(self):
            Base.metadata.create_all(bind=engine)
            
        def get_session(self):
            return SessionLocal()
            
    
    db = Database()
    Session = SessionLocal
    init_db = db.create_all
    
    print("✅ Motor de base de datos inicializado correctamente")
    
except Exception as e:
    print(f"❌ Error conectando al servidor PostgreSQL: {e}")
    print("   Verifica: IP, usuario, contraseña, firewall, PostgreSQL corriendo")
    raise