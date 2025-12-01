# backend/delete_user.py
import sys
import os

# Añadir el path para importar módulos locales
sys.path.append(os.path.abspath(os.path.dirname(__file__)))

from database.db import get_session
from models.user import User
from models.refresh_token import RefreshToken
from models.email_verification import EmailVerificationCode
from models.user_progress import UserCourseProgress, UserLessonProgress
from models.user_badge import UserBadge
from models.daily_term_log import DailyTermLog
from models.user_glossary_progress import UserGlossaryProgress
from models.user_glossary_favorite import UserGlossaryFavorite
from models.test_preference import TestResult, TestAnswer # ✅ Modelos nuevos del test

def delete_user_by_email(email):
    session = get_session()
    try:
        user = session.query(User).filter_by(email=email).first()
        
        if not user:
            print(f"❌ El usuario {email} no existe.")
            return

        print(f"🗑️  Eliminando datos de: {user.name} ({user.email})...")

        # --- 1. Eliminar Tablas de Autenticación ---
        session.query(RefreshToken).filter_by(user_id=user.id).delete()
        session.query(EmailVerificationCode).filter_by(user_id=user.id).delete()
        
        # --- 2. Eliminar Progreso de Cursos y Gamificación ---
        session.query(UserCourseProgress).filter_by(user_id=user.id).delete()
        session.query(UserLessonProgress).filter_by(user_id=user.id).delete()
        session.query(UserBadge).filter_by(user_id=user.id).delete()
        session.query(DailyTermLog).filter_by(user_id=user.id).delete()

        # --- 3. Eliminar Datos del Glosario ---
        session.query(UserGlossaryProgress).filter_by(user_id=user.id).delete()
        session.query(UserGlossaryFavorite).filter_by(user_id=user.id).delete()

        # --- 4. Eliminar Exámenes y Tests (Nuevos y Viejos) ---
        
        # A. Examen Final
        session.query(UserExamAttempt).filter_by(user_id=user.id).delete()

        # B. Test de Preferencias (NUEVO)
        # Primero borramos las respuestas (hijas)
        user_test_ids = [r.id for r in session.query(TestResult).filter_by(user_id=user.id).all()]
        if user_test_ids:
            session.query(TestAnswer).filter(TestAnswer.test_result_id.in_(user_test_ids)).delete(synchronize_session=False)
        # Luego el resultado (padre)
        session.query(TestResult).filter_by(user_id=user.id).delete()
        
        # C. Test de Preferencias (VIEJO - Limpieza por si acaso)
        try:
            session.query(UserPreferenceResult).filter_by(user_id=user.id).delete()
        except:
            pass

        # --- 5. Finalmente eliminar al usuario ---
        session.delete(user)
        
        session.commit()
        print("✅ Usuario eliminado completamente (incluyendo cursos, glosario y tests).")
        print("   -> Ya puedes volver a registrarte con este correo.")
        
    except Exception as e:
        session.rollback()
        print(f"❌ Error eliminando usuario: {e}")
    finally:
        session.close()

if __name__ == "__main__":
    # Cambia esto por el email que quieres borrar
    email_to_delete = "meryjulian300@gmail.com"
    delete_user_by_email(email_to_delete)