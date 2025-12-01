# backend/scripts/load_nist_glossary.py

import json
import os
import sys
from sqlalchemy.exc import IntegrityError
from sqlalchemy import func

# Añadir el path para importar módulos locales (db, models)
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

from database.db import get_session, create_all
from models.glossary import Glossary
# Si no has arreglado activity.py y glossary.py, debes hacerlo antes de ejecutar este script.

# --- CONFIGURACIÓN DE FILTROS ---
MAX_TERMS = 500 
MIN_DEFINITION_LENGTH = 30

def load_terms_from_json(session, terms_data):
    """Procesa una lista de términos desde JSON y los carga a la BD."""
    count = 0
    
    # Inicia una variable de control para saber si hubo un error en la sesión
    error_occurred = False
    
    for term_data in terms_data:
        try:
            # 1. Limpieza y validación básica
            term_name = term_data.get('term', '').strip()
            definition = term_data.get('definition', '').strip()
            
            if not term_name or len(definition) < MIN_DEFINITION_LENGTH:
                continue
            
            # 2. Verificar duplicados por término
            # Esta SELECT es la que falla cuando la transacción está abortada.
            existing_term = session.query(Glossary).filter_by(term=term_name).first()
            if existing_term:
                continue

            # 3. Crear el nuevo objeto Glossary
            new_term = Glossary(
                term=term_name,
                acronym=term_data.get('acronym', None),
                definition=definition,
                example=term_data.get('example', None),
                category=term_data.get('category', 'Ciberseguridad'), 
                difficulty=term_data.get('difficulty', 'Intermedio'),
                where_you_hear_it=term_data.get('where_you_hear_it', None)
            )

            session.add(new_term)
            count += 1
            if count >= MAX_TERMS:
                break
        
        except IntegrityError as e:
            # Error por clave duplicada o nulls. Se maneja y se continúa.
            print(f"❌ Error de Integridad para {term_name}: {e}")
            session.rollback() # ✅ ROLLBACK NECESARIO
            error_occurred = True
            continue
        except Exception as e:
            # Error genérico (el error de transacción abortada anterior)
            print(f"❌ Error al procesar el término {term_data.get('term')}: {e}")
            session.rollback() # ✅ ROLLBACK NECESARIO
            error_occurred = True
            continue

    if not error_occurred and count > 0:
        session.commit()
    elif error_occurred:
        # En caso de que el error ocurra y no se haya hecho rollback
        session.rollback() 
        print("Advertencia: Se realizó un Rollback debido a errores en la carga de datos.")

    return count

def run_glossary_loader():
    print("--- 🚀 INICIANDO CARGA DE GLOSARIO ---")
    session = get_session()
    
    try:
        data_path = os.path.join(os.path.dirname(__file__), '..', 'data', 'glossary_data.json')
        
        with open(data_path, 'r', encoding='utf-8') as f:
            glossary_terms = json.load(f)
            print(f"✅ Cargados {len(glossary_terms)} términos desde el archivo local.")
    except FileNotFoundError:
        print("❌ Error: Asegúrate de que backend/data/glossary_data.json existe en la ruta correcta.")
        return
    except json.JSONDecodeError:
        print("❌ Error: El archivo JSON de glosario está mal formado.")
        return

    try:
        total_loaded = load_terms_from_json(session, glossary_terms)
        print(f"🎉 Éxito: Se cargaron/actualizaron {total_loaded} términos de glosario en la base de datos.")
    except Exception as e:
        print(f"❌ Error crítico durante la carga de BD: {e}")
    finally:
        # Usamos close() que es correcto para la sesión obtenida con get_session()
        session.close() 

if __name__ == '__main__':
    # Asegurarse de que las tablas estén actualizadas
    print("🔄 Verificando y creando tablas de modelos (ej. columna created_at)...")
    create_all() 
    run_glossary_loader()