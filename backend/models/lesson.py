# backend/models/lesson.py
from database.db import Base
from sqlalchemy import Column, Integer, String, ForeignKey
from sqlalchemy.dialects.postgresql import JSONB
from sqlalchemy.orm import relationship

class Lesson(Base):
    """
    Modelo de Lección con soporte para contenido interactivo gaming.
    """
    
    __tablename__ = 'lessons'
    
    # Primary Key
    id = Column(String, primary_key=True)  # Formato: "c1_l1", "c3_l2", etc.
    
    # Relación con Curso
    course_id = Column(Integer, ForeignKey('courses.id'), nullable=False)
    
    # Metadatos de la Lección
    title = Column(String(200), nullable=False)
    description = Column(String(500))
    type = Column(String(50))  # "interactive", "crisis_simulator", "strategy_builder"
    
    # Contenido (Legacy - mantener por compatibilidad y para guardar 'theme' y 'intro')
    content = Column(JSONB, nullable=False)  
    
    # 🎮 CAMPO CRÍTICO: Screens gaming (Array de screens)
    screens = Column(JSONB)  
    
    # Estadísticas
    total_screens = Column(Integer, default=0)
    duration_minutes = Column(Integer, default=15)
    xp_reward = Column(Integer, default=50)
    
    # Ordenamiento
    order_index = Column(Integer, nullable=False)
    
    # Relación inversa con Course
    course = relationship("Course", backref="lessons")
    
    def __repr__(self):
        return f"<Lesson {self.id}: {self.title} ({self.total_screens} screens)>"
    
    def to_dict(self):
        """
        Serializa la lección a diccionario para API response.
        Incluye extracción automática del 'theme' desde el campo content.
        """
        # Intentar extraer el theme si existe en content, si no, null
        lesson_theme = self.content.get("theme") if self.content else None

        return {
            "id": self.id,
            "course_id": self.course_id,
            "title": self.title,
            "description": self.description,
            "type": self.type,
            "screens": self.screens or [],
            "theme": lesson_theme,  # Enviamos el tema al frontend
            "total_screens": self.total_screens,
            "duration_minutes": self.duration_minutes,
            "xp_reward": self.xp_reward,
            "order_index": self.order_index
        }
    
    @staticmethod
    def validate_screen_structure(screen):
        """
        Valida que un screen tenga la estructura mínima requerida.
        """
        required_fields = ["screen_id", "type", "title", "content"]
        return all(field in screen for field in required_fields)
    
    @staticmethod
    def count_screens_by_type(screens):
        """
        Cuenta cuántos screens hay de cada tipo.
        """
        if not screens:
            return {}
        
        type_counts = {}
        for screen in screens:
            screen_type = screen.get("type", "unknown")
            type_counts[screen_type] = type_counts.get(screen_type, 0) + 1
        
        return type_counts