# backend/services/streak_service.py
from database.db import get_session
from models.user import User
from datetime import datetime, timedelta

class StreakService:
    def update_and_get_streak(self, user_id: int):
        """
        Registra la visita de hoy y calcula la racha actual.
        Si es la primera vez que entra, o perdió la racha, la inicia en 1.
        """
        session = get_session()
        try:
            user = session.query(User).filter_by(id=user_id).first()
            if not user:
                return 0

            today = datetime.utcnow().date()
            last_date = user.last_activity_date

            # CASO 1: Usuario nuevo o nunca ha tenido actividad
            # Solución al problema: Inicia en 1 inmediatamente.
            if last_date is None:
                user.current_streak = 1
                user.last_activity_date = today
                session.commit()
                return 1

            # CASO 2: El usuario ya entró hoy (No hacemos nada, devolvemos el valor actual)
            if last_date == today:
                return user.current_streak

            # CASO 3: El usuario entró ayer (Racha continúa)
            # Si la última actividad fue ayer, sumamos 1.
            if last_date == today - timedelta(days=1):
                user.current_streak += 1
                user.last_activity_date = today
                # Actualizar max_streak si se supera
                if user.current_streak > (user.max_streak or 0):
                    user.max_streak = user.current_streak
                session.commit()
                return user.current_streak

            # CASO 4: El usuario rompió la racha (entró hace 2 días o más)
            # Reiniciamos a 1 (porque hoy cuenta como el primer día de la nueva racha)
            user.current_streak = 1
            user.last_activity_date = today
            session.commit()
            return 1

        except Exception as e:
            print(f"Error actualizando racha: {e}")
            session.rollback()
            return 0
        finally:
            session.close()

    def get_current_streak(self, user_id: int):
        """Solo lee la racha sin modificarla (útil para perfil u otras vistas)."""
        session = get_session()
        try:
            user = session.query(User).filter_by(id=user_id).first()
            return user.current_streak if user else 0
        finally:
            session.close()