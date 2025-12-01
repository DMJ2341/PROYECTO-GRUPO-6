# backend/services/lesson_service.py

import uuid
from models.lesson import Lesson as DBLesson
from models.user_progress import UserLessonProgress, UserCourseProgress
from database.db import get_session
from sqlalchemy import desc

def create_lesson(lesson_data: dict):
    """Crea una lección usando los datos del diccionario directamente."""
    session = get_session()
    try:
        if 'course_id' not in lesson_data or 'title' not in lesson_data:
            raise ValueError("Faltan datos obligatorios: course_id o title")

        new_id = f"lesson_{uuid.uuid4().hex[:8]}"
        
        new_lesson = DBLesson(
            id=new_id,
            course_id=lesson_data.get('course_id'),
            title=lesson_data.get('title'),
            description=lesson_data.get('description') or f"Lección {lesson_data.get('title')}",
            type=lesson_data.get('type', "interactive"),
            content=lesson_data,
            screens=lesson_data.get('screens', []),
            total_screens=lesson_data.get('total_screens', len(lesson_data.get('screens', []))),
            duration_minutes=lesson_data.get('duration_minutes', 10),
            xp_reward=lesson_data.get('xp_reward', 10),
            order_index=lesson_data.get('order_index', 0)
        )
        
        session.add(new_lesson)
        session.commit()
        return new_lesson.id

    except Exception as e:
        session.rollback()
        raise e
    finally:
        session.close()

def is_course_accessible(user_id: int, course_id: int):
    """Verifica si un curso está accesible (Prerrequisitos de cursos)."""
    session = get_session()
    try:
        if course_id == 1:
            return {"accessible": True, "reason": "Curso inicial"}
        
        required_course_id = course_id - 1
        required_progress = session.query(UserCourseProgress).filter_by(
            user_id=user_id, course_id=required_course_id
        ).first()
        
        if not required_progress or required_progress.percentage < 100:
            return {
                "accessible": False,
                "reason": f"Debes completar el Curso {required_course_id} primero",
                "required_course_id": required_course_id
            }
        
        return {"accessible": True, "reason": f"Curso {required_course_id} completado"}
    finally:
        session.close()

def get_lesson_content(user_id: int, lesson_id: str):
    """Recupera el contenido de una lección (solo si está desbloqueada)."""
    session = get_session()
    try:
        lesson = session.query(DBLesson).filter_by(id=lesson_id).first()
        if not lesson:
            return {"error": "Lección no encontrada"}, 404

        # Verificar curso
        course_access = is_course_accessible(user_id, lesson.course_id)
        if not course_access["accessible"]:
            return {"error": "Curso bloqueado", "message": course_access["reason"]}, 403
        
        # Verificar lección anterior (Secuencialidad)
        if lesson.order_index > 1:
            prev = session.query(DBLesson).filter(
                DBLesson.course_id == lesson.course_id,
                DBLesson.order_index < lesson.order_index
            ).order_by(desc(DBLesson.order_index)).first()
            
            if prev:
                prog = session.query(UserLessonProgress).filter_by(
                    user_id=user_id, lesson_id=prev.id, completed=True
                ).first()
                if not prog:
                    return {"error": "Lección bloqueada", "message": f"Completa '{prev.title}' primero"}, 403

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
        return {"error": f"Error interno: {str(e)}"}, 500
    finally:
        session.close()

def get_course_lessons_with_status(user_id: int, course_id: int):
    session = get_session()
    try:
        course_access = is_course_accessible(user_id, course_id)
        is_course_locked = not course_access["accessible"]
        
        lessons = session.query(DBLesson).filter_by(course_id=course_id).order_by(DBLesson.order_index).all()
        if not lessons: 
            return []
        
        result = []
        previous_lesson_completed = True  # Primera lección siempre desbloqueada
        
        for i, lesson in enumerate(lessons):
            progress = session.query(UserLessonProgress).filter_by(
                user_id=user_id, lesson_id=lesson.id
            ).first()
            
            is_completed = progress.completed if progress else False
            
            # Lógica de bloqueo clara
            if is_course_locked:
                is_locked = True
            elif i == 0:
                is_locked = False  # Primera lección SIEMPRE desbloqueada
            else:
                is_locked = not previous_lesson_completed
            
            result.append({
                "id": lesson.id,
                "course_id": lesson.course_id,
                "title": lesson.title,
                "description": lesson.description,
                "type": lesson.type,
                "duration_minutes": lesson.duration_minutes,
                "xp_reward": lesson.xp_reward,
                "order_index": lesson.order_index,
                "is_completed": is_completed,
                "is_locked": is_locked 
            })
            
            previous_lesson_completed = is_completed
        
        return result
    finally:
        session.close()