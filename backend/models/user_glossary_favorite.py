# backend/models/user_glossary_favorite.py
from database.db import Base
from sqlalchemy import Column, Integer, DateTime, ForeignKey, UniqueConstraint
from datetime import datetime

class UserGlossaryFavorite(Base):
    __tablename__ = 'user_glossary_favorites'

    id = Column(Integer, primary_key=True)
    user_id = Column(Integer, ForeignKey('users.id'), nullable=False)
    glossary_id = Column(Integer, ForeignKey('glossary.id'), nullable=False)
    added_at = Column(DateTime, default=datetime.utcnow)

    # Restricción única: Un usuario no puede marcar el mismo término dos veces
    __table_args__ = (
        UniqueConstraint('user_id', 'glossary_id', name='unique_user_favorite'),
    )