# backend/models/badge.py
from database.db import Base
from sqlalchemy import Column, Integer, String

class Badge(Base):
    __tablename__ = 'badges'
    
    id = Column(Integer, primary_key=True)
    name = Column(String(100), nullable=False)
    description = Column(String(200))
    icon = Column(String(200))  # Emojis o URLs
    xp_required = Column(Integer, default=0) # Legacy (opcional)
    
    # 🔥 NUEVOS CAMPOS PARA AUTOMATIZACIÓN
    trigger_type = Column(String(50))  # Ej: 'first_lesson', 'xp_milestone', 'course_completed'
    trigger_value = Column(String(50)) # Ej: '1', '100', '5' (ID del curso o cantidad de XP)