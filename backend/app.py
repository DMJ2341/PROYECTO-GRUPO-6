from flask import Flask, jsonify, request
from flask_cors import CORS
from auth_service import AuthService
from gamification_service import GamificationService
from course_service import CourseService
import json
import os
from services.activity_service import ActivityService
from services.badge_service import BadgeService
from models.user import User
from models.course import Course
from models.lesson import Lesson
from functools import wraps

# ✅ IMPORTACIONES NUEVAS - con manejo de errores para compatibilidad
try:
    from database.db import db, init_db
    from config import Config
    from sqlalchemy import text  # ✅ IMPORTAR TEXT AQUÍ
    DB_AVAILABLE = True
    print("✅ Módulos de base de datos cargados correctamente")
except ImportError as e:
    print(f"🔧 Modo JSON - Base de datos no disponible: {e}")
    DB_AVAILABLE = False

app = Flask(__name__)
CORS(app)

# ✅ INICIALIZAR BASE DE DATOS SOLO SI ESTÁ DISPONIBLE
if DB_AVAILABLE:
    try:
        app.config.from_object(Config)
        init_db(app)
        print("✅ Base de datos PostgreSQL inicializada")
    except Exception as e:
        print(f"❌ Error inicializando base de datos: {e}")
        DB_AVAILABLE = False
else:
    print("🔧 Ejecutando en modo JSON")

# Inicializar servicios
auth_service = AuthService()
gamification_service = GamificationService()
course_service = CourseService()

