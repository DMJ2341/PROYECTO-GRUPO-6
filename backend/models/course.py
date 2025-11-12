# backend/models/course.py
from database.db import db  # ✅ Importar db
from datetime import datetime
import json

class Course(db.Model):
    __tablename__ = 'courses'
    
    id = db.Column(db.Integer, primary_key=True)
    title = db.Column(db.String(200), nullable=False)
    description = db.Column(db.Text)
    category = db.Column(db.String(50))
    difficulty = db.Column(db.String(20))
    duration_hours = db.Column(db.Integer)
    image_url = db.Column(db.String(500))
    instructor = db.Column(db.String(100))
    rating = db.Column(db.Float, default=0.0)
    students_count = db.Column(db.Integer, default=0)
    price = db.Column(db.Float, default=0.0)
    language = db.Column(db.String(20), default='Español')
    requirements = db.Column(db.Text)  # JSON
    learning_objectives = db.Column(db.Text)  # JSON
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

# Relación con lecciones
Course.lessons = db.relationship('Lesson', backref='course', lazy=True, cascade='all, delete-orphan')