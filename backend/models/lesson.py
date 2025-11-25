# backend/models/lesson.py
from database.db import Base
from sqlalchemy import Column, Integer, String, ForeignKey
from sqlalchemy.dialects.postgresql import JSONB
from sqlalchemy.orm import relationship

class Lesson(Base):
    __tablename__ = 'lessons'
    
    id = Column(String, primary_key=True)  # ID string como "fundamentos_leccion_1"
    course_id = Column(Integer, ForeignKey('courses.id'), nullable=False)
    title = Column(String(200), nullable=False)
    description = Column(String(500))
    type = Column(String(50))
    content = Column(JSONB, nullable=False)  # JSONB para todo el contenido interactivo
    screens = Column(JSONB)  # JSONB para pantallas (tabs, timelines, labs)
    total_screens = Column(Integer, default=0)
    duration_minutes = Column(Integer, default=15)
    xp_reward = Column(Integer, default=50)
    order_index = Column(Integer, nullable=False)
    
    course = relationship("Course", backref="lessons")