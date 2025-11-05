from database.db import db
from sqlalchemy import text
# Opcional: si creaste el modelo
# from models.course import Course 

class CourseService:
    
    def get_all_courses(self):
        """
        Obtiene todos los cursos desde la base de datos.
        """
        # Usamos SQL simple para leer la tabla 'courses'
        sql = text("""
            SELECT id, title, description, level, xp_reward, image_url 
            FROM courses
        """)
        
        result = db.session.execute(sql).fetchall()
        
        # Convertir las filas de la BD en una lista de diccionarios
        courses = [
            {
                'id': row[0],
                'title': row[1],
                'description': row[2],
                'level': row[3],
                'xp_reward': row[4],
                'image_url': row[5],
                
                # NOTA: El progreso, total de lecciones, etc.
                # ya no deben estar aquí. Deben calcularse 
                # dinámicamente en otro endpoint (ej. /api/user/progress)
            } for row in result
        ]
        return courses

    def get_course_by_id(self, course_id):
        """
        Obtiene un curso específico por ID desde la base de datos.
        """
        sql = text("""
            SELECT id, title, description, level, xp_reward, image_url 
            FROM courses 
            WHERE id = :course_id
        """)
        
        result = db.session.execute(sql, {'course_id': course_id}).fetchone()
        
        if not result:
            return None
        
        # Convertir la fila en un diccionario
        course = {
            'id': result[0],
            'title': result[1],
            'description': result[2],
            'level': result[3],
            'xp_reward': result[4],
            'image_url': result[5]
        }
        return course