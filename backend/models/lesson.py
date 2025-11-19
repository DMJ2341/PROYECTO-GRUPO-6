# backend/models/lesson.py
from database.db import db
from datetime import datetime
import json

class Lesson(db.Model):
    __tablename__ = 'lessons'
    __table_args__ = {'extend_existing': True}  # Permite redefinir
    
    id = db.Column(db.String(100), primary_key=True)
    course_id = db.Column(db.Integer, db.ForeignKey('courses.id'), nullable=True)
    title = db.Column(db.String(200), nullable=False)
    description = db.Column(db.Text)
    content = db.Column(db.Text)
    order_index = db.Column(db.Integer, nullable=False)
    type = db.Column(db.String(20), default='interactive')
    duration_minutes = db.Column(db.Integer)
    xp_reward = db.Column(db.Integer, default=0)
    total_screens = db.Column(db.Integer, default=1)
    screens = db.Column(db.Text)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    
    def __repr__(self):
        return f'<Lesson {self.id}: {self.title}>'
    
    def get_screens_data(self):
        """Obtener screens como JSON/diccionario"""
        try:
            if self.screens:
                return json.loads(self.screens)
        except:
            pass
        return []
    
    def set_screens_data(self, screens_list):
        """Guardar screens como JSON string"""
        try:
            self.screens = json.dumps(screens_list)
        except:
            self.screens = '[]'