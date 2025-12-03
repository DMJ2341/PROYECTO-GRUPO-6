# backend/scripts/load_nist_glossary.py

import json
import os
import sys
from sqlalchemy.exc import IntegrityError

# Añadir el path para importar módulos locales desde la carpeta superior
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

from database.db import get_session, create_all
from models.glossary import Glossary

# --- CONFIGURACIÓN ---
MAX_TERMS = 1000  # Límite de seguridad, ajústalo si tienes más términos

def load_terms_from_json(session, terms_data):
    """Procesa una lista de términos desde JSON y los carga a la BD."""
    count = 0
    error_occurred = False
    
    
    if isinstance(terms_data, dict) and "terms" in terms_data:
        items = terms_data["terms"]
    else:
        items = terms_data

    print(f"📂 Procesando {len(items)} elementos...")

    for term_data in items:
        try:
            # 1. Obtener datos bilingües obligatorios
            t_en = term_data.get('term_en', '').strip()
            t_es = term_data.get('term_es', '').strip()
            d_en = term_data.get('definition_en', '').strip()
            d_es = term_data.get('definition_es', '').strip()
            
            # Validación básica
            if not t_en or not t_es or not d_en or not d_es:
                print(f"⚠️ Saltando término incompleto: {t_en or t_es}")
                continue
            
            # 2. Verificar duplicados (usando el término en inglés como identificador único)
            existing = session.query(Glossary).filter_by(term_en=t_en).first()
            if existing:
                # Si ya existe, lo saltamos para evitar duplicados
                continue

            # 3. Crear el objeto Glossary 
            new_term = Glossary(
                term_en=t_en,
                term_es=t_es,
                definition_en=d_en,
                definition_es=d_es,
                
                # Metadata opcional
                acronym=term_data.get('acronym'),
                category=term_data.get('category', 'General'),
                difficulty=term_data.get('difficulty', 'beginner'),
                
                # Ejemplos (si existieran en el JSON)
                example_en=term_data.get('example_en'),
                example_es=term_data.get('example_es'),
                
                
                where_you_hear_it=term_data.get('sources')
            )

            session.add(new_term)
            count += 1
            
            if count >= MAX_TERMS:
                break
        
        except IntegrityError as e:
            print(f"❌ Error de integridad (duplicado u otro): {e}")
            session.rollback()
            error_occurred = True
        except Exception as e:
            print(f"❌ Error general en '{term_data.get('term_en', 'Desconocido')}': {e}")
            session.rollback()
            error_occurred = True

    # Confirmar cambios si hubo inserciones
    if count > 0:
        try:
            session.commit()
            print("💾 Base de datos actualizada con éxito.")
        except Exception as e:
            print(f"❌ Error al hacer commit final: {e}")
            session.rollback()
    
    return count

def run_glossary_loader():
    print("--- 🚀 INICIANDO CARGA DE GLOSARIO BILINGÜE ---")
    session = get_session()
    
    try:
        # Ruta al archivo JSON generado
        data_path = os.path.join(os.path.dirname(__file__), '..', 'data', 'glossary_data.json')
        
        if not os.path.exists(data_path):
            print(f"❌ Error: No se encuentra el archivo en {data_path}")
            print("   Ejecuta primero: python backend/scripts/generate_full_glossary.py")
            return

        with open(data_path, 'r', encoding='utf-8') as f:
            glossary_json = json.load(f)
            print("✅ Archivo JSON leído correctamente.")

        total = load_terms_from_json(session, glossary_json)
        print(f"🎉 Proceso finalizado: {total} términos nuevos cargados.")

    except json.JSONDecodeError:
        print("❌ Error: El archivo JSON está mal formado.")
    except Exception as e:
        print(f"❌ Error crítico inesperado: {e}")
    finally:
        session.close()

if __name__ == '__main__':
    # Asegura que las tablas existan antes de intentar insertar
    create_all()
    run_glossary_loader()