# backend/models/daily_term_log.py
from database.db import Base
from sqlalchemy import Column, Integer, Date, ForeignKey, DateTime, UniqueConstraint
from datetime import datetime

class DailyTermLog(Base):
    __tablename__ = 'daily_term_logs'

    id = Column(Integer, primary_key=True)
    user_id = Column(Integer, ForeignKey('users.id'), nullable=False)
    glossary_id = Column(Integer, ForeignKey('glossary.id'), nullable=False)
    viewed_date = Column(Date, nullable=False)  
    viewed_at = Column(DateTime, default=datetime.utcnow)

    # Asegura que solo se registre 1 término diario por usuario en la base de datos
    __table_args__ = (
        UniqueConstraint('user_id', 'viewed_date', name='one_per_day'),
    )