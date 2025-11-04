from database.db import db
from datetime import datetime

class UserActivity(db.Model):
    __tablename__ = 'user_activity'  # Ya está bien
    
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('users.id'), nullable=False)  # ← Cambiar referencia
    activity_type = db.Column(db.String(50), nullable=False)
    xp_earned = db.Column(db.Integer, nullable=False)
    lesson_id = db.Column(db.String(100))
    difficulty = db.Column(db.Integer, default=1)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)