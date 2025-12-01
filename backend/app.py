# backend/app.py - VERSIÓN 3.2.1 - XP CORREGIDO
from flask import Flask, request, jsonify
from flask_cors import CORS
from database.db import get_session, create_all
from sqlalchemy import text, desc
import jwt
import datetime
from functools import wraps
import sentry_sdk
from config import Config

# --- MODELOS ---
from models.user import User
from models.user_progress import UserCourseProgress, UserLessonProgress
from models.refresh_token import RefreshToken
from models.user_badge import UserBadge
from models.course import Course
from models.glossary import Glossary
from models.lesson import Lesson
from models.activity import Activity
from models.badge import Badge
from models.password_reset_token import PasswordResetToken
from models.user_glossary_favorite import UserGlossaryFavorite
from models.daily_term_log import DailyTermLog
from models.assessments import (
    FinalExamQuestion, UserExamAttempt, 
    PreferenceQuestion, UserPreferenceResult
)


# --- SERVICIOS ---
from services.activity_service import ActivityService
from services.glossary_service import get_all_glossary_terms, search_glossary, get_glossary_stats
from services.password_reset_service import create_reset_token, validate_reset_token, reset_password
from services.course_service import CourseService
from services.badge_service import BadgeService
from services.progress_service import mark_lesson_completed, get_user_course_progress
from services.auth_service import AuthService
from services.lesson_service import create_lesson
from services.streak_service import StreakService
from services.glossary_favorite_service import toggle_favorite, get_user_favorites, is_favorite
from services.daily_term_service import get_daily_term_for_user, complete_daily_term
from services.exam_service import ExamService
from services.preference_quiz import get_preference_questions, submit_preference_answers, engine  # ✅ CORRECTO
from services.glossary_service import (
    get_all_glossary_terms, 
    search_glossary, 
    mark_term_as_learned,
    get_learned_terms,
    get_glossary_stats,
    record_quiz_attempt
)
from services.test_preference_service import TestPreferenceService

# -------------------------------------------------------------------
# SENTRY CONFIG
# -------------------------------------------------------------------
sentry_sdk.init(
    dsn="https://242076c45c627b58d1b4254f28c0606a@o4510415052931072.ingest.us.sentry.io/4510415138586624",
    send_default_pii=True,
    traces_sample_rate=1.0,
    _experiments={"profiles_sample_rate": 1.0},
)

app = Flask(__name__)
app.config.from_object(Config)
CORS(app)

# INICIALIZACIÓN DE BASE DE DATOS
try:
    with app.app_context():
        create_all()
        session = get_session()
        session.execute(text("SELECT 1"))
        print("✅ Base de datos conectada correctamente")
except Exception as e:
    sentry_sdk.capture_exception(e)
    print(f"❌ Error conectando a la base de datos: {e}")

