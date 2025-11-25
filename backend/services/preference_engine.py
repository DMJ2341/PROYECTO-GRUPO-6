# backend/services/preference_engine.py

from database.db import get_session
from models.assessments import UserPreferenceResult, PreferenceQuestion
import json
import os
from datetime import datetime

DATA_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), '../data/assessments'))

class PreferenceEngine:
    """
    Motor de cálculo para el test de preferencias vocacionales.
    Basado en Holland RIASEC + NICE Framework adaptado a cybersecurity.
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
        """
        Normaliza los scores a porcentajes relativos (suma 100%).
        
        Args:
            raw_scores: dict con {"Red Team": X, "Blue Team": Y, "Purple Team": Z}
        
        Returns:
            dict con porcentajes normalizados
        """
        total = sum(raw_scores.values())
        
        if total == 0:
            # Caso edge: todas las respuestas fueron neutras
            return {
                "Red Team": 33.33,
                "Blue Team": 33.33,
                "Purple Team": 33.34
            }
        
        return {
            team: round((score / total) * 100, 2)
            for team, score in raw_scores.items()
        }
    
    def determine_profile(self, normalized_scores):
        """
        Determina el perfil asignado basado en los scores normalizados.
        
        Returns:
            tuple: (assigned_profile, confidence_level, secondary_profile)
        """
        # Ordenar de mayor a menor
        sorted_teams = sorted(
            normalized_scores.items(),
            key=lambda x: x[1],
            reverse=True
        )
        
        winner = sorted_teams[0][0]
        winner_score = sorted_teams[0][1]
        second = sorted_teams[1][0]
        second_score = sorted_teams[1][1]
        
        difference = winner_score - second_score
        
        # Determinar confidence level
        if difference >= 20:
            confidence = "dominant"  # Clara dominancia
            secondary = None
        elif difference >= 10:
            confidence = "strong"  # Fuerte pero no dominante
            secondary = None
        else:
            confidence = "hybrid"  # Perfil híbrido
            secondary = second
        
        return winner, confidence, secondary
    
    def analyze_personality_traits(self, answers, questions_data):
        """
        Analiza rasgos de personalidad basado en respuestas.
        
        Returns:
            dict con rasgos identificados
        """
        traits = {
            "creativity": 0,
            "analytical_thinking": 0,
            "strategic_vision": 0,
            "detail_oriented": 0,
            "collaboration": 0,
            "independent": 0
        }
        
        # Mapeo simplificado de respuestas a traits
        # En producción, esto sería más sofisticado
        for q_id, answer in answers.items():
            q = next((q for q in questions_data if str(q['id']) == str(q_id)), None)
            if not q:
                continue
            
            selected = next((opt for opt in q['options'] if opt['id'] == answer), None)
            if not selected:
                continue
            
            team = selected['team']
            
            # Mapeo team → traits
            if team == "Red Team":
                traits["creativity"] += selected.get('weight', 1)
                traits["independent"] += 1
            elif team == "Blue Team":
                traits["analytical_thinking"] += selected.get('weight', 1)
                traits["detail_oriented"] += 1
            elif team == "Purple Team":
                traits["strategic_vision"] += selected.get('weight', 1)
                traits["collaboration"] += 1
        
        # Normalizar a high/medium/low
        def categorize(value, thresholds=(30, 15)):
            if value >= thresholds[0]:
                return "high"
            elif value >= thresholds[1]:
                return "medium"
            else:
                return "low"
        
        return {
            trait: categorize(value)
            for trait, value in traits.items()
        }
    
    def calculate_and_save(self, user_id, answers, time_taken=None):
        """
        Calcula resultado del test y guarda en base de datos.
        
        Args:
            user_id: ID del usuario
            answers: dict con respuestas {"1": "a", "2": "b", ...}
            time_taken: segundos que tardó (opcional)
        
        Returns:
            dict con resultado completo incluyendo UI data
        """
        session = get_session()
        
        try:
            # 1. Cargar preguntas desde DB
            questions = session.query(PreferenceQuestion).order_by(PreferenceQuestion.question_number).all()
            
            if len(questions) != 25:
                raise Exception(f"Se esperaban 25 preguntas, se encontraron {len(questions)}")
            
            # 2. Inicializar scores
            raw_scores = {
                "Red Team": 0,
                "Blue Team": 0,
                "Purple Team": 0
            }
            
            # 3. Calcular scores
            answers_log = {}
            
            for q in questions:
                q_id_str = str(q.id)
                ans_key = answers.get(q_id_str)
                
                if not ans_key:
                    continue  # Usuario skipped esta pregunta
                
                # Buscar opción seleccionada
                selected_opt = next(
                    (opt for opt in q.options if opt['id'] == ans_key),
                    None
                )
                
                if selected_opt:
                    team = selected_opt['team']
                    weight = selected_opt.get('weight', 1)
                    raw_scores[team] += weight
                    
                    # Log para auditoría
                    answers_log[q_id_str] = {
                        "selected": ans_key,
                        "team": team,
                        "weight": weight,
                        "question": q.question_text[:50]  # Primeros 50 chars
                    }
            
            # 4. Normalizar scores
            normalized_scores = self.normalize_scores(raw_scores)
            
            # 5. Determinar perfil
            assigned_profile, confidence, secondary = self.determine_profile(normalized_scores)
            
            # 6. Analizar traits de personalidad
            questions_data = [
                {
                    'id': q.id,
                    'options': q.options
                }
                for q in questions
            ]
            personality_traits = self.analyze_personality_traits(answers, questions_data)
            
            # 7. Verificar si ya existe resultado (para retakes)
            existing = session.query(UserPreferenceResult).filter_by(user_id=user_id).first()
            
            if existing:
                # Update
                existing.red_score = raw_scores["Red Team"]
                existing.blue_score = raw_scores["Blue Team"]
                existing.purple_score = raw_scores["Purple Team"]
                existing.red_percentage = normalized_scores["Red Team"]
                existing.blue_percentage = normalized_scores["Blue Team"]
                existing.purple_percentage = normalized_scores["Purple Team"]
                existing.assigned_profile = assigned_profile
                existing.confidence_level = confidence
                existing.secondary_profile = secondary
                existing.answers_log = answers_log
                existing.personality_traits = personality_traits
                existing.completed_at = datetime.utcnow()
                existing.time_taken = time_taken
                existing.retaken = True
                
                result_record = existing
            else:
                # Insert nuevo
                result_record = UserPreferenceResult(
                    user_id=user_id,
                    red_score=raw_scores["Red Team"],
                    blue_score=raw_scores["Blue Team"],
                    purple_score=raw_scores["Purple Team"],
                    red_percentage=normalized_scores["Red Team"],
                    blue_percentage=normalized_scores["Blue Team"],
                    purple_percentage=normalized_scores["Purple Team"],
                    assigned_profile=assigned_profile,
                    confidence_level=confidence,
                    secondary_profile=secondary,
                    answers_log=answers_log,
                    personality_traits=personality_traits,
                    time_taken=time_taken,
                    retaken=False
                )
                session.add(result_record)
            
            session.commit()
            
            # 8. Cargar contenido visual
            visuals = self.load_visual_content()
            profile_ui = visuals.get(assigned_profile, {})
            
            # 9. Construir respuesta completa
            return {
                "success": True,
                "profile": assigned_profile,
                "confidence": confidence,
                "secondary_profile": secondary,
                "scores": {
                    "raw": raw_scores,
                    "normalized": normalized_scores
                },
                "personality_traits": personality_traits,
                "ui_data": profile_ui,  # Contiene todo el JSON visual
                "message": self._generate_message(assigned_profile, confidence, secondary)
            }
            
        except Exception as e:
            session.rollback()
            raise Exception(f"Error calculando resultado: {str(e)}")
        finally:
            session.close()
    
    def _generate_message(self, profile, confidence, secondary):
        """Genera mensaje personalizado basado en resultado."""
        messages = {
            "Red Team": {
                "dominant": "¡Tu perfil es claramente Red Team! Tienes mentalidad ofensiva natural.",
                "strong": "Tu perfil es Red Team con alta confianza. El camino offensive es para ti.",
                "hybrid": f"Tu perfil principal es Red Team, pero también tienes afinidad con {secondary}. Considera un rol híbrido."
            },
            "Blue Team": {
                "dominant": "¡Tu perfil es claramente Blue Team! Defensa y análisis son tu fuerte.",
                "strong": "Tu perfil es Blue Team con alta confianza. La defensa cibernética te espera.",
                "hybrid": f"Tu perfil principal es Blue Team, pero también muestras tendencias {secondary}. Un rol híbrido puede ser ideal."
            },
            "Purple Team": {
                "dominant": "¡Tu perfil es claramente Purple Team! Estrategia y visión integral te definen.",
                "strong": "Tu perfil es Purple Team con alta confianza. Rol de integración y estrategia es perfecto para ti.",
                "hybrid": f"Tu perfil es Purple Team con influencia {secondary}. Perfil versátil e integrador."
            }
        }
        
        return messages.get(profile, {}).get(confidence, "Resultado calculado exitosamente.")
    
    def get_user_result(self, user_id):
        """
        Obtiene resultado guardado de un usuario.
        
        Returns:
            dict con resultado + UI data, o None si no existe
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