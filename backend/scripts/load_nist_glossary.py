# backend/scripts/load_nist_glossary.py

import json
import os
import sys
from sqlalchemy.exc import IntegrityError

# Añadir el path para importar módulos locales
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

from database.db import get_session, create_all
from models.glossary import Glossary

# --- CONFIGURACIÓN ---
MAX_TERMS = 500 

def load_terms_from_json(session, terms_data):
    """Procesa una lista de términos desde JSON y los carga a la BD."""
    count = 0
    error_occurred = False
    
    # Extraer la lista si el JSON tiene una clave raíz "terms" (común en APIs), 
    # o usar la lista directa si el JSON es una lista [].
    if isinstance(terms_data, dict) and "terms" in terms_data:
        items = terms_data["terms"]
    else:
        items = terms_data

    print(f"📂 Procesando {len(items)} elementos...")

    for term_data in items:
        try:
            # 1. Obtener datos bilingües del JSON
            # Nota: Usamos .get() con valores por defecto para evitar errores si falta algo
            t_en = term_data.get('term_en', '').strip()
            t_es = term_data.get('term_es', '').strip()
            d_en = term_data.get('definition_en', '').strip()
            d_es = term_data.get('definition_es', '').strip()
            
            # Validación: Deben existir al menos los términos y definiciones básicos
            if not t_en or not t_es or not d_en or not d_es:
                print(f"⚠️ Saltando término incompleto: {t_en or t_es}")
                continue
            
            # 2. Verificar duplicados (buscamos por el término en inglés como clave principal)
            existing = session.query(Glossary).filter_by(term_en=t_en).first()
            if existing:
                # Opcional: Actualizar si ya existe
                continue

            # 3. Crear el nuevo objeto Glossary con estructura BILINGÜE
            new_term = Glossary(
                term_en=t_en,
                term_es=t_es,
                definition_en=d_en,
                definition_es=d_es,
                # Metadata opcional
                acronym=term_data.get('acronym'),
                category=term_data.get('category', 'General'),
                difficulty=term_data.get('difficulty', 'beginner'),
                example_en=term_data.get('example_en'),
                example_es=term_data.get('example_es'),
                where_you_hear_it=term_data.get('source') # O el campo que corresponda en tu JSON
            )

            session.add(new_term)
            count += 1
            if count >= MAX_TERMS:
                break
        
        except IntegrityError as e:
            print(f"❌ Error de integridad: {e}")
            session.rollback()
            error_occurred = True
        except Exception as e:
            print(f"❌ Error general en {term_data.get('term_en', 'Desconocido')}: {e}")
            session.rollback()
            error_occurred = True

    if not error_occurred and count > 0:
        session.commit()
        print("💾 Commit realizado con éxito.")
    
    return count

def run_glossary_loader():
    print("--- 🚀 INICIANDO CARGA DE GLOSARIO BILINGÜE ---")
    session = get_session()
    
    try:
        # Asegúrate de que este archivo también tenga la estructura nueva (term_en, term_es, etc.)
        data_path = os.path.join(os.path.dirname(__file__), '..', 'data', 'glossary_data.json')
        
        with open(data_path, 'r', encoding='utf-8') as f:
            glossary_json = json.load(f)
            print("✅ Archivo JSON leído correctamente.")
            
    except FileNotFoundError:
        print(f"❌ Error: No se encuentra el archivo en {data_path}")
        return
    except json.JSONDecodeError:
        print("❌ Error: JSON mal formado.")
        return

    try:
        total = load_terms_from_json(session, glossary_json)
        print(f"🎉 Finalizado: {total} términos cargados.")
    except Exception as e:
        print(f"❌ Error crítico: {e}")
    finally:
        session.close()

if __name__ == '__main__':
    create_all() # Asegura que la tabla exista antes de insertar
    run_glossary_loader()