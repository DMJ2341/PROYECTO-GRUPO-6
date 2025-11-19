# backend/models/activity.py
from database.db import db
from datetime import datetime

class Activity(db.Model):
    __tablename__ = 'activities'
    __table_args__ = {'extend_existing': True}
    
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('users.id'), nullable=False)
    activity_type = db.Column(db.String(50), nullable=False)
    xp_earned = db.Column(db.Integer, nullable=False)
    lesson_id = db.Column(db.String(100))
    difficulty = db.Column(db.Integer)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    
    def __repr__(self):
        return f'<Activity {self.id}: {self.activity_type} - {self.xp_earned} XP>'