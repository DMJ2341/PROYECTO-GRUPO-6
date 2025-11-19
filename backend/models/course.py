# backend/models/course.py
from database.db import db  # ✅ Importar db
from datetime import datetime
import json

class Course(db.Model):
    __tablename__ = 'courses'
    
    id = db.Column(db.String(100), primary_key=True)
    title = db.Column(db.String(200), nullable=False)
    description = db.Column(db.Text)
    level = db.Column(db.String(50))
    xp_reward = db.Column(db.Integer, default=0)
    image_url = db.Column(db.String(500))

Course.lessons = db.relationship('Lesson', backref='course', lazy=True, cascade='all, delete-orphan')