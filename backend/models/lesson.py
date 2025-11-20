# backend/models/lesson.py - CORREGIDO CON JSONB Y FK INTEGER
from database.db import db
from datetime import datetime
from sqlalchemy.dialects.postgresql import JSONB

class Lesson(db.Model):
    __tablename__ = 'lessons'
    __table_args__ = {'extend_existing': True}
    
    id = db.Column(db.String(100), primary_key=True)
    course_id = db.Column(db.Integer, db.ForeignKey('courses.id'), nullable=True)  # ✅ Integer FK
    title = db.Column(db.String(200), nullable=False)
    description = db.Column(db.Text)
    content = db.Column(db.Text)
    order_index = db.Column(db.Integer, nullable=False)
    type = db.Column(db.String(20), default='interactive')
    duration_minutes = db.Column(db.Integer)
    xp_reward = db.Column(db.Integer, default=0)
    total_screens = db.Column(db.Integer, default=1)
    screens = db.Column(JSONB)  # ✅ CAMBIADO de Text a JSONB
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    
    def __repr__(self):
        return f'<Lesson {self.id}: {self.title}>'
    
    def get_screens_data(self):
        """Obtener screens directamente (ya es dict/list con JSONB)"""
        return self.screens if self.screens else []
    
    def set_screens_data(self, screens_list):
        """Guardar screens directamente (SQLAlchemy maneja JSONB)"""
        self.screens = screens_list