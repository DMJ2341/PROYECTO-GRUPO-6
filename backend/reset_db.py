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
        try:
            with engine.connect() as connection:
                with connection.begin():
                    print("🔥 Limpiando tablas antiguas y huérfanas...")
                    
                    # --- PASO A: Borrar VISTAS primero (usando DROP VIEW) ---
                    # Es crucial borrar las vistas antes que las tablas
                    views_to_drop = ["v_glossary_user_stats"]
                    
                    for view in views_to_drop:
                        print(f"   - Eliminando vista: {view}")
                        connection.execute(text(f"DROP VIEW IF EXISTS {view} CASCADE;"))

                    # --- PASO B: Borrar TABLAS antiguas (usando DROP TABLE) ---
                    tables_to_drop = [
                        "user_exam_attempts",
                        "user_preference_results",
                        "profile_view_logs",
                        "final_exam_questions",
                        "preference_questions",
                        # Agrega aquí cualquier otra tabla "zombie" que no esté en tus modelos
                    ]
                    
                    for table in tables_to_drop:
                        print(f"   - Eliminando tabla: {table}")
                        connection.execute(text(f"DROP TABLE IF EXISTS {table} CASCADE;"))
                        
        except Exception as e:
            # Imprimimos el error pero no detenemos el script completo, 
            # para ver si SQLAlchemy puede encargarse del resto.
            print(f"⚠️ Advertencia durante limpieza manual: {e}")

        # 2. Borrar tablas actuales de SQLAlchemy
        print("🗑️  Eliminando tablas del esquema actual...")
        # Ahora esto funcionará porque la vista que lo bloqueaba ya fue eliminada arriba
        Base.metadata.drop_all(engine)
        
        # 3. Crear tablas nuevas
        print("✨ Creando tablas nuevas con esquema actualizado...")
        Base.metadata.create_all(engine)
        
        # 4. Restaurar la Vista
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

