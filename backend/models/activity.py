# backend/models/activity.py
from database.db import Base
from sqlalchemy import Column, Integer, String, DateTime, ForeignKey
from datetime import datetime
from enum import Enum 

# Enum para tipos de actividad
class ActivityType(Enum):
    DAILY_TERM = 'DAILY_TERM'
    LESSON_COMPLETED = 'LESSON_COMPLETED'
    EXAM_PASSED = 'EXAM_PASSED'
    BADGE_EARNED = 'BADGE_EARNED'
    

class Activity(Base):
    __tablename__ = 'activities'
    id = Column(Integer, primary_key=True)
    user_id = Column(Integer, ForeignKey('users.id'), nullable=False)
    activity_type = Column(String(50), nullable=False) 
    points = Column(Integer, default=0)
    lesson_id = Column(String)  # String ID
    description = Column(String(200))
    created_at = Column(DateTime, default=datetime.utcnow)