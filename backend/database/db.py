# backend/database/db.py
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, scoped_session
from sqlalchemy.ext.declarative import declarative_base
from config import config
import os

# Motor y sesión
engine = create_engine(config.SQLALCHEMY_DATABASE_URI, pool_pre_ping=True, echo=False)
SessionLocal = scoped_session(sessionmaker(autocommit=False, autoflush=False, bind=engine))
Base = declarative_base()

def get_session():
    return SessionLocal()

# ¡¡¡IMPORTANTE!!! Importar TODOS los modelos aquí para que SQLAlchemy los registre
from models.user import User
from models.course import Course
from models.lesson import Lesson
from models.user_progress import UserCourseProgress, UserLessonProgress
from models.refresh_token import RefreshToken
from models.user_badge import UserBadge
from models.activity import Activity
from models.badge import Badge
from models.glossary import Glossary
from models.user_glossary_progress import UserGlossaryProgress  # ✅ ESTA LÍNEA ES NUEVA
from models.daily_term_log import DailyTermLog
from models.password_reset_token import PasswordResetToken
from models.user_glossary_favorite import UserGlossaryFavorite
from models.assessments import (
    FinalExamQuestion, UserExamAttempt,
    PreferenceQuestion, UserPreferenceResult
)

def create_all():
    """Crear todas las tablas"""
    Base.metadata.create_all(bind=engine)