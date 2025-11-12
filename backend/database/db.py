# backend/database/db.py
import os
from sqlalchemy import create_engine, Column, Integer, String, Text, Float, DateTime, ForeignKey, func
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker, scoped_session, relationship, backref
from sqlalchemy.pool import StaticPool
from dotenv import load_dotenv

# Cargar variables de entorno
if os.path.exists('.env.local'):
    load_dotenv('.env.local')
    print("🔍 Cargando configuración desde .env.local")
elif os.path.exists('.env'):
    load_dotenv('.env')
    print("🔍 Cargando configuración desde .env")

def get_database_url():
    # Detectar si estamos en Docker
    if os.path.exists('/.dockerenv'):
        # En Docker, usar las credenciales del contenedor
        return 'postgresql://cyberlearn_user:CyberLearn2024!@db:5432/cyberlearn_db'
    
    # Localmente, usar las credenciales simples
    return os.getenv('DATABASE_URL', 'postgresql://postgres:postgres123@localhost:5432/cyberlearn')

SQLALCHEMY_DATABASE_URL = get_database_url()
print(f"🔗 Conectando a: {SQLALCHEMY_DATABASE_URL}")

try:
    # Para PostgreSQL real
    if 'postgresql' in SQLALCHEMY_DATABASE_URL:
        engine = create_engine(
            SQLALCHEMY_DATABASE_URL,
            pool_size=5,
            max_overflow=10,
            pool_pre_ping=True,
            pool_recycle=3600,
        )
    else:
        # Fallback para SQLite (desarrollo)
        engine = create_engine(
            SQLALCHEMY_DATABASE_URL,
            connect_args={"check_same_thread": False},
            poolclass=StaticPool,
        )
    
    SessionLocal = scoped_session(sessionmaker(autocommit=False, autoflush=False, bind=engine))
    Base = declarative_base()
    
    # ✅ CORREGIR: Objeto Database con TODOS los atributos necesarios
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
            self.relationship = relationship  # ✅ AGREGADO
            self.backref = backref           # ✅ AGREGADO
            self.func = func
            self.metadata = Base.metadata   # ✅ AGREGADO
            self.session = SessionLocal
            
        def create_all(self):
            Base.metadata.create_all(bind=engine)
            
    
    db = Database()
    Session = SessionLocal
    init_db = db.create_all
    
    print("✅ Motor de base de datos inicializado correctamente")
    
except Exception as e:
    print(f"❌ Error inicializando base de datos: {e}")
    # Fallback a SQLite si PostgreSQL falla
    print("🔧 Intentando fallback a SQLite...")
    SQLALCHEMY_DATABASE_URL = "sqlite:///./cyberlearn.db"
    engine = create_engine(
        SQLALCHEMY_DATABASE_URL,
        connect_args={"check_same_thread": False},
        poolclass=StaticPool,
    )
    SessionLocal = scoped_session(sessionmaker(autocommit=False, autoflush=False, bind=engine))
    Base = declarative_base()
    
    # ✅ CORREGIR: Fallback también con todos los atributos
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
            self.relationship = relationship  # ✅ AGREGADO
            self.backref = backref           # ✅ AGREGADO
            self.func = func                 # ✅ AGREGADO
            self.session = SessionLocal
            
        def create_all(self):
            Base.metadata.create_all(bind=engine)
    
    db = Database()
    Session = SessionLocal
    init_db = db.create_all