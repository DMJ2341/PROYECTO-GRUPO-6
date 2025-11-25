# backend/services/progress_service.py
from database.db import get_session
from models.user_progress import UserCourseProgress, UserLessonProgress
from models.lesson import Lesson
from services.activity_service import ActivityService
from services.badge_service import BadgeService  # ✅ IMPORTANTE
from datetime import datetime

activity_service = ActivityService()

def mark_lesson_completed(user_id: int, lesson_id: str):
    session = get_session()
    try:
        # 1. Buscar o crear registro de progreso de la lección
        progress = session.query(UserLessonProgress).filter_by(
            user_id=user_id, lesson_id=lesson_id
        ).first()

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
            if progress.completed:
                return {"already_completed": True}
            progress.completed = True
            progress.completed_at = datetime.utcnow()
            progress.attempts += 1

        # 2. Dar XP y registrar actividad
        xp_amount = 10
        activity_service.create_activity(
            user_id=user_id,
            activity_type="lesson_completed",
            points=xp_amount,
            lesson_id=lesson_id,
            description=f"Lección {lesson_id} completada"
        )

        # 3. Calcular progreso del CURSO
        lesson = session.query(Lesson).filter_by(id=lesson_id).first()
        if not lesson:
            return {"error": "lesson_not_found"}

        course_id = lesson.course_id

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

        # 4. 🏆 VERIFICAR Y OTORGAR BADGES (LA MAGIA)
        # Guardamos cambios parciales para que el badge service vea el progreso actualizado
        session.flush() 
        
        badge_service = BadgeService()
        new_badges = badge_service.check_and_award_badges(user_id, session)

        session.commit()
        
        return {
            "lesson_completed": True,
            "xp_earned": xp_amount,
            "new_badges": new_badges, # ✅ ¡AQUÍ ESTÁN!
            "course_progress": {
                "course_id": course_id,
                "percentage": percentage,
                "completed": completed_count,
                "total": total_count,
                "course_completed": percentage == 100
            }
        }
    except Exception as e:
        session.rollback()
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
                return {
                    "course_id": progress.course_id,
                    "percentage": progress.percentage,
                    "completed_lessons": progress.completed_lessons,
                    "total_lessons": progress.total_lessons
                }
            return None

        progresses = session.query(UserCourseProgress).filter_by(user_id=user_id).all()
        return [{
            "course_id": p.course_id,
            "percentage": p.percentage,
            "completed_lessons": p.completed_lessons,
            "total_lessons": p.total_lessons
        } for p in progresses]
    finally:
        session.close()