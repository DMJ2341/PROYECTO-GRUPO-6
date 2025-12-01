# backend/services/test_preference_service.py
from database.db import get_session
from models.test_preference import (
    TestQuestion, Certification, Lab, LearningPath,
    RoleSkill, AcademicReference, UserTestResult, UserTestAnswer
)
from datetime import datetime


class TestPreferenceService:
    """
    Servicio para manejar el Test de Preferencias Vocacionales
    Basado en Holland Code (RIASEC) adaptado para ciberseguridad
    """
    
    def get_questions(self):
        """Obtiene todas las preguntas del test ordenadas"""
        session = get_session()
        try:
            questions = session.query(TestQuestion).order_by(TestQuestion.order).all()
            return [
                {
                    'id': q.id,
                    'question': q.question,
                    'emoji': q.emoji,
                    'category': q.category,
                    'order': q.order
                }
                for q in questions
            ]
        finally:
            session.close()
    
    def submit_test(self, user_id, answers, time_taken=None):
        """
        Procesa las respuestas del test y calcula el resultado
        
        Args:
            user_id: ID del usuario
            answers: Dict {question_id: rating} con 28 respuestas (rating 1-5)
            time_taken: Tiempo en segundos que tardó
            
        Returns:
            Dict con resultado completo
        """
        session = get_session()
        try:
            # 1. Validar que tenga 28 respuestas
            if len(answers) != 28:
                raise ValueError(f"Se requieren 28 respuestas, recibidas: {len(answers)}")
            
            # 2. Calcular scores por dimensión
            scores = self._calculate_dimension_scores(answers, session)
            
            # 3. Determinar rol recomendado usando algoritmo Holland Code
            recommended_role, confidence = self._determine_role(scores)
            
            # 4. Guardar resultado en BD
            test_result = UserTestResult(
                user_id=user_id,
                recommended_role=recommended_role,
                confidence=confidence,
                investigative_score=scores['INVESTIGATIVE'],
                realistic_score=scores['REALISTIC'],
                social_score=scores['SOCIAL'],
                conventional_score=scores['CONVENTIONAL'],
                enterprising_score=scores['ENTERPRISING'],
                artistic_score=scores['ARTISTIC'],
                time_taken_seconds=time_taken
            )
            session.add(test_result)
            session.flush()
            
            # 5. Guardar respuestas individuales
            for question_id, rating in answers.items():
                answer = UserTestAnswer(
                    test_result_id=test_result.id,
                    question_id=int(question_id),
                    rating=rating
                )
                session.add(answer)
            
            session.commit()
            
            # 6. Obtener top 3 dimensiones
            sorted_scores = sorted(scores.items(), key=lambda x: x[1], reverse=True)
            top_dimensions = [dim[0] for dim in sorted_scores[:3]]
            
            return {
                'id': test_result.id,
                'recommended_role': recommended_role,
                'confidence': round(confidence, 2),
                'scores': scores,
                'top_dimensions': top_dimensions,
                'created_at': test_result.created_at.isoformat()
            }
            
        except Exception as e:
            session.rollback()
            raise e
        finally:
            session.close()
    
    def _calculate_dimension_scores(self, answers, session):
        """Calcula puntajes por cada dimensión Holland Code"""
        scores = {
            'INVESTIGATIVE': 0,
            'REALISTIC': 0,
            'SOCIAL': 0,
            'CONVENTIONAL': 0,
            'ENTERPRISING': 0,
            'ARTISTIC': 0
        }
        
        # Sumar ratings por categoría
        for question_id, rating in answers.items():
            question = session.query(TestQuestion).filter_by(id=int(question_id)).first()
            if question:
                scores[question.category] += rating
        
        return scores
    
    def _determine_role(self, scores):
        """
        Determina el rol recomendado usando algoritmo Holland Code adaptado
        
        Basado en investigación académica:
        - RED TEAM: Alto en Investigative + Realistic + Artistic (técnico, creativo, análisis)
        - BLUE TEAM: Alto en Investigative + Conventional + Social (procesos, colaboración)
        - PURPLE TEAM: Alto en Social + Investigative + balance (puente entre equipos)
        
        Returns:
            (role, confidence): Tupla con rol y nivel de confianza (0-1)
        """
        inv = scores['INVESTIGATIVE']
        real = scores['REALISTIC']
        soc = scores['SOCIAL']
        conv = scores['CONVENTIONAL']
        ent = scores['ENTERPRISING']
        art = scores['ARTISTIC']
        
        # Algoritmo con pesos basados en perfiles de roles
        red_team_score = (inv * 1.2) + (real * 1.3) + (art * 1.1)
        blue_team_score = (inv * 1.2) + (conv * 1.4) + (soc * 0.8)
        purple_team_score = (soc * 1.5) + (inv * 1.1) + ((real + conv) * 0.5)
        
        # Determinar ganador
        role_scores = {
            'RED_TEAM': red_team_score,
            'BLUE_TEAM': blue_team_score,
            'PURPLE_TEAM': purple_team_score
        }
        
        sorted_roles = sorted(role_scores.items(), key=lambda x: x[1], reverse=True)
        recommended_role = sorted_roles[0][0]
        top_score = sorted_roles[0][1]
        second_score = sorted_roles[1][1]
        
        # Calcular confianza (diferencia entre top y segundo)
        if top_score > 0:
            difference = top_score - second_score
            confidence = min(1.0, max(0.3, difference / top_score))
        else:
            confidence = 0.5
        
        return recommended_role, confidence
    
    def get_recommendations(self, role):
        """Obtiene todas las recomendaciones para un rol específico"""
        if role not in ['RED_TEAM', 'BLUE_TEAM', 'PURPLE_TEAM']:
            raise ValueError(f"Rol inválido: {role}")
        
        session = get_session()
        try:
            # Certificaciones (ordenadas con gratuitas primero)
            certifications = session.query(Certification)\
                .filter_by(role=role)\
                .order_by(Certification.order)\
                .all()
            
            # Labs
            labs = session.query(Lab)\
                .filter_by(role=role)\
                .all()
            
            # Learning Paths
            learning_paths = session.query(LearningPath)\
                .filter_by(role=role)\
                .all()
            
            # Skills
            skills = session.query(RoleSkill)\
                .filter_by(role=role)\
                .order_by(RoleSkill.order)\
                .all()
            
            # Referencia académica
            academic_ref = session.query(AcademicReference)\
                .filter_by(role=role)\
                .first()
            
            return {
                'role': role,
                'certifications': [
                    {
                        'id': c.id,
                        'name': c.name,
                        'provider': c.provider,
                        'is_free': c.is_free,
                        'url': c.url,
                        'difficulty': c.difficulty,
                        'description': c.description,
                        'price_info': c.price_info
                    }
                    for c in certifications
                ],
                'labs': [
                    {
                        'id': l.id,
                        'name': l.name,
                        'platform': l.platform,
                        'url': l.url,
                        'is_free': l.is_free,
                        'description': l.description,
                        'difficulty': l.difficulty
                    }
                    for l in labs
                ],
                'learning_paths': [
                    {
                        'id': lp.id,
                        'name': lp.name,
                        'platform': lp.platform,
                        'url': lp.url,
                        'estimated_hours': lp.estimated_hours,
                        'description': lp.description,
                        'is_free': lp.is_free
                    }
                    for lp in learning_paths
                ],
                'skills': [
                    {
                        'id': s.id,
                        'skill': s.skill
                    }
                    for s in skills
                ],
                'academic_reference': {
                    'reference': academic_ref.reference
                } if academic_ref else None
            }
        finally:
            session.close()
    
    def get_user_result(self, user_id):
        """Obtiene el último resultado del test del usuario"""
        session = get_session()
        try:
            result = session.query(UserTestResult)\
                .filter_by(user_id=user_id)\
                .order_by(UserTestResult.created_at.desc())\
                .first()
            
            if not result:
                return None
            
            # Obtener top 3 dimensiones
            scores = {
                'INVESTIGATIVE': result.investigative_score,
                'REALISTIC': result.realistic_score,
                'SOCIAL': result.social_score,
                'CONVENTIONAL': result.conventional_score,
                'ENTERPRISING': result.enterprising_score,
                'ARTISTIC': result.artistic_score
            }
            sorted_scores = sorted(scores.items(), key=lambda x: x[1], reverse=True)
            top_dimensions = [dim[0] for dim in sorted_scores[:3]]
            
            return {
                'id': result.id,
                'recommended_role': result.recommended_role,
                'confidence': round(result.confidence, 2),
                'scores': scores,
                'top_dimensions': top_dimensions,
                'created_at': result.created_at.isoformat()
            }
        finally:
            session.close()
    
    def get_test_history(self, user_id):
        """Obtiene historial de tests del usuario"""
        session = get_session()
        try:
            results = session.query(UserTestResult)\
                .filter_by(user_id=user_id)\
                .order_by(UserTestResult.created_at.desc())\
                .all()
            
            return [
                {
                    'id': r.id,
                    'recommended_role': r.recommended_role,
                    'confidence': round(r.confidence, 2),
                    'created_at': r.created_at.isoformat()
                }
                for r in results
            ]
        finally:
            session.close()