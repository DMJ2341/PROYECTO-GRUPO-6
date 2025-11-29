# backend/models/email_verification.py
from database.db import Base
from sqlalchemy import Column, Integer, String, DateTime, Boolean, ForeignKey
from datetime import datetime, timedelta

class EmailVerificationCode(Base):
    """Tabla para códigos de verificación de email."""
    __tablename__ = 'email_verification_codes'
    
    id = Column(Integer, primary_key=True)
    user_id = Column(Integer, ForeignKey('users.id'), nullable=False)
    code = Column(String(6), nullable=False)  # Código de 6 dígitos
    created_at = Column(DateTime, default=datetime.utcnow)
    expires_at = Column(DateTime, nullable=False)
    used = Column(Boolean, default=False)
    
    def __init__(self, user_id, code):
        self.user_id = user_id
        self.code = code
        self.created_at = datetime.utcnow()
        self.expires_at = datetime.utcnow() + timedelta(minutes=10)  # Expira en 10 min
        self.used = False
    
    def is_expired(self):
        """Verifica si el código ya expiró."""
        return datetime.utcnow() > self.expires_at
    
    def is_valid(self):
        """Verifica si el código es válido para usar."""
        return not self.used and not self.is_expired()