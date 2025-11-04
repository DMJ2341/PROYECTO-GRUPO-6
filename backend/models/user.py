from database.db import db
from datetime import datetime

class User(db.Model):
    __tablename__ = 'users'  # ← Cambiar de 'user' a 'users'
    
    id = db.Column(db.Integer, primary_key=True)
    email = db.Column(db.String(120), unique=True, nullable=False)
    password_hash = db.Column(db.String(255), nullable=False)
    name = db.Column(db.String(100))
    xp_total = db.Column(db.Integer, default=0)
    level = db.Column(db.Integer, default=1)
    streak = db.Column(db.Integer, default=0)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    last_login = db.Column(db.DateTime)
    last_activity = db.Column(db.DateTime)

class UserBadge(db.Model):
    __tablename__ = 'user_badge'  # Ya está bien
    
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('users.id'), nullable=False)  # ← Cambiar referencia
    badge_id = db.Column(db.String(50), nullable=False)
    earned_at = db.Column(db.DateTime, default=datetime.utcnow)