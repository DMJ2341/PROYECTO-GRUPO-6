# backend/services/course_service.py - LÓGICA PARA CURSOS (CORRECTO)
from database.db import get_session
from models.course import Course
from models.lesson import Lesson

class CourseService:
    def get_courses(self):
        session = get_session()
        try:
            courses = session.query(Course).all()
            return courses
        finally:
            session.close()

    def get_course_lessons(self, course_id):
        session = get_session()
        try:
            lessons = session.query(Lesson).filter_by(course_id=course_id).all()
            return lessons
        finally:
            session.close()