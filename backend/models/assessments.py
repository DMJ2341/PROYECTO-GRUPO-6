# backend/models/assessments.py

from database.db import Base
from sqlalchemy import Column, Integer, String, DateTime, ForeignKey, Float, JSON, Boolean, Text
from sqlalchemy.orm import relationship
from datetime import datetime

# ===========================================
# EXAMEN FINAL INTEGRADOR (Post 5 cursos)
# ===========================================

class FinalExamQuestion(Base):
    """
    Banco de preguntas para el examen final integrador.
    Estructura optimizada para 60 minutos de examen.
    """
    __tablename__ = 'final_exam_questions'
    
    id = Column(Integer, primary_key=True)
    section = Column(String(50), nullable=False)  # "multiple_choice", "case_study", "design"
    question_number = Column(Integer)  # Orden de presentación
    course_focus = Column(String(50))  # "course_1", "integrated", etc.
    difficulty = Column(String(20), default="medium")  # "easy", "medium", "hard"
    
    # Contenido de la pregunta
    question_text = Column(Text, nullable=False)
    question_type = Column(String(50))  # "single_choice", "multiple_choice", "drag_drop", "short_answer"
    
    # Opciones y respuestas
    content = Column(JSON)  # Estructura flexible por tipo de pregunta
    # Ejemplo multiple_choice: 
    # {
    #   "options": [
    #     {"id": "a", "text": "...", "explanation": "..."},
    #     {"id": "b", "text": "...", "explanation": "..."}
    #   ],
    #   "context": "Caso: Empresa XYZ...",
    #   "image_url": "..."
    # }
    
    correct_answer = Column(JSON, nullable=False)  
    # Ejemplo: {"correct": ["a"], "partial": ["b"], "points_breakdown": {"a": 1.0, "b": 0.5}}
    
    points = Column(Float, default=1.0)
    time_allocation = Column(Integer, default=120)  # Segundos sugeridos
    
    # Metadata educativa
    explanation = Column(Text)  # Explicación de la respuesta correcta
    references = Column(JSON)  # ["NIST SP 800-53", "MITRE ATT&CK T1059"]
    
    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
    is_active = Column(Boolean, default=True)


class UserExamAttempt(Base):
    """
    Registro de intentos del examen final por usuario.
    Máximo 3 intentos con cooldown de 48 horas.
    """
    __tablename__ = 'user_exam_attempts'
    
    id = Column(Integer, primary_key=True)
    user_id = Column(Integer, ForeignKey('users.id'), nullable=False)
    attempt_number = Column(Integer, default=1)  # 1, 2, o 3
    
    # Scoring
    total_points = Column(Float, default=0.0)
    max_points = Column(Float, default=100.0)
    percentage = Column(Float, default=0.0)
    passed = Column(Boolean, default=False)  # True si >= 70%
    grade = Column(String(20))  # "PASS - Competent", "PASS - Proficient", "PASS - Expert"
    
    # Timing
    started_at = Column(DateTime, default=datetime.utcnow)
    completed_at = Column(DateTime)
    time_taken = Column(Integer)  # Segundos totales
    
    # Detalle de respuestas
    answers_log = Column(JSON)  
    # Estructura:
    # {
    #   "1": {"answer": "a", "correct": true, "points": 1.0, "time_spent": 45},
    #   "2": {"answer": ["b", "c"], "correct": false, "points": 0, "time_spent": 120}
    # }
    
    section_scores = Column(JSON)
    # {
    #   "multiple_choice": {"score": 18, "max": 20},
    #   "case_study": {"score": 22, "max": 25},
    #   "design": {"score": 12, "max": 15}
    # }
    
    # Metadata
    ip_address = Column(String(50))
    user_agent = Column(String(255))
    
    # Relationship
    user = relationship("User", backref="exam_attempts")


class ExamFeedback(Base):
    """
    Feedback opcional del estudiante post-examen.
    """
    __tablename__ = 'exam_feedback'
    
    id = Column(Integer, primary_key=True)
    attempt_id = Column(Integer, ForeignKey('user_exam_attempts.id'))
    difficulty_rating = Column(Integer)  # 1-5
    clarity_rating = Column(Integer)  # 1-5
    comments = Column(Text)
    created_at = Column(DateTime, default=datetime.utcnow)


# ===========================================
# TEST DE PREFERENCIAS (VOCACIONAL)
# ===========================================

class PreferenceQuestion(Base):
    """
    25 preguntas del test vocacional.
    Basado en Holland RIASEC + NICE Framework.
    """
    __tablename__ = 'preference_questions'
    
    id = Column(Integer, primary_key=True)
    question_number = Column(Integer, unique=True)
    section = Column(String(50))  # "work_preferences", "technical_scenarios", "interests"
    
    question_text = Column(Text, nullable=False)
    question_subtext = Column(Text)  # Contexto adicional opcional
    
    options = Column(JSON, nullable=False)
    # Estructura:
    # [
    #   {
    #     "id": "a",
    #     "text": "Encontrar formas creativas de romper sistemas",
    #     "team": "Red Team",
    #     "weight": 2,
    #     "reasoning": "Pensamiento ofensivo, creatividad"
    #   },
    #   ...
    # ]
    
    image_url = Column(String(255))  # Opcional para hacer más visual
    is_active = Column(Boolean, default=True)
    
    created_at = Column(DateTime, default=datetime.utcnow)


class UserPreferenceResult(Base):
    """
    Resultado del test vocacional con scoring detallado.
    """
    __tablename__ = 'user_preference_results'
    
    id = Column(Integer, primary_key=True)
    user_id = Column(Integer, ForeignKey('users.id'), nullable=False, unique=True)
    
    # Scores por team (0-100)
    red_score = Column(Float, default=0.0)
    blue_score = Column(Float, default=0.0)
    purple_score = Column(Float, default=0.0)
    
    # Normalización (porcentajes relativos)
    red_percentage = Column(Float)
    blue_percentage = Column(Float)
    purple_percentage = Column(Float)
    
    # Perfil asignado
    assigned_profile = Column(String(50))  # "Red Team", "Blue Team", "Purple Team"
    confidence_level = Column(String(20))  # "dominant", "hybrid", "balanced"
    secondary_profile = Column(String(50))  # Si hay híbrido
    
    # Detalle de respuestas
    answers_log = Column(JSON)
    # {
    #   "1": {"selected": "a", "team": "Red Team", "weight": 2},
    #   "2": {"selected": "b", "team": "Blue Team", "weight": 1}
    # }
    
    # Características del perfil (para personalización)
    personality_traits = Column(JSON)
    # {
    #   "creativity": "high",
    #   "detail_oriented": "medium",
    #   "risk_tolerance": "high",
    #   "collaboration": "medium"
    # }
    
    # Metadata
    completed_at = Column(DateTime, default=datetime.utcnow)
    time_taken = Column(Integer)  # Segundos
    retaken = Column(Boolean, default=False)  # Si lo retoma
    
    # Relationship
    user = relationship("User", backref="preference_result", uselist=False)


class ProfileViewLog(Base):
    """
    Tracking de qué recursos visualiza el estudiante en su perfil.
    Útil para analytics y mejora de contenido.
    """
    __tablename__ = 'profile_view_logs'
    
    id = Column(Integer, primary_key=True)
    user_id = Column(Integer, ForeignKey('users.id'))
    profile_type = Column(String(50))  # "Red Team", "Blue Team", "Purple Team"
    section_viewed = Column(String(100))  # "certifications", "practice_labs", etc.
    viewed_at = Column(DateTime, default=datetime.utcnow)