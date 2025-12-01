# backend/services/preference_quiz.py
from database.db import get_session
from models.assessments import PreferenceQuestion, UserPreferenceResult
from datetime import datetime
import sentry_sdk
import json
import os
import logging

# --- INICIALIZACIÓN DEL MOTOR ---
# La lógica del motor de cálculo (PreferenceEngine) se define aquí.
DATA_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), '../data/assessments'))

class PreferenceEngine:
    """
    Motor de cálculo para el test de preferencias vocacionales.
    Contiene la lógica para normalizar scores y determinar el perfil.
    """
    
    def load_visual_content(self):
        """Carga el JSON con el contenido visual de los resultados."""
        path = os.path.join(DATA_DIR, 'preference_results_visual.json')
        try:
            with open(path, 'r', encoding='utf-8') as f:
                return json.load(f)
        except FileNotFoundError:
            raise Exception(f"No se encontró el archivo de contenido visual en: {path}")
    
    def normalize_scores(self, raw_scores):
        """Normaliza los scores a porcentajes relativos (suma 100%)."""
        total = sum(raw_scores.values())
        
        # ... (Implementación de normalize_scores, asumida correcta) ...
        # [Debe ir aquí la lógica completa de normalize_scores]
        if total == 0:
            # Caso edge: todas las respuestas fueron neutras o nulas
            return {"Red Team": 0, "Blue Team": 0, "Purple Team": 0}
        
        return {team: round((score / total) * 100) for team, score in raw_scores.items()}
    
    def calculate_results(self, user_id: int, answers: dict) -> dict:
        """Calcula el perfil principal y secundario basado en las respuestas."""
        # ... (Implementación de calculate_results, asumida correcta) ...
        # Esta función debe ejecutar la lógica central del test y devolver los scores, perfil y mensaje.
        
        # Placeholder: Asumimos que esta es la lógica de tu engine
        scores = {"Red Team": 50, "Blue Team": 30, "Purple Team": 20} # Ejemplo
        normalized = self.normalize_scores(scores)
        profile = "Red Team"
        confidence = 0.85
        secondary = "Blue Team"
        message = "Te enfocas en el ataque..."
        
        return {
            "scores": scores,
            "normalized": normalized,
            "profile": profile,
            "confidence": confidence,
            "secondary_profile": secondary,
            "message": message
        }
        
    def get_user_result(self, user_id: int) -> dict:
        """
        Devuelve el resultado final del test de un usuario, incluyendo datos UI.
        """
        session = get_session()
        try:
            result = session.query(UserPreferenceResult).filter_by(user_id=user_id).first()
            
            if not result:
                return None
            
            # Cargar visual content
            visuals = self.load_visual_content()
            profile_ui = visuals.get(result.assigned_profile, {})
            
            return {
                "profile": result.assigned_profile,
                "confidence": result.confidence_level,
                "secondary_profile": result.secondary_profile,
                "scores": {
                    "raw": {
                        "Red Team": result.red_score,
                        "Blue Team": result.blue_score,
                        "Purple Team": result.purple_score
                    },
                    "normalized": {
                        "Red Team": result.red_percentage,
                        "Blue Team": result.blue_percentage,
                        "Purple Team": result.purple_percentage
                    }
                },
                "personality_traits": result.personality_traits,
                "completed_at": result.completed_at.isoformat() if result.completed_at else None,
                "time_taken": result.time_taken,
                "retaken": result.retaken,
                "ui_data": profile_ui
            }
        finally:
            session.close()


engine = PreferenceEngine() # Inicialización

# --- FUNCIONES DE SERVICIO (API) ---

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
    """
    session = get_session()
    try:
        # 1. Calcular resultados usando el Engine
        results = engine.calculate_results(user_id, answers)
        scores = results["scores"]
        normalized = results["normalized"]
        profile = results["profile"]
        confidence = results["confidence"]
        secondary = results["secondary_profile"]
        message = results["message"]

        # 2. Guardar o actualizar en DB
        existing = session.query(UserPreferenceResult).filter_by(user_id=user_id).first()
        
        if existing:
            # Actualizar resultado existente (como un retake)
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
            # Crear nuevo resultado
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