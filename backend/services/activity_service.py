# backend/services/activity_service.py - LÓGICA PARA ACTIVIDADES Y XP (CORRECTO)
from database.db import get_session
from models.activity import Activity

class ActivityService:
    def create_activity(self, user_id, activity_type, points, lesson_id=None, description=None):
        session = get_session()
        try:
            new_activity = Activity(
                user_id=user_id,
                activity_type=activity_type,
                points=points,
                lesson_id=lesson_id,
                description=description
            )
            session.add(new_activity)
            session.commit()
            return new_activity.id
        finally:
            session.close()

    def get_total_xp(self, user_id):
        session = get_session()
        try:
            activities = session.query(Activity).filter_by(user_id=user_id).all()
            return sum(a.points for a in activities)
        finally:
            session.close()