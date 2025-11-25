# backend/models/user.py - CORRECCIÓN PARA NAMEERROR 'Date'
from database.db import Base
from sqlalchemy import Column, Integer, String, DateTime, Date, ForeignKey  # ✅ Añadido Date
from datetime import datetime

class User(Base):
    __tablename__ = 'users'
    
    id = Column(Integer, primary_key=True)
    email = Column(String(120), unique=True, nullable=False)
    password_hash = Column(String(255), nullable=False)
    name = Column(String(100))
    total_xp = Column(Integer, default=0)
    current_streak = Column(Integer, default=0)
    max_streak = Column(Integer, default=0)
    last_activity_date = Column(Date)  # ✅ Usa Date
    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)