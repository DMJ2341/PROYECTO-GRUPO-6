# backend/scripts/seed_preference_quiz.py

import sys
import os
import json

# Agregar path del backend
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

from database.db import get_session, create_all
# ✅ AGREGAR ESTA LÍNEA AQUÍ:
from models.user import User  # <--- ESTO ARREGLA EL ERROR DE REGISTRO
from models.assessments import PreferenceQuestion

def seed_quiz():
    """
    Carga las 25 preguntas del test de preferencias desde JSON a la base de datos.
    """
    # Inicializar DB (crear tablas si no existen)
    try:
        create_all()
    except Exception as e:
        print(f"⚠️ Nota: create_all lanzó advertencia: {e}")
    
    session = get_session()
    
    # Cargar preguntas desde JSON
    json_path = os.path.join(
        os.path.dirname(__file__),
        '../data/assessments/preference_questions.json'
    )
    
    try:
        with open(json_path, 'r', encoding='utf-8') as f:
            questions_data = json.load(f)
    except FileNotFoundError:
        print(f"❌ ERROR: No se encontró {json_path}")
        return
    
    print(f"🚀 Cargando {len(questions_data)} preguntas del Test de Preferencias...")
    
    loaded_count = 0
    updated_count = 0
    
    for q_data in questions_data:
        # Verificar si ya existe
        existing = session.query(PreferenceQuestion).filter_by(id=q_data['id']).first()
        
        if existing:
            # Actualizar (por si cambió el contenido)
            existing.question_number = q_data['id']
            existing.section = q_data.get('section', 'general')
            existing.question_text = q_data['question_text']
            # existing.question_subtext = q_data.get('question_subtext') 
            existing.options = q_data['options']
            # existing.image_url = q_data.get('image_url') 
            existing.is_active = True
            
            updated_count += 1
        else:
            # Crear nueva
            new_question = PreferenceQuestion(
                id=q_data['id'],
                question_number=q_data['id'],
                section=q_data.get('section', 'general'),
                question_text=q_data['question_text'],
                # question_subtext=q_data.get('question_subtext'),
                options=q_data['options'],
                # image_url=q_data.get('image_url'),
                is_active=True
            )
            session.add(new_question)
            loaded_count += 1
    
    try:
        session.commit()
        print(f"✅ Test cargado correctamente:")
        print(f"   - {loaded_count} preguntas nuevas")
        print(f"   - {updated_count} preguntas actualizadas")
        print(f"   - Total: {loaded_count + updated_count} preguntas en DB")
    except Exception as e:
        session.rollback()
        print(f"❌ ERROR al guardar: {str(e)}")
    finally:
        session.close()

if __name__ == "__main__":
    seed_quiz()