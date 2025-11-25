# backend/services/lesson_service.py - CORREGIDO
import uuid
from models.lesson_schema import Lesson as LessonSchema  # Pydantic
from models.lesson import Lesson as DBLesson             # SQLAlchemy
from database.db import get_session
from pydantic import ValidationError

# ✅ CAMBIO: Ahora es una función suelta, NO una clase
def create_lesson(lesson_data: dict):
    """
    Crea una lección validando el JSON y mapeando los campos requeridos.
    """
    # 1. Validar con Pydantic
    try:
        validated = LessonSchema.model_validate(lesson_data)
    except ValidationError as e:
        raise ValueError(f"JSON inválido: {str(e)}")

    session = get_session()
    try:
        # 2. Generar ID único (String)
        # Tu modelo Lesson usa ID tipo String, no se genera solo.
        new_id = f"lesson_{uuid.uuid4().hex[:8]}"
        
        # 3. Convertir Pydantic a Diccionario
        data_dict = validated.model_dump()
        
        # 4. Crear objeto SQLAlchemy (Mapeando campos explícitamente)
        # ⚠️ Si solo pasas 'content', fallará porque 'title' y 'course_id' son obligatorios
        new_lesson = DBLesson(
            id=new_id,
            course_id=validated.course_id,
            title=validated.title,
            description=validated.description or f"Lección {validated.title}",
            type=validated.type or "interactive",
            
            # Guardamos la data completa en los campos JSONB
            content=data_dict,          # Todo el JSON
            screens=data_dict.get('screens', []), # Solo las pantallas
            
            total_screens=validated.total_screens,
            duration_minutes=validated.duration_minutes,
            xp_reward=validated.xp_reward,
            order_index=validated.order_index
        )
        
        session.add(new_lesson)
        session.commit()
        return new_lesson.id

    except Exception as e:
        session.rollback()
        raise e
    finally:
        session.close()