# backend/scripts/validate_all_lessons.py
import sys
import os
import json

# 1. 🛠️ TRUCO: Agregar el directorio padre ('backend') al path de Python
# Esto permite que el script encuentre la carpeta 'models'
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from models.lesson_schema import Lesson as LessonSchema  # Ahora sí funciona el import
from pydantic import ValidationError

def validate_json_file(file_path):
    """Valida un archivo JSON con Pydantic"""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            data = json.load(f)
        
        # Validación estricta
        LessonSchema.model_validate(data)
        print(f"✅ {os.path.basename(file_path)} válido!")
        return True
        
    except json.JSONDecodeError:
        print(f"❌ {os.path.basename(file_path)}: ERROR - JSON mal formado (comas, llaves, etc)")
        return False
    except ValidationError as e:
        print(f"❌ {os.path.basename(file_path)}: ERROR DE ESQUEMA")
        # Imprimir error simplificado
        for err in e.errors():
            loc = " -> ".join(str(x) for x in err['loc'])
            print(f"   - Campo '{loc}': {err['msg']}")
        return False
    except Exception as e:
        print(f"❌ {os.path.basename(file_path)}: Error desconocido - {str(e)}")
        return False

# Definir ruta: sube un nivel desde 'scripts' y entra a 'content/lessons'
base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
lessons_dir = os.path.join(base_dir, 'content', 'lessons')

print(f"🔍 Buscando lecciones en: {lessons_dir}")

files_checked = 0
errors_found = 0

if not os.path.exists(lessons_dir):
    print("⚠️ La carpeta de lecciones no existe. Creando carpeta vacía...")
    os.makedirs(lessons_dir, exist_ok=True)

for root, dirs, files in os.walk(lessons_dir):
    for file in files:
        if file.endswith('.json'):
            files_checked += 1
            full_path = os.path.join(root, file)
            if not validate_json_file(full_path):
                errors_found += 1

print("-" * 40)
print(f"Resumen: {files_checked} archivos revisados. {errors_found} errores.")

# Si hay errores, salimos con código de error (útil para CI/CD o scripts automáticos)
if errors_found > 0:
    sys.exit(1)