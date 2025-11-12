from datetime import date, timedelta
from models.activity import Activity
from sqlalchemy import func
from database.db import Session

class StreakService:
    @staticmethod
    def get_current_streak(user_id: int) -> int:
        """Obtener racha actual de días consecutivos completando lecciones"""
        session = Session()
        
        try:
            # Obtener última actividad de lección completada
            last_activity = session.query(func.max(Activity.created_at)).filter(
                Activity.user_id == user_id,
                Activity.type == 'lesson_completed'
            ).scalar()
            
            if not last_activity:
                return 0
                
            days_since_last = (date.today() - last_activity.date()).days
            
            # Si pasó más de 1 día, reiniciar racha
            if days_since_last > 1:
                return 0
            elif days_since_last == 0:
                # Hoy ya completó, verificar cuántos días seguidos
                streak = 0
                current_date = date.today()
                
                while True:
                    count = session.query(Activity).filter(
                        Activity.user_id == user_id,
                        Activity.type == 'lesson_completed',
                        func.date(Activity.created_at) == current_date
                    ).count()
                    
                    if count == 0:
                        break
                        
                    streak += 1
                    current_date -= timedelta(days=1)
                    
                return streak
            else:
                # Ayer completó, racha de 1
                return 1
                
        finally:
            session.close()
    
    @staticmethod
    def get_streak_bonus(streak_days: int) -> int:
        """Calcular bonus XP por racha"""
        if streak_days >= 7:
            return 25
        elif streak_days >= 3:
            return 15
        elif streak_days >= 2:
            return 10
        else:
            return 0