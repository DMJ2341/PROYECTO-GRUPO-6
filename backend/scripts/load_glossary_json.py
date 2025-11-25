import json
import sys
import os

# Ajustar path para importar módulos del backend
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

from database.db import get_session
from models.glossary import Glossary

def load_json_data():
    # Ruta al archivo JSON
    json_path = os.path.join(os.path.dirname(__file__), '../data/glossary_data.json')
    
    if not os.path.exists(json_path):
        print(f"❌ No se encontró el archivo: {json_path}")
        return

    print(f"📂 Leyendo {json_path}...")
    try:
        with open(json_path, 'r', encoding='utf-8') as f:
            data = json.load(f)
    except Exception as e:
        print(f"❌ Error leyendo JSON: {e}")
        return

    session = get_session()
    added_count = 0
    skipped_count = 0

    print(f"🚀 Procesando {len(data)} términos...")

    for item in data:
        # Verificar duplicados por término
        exists = session.query(Glossary).filter_by(term=item['term']).first()
        
        if not exists:
            new_term = Glossary(
                term=item.get('term'),
                acronym=item.get('acronym'),
                definition=item.get('definition'),
                example=item.get('example'),
                category=item.get('category', 'General'),
                difficulty=item.get('difficulty', 'Medio'),
                where_you_hear_it=item.get('where_you_hear_it')
            )
            session.add(new_term)
            added_count += 1
        else:
            skipped_count += 1

    try:
        session.commit()
        print("\n🎉 ¡Carga completada!")
        print(f"   ✅ Agregados: {added_count}")
        print(f"   ⏭️  Omitidos (ya existían): {skipped_count}")
    except Exception as e:
        session.rollback()
        print(f"❌ Error guardando en BD: {e}")
    finally:
        session.close()

if __name__ == "__main__":
    load_json_data()