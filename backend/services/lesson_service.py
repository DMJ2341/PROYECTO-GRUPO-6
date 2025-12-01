# backend/services/lesson_service.py - VERSIÓN CORREGIDA CON DESBLOQUEO POR CURSO

import uuid
from models.lesson_schema import Lesson as LessonSchema  # Pydantic
from models.lesson import Lesson as DBLesson             # SQLAlchemy
from models.user_progress import UserLessonProgress, UserCourseProgress
from database.db import get_session
from pydantic import ValidationError
from sqlalchemy import func

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
        new_id = f"lesson_{uuid.uuid4().hex[:8]}"
        
        # 3. Convertir Pydantic a Diccionario
        data_dict = validated.model_dump()
        
        # 4. Crear objeto SQLAlchemy
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


# =================================================================
# 🔑 FUNCIÓN CORREGIDA: Desbloqueo por CURSO completo
# =================================================================

def is_course_accessible(user_id: int, course_id: int):
    """
    Verifica si un curso está accesible para el usuario.
    
    LÓGICA:
    - Curso 1: Siempre accesible
    - Curso 2: Requiere Curso 1 completo (100%)
    - Curso 3: Requiere Curso 2 completo (100%)
    - Curso 4: Requiere Curso 3 completo (100%)
    - Curso 5: Requiere Curso 4 completo (100%)
    
    Returns:
        dict: {
            "accessible": bool,
            "reason": str (si no es accesible),
            "required_course_id": int (si hay prerrequisito)
        }
    """
    session = get_session()
    try:
        # Curso 1 siempre está desbloqueado
        if course_id == 1:
            return {
                "accessible": True,
                "reason": "Curso inicial"
            }
        
        # Para cursos 2-5, verificar que el curso anterior esté completo
        required_course_id = course_id - 1
        
        # Buscar progreso del curso requerido
        required_progress = session.query(UserCourseProgress).filter_by(
            user_id=user_id,
            course_id=required_course_id
        ).first()
        
        # Si no existe progreso o no está al 100%, el curso está bloqueado
        if not required_progress or required_progress.percentage < 100:
            return {
                "accessible": False,
                "reason": f"Debes completar el Curso {required_course_id} primero",
                "required_course_id": required_course_id
            }
        
        return {
            "accessible": True,
            "reason": f"Curso {required_course_id} completado"
        }
        
    finally:
        session.close()


def get_lesson_content(user_id: int, lesson_id: str):
    """
    Recupera el contenido de una lección verificando acceso al curso.
    
    NUEVA LÓGICA:
    - Verifica si el curso de la lección está accesible
    - Si el curso está accesible, todas sus lecciones lo están
    """
    session = get_session()
    try:
        # 1. Obtener los detalles de la lección
        lesson = session.query(DBLesson).filter_by(id=lesson_id).first()

        if not lesson:
            return {"error": "Lección no encontrada"}, 404

        # 2. Verificar si el curso está accesible
        course_access = is_course_accessible(user_id, lesson.course_id)
        
        if not course_access["accessible"]:
            return {
                "error": "Curso bloqueado",
                "message": course_access["reason"],
                "required_course_id": course_access.get("required_course_id")
            }, 403
        
        # 3. Si el curso está accesible, devolver el contenido
        return {
            "success": True,
            "id": lesson.id,
            "title": lesson.title,
            "description": lesson.description,
            "content": lesson.content,
            "type": lesson.type,
            "screens": lesson.screens,
            "total_screens": lesson.total_screens,
            "duration_minutes": lesson.duration_minutes,
            "xp_reward": lesson.xp_reward,
            "order_index": lesson.order_index,
            "course_id": lesson.course_id
        }, 200
            
    except Exception as e:
        session.rollback()
        return {"error": f"Error interno del servidor: {str(e)}"}, 500
    finally:
        session.close()


def get_course_lessons_with_status(user_id: int, course_id: int):
    """
    Obtiene todas las lecciones de un curso con su estado de acceso.
    
    NUEVA LÓGICA:
    - Si el curso está accesible, todas las lecciones están desbloqueadas
    - Si el curso está bloqueado, todas las lecciones están bloqueadas
    """
    session = get_session()
    try:
        # 1. Verificar acceso al curso
        course_access = is_course_accessible(user_id, course_id)
        
        # 2. Obtener todas las lecciones del curso
        lessons = session.query(DBLesson).filter_by(
            course_id=course_id
        ).order_by(DBLesson.order_index).all()
        
        if not lessons:
            return []
        
        result = []
        for lesson in lessons:
            # Buscar progreso del usuario
            progress = session.query(UserLessonProgress).filter_by(
                user_id=user_id,
                lesson_id=lesson.id
            ).first()
            
            result.append({
                "id": lesson.id,
                "course_id": lesson.course_id,
                "title": lesson.title,
                "description": lesson.description,
                "type": lesson.type,
                "duration_minutes": lesson.duration_minutes,
                "xp_reward": lesson.xp_reward,
                "order_index": lesson.order_index,
                "is_completed": progress.completed if progress else False,
                "is_locked": not course_access["accessible"]  # ← CLAVE: Basado en acceso al curso
            })
        
        return result
        
    finally:
        session.close()