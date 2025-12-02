# backend/models/glossary.py
from database.db import Base
from sqlalchemy import Column, Integer, String, Text, DateTime
from datetime import datetime

class Glossary(Base):
    __tablename__ = 'glossary'
    
    id = Column(Integer, primary_key=True)
    
    # Campos Bilingües
    term_en = Column(String(255), nullable=False)
    term_es = Column(String(255), nullable=False)
    definition_en = Column(Text, nullable=False)
    definition_es = Column(Text, nullable=False)
    
    # Metadata
    acronym = Column(String(50), nullable=True)
    category = Column(String(100), nullable=True)
    difficulty = Column(String(50), default='beginner')
    
    # Ejemplos
    example_en = Column(Text, nullable=True)
    example_es = Column(Text, nullable=True)
    
    # ESTA ES LA COLUMNA DE VALIDACIÓN IMPORTANTE
    where_you_hear_it = Column(String(255), nullable=True)
    
    created_at = Column(DateTime, default=datetime.utcnow)

    def to_dict(self):
        return {
            'id': self.id,
            'term_en': self.term_en,
            'term_es': self.term_es,
            'term': self.term_es, 
            'definition_en': self.definition_en,
            'definition_es': self.definition_es,
            'definition': self.definition_es, 
            'acronym': self.acronym,
            'category': self.category,
            'difficulty': self.difficulty,
            'example_en': self.example_en,
            'example_es': self.example_es,
            'example': self.example_es, 
            'reference': self.where_you_hear_it 
        }