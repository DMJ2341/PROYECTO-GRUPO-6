import sys
import os
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

from database.db import get_session
from models.badge import Badge

def seed_badges():
    session = get_session()
    print("🏅 Creando Badges...")

    badges_data = [
        {"name": "Primeros Pasos", "desc": "Completaste tu primera lección", "icon": "🏃‍♂️", "type": "first_lesson", "val": "1"},
        {"name": "Maestro del Phishing", "desc": "Completaste el Curso 1", "icon": "🎣", "type": "course_completed", "val": "1"},
        {"name": "Novato en Redes", "desc": "Completaste el Curso 2", "icon": "🌐", "type": "course_completed", "val": "2"},
        {"name": "Guardián IAM", "desc": "Completaste el Curso 3", "icon": "🛡️", "type": "course_completed", "val": "3"},
        {"name": "Detective Forense", "desc": "Completaste el Curso 4", "icon": "🔍", "type": "course_completed", "val": "4"},
        {"name": "Risk Manager", "desc": "Completaste el Curso 5", "icon": "📊", "type": "course_completed", "val": "5"},
        {"name": "Hacker Ético Jr", "desc": "Completaste los 5 cursos básicos", "icon": "🎓", "type": "all_basic_courses", "val": "5"},
        {"name": "100 XP", "desc": "Alcanzaste 100 XP", "icon": "💎", "type": "xp_milestone", "val": "100"},
        {"name": "500 XP", "desc": "Alcanzaste 500 XP", "icon": "🔥", "type": "xp_milestone", "val": "500"},
        {"name": "Racha 3 Días", "desc": "Estudiaste 3 días seguidos", "icon": "⚡", "type": "streak", "val": "3"},
        {"name": "Racha Semanal", "desc": "Estudiaste 7 días seguidos", "icon": "📅", "type": "streak", "val": "7"}
    ]

    for b in badges_data:
        exists = session.query(Badge).filter_by(name=b["name"]).first()
        if not exists:
            new_badge = Badge(
                name=b["name"], 
                description=b["desc"], 
                icon=b["icon"], 
                trigger_type=b["type"], 
                trigger_value=b["val"]
            )
            session.add(new_badge)
            print(f"   ✅ Badge creado: {b['name']}")
        else:
            # Actualizar lógica si ya existe
            exists.trigger_type = b["type"]
            exists.trigger_value = b["val"]
            exists.icon = b["icon"]
            
    session.commit()
    session.close()
    print("✨ Sistema de Badges listo.")

if __name__ == "__main__":
    seed_badges()