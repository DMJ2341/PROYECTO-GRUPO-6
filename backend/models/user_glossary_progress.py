# backend/models/user_glossary_progress.py
from database.db import Base
from sqlalchemy import Column, Integer, ForeignKey, Boolean, DateTime, String
from datetime import datetime

class UserGlossaryProgress(Base):
    """Tabla para rastrear el progreso del usuario en el glosario."""
    __tablename__ = 'user_glossary_progress'
    
    id = Column(Integer, primary_key=True)
    user_id = Column(Integer, ForeignKey('users.id'), nullable=False)
    glossary_id = Column(Integer, ForeignKey('glossary.id'), nullable=False)
    
    # Estado de aprendizaje
    is_learned = Column(Boolean, default=False)
    learned_at = Column(DateTime, nullable=True)
    
    # Estadísticas de práctica (para quiz)
    times_practiced = Column(Integer, default=0)
    times_correct = Column(Integer, default=0)
    last_practiced_at = Column(DateTime, nullable=True)
    
    # Metadata
    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
    
    def __repr__(self):
        return f"<UserGlossaryProgress user={self.user_id} term={self.glossary_id} learned={self.is_learned}>"