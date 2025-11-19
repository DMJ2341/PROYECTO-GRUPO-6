# backend/models/badge.py
from database.db import db
from datetime import datetime

class Badge(db.Model):
    __tablename__ = 'badges'
    __table_args__ = {'extend_existing': True}
    
    id = db.Column(db.String(50), primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    description = db.Column(db.String(255))
    icon = db.Column(db.String(200))
    xp_required = db.Column(db.Integer)

    def __repr__(self):
        return f'<Badge {self.id}: {self.name}>'