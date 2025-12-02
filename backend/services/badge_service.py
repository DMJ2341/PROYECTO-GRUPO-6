# backend/services/badge_service.py - NO CAMBIOS NECESARIOS
from database.db import get_session
from models.badge import Badge
from models.user_badge import UserBadge
from models.user import User
from models.user_progress import UserCourseProgress, UserLessonProgress
from datetime import datetime

class BadgeService:
    def get_user_badges(self, user_id):
        session = get_session()
        try:
            user_badges = session.query(UserBadge, Badge).join(
                Badge, UserBadge.badge_id == Badge.id
            ).filter(UserBadge.user_id == user_id).all()
            
            return [{
                "id": badge.id,
                "name": badge.name,
                "description": badge.description,
                "icon": badge.icon,
                "earned_at": ub.earned_at.isoformat()
            } for ub, badge in user_badges]
        finally:
            session.close()

    # 🔥 EL MÉTODO MÁGICO
    def check_and_award_badges(self, user_id, session):
        """Revisa si el usuario merece nuevos badges y los otorga. (Usa sesión existente)"""
        new_badges = []
        user = session.query(User).get(user_id)
        if not user: return []

        xp_badges = session.query(Badge).filter_by(trigger_type='xp_milestone').all()
        for badge in xp_badges:
            if user.total_xp >= int(badge.trigger_value):
                if self._award(user_id, badge, session):
                    new_badges.append(badge.name)

        # Badges de Lecciones (
        total_lessons = session.query(UserLessonProgress).filter_by(user_id=user_id, completed=True).count()
        if total_lessons >= 1:
            first_badge = session.query(Badge).filter_by(trigger_type='first_lesson').first()
            if first_badge and self._award(user_id, first_badge, session):
                new_badges.append(first_badge.name)

        # Badges de Cursos Completados 
        completed_courses = session.query(UserCourseProgress).filter_by(user_id=user_id, percentage=100).all()
        for cp in completed_courses:
            
            c_badge = session.query(Badge).filter_by(
                trigger_type='course_completed', 
                trigger_value=str(cp.course_id)
            ).first()
            if c_badge and self._award(user_id, c_badge, session):
                new_badges.append(c_badge.name)

        # Badge Maestro (Todos los cursos básicos)
    
        if len(completed_courses) >= 5:
            master_badge = session.query(Badge).filter_by(trigger_type='all_basic_courses').first()
            if master_badge and self._award(user_id, master_badge, session):
                new_badges.append(master_badge.name)

        # 5. Badges de Racha (Streak)
        streak_badges = session.query(Badge).filter_by(trigger_type='streak').all()
        for badge in streak_badges:
            if user.current_streak >= int(badge.trigger_value):
                if self._award(user_id, badge, session):
                    new_badges.append(badge.name)

        return new_badges

    def _award(self, user_id, badge, session):
        """Helper privado para insertar si no existe"""
        exists = session.query(UserBadge).filter_by(user_id=user_id, badge_id=badge.id).first()
        if not exists:
            print(f"🏆 ¡Badge Otorgado! Usuario {user_id} ganó: {badge.name}")
            new_ub = UserBadge(user_id=user_id, badge_id=badge.id, earned_at=datetime.utcnow())
            session.add(new_ub)
            return True
        return False