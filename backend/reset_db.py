# backend/reset_db.py
from app import app
from database.db import Base, engine
from sqlalchemy import text

# Importamos TODOS los modelos actuales para que SQLAlchemy los conozca
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
from models.user_glossary_progress import UserGlossaryProgress
# Nuevos modelos del Test
from models.test_preference import (
    TestQuestion, Certification, Lab, LearningPath,
    RoleSkill, AcademicReference, TestResult, TestAnswer
)

if __name__ == "__main__":
    with app.app_context():
        print("🔄 Iniciando reinicio de base de datos...")
        
        # 1. Borrar tablas "Zombies" y Vistas manualmente (SQL Crudo)
        # Esto es necesario porque estas tablas ya no existen en tus modelos de Python,
        # pero siguen existiendo en PostgreSQL y bloquean el borrado de 'users'.
        try:
            with engine.connect() as connection:
                with connection.begin():
                    print("🔥 Limpiando tablas antiguas y huérfanas...")
                    # Borramos tablas viejas usando CASCADE para romper relaciones
                    tables_to_drop = [
                        "v_glossary_user_stats",      # Vista
                        "user_exam_attempts",         # Tabla vieja
                        "user_preference_results",    # Tabla vieja
                        "profile_view_logs",          # Tabla vieja
                        "final_exam_questions",       # Tabla vieja
                        "preference_questions"        # Tabla vieja (si existiera con ese nombre)
                    ]
                    
                    for table in tables_to_drop:
                        connection.execute(text(f"DROP TABLE IF EXISTS {table} CASCADE;"))
                        connection.execute(text(f"DROP VIEW IF EXISTS {table} CASCADE;"))
                        
        except Exception as e:
            print(f"⚠️ Advertencia limpieza manual: {e}")

        # 2. Borrar tablas actuales de SQLAlchemy
        print("🗑️  Eliminando tablas del esquema actual...")
        Base.metadata.drop_all(engine)
        
        # 3. Crear tablas nuevas
        print("✨ Creando tablas nuevas con esquema actualizado...")
        Base.metadata.create_all(engine)
        
        # 4. Restaurar la Vista (Opcional)
        try:
            with engine.connect() as connection:
                with connection.begin():
                    print("📊 Restaurando Vista de Estadísticas...")
                    connection.execute(text("""
                        CREATE OR REPLACE VIEW v_glossary_user_stats AS
                        SELECT 
                            u.id AS user_id,
                            u.email,
                            u.name,
                            COUNT(DISTINCT g.id) AS total_terms_available,
                            COUNT(DISTINCT CASE WHEN ugp.is_learned THEN ugp.glossary_id END) AS terms_learned,
                            ROUND((COUNT(DISTINCT CASE WHEN ugp.is_learned THEN ugp.glossary_id END)::NUMERIC / NULLIF(COUNT(DISTINCT g.id), 0)) * 100, 1) AS percentage_learned
                        FROM users u
                        CROSS JOIN glossary g
                        LEFT JOIN user_glossary_progress ugp ON u.id = ugp.user_id AND g.id = ugp.glossary_id
                        GROUP BY u.id, u.email, u.name;
                    """))
        except Exception as e:
             print(f"⚠️ No se pudo restaurar la vista automáticamente: {e}")

        print("✅ Base de datos reiniciada exitosamente.")