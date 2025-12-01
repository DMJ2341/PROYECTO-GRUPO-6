import sys
import os
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

from database.db import get_session
from models.badge import Badge

def seed_badges():
    session = get_session()
    print("🏅 Creando Badges...")

    badges_data = [
        # ✅ MEDALLA INICIAL
        {
            "name": "Primer Paso", 
            "desc": "Completaste tu primera lección", 
            "icon": "🎯", 
            "type": "first_lesson", 
            "val": "1"
        },
        
        # ✅ MEDALLAS POR CURSO COMPLETO (100%)
        {
            "name": "Guardián de la Información", 
            "desc": "Completaste 'Fundamentos de Seguridad de la Información'", 
            "icon": "🛡️", 
            "type": "course_completed", 
            "val": "1"
        },
        {
            "name": "Defensor de Redes", 
            "desc": "Completaste 'Seguridad de Redes'", 
            "icon": "🌐", 
            "type": "course_completed", 
            "val": "2"
        },
        {
            "name": "Maestro IAM", 
            "desc": "Completaste 'Gestión de Identidades y Accesos'", 
            "icon": "🔐", 
            "type": "course_completed", 
            "val": "3"
        },
        {
            "name": "Detective Forense", 
            "desc": "Completaste 'Respuesta a Incidentes y Forense Digital'", 
            "icon": "🔍", 
            "type": "course_completed", 
            "val": "4"
        },
        {
            "name": "Analista de Riesgos", 
            "desc": "Completaste 'Gestión de Riesgos y Cumplimiento'", 
            "icon": "📊", 
            "type": "course_completed", 
            "val": "5"
        },
        
        # ✅ MEDALLA MAESTRA (Todos los cursos)
        {
            "name": "Hacker Ético Certificado", 
            "desc": "Completaste los 5 cursos de CyberLearn", 
            "icon": "🎓", 
            "type": "all_basic_courses", 
            "val": "5"
        },
        
        # ✅ MEDALLAS DE XP (Hitos de experiencia)
        {
            "name": "Novato Cibernético", 
            "desc": "Alcanzaste 100 XP", 
            "icon": "⭐", 
            "type": "xp_milestone", 
            "val": "100"
        },
        {
            "name": "Aprendiz Avanzado", 
            "desc": "Alcanzaste 500 XP", 
            "icon": "💎", 
            "type": "xp_milestone", 
            "val": "500"
        },
        {
            "name": "Experto en Ascenso", 
            "desc": "Alcanzaste 1000 XP", 
            "icon": "🔥", 
            "type": "xp_milestone", 
            "val": "1000"
        },
        
        # ✅ MEDALLAS DE RACHA (Constancia)
        {
            "name": "Racha Inicial", 
            "desc": "Estudiaste 3 días seguidos", 
            "icon": "⚡", 
            "type": "streak", 
            "val": "3"
        },
        {
            "name": "Dedicación Semanal", 
            "desc": "Estudiaste 7 días seguidos", 
            "icon": "📅", 
            "type": "streak", 
            "val": "7"
        },
        {
            "name": "Compromiso Total", 
            "desc": "Estudiaste 30 días seguidos", 
            "icon": "🏆", 
            "type": "streak", 
            "val": "30"
        }
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
            # ✅ Actualizar si ya existe para mantener coherencia
            exists.description = b["desc"]
            exists.icon = b["icon"]
            exists.trigger_type = b["type"]
            exists.trigger_value = b["val"]
            print(f"   🔄 Badge actualizado: {b['name']}")
            
    session.commit()
    session.close()
    print("✨ Sistema de Badges listo.")

if __name__ == "__main__":
    seed_badges()