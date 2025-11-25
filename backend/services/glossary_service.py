from database.db import get_session
from models.glossary import Glossary
from sqlalchemy import func

def get_all_glossary_terms():
    session = get_session()
    try:
        terms = session.query(Glossary).order_by(Glossary.term).all()
        return [term.to_dict() for term in terms]
    finally:
        session.close()

def search_glossary(query: str):
    session = get_session()
    try:
        query = query.strip().lower()
        terms = session.query(Glossary).filter(
            func.lower(Glossary.term).contains(query) |
            func.lower(Glossary.definition).contains(query) |
            func.lower(Glossary.acronym).contains(query)
        ).order_by(Glossary.term).all()
        return [term.to_dict() for term in terms]
    finally:
        session.close()

def get_daily_term():
    session = get_session()
    try:
        # Nota: func.random() es para PostgreSQL.
        term = session.query(Glossary).order_by(func.random()).first()
        return term.to_dict() if term else None
    finally:
        session.close()

def get_glossary_stats():
    session = get_session()
    try:
        total = session.query(Glossary).count()
        categories = session.query(Glossary.category, func.count(Glossary.id)).group_by(Glossary.category).all()
        return {
            "total_terms": total,
            "categories": [{"name": c[0] or "Sin categoría", "count": c[1]} for c in categories]
        }
    finally:
        session.close()