# ---------- AUTH DECORATOR ----------
def token_required(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        token = None
        auth_header = request.headers.get('Authorization')
        
        if not auth_header:
            return jsonify({'error': 'Token es requerido'}), 401
        
        parts = auth_header.split()
        if len(parts) == 2 and parts[0].lower() == 'bearer':
            token = parts[1]
        elif len(parts) == 1:
            token = parts[0]
        else:
            return jsonify({'error': 'Formato de token inválido'}), 401

        try:
            data = jwt.decode(token, app.config['SECRET_KEY'], algorithms=['HS256'])
            current_user_id = data['user_id']
            sentry_sdk.set_user({"id": current_user_id, "email": data.get('email')})
        except jwt.ExpiredSignatureError:
            return jsonify({'error': 'Token expirado'}), 401
        except jwt.InvalidTokenError:
            return jsonify({'error': 'Token inválido'}), 401
        except Exception as e:
            sentry_sdk.capture_exception(e)
            return jsonify({'error': 'Error procesando token'}), 401
            
        return f(current_user_id, *args, **kwargs)
    return decorated

# ---------- HEALTH CHECK ----------
@app.route('/api/health', methods=['GET'])
def health_check():
    return jsonify({
        'status': 'OK', 
        'message': 'CyberLearn API Operativa',
        'version': '3.2.1',  # ✅ ACTUALIZADO
        'database': 'Conectada',
        'environment': 'Producción'
    })

# ==========================================
# 🔐 RECUPERACIÓN DE CONTRASEÑA
# ==========================================

@app.route('/api/auth/forgot-password', methods=['POST'])
def forgot_password_route():
    data = request.get_json()
    email = data.get('email')
    if not email:
        return jsonify({"error": "Email requerido"}), 400

    try:
        result = create_reset_token(email)
        return jsonify(result), 200
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({"error": "Error interno"}), 500

@app.route('/api/auth/validate-reset-token', methods=['POST'])
def validate_reset_token_route():
    data = request.get_json()
    token = data.get('token')
    email = data.get('email')
    if not token or not email:
        return jsonify({"error": "Token y email requeridos"}), 400

    try:
        result = validate_reset_token(token, email)
        return jsonify(result), 200
    except ValueError as e:
        return jsonify({"error": str(e)}), 400
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({"error": "Error validando token"}), 500

@app.route('/api/auth/reset-password', methods=['POST'])
def reset_password_route():
    data = request.get_json()
    token = data.get('token')
    email = data.get('email')
    password = data.get('password')
    
    if not all([token, email, password]):
        return jsonify({"error": "Faltan datos"}), 400
    if len(password) < 8:
        return jsonify({"error": "Contraseña muy corta (mínimo 8 caracteres)"}), 400

    try:
        result = reset_password(token, email, password)
        return jsonify(result), 200
    except ValueError as e:
        return jsonify({"error": str(e)}), 400
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({"error": "Error reseteando contraseña"}), 500


@app.route('/api/auth/login', methods=['POST'])
def login():
    try:
        data = request.get_json()
        if not data or not data.get('email') or not data.get('password'):
            return jsonify({"error": "Email y password son requeridos"}), 400
        
        auth_service = AuthService()
        login_data = auth_service.login(data['email'], data['password'])
        
        return jsonify({
            "success": True,
            "access_token": login_data['access_token'],
            "refresh_token": login_data['refresh_token'],
            "user": login_data['user']
        })
        
    except ValueError as ve:
        return jsonify({"error": str(ve)}), 401
    except Exception as e:
        sentry_sdk.capture_exception(e)
        print(f"❌ Error en login: {e}")
        return jsonify({"error": "Error interno"}), 500

@app.route('/api/auth/refresh', methods=['POST'])
def refresh_token_route():
    try:
        data = request.get_json()
        refresh_token = data.get('refresh_token')
        if not refresh_token:
            return jsonify({"error": "refresh_token requerido"}), 400

        auth_service = AuthService()
        tokens = auth_service.refresh(refresh_token)
        return jsonify(tokens), 200
        
    except ValueError as e:
        return jsonify({"error": str(e)}), 401
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({"error": "Error interno"}), 500

@app.route('/api/auth/logout', methods=['POST'])
@token_required
def logout_route(current_user_id):
    try:
        data = request.get_json()
        refresh_token = data.get('refresh_token')
        if refresh_token:
            auth_service = AuthService()
            auth_service.revoke_refresh_token(refresh_token)
        return jsonify({"success": True, "message": "Logout exitoso"})
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({"error": "Error en logout"}), 500

# ==========================================
# 📚 CURSOS Y LECCIONES
# ==========================================

@app.route('/api/courses', methods=['GET'])
def get_courses():
    try:
        session = get_session()
        try:
            courses = session.query(Course).all()
            out = []
            for c in courses:
                out.append({
                    'id': c.id,
                    'title': c.title,
                    'description': c.description,
                    'level': c.level,
                    'xp_reward': c.xp_reward,
                    'image_url': c.image_url or ""
                })
            return jsonify(out)
        finally:
            session.close()
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({"error": "Error interno al obtener cursos"}), 500

@app.route('/api/courses/<int:course_id>/lessons', methods=['GET'])
def get_course_lessons(course_id):
    try:
        session = get_session()
        try:
            lessons = session.query(Lesson).filter_by(course_id=course_id).order_by(Lesson.order_index).all()
            
            if not lessons:
                return jsonify([]), 200
            
            result = []
            for l in lessons:
                result.append({
                    'id': l.id,
                    'course_id': l.course_id,
                    'title': l.title,
                    'description': l.description,
                    'type': l.type,
                    'duration_minutes': l.duration_minutes,
                    'xp_reward': getattr(l, 'xp_reward', 10),
                    'order_index': l.order_index,
                    'is_completed': False 
                })
            return jsonify(result)
        finally:
            session.close()
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({"error": "Error interno"}), 500

@app.route('/api/lessons/<lesson_id>', methods=['GET'])
@token_required
def get_lesson_secure(current_user_id, lesson_id):
    """Obtiene una lección con validación de bloqueo secuencial."""
    try:
        session = get_session()
        try:
            lesson = session.query(Lesson).filter_by(id=lesson_id).first()
            if not lesson:
                return jsonify({"error": "Lección no encontrada"}), 404
            
            previous_lesson = session.query(Lesson)\
                .filter(Lesson.course_id == lesson.course_id, Lesson.order_index < lesson.order_index)\
                .order_by(desc(Lesson.order_index))\
                .first()

            if previous_lesson:
                prog = session.query(UserLessonProgress).filter_by(
                    user_id=current_user_id,
                    lesson_id=previous_lesson.id,
                    completed=True
                ).first()

                if not prog:
                    return jsonify({
                        "error": "Lección bloqueada",
                        "message": f"Debes completar la lección '{previous_lesson.title}' antes de acceder a esta.",
                        "previous_lesson_id": previous_lesson.id
                    }), 403
            
            return jsonify({
                "success": True,
                "id": lesson.id,
                "title": lesson.title,
                "description": lesson.description,
                "content": lesson.content,
                "type": lesson.type,
                "screens": lesson.screens,
                "total_screens": getattr(lesson, 'total_screens', 0),
                "duration_minutes": lesson.duration_minutes,
                "xp_reward": getattr(lesson, 'xp_reward', 10),
                "order_index": lesson.order_index
            })
        finally:
            session.close()
    except Exception as e:
        sentry_sdk.capture_exception(e)
        print(f"❌ Error en lesson: {e}")
        return jsonify({"error": "Error interno"}), 500

@app.route('/api/lessons', methods=['POST'])
def create_lesson_route():
    try:
        data = request.json
        lesson_id = create_lesson(data)
        return jsonify({"success": True, "id": lesson_id}), 201
    except ValueError as ve:
        return jsonify({"error": str(ve)}), 400
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({"error": "Error interno"}), 500

# ==========================================
# 👤 USUARIO Y DASHBOARD
# ==========================================

@app.route('/api/user/profile', methods=['GET'])
@token_required
def get_user_profile(current_user_id):
    try:
        session = get_session()
        try:
            user = session.query(User).filter_by(id=current_user_id).first()
            if not user:
                return jsonify({"error": "Usuario no encontrado"}), 404
            
            # ✅ AGREGADO: Contar cursos completados
            completed_courses = session.query(UserCourseProgress).filter_by(
                user_id=current_user_id,
                percentage=100
            ).count()
            
            # ✅ AGREGADO: Contar insignias
            badges_count = session.query(UserBadge).filter_by(
                user_id=current_user_id
            ).count()
            
            return jsonify({
                "success": True,
                "user": {
                    "id": user.id,
                    "email": user.email,
                    "name": user.name,
                    "created_at": user.created_at.isoformat() if user.created_at else None,
                    "total_xp": user.total_xp,  # ✅ AGREGADO
                    "level": (user.total_xp // 100) + 1,  # ✅ AGREGADO
                    "completed_courses": completed_courses,  # ✅ AGREGADO
                    "badges_count": badges_count  # ✅ AGREGADO
                }
            })
        finally:
            session.close()
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({"error": "Error interno"}), 500

@app.route('/api/user/dashboard', methods=['GET'])
@token_required
def get_user_dashboard(current_user_id):
    try:
        session = get_session()
        streak_service = StreakService()

        # ✅ CORREGIDO: Obtener user y leer total_xp directamente
        user = session.query(User).filter_by(id=current_user_id).first()
        if not user:
            return jsonify({"error": "Usuario no encontrado"}), 404
        
        total_xp = user.total_xp  # ✅ Lee de users.total_xp, NO de activities
        current_streak = streak_service.get_current_streak(current_user_id)
        streak_bonus = streak_service.get_streak_bonus(current_streak)
        user_badges_count = session.query(UserBadge).filter_by(user_id=current_user_id).count()

        course_progress_list = []
        courses = session.query(Course).order_by(Course.id).all()

        for course in courses:
            progress = session.query(UserCourseProgress).filter_by(
                user_id=current_user_id,
                course_id=course.id
            ).first()

            if progress:
                course_data = {
                    "course_id": course.id,
                    "title": course.title,
                    "description": course.description or "",
                    "image_url": course.image_url or "",
                    "completed_lessons": progress.completed_lessons,
                    "total_lessons": progress.total_lessons,
                    "percentage": progress.percentage,
                    "completed": progress.percentage == 100,
                    "completed_at": progress.completed_at.isoformat() if progress.completed_at else None
                }
            else:
                total_lessons = session.query(Lesson).filter_by(course_id=course.id).count()
                course_data = {
                    "course_id": course.id,
                    "title": course.title,
                    "description": course.description or "",
                    "image_url": course.image_url or "",
                    "completed_lessons": 0,
                    "total_lessons": total_lessons,
                    "percentage": 0,
                    "completed": False,
                    "completed_at": None
                }
            course_progress_list.append(course_data)

        next_course = None
        for prog in course_progress_list:
            if prog["percentage"] < 100:
                next_course = {
                    "course_id": prog["course_id"],
                    "title": prog["title"],
                    "level": "En curso" if prog["percentage"] > 0 else "Nuevo"
                }
                break
        
        if not next_course and course_progress_list:
             next_course = {"title": "¡Todo completado!", "level": "Maestro"}
        elif not next_course:
             next_course = {"title": "Sin cursos disponibles", "level": "-"}

        # ✅ AGREGADO: Verificar si tiene resultado del test vocacional
        has_preference_result = session.query(UserPreferenceResult).filter_by(
            user_id=current_user_id
        ).first() is not None

        session.close()

        return jsonify({
            "success": True,
            "dashboard": {
                "total_xp": total_xp,  # ✅ Ahora lee de user.total_xp correctamente
                "level": (total_xp // 100) + 1,
                "current_streak": current_streak,
                "streak_bonus": streak_bonus,
                "badges_count": user_badges_count,
                "courses_progress": course_progress_list,
                "next_course": next_course,
                "completed_courses": sum(1 for c in course_progress_list if c["completed"]),
                "total_courses": len(course_progress_list),
                "has_preference_result": has_preference_result,  # ✅ AGREGADO
                "final_exam_passed": False  # ✅ AGREGADO
            }
        })

    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({"error": "Error cargando dashboard"}), 500

# ==========================================
# 📊 PROGRESO Y ACTIVIDAD
# ==========================================

@app.route('/api/progress/lesson/<lesson_id>', methods=['POST'])
@token_required
def complete_lesson_route(current_user_id, lesson_id):
    try:
        result = mark_lesson_completed(current_user_id, lesson_id)
        return jsonify({"success": True, "data": result}), 200
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({"error": "Error marcando lección"}), 500

@app.route('/api/progress/course/<int:course_id>', methods=['GET'])
@token_required
def get_course_progress_route(current_user_id, course_id):
    try:
        progress = get_user_course_progress(current_user_id, course_id)
        if not progress:
            return jsonify({"percentage": 0, "completed_lessons": 0, "total_lessons": 0})
        return jsonify(progress)
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({"error": "Error obteniendo progreso"}), 500

@app.route('/api/progress/all', methods=['GET'])
@token_required
def get_all_progress_route(current_user_id):
    try:
        progresses = get_user_course_progress(current_user_id)
        return jsonify({"success": True, "courses": progresses or []})
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({"error": "Error obteniendo progresos"}), 500

# ==========================================
# 🏅 MEDALLAS
# ==========================================

@app.route('/api/badges/available', methods=['GET'])
def get_available_badges():
    try:
        session = get_session()
        try:
            badges = session.query(Badge).all()
            return jsonify({"success": True, "badges": [
                {"id": b.id, "name": b.name, "description": b.description, "icon": b.icon,
                 "xp_required": b.xp_required} for b in badges]})
        finally:
            session.close()
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({"error": "Error interno"}), 500

@app.route('/api/user/badges', methods=['GET'])
@token_required
def get_user_badges(current_user_id):
    try:
        session = get_session()
        try:
            user_badges = session.query(UserBadge, Badge).join(
                Badge, UserBadge.badge_id == Badge.id
            ).filter(UserBadge.user_id == current_user_id).all()

            return jsonify({"success": True, "badges": [
                {
                    "id": badge.id,
                    "name": badge.name,
                    "description": badge.description,
                    "icon": badge.icon,
                    "xp_required": badge.xp_required,
                    "earned_at": ub.earned_at.isoformat(),
                    "earned_value": ub.earned_value
                } for ub, badge in user_badges
            ]})
        finally:
            session.close()
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({"error": "Error interno al obtener medallas"}), 500


# ==========================================
# 📖 GLOSARIO MEJORADO
# ==========================================

@app.route('/api/glossary', methods=['GET'])
@token_required
def get_glossary_route(current_user_id):
    """Obtiene todos los términos con progreso del usuario."""
    try:
        terms = get_all_glossary_terms(user_id=current_user_id)
        return jsonify({"success": True, "terms": terms})
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({"error": "Error cargando glosario"}), 500

@app.route('/api/glossary/search', methods=['GET'])
@token_required
def search_glossary_route(current_user_id):
    """Busca términos en el glosario."""
    query = request.args.get('q', '')
    if len(query) < 2:
        return jsonify({"success": True, "terms": []})
    try:
        results = search_glossary(query, user_id=current_user_id)
        return jsonify({"success": True, "terms": results})
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({"error": "Error en búsqueda"}), 500

@app.route('/api/glossary/<int:glossary_id>/mark-learned', methods=['POST'])
@token_required
def mark_learned_route(current_user_id, glossary_id):
    """Marca un término como aprendido/no aprendido."""
    try:
        data = request.get_json()
        is_learned = data.get('is_learned', True)
        
        result = mark_term_as_learned(current_user_id, glossary_id, is_learned)
        return jsonify(result), 200
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({"error": "Error marcando término"}), 500

@app.route('/api/glossary/learned', methods=['GET'])
@token_required
def get_learned_terms_route(current_user_id):
    """Obtiene solo los términos aprendidos (para quiz)."""
    try:
        terms = get_learned_terms(current_user_id)
        return jsonify({"success": True, "terms": terms})
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({"error": "Error obteniendo términos aprendidos"}), 500

@app.route('/api/glossary/stats', methods=['GET'])
@token_required
def glossary_stats_route(current_user_id):
    """Obtiene estadísticas del glosario del usuario."""
    try:
        stats = get_glossary_stats(user_id=current_user_id)
        return jsonify({"success": True, "stats": stats})
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({"error": "Error obteniendo estadísticas"}), 500

@app.route('/api/glossary/<int:glossary_id>/quiz-attempt', methods=['POST'])
@token_required
def quiz_attempt_route(current_user_id, glossary_id):
    """Registra un intento de quiz (práctica)."""
    try:
        data = request.get_json()
        is_correct = data.get('is_correct', False)
        
        result = record_quiz_attempt(current_user_id, glossary_id, is_correct)
        return jsonify(result), 200
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({"error": "Error registrando intento"}), 500


# ==========================================
# 🎓 EXÁMENES Y APTITUD (EVALUACIONES)
# ==========================================

@app.route('/api/exam/final', methods=['GET'])
@token_required
def get_final_exam(current_user_id):
    service = ExamService()
    status = service.can_take_exam(current_user_id)
    
    if not status['allowed']:
        return jsonify({"error": "No puedes tomar el examen", "reason": status}), 403
        
    content = service.get_final_exam_content()
    return jsonify({"success": True, "exam": content})

@app.route('/api/exam/final/submit', methods=['POST'])
@token_required
def submit_final_exam(current_user_id):
    data = request.get_json()
    answers = data.get('answers')
    
    service = ExamService()
    result = service.submit_exam(current_user_id, answers)
    return jsonify({"success": True, "result": result})

# ==========================================
# 🎯 TEST DE PREFERENCIAS VOCACIONALES
# ==========================================

@app.route('/api/test/questions', methods=['GET'])
@token_required
def get_test_questions(current_user_id):
    """
    Obtiene las 28 preguntas del test
    GET /api/test/questions
    """
    try:
        service = TestPreferenceService()
        questions = service.get_questions()
        return jsonify({
            'success': True,
            'questions': questions,
            'total': len(questions)
        }), 200
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({'error': 'Error cargando preguntas'}), 500


@app.route('/api/test/submit', methods=['POST'])
@token_required
def submit_test_preference(current_user_id):
    """
    Envía respuestas del test y calcula resultado
    POST /api/test/submit
    Body: {
        "answers": {
            "1": 5,
            "2": 4,
            ...
        },
        "time_taken": 320  # segundos (opcional)
    }
    """
    try:
        data = request.get_json()
        answers = data.get('answers')
        time_taken = data.get('time_taken')
        
        if not answers or not isinstance(answers, dict):
            return jsonify({'error': 'Respuestas inválidas'}), 400
        
        if len(answers) != 28:
            return jsonify({
                'error': f'Se requieren 28 respuestas, recibidas: {len(answers)}'
            }), 400
        
        service = TestPreferenceService()
        result = service.submit_test(current_user_id, answers, time_taken)
        
        return jsonify({
            'success': True,
            'result': result
        }), 200
        
    except ValueError as ve:
        return jsonify({'error': str(ve)}), 400
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({'error': 'Error procesando test'}), 500


@app.route('/api/test/recommendations/<role>', methods=['GET'])
@token_required
def get_test_recommendations(current_user_id, role):
    """
    Obtiene recomendaciones completas para un rol
    GET /api/test/recommendations/RED_TEAM
    GET /api/test/recommendations/BLUE_TEAM
    GET /api/test/recommendations/PURPLE_TEAM
    """
    try:
        if role not in ['RED_TEAM', 'BLUE_TEAM', 'PURPLE_TEAM']:
            return jsonify({'error': 'Rol inválido'}), 400
        
        service = TestPreferenceService()
        recommendations = service.get_recommendations(role)
        
        return jsonify({
            'success': True,
            'recommendations': recommendations
        }), 200
        
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({'error': 'Error cargando recomendaciones'}), 500


@app.route('/api/test/result', methods=['GET'])
@token_required
def get_user_test_result(current_user_id):
    """
    Obtiene el último resultado del test del usuario
    GET /api/test/result
    """
    try:
        service = TestPreferenceService()
        result = service.get_user_result(current_user_id)
        
        if not result:
            return jsonify({
                'success': True,
                'has_result': False
            }), 200
        
        return jsonify({
            'success': True,
            'has_result': True,
            'result': result
        }), 200
        
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({'error': 'Error obteniendo resultado'}), 500


@app.route('/api/test/history', methods=['GET'])
@token_required
def get_user_test_history(current_user_id):
    """
    Obtiene historial completo de tests del usuario
    GET /api/test/history
    """
    try:
        service = TestPreferenceService()
        history = service.get_test_history(current_user_id)
        
        return jsonify({
            'success': True,
            'history': history,
            'total': len(history)
        }), 200
        
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({'error': 'Error obteniendo historial'}), 500


@app.route('/api/test/retake', methods=['POST'])
@token_required
def retake_test_preference(current_user_id):
    """
    Permite retomar el test (no borra resultados anteriores, solo confirma)
    POST /api/test/retake
    """
    try:
        return jsonify({
            'success': True,
            'message': 'Puedes retomar el test cuando desees. Tu historial se mantendrá.'
        }), 200
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({'error': 'Error'}), 500


# ==========================================
# 📊 ESTADÍSTICAS ADMIN (OPCIONAL)
# ==========================================

@app.route('/api/admin/test/stats', methods=['GET'])
@token_required
def get_test_stats_admin(current_user_id):
    """
    Estadísticas globales del test (para admin/analytics)
    GET /api/admin/test/stats
    """
    try:
        session = get_session()
        try:
            from models.test_preference import UserTestResult
            
            results = session.query(UserTestResult).all()
            
            distribution = {
                'RED_TEAM': 0,
                'BLUE_TEAM': 0,
                'PURPLE_TEAM': 0
            }
            
            total_time = 0
            count = len(results)
            
            for r in results:
                if r.recommended_role in distribution:
                    distribution[r.recommended_role] += 1
                if r.time_taken_seconds:
                    total_time += r.time_taken_seconds
            
            avg_time = round(total_time / count) if count > 0 else 0
            
            return jsonify({
                'success': True,
                'stats': {
                    'total_completed': count,
                    'distribution': distribution,
                    'avg_time_seconds': avg_time,
                    'avg_time_minutes': round(avg_time / 60, 1) if avg_time > 0 else 0
                }
            }), 200
            
        finally:
            session.close()
            
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({'error': 'Error obteniendo estadísticas'}), 500
        
# ==========================================
# 📧 VERIFICACIÓN DE EMAIL (NUEVOS ENDPOINTS)
# ==========================================

@app.route('/api/auth/verify-email', methods=['POST'])
def verify_email_route():
    """Verifica el código de email enviado al usuario."""
    try:
        data = request.get_json()
        email = data.get('email')
        code = data.get('code')
        
        if not email or not code:
            return jsonify({"error": "Email y código son requeridos"}), 400
        
        auth_service = AuthService()
        result = auth_service.verify_email(email, code)
        
        return jsonify(result), 200
        
    except ValueError as ve:
        return jsonify({"error": str(ve)}), 400
    except Exception as e:
        sentry_sdk.capture_exception(e)
        print(f"❌ Error en verify-email: {e}")
        return jsonify({"error": "Error interno"}), 500

@app.route('/api/auth/resend-code', methods=['POST'])
def resend_code_route():
    """Reenvía el código de verificación."""
    try:
        data = request.get_json()
        email = data.get('email')
        
        if not email:
            return jsonify({"error": "Email es requerido"}), 400
        
        auth_service = AuthService()
        result = auth_service.resend_verification_code(email)
        
        return jsonify(result), 200
        
    except ValueError as ve:
        return jsonify({"error": str(ve)}), 400
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({"error": "Error interno"}), 500

# ==========================================
# MODIFICAR EL ENDPOINT DE REGISTER EXISTENTE
# ==========================================

@app.route('/api/auth/register', methods=['POST'])
def register():
    try:
        data = request.get_json()
        
        if not data or not data.get('email') or not data.get('password'):
            return jsonify({"error": "Email y password son requeridos"}), 400
        
        if not data.get('name'):
            return jsonify({"error": "El nombre es requerido"}), 400
        
        auth_service = AuthService()
        result = auth_service.register(data)
        
        # ✅ NUEVA RESPUESTA: Usuario creado pero debe verificar
        return jsonify(result), 201
        
    except ValueError as ve:
        return jsonify({"error": str(ve)}), 400
    except Exception as e:
        sentry_sdk.capture_exception(e)
        print(f"❌ Error en register: {e}")
        return jsonify({"error": "Error interno"}), 500

# ==========================================
# 🚀 INICIO
# ==========================================
@app.route('/')
def home():
    return jsonify({
        "message": "🚀 CyberLearn API - Servidor de Producción",
        "status": "activo", 
        "version": "3.2.1 - XP Fixed",  # ✅ ACTUALIZADO
        "features": "Auth, CMS, Progress, Badges, Glossary, Assessments"
    })

if __name__ == '__main__':
    print("🚀 INICIANDO CYBERLEARN BACKEND - SERVIDOR")
    try:
        create_all()
        print("✅ Tablas de base de datos verificadas")
    except Exception as e:
        sentry_sdk.capture_exception(e)
        print(f"⚠️ Error creando tablas: {e}")
    
    app.run(host='0.0.0.0', port=8000, debug=False)