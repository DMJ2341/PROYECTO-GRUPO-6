# backend/services/exam_service.py
from database.db import get_session
from models.assessments import UserExamAttempt, FinalExamQuestion
from datetime import datetime, timedelta
import json
import os

# Ruta a los datos 
DATA_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), '../data/assessments'))

class ExamService:
    def get_final_exam_content(self):
        # Aquí se cargaría el JSON del examen final.
        # Si aún no tienes el archivo 'final_integrative_exam.json', 
        # devolvemos una estructura básica para que no falle.
        return {
            "title": "Examen Final Integrador",
            "sections": [
                {"id": "knowledge", "title": "Conocimiento General", "questions": []},
                {"id": "case_study", "title": "Caso de Estudio", "questions": []},
                {"id": "design", "title": "Diseño de Solución", "questions": []}
            ]
        }

    def can_take_exam(self, user_id):
        session = get_session()
        try:
            # Buscar último intento
            last_attempt = session.query(UserExamAttempt).filter_by(
                user_id=user_id
            ).order_by(UserExamAttempt.started_at.desc()).first()

            if not last_attempt:
                return {"allowed": True, "attempt": 1}

            # Validar Cooldown 48h si no aprobó y ya terminó el intento
            if last_attempt.completed_at and not last_attempt.passed:
                hours_passed = (datetime.utcnow() - last_attempt.completed_at).total_seconds() / 3600
                if hours_passed < 48:
                    return {
                        "allowed": False, 
                        "reason": "cooldown", 
                        "wait_hours": round(48 - hours_passed, 1)
                    }

            # Validar Max Intentos (3)
            attempts_count = session.query(UserExamAttempt).filter_by(user_id=user_id).count()
            
            if attempts_count >= 3 and not last_attempt.passed:
                return {"allowed": False, "reason": "max_attempts"}

            return {"allowed": True, "attempt": attempts_count + 1}
        finally:
            session.close()

    def submit_exam(self, user_id, answers):
        # Lógica simplificada de calificación para el MVP
        # En producción, aquí compararías 'answers' con las respuestas correctas en DB
        score_percent = 0 
        passed = False
        
        # Lógica dummy para que funcione la prueba: si responde algo, le damos 100
        if answers:
            score_percent = 100
            passed = True
        
        session = get_session()
        try:
            # Calcular número de intento
            count = session.query(UserExamAttempt).filter_by(user_id=user_id).count()
            
            attempt = UserExamAttempt(
                user_id=user_id,
                score=score_percent,
                passed=passed,
                attempt_number=count + 1,
                started_at=datetime.utcnow(),
                completed_at=datetime.utcnow(),
                answers_log=answers # Guardamos lo que respondió
            )
            session.add(attempt)
            session.commit()
            
            return {
                "score": score_percent, 
                "passed": passed,
                "grade": "PASS - Expert" if passed else "FAIL"
            }
        finally:
            session.close()