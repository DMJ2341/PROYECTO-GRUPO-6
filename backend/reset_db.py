from app import app
from database.db import Base, engine

# Importamos TODOS los modelos para asegurarnos de que SQLAlchemy los conozca
from models.user import User
from models.course import Course
from models.lesson import Lesson
from models.user_progress import UserCourseProgress, UserLessonProgress
from models.refresh_token import RefreshToken
from models.user_badge import UserBadge
from models.activity import Activity
from models.badge import Badge
from models.glossary import Glossary
from models.daily_term_log import DailyTermLog
from models.password_reset_token import PasswordResetToken
from models.user_glossary_favorite import UserGlossaryFavorite

if __name__ == "__main__":
    with app.app_context():
        print("🗑️  Eliminando tablas antiguas...")
        # Esto borra TODAS las tablas definidas en los modelos importados
        Base.metadata.drop_all(engine)
        
        print("✨ Creando tablas nuevas con esquema actualizado...")
        # Esto crea las tablas de nuevo con todas las columnas (total_xp, etc.)
        Base.metadata.create_all(engine)
        
        print("✅ Base de datos reiniciada exitosamente.")