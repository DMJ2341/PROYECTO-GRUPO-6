# backend/models/__init__.py
from .user import User, UserBadge
from .course import Course
from .lesson import Lesson
from .activity import Activity
from .badge import Badge  # ✅ AGREGADO

__all__ = ['User', 'UserBadge', 'Course', 'Lesson', 'Activity', 'Badge']