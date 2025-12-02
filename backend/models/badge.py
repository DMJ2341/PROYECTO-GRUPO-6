# backend/models/badge.py
from database.db import Base
from sqlalchemy import Column, Integer, String

class Badge(Base):
    __tablename__ = 'badges'
    
    id = Column(Integer, primary_key=True)
    name = Column(String(100), nullable=False)
    description = Column(String(200))
    icon = Column(String(200))  
    xp_required = Column(Integer, default=0) 
    trigger_type = Column(String(50))  
    trigger_value = Column(String(50)) 