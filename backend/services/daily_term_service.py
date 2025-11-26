# backend/services/daily_term_service.py
from database.db import get_session
from models.glossary import Glossary
from models.daily_term_log import DailyTermLog
from models.activity import Activity, ActivityType # Asegúrate de que ActivityType esté definido en activity.py
from sqlalchemy import func
from datetime import date
import sentry_sdk

def get_daily_term_for_user(user_id):
    """
    Obtiene el término del día para el usuario. No otorga XP automáticamente.
    """
    session = get_session()
    today = date.today()
    try:
        # 1. Determinar el término del día (Lógica de rotación simple)
        total_terms = session.query(Glossary).count()
        if total_terms == 0:
            return None

        day_of_year = today.timetuple().tm_yday
        term_id_offset = (day_of_year % total_terms) if total_terms > 0 else 0
        
        # Obtener el término basado en el offset
        daily_term = session.query(Glossary).order_by(Glossary.id).offset(term_id_offset).first()
        
        if not daily_term:
            return None

        # 2. Verificar si el usuario ya completó este término hoy
        already_viewed = session.query(DailyTermLog).filter(
            DailyTermLog.user_id == user_id,
            DailyTermLog.glossary_id == daily_term.id,
            func.date(DailyTermLog.viewed_at) == today
        ).first()

        return {
            "term": daily_term.to_dict(),
            "already_viewed_today": already_viewed is not None,
            "xp_reward": 5 # XP que se otorga al completarlo
        }
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return None
    finally:
        session.remove()

def complete_daily_term(user_id, term_id, xp_amount=5):
    """
    Marca el término como completado y otorga el XP.
    """
    session = get_session()
    today = date.today()
    
    try:
        # 1. Verificar si ya se completó hoy
        already_logged = session.query(DailyTermLog).filter(
            DailyTermLog.user_id == user_id,
            DailyTermLog.glossary_id == term_id,
            func.date(DailyTermLog.viewed_at) == today
        ).first()

        if already_logged:
            return {"success": False, "xp_earned": 0, "message": "Término diario ya completado hoy."}

        # 2. Registrar la actividad de XP
        activity = Activity(
            user_id=user_id,
            type=ActivityType.DAILY_TERM.value,
            xp_amount=xp_amount,
            description=f"Término diario ID {term_id} completado."
        )
        session.add(activity)

        # 3. Registrar el log del término diario
        daily_log = DailyTermLog(
            user_id=user_id,
            glossary_id=term_id
        )
        session.add(daily_log)
        
        session.commit()
        
        return {"success": True, "xp_earned": xp_amount, "message": f"¡Ganaste {xp_amount} XP por completar el término!"}

    except Exception as e:
        session.rollback()
        sentry_sdk.capture_exception(e)
        return {"success": False, "xp_earned": 0, "message": "Error interno al procesar el término."}
    finally:
        session.remove()