def token_required(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        token = request.headers.get('Authorization', '').replace('Bearer ', '')
        user_data = auth_service.verify_token(token)
        
        if not user_data:
            return jsonify({'error': 'Token inválido'}), 401
            
        return f(user_data, *args, **kwargs)
    return decorated

@app.route('/api/auth/register', methods=['POST'])
def register():
    try:
        data = request.get_json()
        email = data.get('email')
        password = data.get('password')
        name = data.get('name', 'Usuario')
        
        result = auth_service.register(email, password, name)
        return jsonify(result)
    except Exception as e:
        return jsonify({'success': False, 'message': str(e)}), 400

@app.route('/api/auth/login', methods=['POST'])
def login():
    try:
        data = request.get_json()
        email = data.get('email')
        password = data.get('password')
        
        result = auth_service.login(email, password)
        return jsonify(result)
    except Exception as e:
        return jsonify({'success': False, 'message': str(e)}), 401

@app.route('/api/user/progress', methods=['GET'])
def get_user_progress():
    try:
        token = request.headers.get('Authorization', '').replace('Bearer ', '')
        user_data = auth_service.verify_token(token)
        
        if not user_data:
            return jsonify({'error': 'Token inválido'}), 401
            
        progress = gamification_service.get_user_progress(user_data['email'])
        return jsonify(progress)
    except Exception as e:
        return jsonify({'error': str(e)}), 400

@app.route('/api/courses', methods=['GET'])
def get_courses():
    try:
        token = request.headers.get('Authorization', '').replace('Bearer ', '')
        user_data = auth_service.verify_token(token)
        
        if not user_data:
            return jsonify({'error': 'Token inválido'}), 401
            
        courses = course_service.get_all_courses()
        return jsonify(courses)
    except Exception as e:
        return jsonify({'error': str(e)}), 400

@app.route('/api/courses/<course_id>', methods=['GET'])
def get_course_detail(course_id):
    try:
        token = request.headers.get('Authorization', '').replace('Bearer ', '')
        user_data = auth_service.verify_token(token)
        
        if not user_data:
            return jsonify({'error': 'Token inválido'}), 401
            
        course = course_service.get_course_by_id(course_id)
        return jsonify(course)
    except Exception as e:
        return jsonify({'error': str(e)}), 400

@app.route('/api/achievements', methods=['GET'])
def get_achievements():
    try:
        token = request.headers.get('Authorization', '').replace('Bearer ', '')
        user_data = auth_service.verify_token(token)
        
        if not user_data:
            return jsonify({'error': 'Token inválido'}), 401
            
        achievements = gamification_service.get_user_achievements(user_data['email'])
        return jsonify(achievements)
    except Exception as e:
        return jsonify({'error': str(e)}), 400

# ✅ ENDPOINTS PARA LECCIONES
@app.route('/api/courses/<course_id>/lessons', methods=['GET'])
def get_course_lessons(course_id):
    """Obtener todas las lecciones de un curso"""
    try:
        token = request.headers.get('Authorization', '').replace('Bearer ', '')
        user_data = auth_service.verify_token(token)
        
        if not user_data:
            return jsonify({'error': 'Token inválido'}), 401
        
        if not DB_AVAILABLE:
            return jsonify({'lessons': []}), 200
        
        result = db.session.execute(
            text("""
                SELECT lesson_id, title, lesson_order, xp_reward, duration_minutes 
                FROM lessons 
                WHERE course_id = :course_id 
                ORDER BY lesson_order
            """),
            {"course_id": course_id}
        ).fetchall()
        
        lessons = []
        for row in result:
            lessons.append({
                'id': row[0],
                'title': row[1],
                'order': row[2],
                'xp_reward': row[3],
                'duration_minutes': row[4]
            })
        
        return jsonify({'lessons': lessons})
    
    except Exception as e:
        return jsonify({'error': str(e)}), 400

@app.route('/api/lessons/<lesson_id>', methods=['GET'])
def get_lesson_content(lesson_id):
    """Obtener el contenido completo de una lección"""
    try:
        token = request.headers.get('Authorization', '').replace('Bearer ', '')
        user_data = auth_service.verify_token(token)
        
        if not user_data:
            return jsonify({'error': 'Token inválido'}), 401
        
        if not DB_AVAILABLE:
            return jsonify({'error': 'Base de datos no disponible'}), 503
        
        result = db.session.execute(
            text("""
                SELECT lesson_id, title, content, xp_reward, duration_minutes, course_id
                FROM lessons 
                WHERE lesson_id = :lesson_id
            """),
            {"lesson_id": lesson_id}
        ).fetchone()
        
        if not result:
            return jsonify({'error': 'Lección no encontrada'}), 404
        
        lesson = {
            'id': result[0],
            'title': result[1],
            'content': result[2],
            'xp_reward': result[3],
            'duration_minutes': result[4],
            'course_id': result[5]
        }
        
        return jsonify(lesson)
    
    except Exception as e:
        return jsonify({'error': str(e)}), 400

# ✅ ENDPOINT CORREGIDO PARA LECCIONES INTERACTIVAS
@app.route('/api/lessons/<lesson_id>/interactive', methods=['GET'])
def get_interactive_lesson(lesson_id):
    """Obtener lección interactiva desde PostgreSQL"""
    print(f"🔹 Petición de lección interactiva: {lesson_id}")
    
    token = request.headers.get('Authorization', '').replace('Bearer ', '')
    
    # Validar token usando el método existente
    user_data = auth_service.verify_token(token)
    
    if not user_data:
        print("❌ Token inválido")
        return jsonify({'error': 'No autorizado'}), 401
    
    try:
        print(f"🔹 Usuario autenticado: {user_data['email']}")
        
        # Verificar que la base de datos está disponible
        if not DB_AVAILABLE:
            print("❌ Base de datos no disponible")
            return jsonify({'error': 'Base de datos no disponible'}), 503
        
        # Buscar lección en PostgreSQL
        result = db.session.execute(
            text("""
                SELECT 
                    lesson_id,
                    course_id,
                    title,
                    lesson_order,
                    xp_reward,
                    duration_minutes,
                    lesson_type,
                    screens,
                    total_screens
                FROM lessons 
                WHERE lesson_id = :lesson_id 
                AND lesson_type = 'interactive'
            """),
            {"lesson_id": lesson_id}
        ).fetchone()
        
        if not result:
            print(f"❌ Lección interactiva no encontrada: {lesson_id}")
            return jsonify({'error': 'Lección no encontrada'}), 404
        
        print(f"🔹 Lección encontrada: {result.title}")
        print(f"🔹 Tipo de screens: {type(result.screens)}")
        
        # Manejar diferentes formatos de screens
        screens_data = result.screens
        
        # Si screens es un diccionario con clave 'screens', extraer el array
        if isinstance(screens_data, dict) and 'screens' in screens_data:
            screens_array = screens_data['screens']
        # Si screens es directamente el array, usarlo tal cual
        elif isinstance(screens_data, list):
            screens_array = screens_data
        else:
            print(f"❌ Formato de screens no reconocido: {type(screens_data)}")
            screens_array = []
        
        # Construir respuesta
        lesson_data = {
            'lesson_id': result.lesson_id,
            'course_id': result.course_id,
            'title': result.title,
            'lesson_order': result.lesson_order,
            'xp_reward': result.xp_reward,
            'duration_minutes': result.duration_minutes,
            'lesson_type': result.lesson_type,
            'total_screens': result.total_screens,
            'screens': screens_array  # Usar el array extraído
        }
        
        print(f"✅ Lección cargada desde PostgreSQL: {lesson_id}")
        print(f"✅ Total de pantallas: {len(screens_array)}")
        
        return jsonify({
            'success': True,
            'lesson': lesson_data
        }), 200
        
    except Exception as e:
        print(f"❌ Error obteniendo lección: {e}")
        import traceback
        traceback.print_exc()
        return jsonify({'error': str(e)}), 500

@app.route('/api/lessons/<lesson_id>/progress', methods=['POST'])
def save_lesson_progress(lesson_id):
    """Guardar progreso por pantalla"""
    try:
        token = request.headers.get('Authorization', '').replace('Bearer ', '')
        user_data = auth_service.verify_token(token)
        
        if not user_data:
            return jsonify({'error': 'Token inválido'}), 401
        
        if not DB_AVAILABLE:
            return jsonify({'success': True, 'message': 'Modo JSON - Progreso simulado'}), 200
        
        data = request.json
        email = user_data['email']
        
        # Actualizar o insertar progreso
        db.session.execute(
            text("""
                INSERT INTO user_lesson_progress 
                (user_email, lesson_id, current_screen, completed_screens, signals_found, quiz_answers)
                VALUES (:email, :lesson_id, :current_screen, :completed_screens, :signals_found, :quiz_answers)
                ON CONFLICT (user_email, lesson_id) 
                DO UPDATE SET
                    current_screen = :current_screen,
                    completed_screens = :completed_screens,
                    signals_found = :signals_found,
                    quiz_answers = :quiz_answers
            """),
            {
                'email': email,
                'lesson_id': lesson_id,
                'current_screen': data.get('current_screen', 1),
                'completed_screens': data.get('completed_screens', []),
                'signals_found': data.get('signals_found', []),
                'quiz_answers': json.dumps(data.get('quiz_answers', {}))
            }
        )
        db.session.commit()
        
        return jsonify({'success': True}), 200
        
    except Exception as e:
        if DB_AVAILABLE:
            db.session.rollback()
        print(f"Error guardando progreso: {e}")
        return jsonify({'error': str(e)}), 500

@app.route('/api/user/complete-activity', methods=['POST'])
@token_required
def complete_activity(current_user):
    try:
        data = request.get_json()
        activity_type = data.get('type', 'lesson_completed')
        lesson_id = data.get('lesson_id')
        difficulty = data.get('difficulty', 1)
        
        # Obtener user_id del email
        user = User.query.filter_by(email=current_user['email']).first()
        if not user:
            return jsonify({'error': 'Usuario no encontrado'}), 404
        
        result = ActivityService.record_activity(
            user.id, activity_type, lesson_id, difficulty
        )
        
        # Verificar badges
        new_badges = BadgeService.check_and_award_badges(user.id)
        
        return jsonify({
            'success': True,
            'activity_result': result,
            'new_badges': new_badges
        })
        
    except Exception as e:
        return jsonify({'error': str(e)}), 400

@app.route('/api/user/badges', methods=['GET'])
@token_required
def get_user_badges(current_user):
    try:
        user = User.query.filter_by(email=current_user['email']).first()
        if not user:
            return jsonify({'error': 'Usuario no encontrado'}), 404
        
        badges = BadgeService.get_user_badges(user.id)
        return jsonify({'badges': badges})
        
    except Exception as e:
        return jsonify({'error': str(e)}), 400

@app.route('/')
def home():
    endpoints = [
        'POST /api/auth/register',
        'POST /api/auth/login', 
        'GET /api/user/progress',
        'GET /api/courses',
        'GET /api/achievements',
        'GET /api/courses/<course_id>/lessons',
        'GET /api/lessons/<lesson_id>',
        'GET /api/lessons/<lesson_id>/interactive',
        'POST /api/lessons/<lesson_id>/progress'
    ]
    
    return jsonify({
        'message': '🚀 CyberLearn Backend funcionando!',
        'version': '1.0',
        'endpoints': endpoints
    })

if __name__ == '__main__':
    print("=" * 60)
    print("🔥 CYBERLEARN BACKEND - INICIANDO SERVIDOR")
    print("📍 URL Local: http://localhost:5000")
    print("📱 URL Android: http://10.0.2.2:5000")
    print("📚 Endpoints disponibles:")
    print("   POST /api/auth/register")
    print("   POST /api/auth/login")
    print("   GET /api/user/progress") 
    print("   GET /api/courses")
    print("   GET /api/achievements")
    print("   GET /api/courses/<course_id>/lessons")
    print("   GET /api/lessons/<lesson_id>")
    print("   GET /api/lessons/<lesson_id>/interactive")
    print("   POST /api/lessons/<lesson_id>/progress")
    print("=" * 60)
    app.run(debug=True, port=5000, host='0.0.0.0')