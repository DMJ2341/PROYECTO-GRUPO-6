# backend/models/glossary.py
from sqlalchemy import Column, Integer, String, Text, DateTime
from datetime import datetime
from database.db import Base 

class Glossary(Base):
    __tablename__ = 'glossary'
    id = Column(Integer, primary_key=True)
    term = Column(String(255), unique=True, nullable=False)
    acronym = Column(String(50), nullable=True)
    definition = Column(Text, nullable=False)
    example = Column(Text, nullable=True)
    category = Column(String(100), nullable=True)
    difficulty = Column(String(50), default='Básico')
    where_you_hear_it = Column(String(255), nullable=True)
    created_at = Column(DateTime, default=datetime.utcnow) # <-- COLUMNA QUE FALTABA EN LA BD

    def to_dict(self):
        """Devuelve una representación de diccionario del término."""
        return {
            'id': self.id,
            'term': self.term,
            'acronym': self.acronym,
            'definition': self.definition,
            'example': self.example,
            'category': self.category,
            'difficulty': self.difficulty,
            'where_you_hear_it': self.where_you_hear_it
        }

    def __repr__(self):
        return f"<Glossary(term='{self.term}')>"