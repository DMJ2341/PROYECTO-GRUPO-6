# backend/services/glossary_favorite_service.py
from database.db import get_session
from models.user_glossary_favorite import UserGlossaryFavorite
from models.glossary import Glossary

def toggle_favorite(user_id: int, glossary_id: int):
    session = get_session()
    try:
        # Buscar si ya existe
        favorite = session.query(UserGlossaryFavorite).filter_by(
            user_id=user_id, 
            glossary_id=glossary_id
        ).first()

        if favorite:
            # Quitar de favoritos
            session.delete(favorite)
            session.commit()
            return {"action": "removed", "is_favorite": False}
        else:
            # Añadir a favoritos
            new_fav = UserGlossaryFavorite(user_id=user_id, glossary_id=glossary_id)
            session.add(new_fav)
            session.commit()
            return {"action": "added", "is_favorite": True}
    except Exception as e:
        session.rollback()
        raise e
    finally:
        session.close()

def get_user_favorites(user_id: int):
    session = get_session()
    try:
        # Join para obtener los datos completos del término
        favorites = session.query(Glossary).join(
            UserGlossaryFavorite,
            Glossary.id == UserGlossaryFavorite.glossary_id
        ).filter(
            UserGlossaryFavorite.user_id == user_id
        ).order_by(Glossary.term).all()

        return [term.to_dict() for term in favorites]
    finally:
        session.close()

def is_favorite(user_id: int, glossary_id: int) -> bool:
    session = get_session()
    try:
        exists = session.query(UserGlossaryFavorite).filter_by(
            user_id=user_id, 
            glossary_id=glossary_id
        ).first()
        return exists is not None
    finally:
        session.close()