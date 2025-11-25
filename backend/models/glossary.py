from database.db import Base
from sqlalchemy import Column, Integer, String, Text

class Glossary(Base):
    __tablename__ = 'glossary'

    id = Column(Integer, primary_key=True)
    term = Column(String(100), nullable=False, unique=True)
    acronym = Column(String(100))
    definition = Column(Text, nullable=False)
    example = Column(Text)
    category = Column(String(50))
    difficulty = Column(String(20))
    where_you_hear_it = Column(Text)

    def to_dict(self):
        return {
            "id": self.id,
            "term": self.term,
            "acronym": self.acronym or "",
            "definition": self.definition,
            "example": self.example or "",
            "category": self.category or "General",
            "difficulty": self.difficulty or "Medio",
            "where_you_hear_it": self.where_you_hear_it or ""
        }