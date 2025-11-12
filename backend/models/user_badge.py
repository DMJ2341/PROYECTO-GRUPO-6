# backend/models/user_badge.py
from database.db import db
from datetime import datetime

class UserBadge(db.Model):
    __tablename__ = 'user_badges'
    
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('users.id'), nullable=False)
    badge_id = db.Column(db.Integer, db.ForeignKey('badges.id'), nullable=False)
    earned_at = db.Column(db.DateTime, default=datetime.utcnow)
    earned_value = db.Column(db.Integer, nullable=False)  # Valor conseguido
    
    def __repr__(self):
        return f'<UserBadge {self.id}: User {self.user_id}, Badge {self.badge_id}>'