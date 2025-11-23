from models.lesson_schema import Lesson
import os
import json

def validate_json_file(file_path):
    with open(file_path, 'r') as f:
        data = json.load(f)
    Lesson.model_validate(data)
    print(f"{file_path} válido!")

# Valida todos JSON en content/lessons
for root, dirs, files in os.walk('content/lessons'):
    for file in files:
        if file.endswith('.json'):
            validate_json_file(os.path.join(root, file))