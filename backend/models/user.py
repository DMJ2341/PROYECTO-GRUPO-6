# backend/models/user.py
from database.db import Base
from sqlalchemy import Column, Integer, String, DateTime, Date, Boolean
from datetime import datetime

class User(Base):
    __tablename__ = 'users'
    
    id = Column(Integer, primary_key=True)
    email = Column(String(120), unique=True, nullable=False)
    password_hash = Column(String(255), nullable=False)
    name = Column(String(100))
    
    email_verified = Column(Boolean, default=False)
    email_verified_at = Column(DateTime, nullable=True)
    terms_accepted_at = Column(DateTime, nullable=True)
    
    # Campos de gamificación
    total_xp = Column(Integer, default=0)
    current_streak = Column(Integer, default=0)
    max_streak = Column(Integer, default=0)
    last_activity_date = Column(Date)
    
    # Metadata
    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
    
    def __repr__(self):
        return f"<User {self.email}>"
    
    def is_verified(self):
        """Verifica si el usuario ha confirmado su email."""
        return self.email_verified
    
    def is_academic_email(self):
        """Detecta si es correo académico."""
        academic_domains = ['@uni.pe']
        return any(self.email.endswith(domain) for domain in academic_domains)
    
    def get_institution(self):
        """Retorna la institución del usuario si es académico."""
        if '@uni.pe' in self.email:
            return 'UNI'
        return None

    # --- LÓGICA DE NIVELES  ---
    def get_level(self):
        """Calcula el nivel basado en 250 XP por nivel."""
        # 250 XP = 1 Nivel.
        # 0-249 XP = Nivel 1
        # 250-499 XP = Nivel 2
        # ...
        # 2500+ XP = Nivel 11 (Final)
        return (self.total_xp // 250) + 1
    
    def get_xp_for_next_level(self):
        """Calcula cuánta XP falta para el siguiente nivel."""
        current_level = self.get_level()
        next_level_xp = current_level * 250
        return next_level_xp - self.total_xp