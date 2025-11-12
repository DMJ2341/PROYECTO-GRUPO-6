# backend/models/badge.py
from database.db import db
from datetime import datetime

class Badge(db.Model):
    __tablename__ = 'badges'

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    description = db.Column(db.Text)
    icon = db.Column(db.String(200))
    condition = db.Column(db.Text)           # ✅ Faltaba
    points_required = db.Column(db.Integer)
    rarity = db.Column(db.String(20))
    xp_value = db.Column(db.Integer, default=10)
    category = db.Column(db.String(50))
    condition_type = db.Column(db.String(50))
    condition_value = db.Column(db.Integer, default=0)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)

    def __repr__(self):
        return f'<Badge {self.id}: {self.name}>'