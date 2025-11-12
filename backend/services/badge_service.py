# backend/services/badge_service.py
import json
from database.db import Session
from models.badge import Badge
from models.user import UserBadge
from models.activity import Activity
from sqlalchemy import func


class BadgeService:
    def __init__(self):
        self.db = Session()

    # ---------- MÉDULA DEL CURSO 1 ----------
    @staticmethod
    def award_lesson_badges(user_id: int, lesson_id: int):
        """Otorga medallas al completar lecciones del CURSO 1."""
        mapping = {
            1: 1,   # Lección 1 → medalla 1: Primer Respondedor
            2: 2,   # Lección 2 → medalla 2: Cazador de Phishing
            3: 3,   # Lección 3 → medalla 3: Contenedor de Ransomware
            4: 4,   # Lección 4 → medalla 4: Guardián Móvil
            5: 5,   # Lección 5 → medalla 5: Guardián CIA
            6: 6    # Lección 6 → medalla 6: Escudo Ciudadano
        }
        badge_id = mapping.get(lesson_id)
        if not badge_id:
            return

        session = Session()
        try:
            # ¿Ya la tiene?
            exists = session.query(UserBadge).filter_by(user_id=user_id, badge_id=badge_id).first()
            if exists:
                return

            # Entregar
            ub = UserBadge(user_id=user_id, badge_id=badge_id, earned_at=func.now())
            session.add(ub)
            session.commit()
            print(f"🏅 Medalla '{badge_id}' otorgada a usuario {user_id}")
        finally:
            session.close()

    # ---------- CRUD BÁSICO ----------
    def get_all_badges(self):
        return self.db.query(Badge).all()

    def get_user_badges(self, user_id: int):
        return (self.db.query(UserBadge, Badge)
                       .join(Badge, UserBadge.badge_id == Badge.id)
                       .filter(UserBadge.user_id == user_id)
                       .all())

    def award_badge(self, user_id: int, badge_id: int):
        existing = self.db.query(UserBadge).filter_by(user_id=user_id, badge_id=badge_id).first()
        if existing:
            return existing
        ub = UserBadge(user_id=user_id, badge_id=badge_id, earned_at=func.now(), earned_value=1)
        self.db.add(ub)
        self.db.commit()
        self.db.refresh(ub)
        return ub

    def check_and_award_badges(self, user_id: int):
        """Otorga badges por puntos o actividades (genérico)."""
        user_badges = []
        from services.activity_service import ActivityService
        act = ActivityService()
        total_points = act.get_user_points(user_id)
        total_activities = self.db.query(Activity).filter_by(user_id=user_id).count()

        # Por puntos
        for badge in self.db.query(Badge).filter(Badge.points_required <= total_points).all():
            ub = self.award_badge(user_id, badge.id)
            if ub:
                user_badges.append(ub)

        # Por actividades (JSON condition)
        for badge in self.db.query(Badge).all():
            cond = json.loads(badge.condition) if badge.condition else {}
            if cond.get("type") == "activities" and cond.get("count", 0) <= total_activities:
                ub = self.award_badge(user_id, badge.id)
                if ub:
                    user_badges.append(ub)
        return user_badges

    def get_badge_progress(self, user_id: int):
        user_badge_ids = [ub.badge_id for ub in self.db.query(UserBadge).filter_by(user_id=user_id).all()]
        available = self.db.query(Badge).filter(~Badge.id.in_(user_badge_ids)).all()
        from services.activity_service import ActivityService
        total_points = ActivityService().get_user_points(user_id)
        progress = []
        for b in available:
            pct = min(100, (total_points / b.points_required * 100)) if b.points_required else 0
            progress.append({"badge": b, "progress_percent": pct})
        return progress

    def __del__(self):
        if hasattr(self, 'db'):
            self.db.close()