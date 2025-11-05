from database.db import db

class Course(db.Model):
    __tablename__ = 'courses'
    
    # El ID del curso, ej: 'fundamentos_ciberseguridad'
    id = db.Column(db.String, primary_key=True) 
    title = db.Column(db.String, nullable=False)
    description = db.Column(db.String, nullable=False)
    level = db.Column(db.String)
    xp_reward = db.Column(db.Integer)
    image_url = db.Column(db.String) # Para el emoji o una URL

    def to_dict(self):
        return {
            'id': self.id,
            'title': self.title,
            'description': self.description,
            'level': self.level,
            'xp_reward': self.xp_reward,
            'image_url': self.image_url
        }