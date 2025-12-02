# backend/services/activity_service.py 
from database.db import get_session
from models.activity import Activity
from datetime import datetime

class ActivityService:
    def create_activity(self, user_id, activity_type, points, lesson_id=None, description=None, session=None):
        """
        Crea una nueva actividad.
        
        Args:
            session: Si se provee, usa esta sesión existente (NO hace commit).
                    Si es None, crea su propia sesión y hace commit.
        """
        own_session = session is None
        if own_session:
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
            
            
            if own_session:
                session.commit()
                return new_activity.id
            else:
                
                session.flush()  # Para obtener el ID
                return new_activity.id
        except Exception as e:
            if own_session:
                session.rollback()
            raise e
        finally:
            if own_session:
                session.close()

    def get_total_xp(self, user_id):
        session = get_session()
        try:
            activities = session.query(Activity).filter_by(user_id=user_id).all()
            return sum(a.points for a in activities)
        finally:
            session.close()