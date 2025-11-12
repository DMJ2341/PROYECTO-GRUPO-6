
from database.db import Session
from models.course import Course
from models.lesson import Lesson

class CourseService:
    def __init__(self):
        self.db = Session()
    
    def get_all_courses(self):
        """Obtener todos los cursos con columnas correctas"""
        try:
            # ✅ Usar SQLAlchemy ORM en lugar de SQL crudo
            courses = self.db.query(Course).all()
            
            # Convertir a diccionario con columnas reales
            return [{
                "id": course.id,
                "title": course.title,
                "description": course.description,
                "category": course.category,
                "difficulty": course.difficulty,  # ✅ Columna real
                "duration_hours": course.duration_hours,
                "image_url": course.image_url,
                "instructor": course.instructor,
                "rating": course.rating,  # ✅ Columna real
                "students_count": course.students_count,  # ✅ Columna real
                "price": course.price,
                "language": course.language
            } for course in courses]
            
        except Exception as e:
            print(f"Error en get_all_courses: {e}")
            raise e
    
    def get_course_by_id(self, course_id):
        """Obtener curso por ID"""
        try:
            course = self.db.query(Course).filter_by(id=course_id).first()
            if not course:
                return None
            
            return {
                "id": course.id,
                "title": course.title,
                "description": course.description,
                "category": course.category,
                "difficulty": course.difficulty,
                "duration_hours": course.duration_hours,
                "image_url": course.image_url,
                "instructor": course.instructor,
                "rating": course.rating,
                "students_count": course.students_count,
                "price": course.price,
                "language": course.language,
                "requirements": course.requirements,
                "learning_objectives": course.learning_objectives,
                "created_at": course.created_at.isoformat() if course.created_at else None,
                "updated_at": course.updated_at.isoformat() if course.updated_at else None
            }
        except Exception as e:
            print(f"Error en get_course_by_id: {e}")
            raise e
    
    def get_course_lessons(self, course_id):
        """Obtener lecciones de un curso"""
        try:
            lessons = self.db.query(Lesson).filter_by(course_id=course_id).order_by(Lesson.order_index).all()
            
            return [{
                "id": lesson.id,
                "course_id": lesson.course_id,
                "title": lesson.title,
                "description": lesson.description,
                "content": lesson.content,
                "order_index": lesson.order_index,
                "type": lesson.type,
                "duration_minutes": lesson.duration_minutes,
                "created_at": lesson.created_at.isoformat() if lesson.created_at else None
            } for lesson in lessons]
            
        except Exception as e:
            print(f"Error en get_course_lessons: {e}")
            raise e
    
    def get_lesson_by_id(self, lesson_id):
        """Obtener lección por ID"""
        try:
            lesson = self.db.query(Lesson).filter_by(id=lesson_id).first()
            if not lesson:
                return None
            
            return {
                "id": lesson.id,
                "course_id": lesson.course_id,
                "title": lesson.title,
                "description": lesson.description,
                "content": lesson.content,
                "order_index": lesson.order_index,
                "type": lesson.type,
                "duration_minutes": lesson.duration_minutes
            }
        except Exception as e:
            print(f"Error en get_lesson_by_id: {e}")
            raise e
    
    def get_interactive_content(self, lesson_id):
        """Obtener contenido interactivo de una lección"""
        try:
            lesson = self.db.query(Lesson).filter_by(id=lesson_id).first()
            if not lesson:
                return None
            
            # Parsear contenido JSON si existe
            import json
            try:
                content = json.loads(lesson.content) if lesson.content else {}
            except:
                content = {"video_url": "", "resources": []}
            
            return {
                "lesson_id": lesson.id,
                "title": lesson.title,
                "content": content,
                "type": lesson.type,
                "duration_minutes": lesson.duration_minutes
            }
        except Exception as e:
            print(f"Error en get_interactive_content: {e}")
            raise e
    
    def update_lesson_progress(self, user_id, lesson_id, completed):
        """Actualizar progreso de lección"""
        try:
            # Aquí iría la lógica para guardar progreso
            # Por ahora retornamos un mock
            return {
                "user_id": user_id,
                "lesson_id": lesson_id,
                "completed": completed,
                "progress_percent": 100 if completed else 0,
                "message": "Progreso actualizado exitosamente"
            }
        except Exception as e:
            print(f"Error en update_lesson_progress: {e}")
            raise e

    def get_course_lessons_count(self, lesson_id):
        """Obtener número total de lecciones en el curso de una lección"""
        try:
            # Primero obtener el course_id de la lección
            lesson = self.db.query(Lesson).filter_by(id=lesson_id).first()
            if not lesson:
                return 0
            
            # Contar lecciones en ese curso
            return self.db.query(Lesson).filter_by(course_id=lesson.course_id).count()
        except Exception as e:
            print(f"Error contando lecciones del curso: {e}")
            return 0

    def get_completed_lessons_count(self, user_id, lesson_id):
        """Obtener número de lecciones completadas por el usuario en el curso"""
        try:
            # Obtener course_id de la lección
            lesson = self.db.query(Lesson).filter_by(id=lesson_id).first()
            if not lesson:
                return 0
            
            # Contar actividades de lecciones completadas del usuario en ese curso
            from models.activity import Activity
            from sqlalchemy import and_
            
            # Buscar todas las lecciones del curso
            course_lessons = self.db.query(Lesson).filter_by(course_id=lesson.course_id).all()
            lesson_ids = [l.id for l in course_lessons]
            
            # Contar actividades de lecciones completadas
            completed_count = self.db.query(Activity).filter(
                and_(
                    Activity.user_id == user_id,
                    Activity.type == 'lesson_completed',
                    Activity.description.like('Lección % completada')
                )
            ).count()
            
            return completed_count
        except Exception as e:
            print(f"Error contando lecciones completadas: {e}")
            return 0
    
    def __del__(self):
        if hasattr(self, 'db'):
            self.db.close()