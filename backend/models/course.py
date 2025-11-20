# backend/models/course.py - CORREGIDO PARA COINCIDIR CON BD REAL
from database.db import db
from datetime import datetime

class Course(db.Model):
    __tablename__ = 'courses'
    __table_args__ = {'extend_existing': True}
    
    id = db.Column(db.Integer, primary_key=True)  # ✅ CAMBIADO de String a Integer
    title = db.Column(db.String(200), nullable=False)
    description = db.Column(db.Text)
    level = db.Column(db.String(50))
    xp_reward = db.Column(db.Integer, default=0)
    image_url = db.Column(db.String(500))
    category = db.Column(db.String(100))
    duration_hours = db.Column(db.Integer)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    
    def __repr__(self):
        return f'<Course {self.id}: {self.title}>'

# Relación con lecciones
Course.lessons = db.relationship('Lesson', backref='course', lazy=True, cascade='all, delete-orphan')