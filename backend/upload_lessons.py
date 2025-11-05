
import sys
import os
import json
from sqlalchemy import text
from app import app
from database.db import db

# --- Configuración para importar desde la carpeta 'content' ---
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '.')))

# --- Importa SOLAMENTE la lección de Phishing ---
from content.lessons.fundamentos.leccion_phishing_interactiva import get_lesson_data as get_phishing_data

# --- Lista Maestra de Lecciones ---
# Como puedes ver, aquí NO hay rastro de "Triada CIA"
LESSONS_TO_LOAD = [
    get_phishing_data(),
]

# -----------------------------------------------------------------
# (El resto del script es genérico y no necesitas tocarlo)
# -----------------------------------------------------------------

def upsert_lesson(session, lesson_data):
    """
    Inserta o actualiza una lección en la base de datos.
    Maneja tanto lecciones de texto como interactivas.
    """
    content_text = lesson_data.get('content')
    screens_data = lesson_data.get('screens')
    screens_json = None
    
    if screens_data and lesson_data.get('lesson_type') == 'interactive':
        screens_json = json.dumps(screens_data)

    sql = text("""
        INSERT INTO lessons (
            lesson_id, course_id, title, lesson_order, xp_reward, 
            duration_minutes, lesson_type, screens, total_screens, content
        )
        VALUES (
            :lesson_id, :course_id, :title, :lesson_order, :xp_reward, 
            :duration_minutes, :lesson_type, :screens, :total_screens, :content
        )
        ON CONFLICT (lesson_id) 
        DO UPDATE SET 
            course_id = EXCLUDED.course_id,
            title = EXCLUDED.title,
            lesson_order = EXCLUDED.lesson_order,
            xp_reward = EXCLUDED.xp_reward,
            duration_minutes = EXCLUDED.duration_minutes,
            lesson_type = EXCLUDED.lesson_type,
            screens = EXCLUDED.screens,
            total_screens = EXCLUDED.total_screens,
            content = EXCLUDED.content;
    """)
    
    try:
        session.execute(sql, {
            "lesson_id": lesson_data["lesson_id"],
            "course_id": lesson_data["course_id"],
            "title": lesson_data["title"],
            "lesson_order": lesson_data["lesson_order"],
            "xp_reward": lesson_data["xp_reward"],
            "duration_minutes": lesson_data["duration_minutes"],
            "lesson_type": lesson_data["lesson_type"],
            "screens": screens_json,
            "total_screens": lesson_data.get("total_screens", 0),
            "content": content_text
        })
        print(f"  ✅ Procesada: {lesson_data['title']} (ID: {lesson_data['lesson_id']})")
    
    except Exception as e:
        print(f"  ❌ ERROR procesando {lesson_data['lesson_id']}: {e}")
        raise

def main():
    print("="*60)
    print(f"🌱 INICIANDO CARGA DE {len(LESSONS_TO_LOAD)} LECCIÓN(ES)")
    print("="*60)
    
    if not LESSONS_TO_LOAD:
        print("🤷 No hay lecciones marcadas para cargar. Saliendo.")
        return

    try:
        with app.app_context():
            session = db.session
            for lesson_data in LESSONS_TO_LOAD:
                upsert_lesson(session, lesson_data)
            
            session.commit()
            print("\n" + "="*60)
            print("🎉 ¡Lecciones seleccionadas han sido cargadas/actualizadas!")
            print("="*60)
            
    except Exception as e:
        db.session.rollback()
        print(f"\n❌ ERROR FATAL: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)

if __name__ == '__main__':
    main()