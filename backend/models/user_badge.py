# backend/models/user_badge.py
from database.db import Base
from sqlalchemy import Column, Integer, DateTime, ForeignKey
from datetime import datetime

class UserBadge(Base):
    __tablename__ = 'user_badges'
    id = Column(Integer, primary_key=True)
    user_id = Column(Integer, ForeignKey('users.id'), nullable=False)
    badge_id = Column(Integer, ForeignKey('badges.id'), nullable=False)
    earned_at = Column(DateTime, default=datetime.utcnow)
    earned_value = Column(Integer, default=1)