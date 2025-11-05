from database.db import db
from sqlalchemy.dialects.postgresql import JSONB

class Lesson(db.Model):
    __tablename__ = 'lessons'

    # Columnas que coinciden con tu script upload_lessons.py
    lesson_id = db.Column(db.String, primary_key=True)
    
    # Establece la relación con la tabla 'courses'
    course_id = db.Column(db.String, db.ForeignKey('courses.id'), nullable=False)
    
    title = db.Column(db.String, nullable=False)
    lesson_order = db.Column(db.Integer)
    xp_reward = db.Column(db.Integer)
    duration_minutes = db.Column(db.Integer)
    lesson_type = db.Column(db.String(50)) # 'text' o 'interactive'
    
    # 'screens' se guarda como JSON. Usamos JSONB para PostgreSQL.
    screens = db.Column(JSONB) 
    
    total_screens = db.Column(db.Integer)
    
    # 'content' es para lecciones de solo texto (como la Triada CIA)
    content = db.Column(db.Text)