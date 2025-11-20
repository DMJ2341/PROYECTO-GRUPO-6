# backend/services/activity_service.py - CORREGIDO PARA COINCIDIR CON BD
from database.db import Session
from models.activity import Activity
from sqlalchemy import func
from datetime import datetime

class ActivityService:
    def __init__(self):
        self.db = Session()
    
    def create_activity(self, user_id, activity_type, points=0, lesson_id=None, description=None):
        """Crear una nueva actividad"""
        try:
            activity = Activity(
                user_id=user_id,
                activity_type=activity_type,
                points=points,  # ✅ CAMBIADO de xp_earned
                lesson_id=lesson_id,
                description=description,  # ✅ AGREGADO
                timestamp=datetime.utcnow()  # ✅ CAMBIADO de created_at
            )
            self.db.add(activity)
            self.db.commit()
            self.db.refresh(activity)
            return activity
        except Exception as e:
            self.db.rollback()
            raise e
    
    def get_user_activities(self, user_id, limit=50):
        """Obtener actividades de un usuario"""
        return self.db.query(Activity).filter_by(user_id=user_id).order_by(Activity.timestamp.desc()).limit(limit).all()
    
    def get_user_points(self, user_id):
        """Obtener puntos totales de un usuario"""
        result = self.db.query(func.sum(Activity.points)).filter_by(user_id=user_id).first()  # ✅ CAMBIADO de xp_earned
        return result[0] or 0 if result else 0

    def get_total_xp(self, user_id):
        """Obtener XP total del usuario (alias para get_user_points)"""
        return self.get_user_points(user_id)
    
    def get_user_progress(self, user_id):
        """Obtener progreso completo del usuario"""
        try:
            total_activities = self.db.query(Activity).filter_by(user_id=user_id).count()
            total_points = self.get_user_points(user_id)
            
            stats_by_type = {}
            for activity_type in ['lesson_completed', 'course_completed', 'badge_earned', 'login']:
                count = self.db.query(Activity).filter_by(
                    user_id=user_id, 
                    activity_type=activity_type
                ).count()
                stats_by_type[activity_type] = count
            
            return {
                "user_id": user_id,
                "total_activities": total_activities,
                "total_points": total_points,
                "activities_by_type": stats_by_type,
                "streak": self.get_user_streak(user_id),
                "last_activity": self.get_last_activity(user_id)
            }
        except Exception as e:
            print(f"Error obteniendo progreso: {e}")
            return {
                "user_id": user_id,
                "total_activities": 0,
                "total_points": 0,
                "activities_by_type": {},
                "streak": 0,
                "last_activity": None
            }
    
    def get_user_streak(self, user_id):
        """Obtener racha de actividades del usuario"""
        try:
            from datetime import timedelta
            last_month = datetime.utcnow() - timedelta(days=30)
            
            activities = self.db.query(Activity).filter(
                Activity.user_id == user_id,
                Activity.timestamp >= last_month  # ✅ CAMBIADO de created_at
            ).count()
            
            return activities
        except Exception as e:
            print(f"Error calculando racha: {e}")
            return 0
    
    def get_last_activity(self, user_id):
        """Obtener última actividad del usuario"""
        try:
            last_activity = self.db.query(Activity).filter_by(user_id=user_id).order_by(Activity.timestamp.desc()).first()  # ✅ CAMBIADO
            if last_activity:
                return {
                    "id": last_activity.id,
                    "type": last_activity.activity_type,
                    "points": last_activity.points,  # ✅ CAMBIADO de xp_earned
                    "lesson_id": last_activity.lesson_id,
                    "description": last_activity.description,  # ✅ AGREGADO
                    "timestamp": last_activity.timestamp.isoformat()  # ✅ CAMBIADO de created_at
                }
            return None
        except Exception as e:
            print(f"Error obteniendo última actividad: {e}")
            return None
    
    def __del__(self):
        if hasattr(self, 'db'):
            self.db.close()