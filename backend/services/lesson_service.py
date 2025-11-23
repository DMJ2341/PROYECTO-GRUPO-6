from models.lesson_schema import Lesson  # Tus schemas Pydantic
from models.lesson import DBLesson  # Tu modelo SQLAlchemy con JSONB
from database.db import db
from pydantic import ValidationError

def create_lesson(lesson_data: dict):
    try:
        validated = Lesson.model_validate(lesson_data)  # Valida
        new_lesson = DBLesson(
            content=validated.model_dump()  # Convierte a dict para JSONB
        )
        db.session.add(new_lesson)
        db.session.commit()
        return new_lesson.id
    except ValidationError as e:
        raise ValueError(f"JSON inválido: {str(e)}")