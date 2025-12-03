# backend/models/user_progress.py
from database.db import Base
from sqlalchemy import Column, Integer, String, Boolean, DateTime, ForeignKey, UniqueConstraint
from sqlalchemy.orm import relationship
from datetime import datetime

class UserCourseProgress(Base):
    __tablename__ = 'user_course_progress'

    id = Column(Integer, primary_key=True)
    user_id = Column(Integer, ForeignKey('users.id'), nullable=False)
    course_id = Column(Integer, ForeignKey('courses.id'), nullable=False)
    completed_lessons = Column(Integer, default=0)
    total_lessons = Column(Integer, default=0)
    percentage = Column(Integer, default=0) 
    completed_at = Column(DateTime, nullable=True)  
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    
    user = relationship("User", backref="course_progress")
    course = relationship("Course")

class UserLessonProgress(Base):
    __tablename__ = 'user_lesson_progress'

    id = Column(Integer, primary_key=True)
    user_id = Column(Integer, ForeignKey('users.id'), nullable=False)
    lesson_id = Column(String, ForeignKey('lessons.id'), nullable=False)  
    completed = Column(Boolean, default=False)
    completed_at = Column(DateTime, nullable=True)
    attempts = Column(Integer, default=0)

    # Evitar duplicados: un usuario solo tiene un registro por lección
    __table_args__ = (
        UniqueConstraint('user_id', 'lesson_id', name='unique_user_lesson'),
    )