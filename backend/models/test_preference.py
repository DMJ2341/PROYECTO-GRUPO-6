# backend/models/test_preference.py
from database.db import Base
from sqlalchemy import Column, Integer, String, Boolean, Float, ForeignKey, DateTime, Text
from sqlalchemy.orm import relationship, backref
from datetime import datetime

class TestQuestion(Base):
    """Preguntas del test de preferencias basado en Holland Code (RIASEC)"""
    __tablename__ = 'test_questions'
    
    id = Column(Integer, primary_key=True)
    question = Column(String(500), nullable=False)
    emoji = Column(String(10), nullable=False)
    category = Column(String(50), nullable=False) 
    order = Column(Integer, nullable=False)
    created_at = Column(DateTime, default=datetime.utcnow)

    def to_dict(self):
        return {
            'id': self.id,
            'question': self.question,
            'emoji': self.emoji,
            'category': self.category,
            'order': self.order
        }

class Certification(Base):
    __tablename__ = 'test_certifications'
    
    id = Column(Integer, primary_key=True)
    name = Column(String(200), nullable=False)
    provider = Column(String(200), nullable=False)
    role = Column(String(50), nullable=False)
    is_free = Column(Boolean, default=False)
    url = Column(String(500), nullable=False)
    difficulty = Column(String(50), nullable=False)
    description = Column(Text, nullable=False)
    price_info = Column(String(200))
    order = Column(Integer, nullable=False)
    created_at = Column(DateTime, default=datetime.utcnow)

    def to_dict(self):
        return {
            'id': self.id,
            'name': self.name,
            'provider': self.provider,
            'role': self.role,
            'is_free': self.is_free,
            'url': self.url,
            'difficulty': self.difficulty,
            'description': self.description,
            'price_info': self.price_info
        }

class Lab(Base):
    __tablename__ = 'test_labs'
    
    id = Column(Integer, primary_key=True)
    name = Column(String(200), nullable=False)
    platform = Column(String(200), nullable=False)
    role = Column(String(50), nullable=False)
    url = Column(String(500), nullable=False)
    is_free = Column(Boolean, default=False)
    description = Column(Text, nullable=False)
    difficulty = Column(String(50))
    created_at = Column(DateTime, default=datetime.utcnow)

    def to_dict(self):
        return {
            'id': self.id,
            'name': self.name,
            'platform': self.platform,
            'role': self.role,
            'url': self.url,
            'is_free': self.is_free,
            'description': self.description,
            'difficulty': self.difficulty
        }

class LearningPath(Base):
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

    def to_dict(self):
        return {
            'id': self.id,
            'name': self.name,
            'platform': self.platform,
            'role': self.role,
            'url': self.url,
            'estimated_hours': self.estimated_hours,
            'description': self.description,
            'is_free': self.is_free
        }

class RoleSkill(Base):
    __tablename__ = 'test_role_skills'
    
    id = Column(Integer, primary_key=True)
    role = Column(String(50), nullable=False)
    skill = Column(String(200), nullable=False)
    order = Column(Integer, nullable=False)
    created_at = Column(DateTime, default=datetime.utcnow)

    def to_dict(self):
        return {
            'id': self.id,
            'role': self.role,
            'skill': self.skill
        }

class AcademicReference(Base):
    __tablename__ = 'test_academic_references'
    
    id = Column(Integer, primary_key=True)
    role = Column(String(50), nullable=False, unique=True)
    reference = Column(Text, nullable=False)
    created_at = Column(DateTime, default=datetime.utcnow)

    def to_dict(self):
        return {
            'id': self.id,
            'role': self.role,
            'reference': self.reference
        }

# ✅ RENOMBRADO: De UserTestResult a TestResult para coincidir con db.py y service
class TestResult(Base):
    __tablename__ = 'user_test_results'
    
    id = Column(Integer, primary_key=True)
    user_id = Column(Integer, ForeignKey('users.id'), nullable=False)
    recommended_role = Column(String(50), nullable=False)
    confidence = Column(Float, nullable=False)
    
    # Scores
    investigative_score = Column(Integer, nullable=False)
    realistic_score = Column(Integer, nullable=False)
    social_score = Column(Integer, nullable=False)
    conventional_score = Column(Integer, nullable=False)
    enterprising_score = Column(Integer, nullable=False)
    artistic_score = Column(Integer, nullable=False)
    
    # Metadata
    time_taken_seconds = Column(Integer)
    created_at = Column(DateTime, default=datetime.utcnow)
    
    user = relationship("User", backref="test_results")

# ✅ RENOMBRADO: De UserTestAnswer a TestAnswer
class TestAnswer(Base):
    __tablename__ = 'user_test_answers'
    
    id = Column(Integer, primary_key=True)
    test_result_id = Column(Integer, ForeignKey('user_test_results.id'), nullable=False)
    question_id = Column(Integer, ForeignKey('test_questions.id'), nullable=False)
    rating = Column(Integer, nullable=False)
    created_at = Column(DateTime, default=datetime.utcnow)
    
    test_result = relationship("TestResult", backref="answers")
    question = relationship("TestQuestion", backref="answers")