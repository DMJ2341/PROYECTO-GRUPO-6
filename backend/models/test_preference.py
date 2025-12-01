# backend/models/test_preference.py
from database.db import Base
from sqlalchemy import Column, Integer, String, Boolean, Float, ForeignKey, DateTime, Text
from sqlalchemy.dialects.postgresql import JSONB
from sqlalchemy.orm import relationship
from datetime import datetime

class TestQuestion(Base):
    """Preguntas del test de preferencias basado en Holland Code (RIASEC)"""
    __tablename__ = 'test_questions'
    
    id = Column(Integer, primary_key=True)
    question = Column(String(500), nullable=False)  # Solo español
    emoji = Column(String(10), nullable=False)
    category = Column(String(50), nullable=False)  # INVESTIGATIVE, REALISTIC, SOCIAL, CONVENTIONAL, ENTERPRISING, ARTISTIC
    order = Column(Integer, nullable=False)
    created_at = Column(DateTime, default=datetime.utcnow)


class Certification(Base):
    """Certificaciones recomendadas por rol"""
    __tablename__ = 'test_certifications'
    
    id = Column(Integer, primary_key=True)
    name = Column(String(200), nullable=False)
    provider = Column(String(200), nullable=False)
    role = Column(String(50), nullable=False)  # RED_TEAM, BLUE_TEAM, PURPLE_TEAM
    is_free = Column(Boolean, default=False)
    url = Column(String(500), nullable=False)
    difficulty = Column(String(50), nullable=False)  # Beginner, Intermediate, Advanced
    description = Column(Text, nullable=False)
    price_info = Column(String(200))  # Ej: "$249 USD", "Gratis"
    order = Column(Integer, nullable=False)
    created_at = Column(DateTime, default=datetime.utcnow)


class Lab(Base):
    """Laboratorios prácticos recomendados"""
    __tablename__ = 'test_labs'
    
    id = Column(Integer, primary_key=True)
    name = Column(String(200), nullable=False)
    platform = Column(String(200), nullable=False)  # HTB, THM, BTLO, etc.
    role = Column(String(50), nullable=False)
    url = Column(String(500), nullable=False)
    is_free = Column(Boolean, default=False)
    description = Column(Text, nullable=False)
    difficulty = Column(String(50))  # Easy, Medium, Hard
    created_at = Column(DateTime, default=datetime.utcnow)


class LearningPath(Base):
    """Rutas de aprendizaje recomendadas"""
    __tablename__ = 'test_learning_paths'
    
    id = Column(Integer, primary_key=True)
    name = Column(String(200), nullable=False)
    platform = Column(String(200), nullable=False)
    role = Column(String(50), nullable=False)
    url = Column(String(500), nullable=False)
    estimated_hours = Column(Integer, nullable=False)
    description = Column(Text, nullable=False)
    is_free = Column(Boolean, default=False)
    created_at = Column(DateTime, default=datetime.utcnow)


class RoleSkill(Base):
    """Skills necesarias por rol"""
    __tablename__ = 'test_role_skills'
    
    id = Column(Integer, primary_key=True)
    role = Column(String(50), nullable=False)
    skill = Column(String(200), nullable=False)
    order = Column(Integer, nullable=False)
    created_at = Column(DateTime, default=datetime.utcnow)


class AcademicReference(Base):
    """Referencias académicas que respaldan cada rol"""
    __tablename__ = 'test_academic_references'
    
    id = Column(Integer, primary_key=True)
    role = Column(String(50), nullable=False, unique=True)
    reference = Column(Text, nullable=False)  # Texto completo de la referencia
    created_at = Column(DateTime, default=datetime.utcnow)


class UserTestResult(Base):
    """Resultados del test de cada usuario"""
    __tablename__ = 'user_test_results'
    
    id = Column(Integer, primary_key=True)
    user_id = Column(Integer, ForeignKey('users.id'), nullable=False)
    recommended_role = Column(String(50), nullable=False)  # RED_TEAM, BLUE_TEAM, PURPLE_TEAM
    confidence = Column(Float, nullable=False)  # 0.0 - 1.0
    
    # Scores por dimensión Holland Code
    investigative_score = Column(Integer, nullable=False)
    realistic_score = Column(Integer, nullable=False)
    social_score = Column(Integer, nullable=False)
    conventional_score = Column(Integer, nullable=False)
    enterprising_score = Column(Integer, nullable=False)
    artistic_score = Column(Integer, nullable=False)
    
    # Metadata
    time_taken_seconds = Column(Integer)  # Tiempo que tardó en completar
    created_at = Column(DateTime, default=datetime.utcnow)
    
    # Relationship
    user = relationship("User", backref="test_results")


class UserTestAnswer(Base):
    """Respuestas individuales del usuario"""
    __tablename__ = 'user_test_answers'
    
    id = Column(Integer, primary_key=True)
    test_result_id = Column(Integer, ForeignKey('user_test_results.id'), nullable=False)
    question_id = Column(Integer, ForeignKey('test_questions.id'), nullable=False)
    rating = Column(Integer, nullable=False)  # 1-5
    created_at = Column(DateTime, default=datetime.utcnow)
    
    # Relationships
    test_result = relationship("UserTestResult", backref="answers")
    question = relationship("TestQuestion", backref="answers")