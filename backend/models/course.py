# backend/models/course.py
from database.db import Base
from sqlalchemy import Column, Integer, String

class Course(Base):
    __tablename__ = 'courses'
    id = Column(Integer, primary_key=True)
    title = Column(String(200), nullable=False)
    description = Column(String(500))
    level = Column(String(50))
    xp_reward = Column(Integer, default=0)
    image_url = Column(String(500))