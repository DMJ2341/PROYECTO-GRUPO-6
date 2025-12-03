# backend/services/progress_service.py
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
        user = session.query(User).filter_by(id=user_id).first()
        if not user:
            return {"error": "user_not_found"}

        lesson = session.query(Lesson).filter_by(id=lesson_id).first()
        if not lesson:
            return {"error": "lesson_not_found"}

        course = session.query(Course).filter_by(id=lesson.course_id).first()

        # Verificar si ya estaba completada
        progress = session.query(UserLessonProgress).filter_by(
            user_id=user_id, lesson_id=lesson_id
        ).first()

        # Calcular XP a otorgar
        xp_amount = lesson.xp_reward if lesson.xp_reward else 20

        # Si ya estaba completada → no dar XP de nuevo
        xp_earned = 0
        if not progress or not progress.completed:
            xp_earned = xp_amount
            current_xp = user.total_xp if user.total_xp is not None else 0
            user.total_xp = current_xp + xp_earned

        # Crear o actualizar progreso de la lección
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
            if not progress.completed:
                progress.completed = True
                progress.completed_at = datetime.utcnow()
            progress.attempts += 1

        # Registrar actividad solo si se ganó XP
        if xp_earned > 0:
            activity_service.create_activity(
                user_id=user_id,
                activity_type="lesson_completed",
                points=xp_earned,
                lesson_id=lesson_id,
                description=f"Lección {lesson_id} completada",
                session=session
            )

        # Calcular progreso del curso
        total_lessons = session.query(Lesson).filter_by(course_id=lesson.course_id).count()
        completed_count = session.query(UserLessonProgress).join(Lesson).filter(
            UserLessonProgress.user_id == user_id,
            Lesson.course_id == lesson.course_id,
            UserLessonProgress.completed == True
        ).count()

        percentage = int((completed_count / total_lessons) * 100) if total_lessons > 0 else 0

        # Actualizar progreso del curso
        course_prog = session.query(UserCourseProgress).filter_by(
            user_id=user_id, course_id=lesson.course_id
        ).first()

        if not course_prog:
            course_prog = UserCourseProgress(
                user_id=user_id,
                course_id=lesson.course_id,
                completed_lessons=completed_count,
                total_lessons=total_lessons,
                percentage=percentage
            )
            session.add(course_prog)
        else:
            course_prog.completed_lessons = completed_count
            course_prog.total_lessons = total_lessons
            course_prog.percentage = percentage
            if percentage == 100 and not course_prog.completed_at:
                course_prog.completed_at = datetime.utcnow()

        # Detectar nuevas insignias
        session.flush()
        badge_service = BadgeService()
        new_badges = badge_service.check_and_award_badges(user_id, session)

        # COMMIT + REFRESH (¡ESTO ES LO QUE ARREGLA EL PROBLEMA DEL XP!)
        session.commit()
        session.refresh(user)   # ← ¡AQUÍ ESTÁ LA MAGIA!

        return {
            "lesson_completed": True,
            "xp_earned": xp_earned,
            "new_badges": new_badges,
            "course_progress": {
                "course_id": lesson.course_id,
                "title": course.title if course else "Curso",
                "percentage": percentage,
                "completed_lessons": completed_count,
                "total_lessons": total_lessons,
                "completed": percentage == 100
            }
        }

    except Exception as e:
        session.rollback()
        print(f"Error en progress: {e}")
        raise e
    finally:
        session.close()


# La otra función la dejas tal cual
def get_user_course_progress(user_id: int, course_id: int = None):
    session = get_session()
    try:
        if course_id:
            progress = session.query(UserCourseProgress).filter_by(user_id=user_id, course_id=course_id).first()
            if progress:
                course = session.query(Course).filter_by(id=course_id).first()
                return {
                    "course_id": progress.course_id,
                    "title": course.title,
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
                "title": course.title,
                "percentage": p.percentage,
                "completed_lessons": p.completed_lessons,
                "total_lessons": p.total_lessons
            })
        return result
    finally:
        session.close()