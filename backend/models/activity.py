# backend/models/activity.py - CORREGIDO PARA COINCIDIR CON BD REAL
from database.db import db
from datetime import datetime

class Activity(db.Model):
    __tablename__ = 'activities'
    __table_args__ = {'extend_existing': True}
    
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('users.id'), nullable=False)
    activity_type = db.Column(db.String(50), nullable=False)
    description = db.Column(db.String(255))  # ✅ EXISTE EN BD
    points = db.Column(db.Integer, nullable=False)  # ✅ ERA "xp_earned" en modelo viejo
    timestamp = db.Column(db.DateTime, default=datetime.utcnow)  # ✅ ERA "created_at" en modelo viejo
    lesson_id = db.Column(db.String(100))
    
    def __repr__(self):
        return f'<Activity {self.id}: {self.activity_type} - {self.points} pts>'