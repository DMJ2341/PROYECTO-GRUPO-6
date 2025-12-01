# backend/services/streak_service.py - LÓGICA PARA RACHAS (CORRECTO)
from database.db import get_session
from models.user import User
from datetime import datetime, timedelta

class StreakService:
    def get_current_streak(self, user_id):
        session = get_session()
        try:
            user = session.query(User).get(user_id)
            if not user:
                return 0
            if not user.last_activity_date:
                return 0
            today = datetime.now().date()
            if today - user.last_activity_date == timedelta(days=1):
                user.current_streak += 1
            elif today - user.last_activity_date > timedelta(days=1):
                user.current_streak = 1
            user.last_activity_date = today
            session.commit()
            return user.current_streak
        finally:
            session.close()

    def get_streak_bonus(self, streak_days):
        if streak_days >= 30:
            return 50
        elif streak_days >= 7:
            return 20
        return 0