from database.db import get_session
from models.glossary import Glossary
from models.daily_term_log import DailyTermLog
from services.activity_service import ActivityService
from sqlalchemy import func
from datetime import date

activity_service = ActivityService()
DAILY_XP = 5

def get_daily_term_for_user(user_id: int):
    session = get_session()
    try:
        today = date.today()

        # 1. ¿Ya vio el término de hoy?
        already_viewed = session.query(DailyTermLog).filter_by(
            user_id=user_id,
            viewed_date=today
        ).first()

        if already_viewed:
            # Devolver el mismo término que ya vio hoy
            term = session.query(Glossary).get(already_viewed.glossary_id)
            # ✅ CORRECCIÓN: Convertir a dict AHORA, dentro de la sesión viva
            term_dict = term.to_dict() 
            
            return {
                "term": term_dict,
                "already_viewed": True,
                "xp_reward": 0
            }

        # 2. Elegir término aleatorio no visto
        viewed_ids = [
            row[0] for row in session.query(DailyTermLog.glossary_id)
            .filter_by(user_id=user_id)
            .all()
        ]

        query = session.query(Glossary)
        if viewed_ids:
            query = query.filter(Glossary.id.notin_(viewed_ids))

        term = query.order_by(func.random()).first()

        if not term:
            # Si ya vio todos, repetir aleatorio
            term = session.query(Glossary).order_by(func.random()).first()

        if not term:
             return None

        # ✅ CORRECCIÓN: Convertir a dict AHORA, antes de cualquier commit/close
        term_dict = term.to_dict()
        term_id = term.id # Guardamos el ID simple para usarlo después

        # 3. Registrar visita y XP
        log = DailyTermLog(
            user_id=user_id,
            glossary_id=term_id,
            viewed_date=today
        )
        session.add(log)

        activity_service.create_activity(
            user_id=user_id,
            activity_type="daily_term",
            points=DAILY_XP,
            description="Término del día aprendido"
        )

        session.commit()

        return {
            "term": term_dict, # Devolvemos el diccionario, no el objeto SQLAlchemy
            "already_viewed": False,
            "xp_reward": DAILY_XP
        }

    except Exception as e:
        session.rollback()
        raise e
    finally:
        session.close()