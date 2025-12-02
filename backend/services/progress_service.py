# backend/services/progress_service.py - COMPLETAMENTE CORREGIDO
from database.db import get_session
from models.user_progress import UserCourseProgress, UserLessonProgress
from models.lesson import Lesson
from models.course import Course
from models.user import User
from services.activity_service import ActivityService
from services.badge_service import BadgeService
from datetime import datetime

activity_service = ActivityService()

def mark_lesson_completed(user_id: int, lesson_id: str):
    session = get_session()
    try:
        #1. Obtener usuario
        user = session.query(User).filter_by(id=user_id).first()
        if not user:
            return {"error": "user_not_found"}

        #2. Obtener lección
        lesson = session.query(Lesson).filter_by(id=lesson_id).first()
        if not lesson:
            return {"error": "lesson_not_found"}

        #3. Obtener curso (para incluir title en respuesta)
        course = session.query(Course).filter_by(id=lesson.course_id).first()
        if not course:
            return {"error": "course_not_found"}

        #4. Buscar o crear registro de progreso de la lección
        progress = session.query(UserLessonProgress).filter_by(
            user_id=user_id, lesson_id=lesson_id
        ).first()

        # Calcular progreso actual del curso
        completed_count = session.query(UserLessonProgress).join(Lesson).filter(
            UserLessonProgress.user_id == user_id,
            Lesson.course_id == lesson.course_id,
            UserLessonProgress.completed == True
        ).count()
        
        total_count = session.query(Lesson).filter_by(course_id=lesson.course_id).count()
        percentage = int((completed_count / total_count) * 100) if total_count > 0 else 0

        # Si ya está completada, devolver respuesta válida sin dar XP
        if progress and progress.completed:
            return {
                "lesson_completed": True,
                "xp_earned": 0,
                "new_badges": [],
                "course_progress": {
                    "course_id": lesson.course_id,
                    "title": course.title, 
                    "percentage": percentage,
                    "completed_lessons": completed_count,
                    "total_lessons": total_count,
                    "course_completed": percentage == 100
                }
            }

        #5. Crear o actualizar progreso
        if not progress:
            progress = UserLessonProgress(
                user_id=user_id,
                lesson_id=lesson_id,
                completed=True,
                completed_at=datetime.utcnow(),
                attempts=1
            )
            session.add(progress)
        else:
            progress.completed = True
            progress.completed_at = datetime.utcnow()
            progress.attempts += 1

        #6. ACTUALIZAR XP DEL USUARIO (CRÍTICO)
        xp_amount = lesson.xp_reward if lesson.xp_reward else 20
        user.total_xp += xp_amount  

        #7. Registrar actividad 
        activity_service.create_activity(
            user_id=user_id,
            activity_type="lesson_completed",
            points=xp_amount,
            lesson_id=lesson_id,
            description=f"Lección {lesson_id} completada",
            session=session  
        )

        # 8. Calcular progreso del CURSO
        course_id = lesson.course_id

        # Recalcular después de agregar esta lección
        completed_count = session.query(UserLessonProgress).join(Lesson).filter(
            UserLessonProgress.user_id == user_id,
            Lesson.course_id == course_id,
            UserLessonProgress.completed == True
        ).count()

        total_count = session.query(Lesson).filter_by(course_id=course_id).count()
        percentage = int((completed_count / total_count) * 100) if total_count > 0 else 0

        course_progress = session.query(UserCourseProgress).filter_by(
            user_id=user_id, course_id=course_id
        ).first()

        if not course_progress:
            course_progress = UserCourseProgress(
                user_id=user_id,
                course_id=course_id,
                completed_lessons=completed_count,
                total_lessons=total_count,
                percentage=percentage
            )
            if percentage == 100:
                course_progress.completed_at = datetime.utcnow()
            session.add(course_progress)
        else:
            course_progress.completed_lessons = completed_count
            course_progress.total_lessons = total_count
            course_progress.percentage = percentage
            if percentage == 100 and not course_progress.completed_at:
                course_progress.completed_at = datetime.utcnow()

        #9. VERIFICAR Y OTORGAR BADGES
        
        session.flush() 
        
        badge_service = BadgeService()
        new_badges = badge_service.check_and_award_badges(user_id, session)

        session.commit()
        
        return {
            "lesson_completed": True,
            "xp_earned": xp_amount,
            "new_badges": new_badges,
            "course_progress": {
                "course_id": course_id,
                "title": course.title, 
                "percentage": percentage,
                "completed_lessons": completed_count,
                "total_lessons": total_count,
                "course_completed": percentage == 100
            }
        }
    except Exception as e:
        session.rollback()
        print(f"❌ ERROR en mark_lesson_completed: {str(e)}")
        import traceback
        traceback.print_exc()
        raise e
    finally:
        session.close()

def get_user_course_progress(user_id: int, course_id: int = None):
    session = get_session()
    try:
        if course_id:
            progress = session.query(UserCourseProgress).filter_by(
                user_id=user_id, course_id=course_id
            ).first()
            if progress:
                course = session.query(Course).filter_by(id=course_id).first()
                return {
                    "course_id": progress.course_id,
                    "title": course.title if course else "Unknown",  
                    "percentage": progress.percentage,
                    "completed_lessons": progress.completed_lessons,
                    "total_lessons": progress.total_lessons
                }
            return None

        progresses = session.query(UserCourseProgress).filter_by(user_id=user_id).all()
        result = []
        for p in progresses:
            course = session.query(Course).filter_by(id=p.course_id).first()
            result.append({
                "course_id": p.course_id,
                "title": course.title if course else "Unknown",  
                "percentage": p.percentage,
                "completed_lessons": p.completed_lessons,
                "total_lessons": p.total_lessons
            })
        return result
    finally:
        session.close()