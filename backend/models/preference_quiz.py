# backend/services/preference_quiz.py
from database.db import get_session
from models.assessments import PreferenceQuestion, UserPreferenceResult
from services.preference_engine import PreferenceEngine
from datetime import datetime
import sentry_sdk

engine = PreferenceEngine()

def get_preference_questions():
    """Devuelve todas las preguntas activas del test de preferencias."""
    session = get_session()
    try:
        questions = session.query(PreferenceQuestion) \
            .filter_by(is_active=True) \
            .order_by(PreferenceQuestion.question_number) \
            .all()
        
        return [{
            "id": q.id,
            "question_number": q.question_number,
            "section": q.section,
            "question_text": q.question_text,
            "options": q.options
        } for q in questions]
    except Exception as e:
        sentry_sdk.capture_exception(e)
        raise e
    finally:
        session.close()


def submit_preference_answers(user_id: int, answers: dict, time_taken: int = None):
    """
    Procesa las respuestas del test de preferencias y guarda el resultado.
    answers = { "1": "A", "2": "B", ... }
    """
    session = get_session()
    try:
        # 1. Verificar que ya no haya hecho el test (salvo que sea retake)
        existing = session.query(UserPreferenceResult) \
            .filter_by(user_id=user_id) \
            .first()

        if existing and not existing.retaken:
            return {"error": "Ya completaste el test. Usa /retake si deseas hacerlo de nuevo."}

        # 2. Contar respuestas por equipo
        scores = {"Red Team": 0, "Blue Team": 0, "Purple Team": 0}

        questions = session.query(PreferenceQuestion).all()
        question_map = {q.id: q.options for q in questions}

        for q_id_str, choice in answers.items():
            q_id = int(q_id_str)
            options = question_map.get(q_id)
            if not options:
                continue

            # Buscar qué equipo tiene más peso en esa opción
            max_score = 0
            winning_team = None
            for opt in options:
                if opt["value"] == choice:
                    # Formato: {"text": "...", "value": "A", "scores": {"Red Team": 3, ...}}
                    team_scores = opt.get("scores", {})
                    for team, pts in team_scores.items():
                        if pts > max_score:
                            max_score = pts
                            winning_team = team
                        scores[team] += pts  # Sumamos todos los puntos

        # 3. Normalizar y determinar perfil
        normalized = engine.normalize_scores(scores)
        profile, confidence, secondary = engine.determine_profile(normalized)
        message = engine.get_result_message(profile, confidence, secondary)

        # 4. Guardar o actualizar resultado
        if existing:
            # Es un retake
            existing.red_score = scores["Red Team"]
            existing.blue_score = scores["Blue Team"]
            existing.purple_score = scores["Purple Team"]
            existing.red_percentage = normalized["Red Team"]
            existing.blue_percentage = normalized["Blue Team"]
            existing.purple_percentage = normalized["Purple Team"]
            existing.assigned_profile = profile
            existing.confidence_level = confidence
            existing.secondary_profile = secondary
            existing.personality_traits = message
            existing.time_taken = time_taken
            existing.completed_at = datetime.utcnow()
            existing.retaken = False  # Ya no está pendiente de retake
        else:
            result = UserPreferenceResult(
                user_id=user_id,
                red_score=scores["Red Team"],
                blue_score=scores["Blue Team"],
                purple_score=scores["Purple Team"],
                red_percentage=normalized["Red Team"],
                blue_percentage=normalized["Blue Team"],
                purple_percentage=normalized["Purple Team"],
                assigned_profile=profile,
                confidence_level=confidence,
                secondary_profile=secondary,
                personality_traits=message,
                time_taken=time_taken,
                completed_at=datetime.utcnow(),
                retaken=False
            )
            session.add(result)

        session.commit()

        # Devolver resultado completo con datos visuales
        full_result = engine.get_user_result(user_id)
        return full_result

    except Exception as e:
        session.rollback()
        sentry_sdk.capture_exception(e)
        raise e
    finally:
        session.close()