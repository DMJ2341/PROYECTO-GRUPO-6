import sys
import os
from sqlalchemy import text
from app import app
from database.db import db

# --- CURSO(S) A CARGAR ---
# Aquí defines tu curso obligatorio
COURSES_TO_LOAD = [
    {
        'id': 'fundamentos_ciberseguridad', # ID que coincide con tus lecciones
        'title': 'Fundamentos de Ciberseguridad',
        'description': 'El curso obligatorio para iniciar tu camino en ciberseguridad.',
        'level': 'Principiante',
        'xp_reward': 150, # Puedes calcular esto o poner un total
        'image_url': '🚀' # Emoji
    },
    # ...Aquí puedes agregar los otros cursos de tu archivo original si quieres...
    # {
    #     'id': 'ethical_hacking',
    #     'title': 'Ethical Hacking',
    #     'description': 'Técnicas de hacking ético y pruebas de penetración',
    #     'level': 'Intermedio', 
    #     'xp_reward': 80,
    #     'image_url': '🛡️'
    # },
]

def upsert_course(session, course_data):
    sql = text("""
        INSERT INTO courses (
            id, title, description, level, xp_reward, image_url
        )
        VALUES (
            :id, :title, :description, :level, :xp_reward, :image_url
        )
        ON CONFLICT (id) 
        DO UPDATE SET 
            title = EXCLUDED.title,
            description = EXCLUDED.description,
            level = EXCLUDED.level,
            xp_reward = EXCLUDED.xp_reward,
            image_url = EXCLUDED.image_url;
    """)
    
    try:
        session.execute(sql, course_data)
        print(f"  ✅ Procesado: {course_data['title']} (ID: {course_data['id']})")
    
    except Exception as e:
        print(f"  ❌ ERROR procesando {course_data['id']}: {e}")
        raise

def main():
    print("="*60)
    print(f"🌱 INICIANDO CARGA DE {len(COURSES_TO_LOAD)} CURSO(S)")
    print("="*60)
    
    try:
        with app.app_context():
            session = db.session
            for course_data in COURSES_TO_LOAD:
                upsert_course(session, course_data)
            
            session.commit()
            print("\n" + "="*60)
            print("🎉 ¡Cursos seleccionados han sido cargados/actualizados!")
            print("="*60)
            
    except Exception as e:
        db.session.rollback()
        print(f"\n❌ ERROR FATAL: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)

if __name__ == '__main__':
    main()