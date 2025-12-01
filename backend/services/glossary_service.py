# backend/services/glossary_service.py
from database.db import get_session
from models.glossary import Glossary
from models.user_glossary_progress import UserGlossaryProgress
from sqlalchemy import func, or_
from datetime import datetime

def get_all_glossary_terms(user_id=None):
    """Obtiene todos los términos, con progreso del usuario si está autenticado."""
    session = get_session()
    try:
        terms = session.query(Glossary).order_by(Glossary.term_es).all()
        
        result = []
        for term in terms:
            term_dict = term.to_dict()
            
            # Si hay usuario, agregar progreso
            if user_id:
                progress = session.query(UserGlossaryProgress).filter_by(
                    user_id=user_id,
                    glossary_id=term.id
                ).first()
                
                term_dict['is_learned'] = progress.is_learned if progress else False
                term_dict['times_practiced'] = progress.times_practiced if progress else 0
            else:
                term_dict['is_learned'] = False
                term_dict['times_practiced'] = 0
            
            result.append(term_dict)
        
        return result
    finally:
        session.close()

def search_glossary(query: str, user_id=None):
    """Busca términos en el glosario (bilingüe)."""
    session = get_session()
    try:
        query = query.strip().lower()
        
        # Buscar en español e inglés
        terms = session.query(Glossary).filter(
            or_(
                func.lower(Glossary.term_en).contains(query),
                func.lower(Glossary.term_es).contains(query),
                func.lower(Glossary.definition_en).contains(query),
                func.lower(Glossary.definition_es).contains(query),
                func.lower(Glossary.acronym).contains(query) if Glossary.acronym else False
            )
        ).order_by(Glossary.term_es).all()
        
        result = []
        for term in terms:
            term_dict = term.to_dict()
            
            if user_id:
                progress = session.query(UserGlossaryProgress).filter_by(
                    user_id=user_id,
                    glossary_id=term.id
                ).first()
                
                term_dict['is_learned'] = progress.is_learned if progress else False
                term_dict['times_practiced'] = progress.times_practiced if progress else 0
            else:
                term_dict['is_learned'] = False
                term_dict['times_practiced'] = 0
            
            result.append(term_dict)
        
        return result
    finally:
        session.close()

def mark_term_as_learned(user_id: int, glossary_id: int, is_learned: bool):
    """Marca un término como aprendido/no aprendido."""
    session = get_session()
    try:
        progress = session.query(UserGlossaryProgress).filter_by(
            user_id=user_id,
            glossary_id=glossary_id
        ).first()
        
        if not progress:
            progress = UserGlossaryProgress(
                user_id=user_id,
                glossary_id=glossary_id,
                is_learned=is_learned,
                learned_at=datetime.utcnow() if is_learned else None
            )
            session.add(progress)
        else:
            progress.is_learned = is_learned
            progress.learned_at = datetime.utcnow() if is_learned else None
            progress.updated_at = datetime.utcnow()
        
        session.commit()
        return {"success": True, "is_learned": is_learned}
    finally:
        session.close()

def get_learned_terms(user_id: int):
    """Obtiene solo los términos marcados como aprendidos."""
    session = get_session()
    try:
        learned_progress = session.query(UserGlossaryProgress).filter_by(
            user_id=user_id,
            is_learned=True
        ).all()
        
        glossary_ids = [p.glossary_id for p in learned_progress]
        
        if not glossary_ids:
            return []
        
        terms = session.query(Glossary).filter(
            Glossary.id.in_(glossary_ids)
        ).all()
        
        result = []
        for term in terms:
            term_dict = term.to_dict()
            
            progress = next((p for p in learned_progress if p.glossary_id == term.id), None)
            if progress:
                term_dict['is_learned'] = True
                term_dict['times_practiced'] = progress.times_practiced
                term_dict['learned_at'] = progress.learned_at.isoformat() if progress.learned_at else None
            
            result.append(term_dict)
        
        return result
    finally:
        session.close()

def get_glossary_stats(user_id=None):
    """Obtiene estadísticas del glosario."""
    session = get_session()
    try:
        total = session.query(Glossary).count()
        
        categories = session.query(
            Glossary.category, 
            func.count(Glossary.id)
        ).group_by(Glossary.category).all()
        
        stats = {
            "total_terms": total,
            "categories": [
                {"name": c[0] or "Sin categoría", "count": c[1]} 
                for c in categories
            ]
        }
        
        # Si hay usuario, agregar progreso
        if user_id:
            learned_count = session.query(UserGlossaryProgress).filter_by(
                user_id=user_id,
                is_learned=True
            ).count()
            
            stats["learned_count"] = learned_count
            stats["progress_percentage"] = round((learned_count / total * 100), 1) if total > 0 else 0
        
        return stats
    finally:
        session.close()

def record_quiz_attempt(user_id: int, glossary_id: int, is_correct: bool):
    """Registra un intento de práctica (quiz)."""
    session = get_session()
    try:
        progress = session.query(UserGlossaryProgress).filter_by(
            user_id=user_id,
            glossary_id=glossary_id
        ).first()
        
        if not progress:
            progress = UserGlossaryProgress(
                user_id=user_id,
                glossary_id=glossary_id,
                times_practiced=1,
                times_correct=1 if is_correct else 0,
                last_practiced_at=datetime.utcnow()
            )
            session.add(progress)
        else:
            progress.times_practiced += 1
            if is_correct:
                progress.times_correct += 1
            progress.last_practiced_at = datetime.utcnow()
            progress.updated_at = datetime.utcnow()
        
        session.commit()
        
        # Calcular accuracy
        accuracy = (progress.times_correct / progress.times_practiced * 100) if progress.times_practiced > 0 else 0
        
        return {
            "success": True,
            "times_practiced": progress.times_practiced,
            "times_correct": progress.times_correct,
            "accuracy": round(accuracy, 1)
        }
    finally:
        session.close()