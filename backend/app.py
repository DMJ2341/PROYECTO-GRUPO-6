# backend/app.py - VERSIÓN CORREGIDA Y COMPLETA
from flask import Flask, request, jsonify
from flask_cors import CORS
from database.db import get_session, create_all
from sqlalchemy import text, desc
import jwt
import datetime
from functools import wraps
import sentry_sdk
from config import Config  # ✅ Importamos la config centralizada

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
from services.glossary_service import get_all_glossary_terms, search_glossary, get_daily_term, get_glossary_stats
from services.password_reset_service import create_reset_token, validate_reset_token, reset_password
from services.course_service import CourseService
from services.badge_service import BadgeService
from services.progress_service import mark_lesson_completed, get_user_course_progress
from services.auth_service import AuthService
from services.lesson_service import create_lesson
from services.streak_service import StreakService
from services.glossary_favorite_service import toggle_favorite, get_user_favorites, is_favorite
from services.daily_term_service import get_daily_term_for_user
from services.exam_service import ExamService
from services.preference_engine import PreferenceEngine

# -------------------------------------------------------------------
# 🟢 SENTRY CONFIG
# -------------------------------------------------------------------
sentry_sdk.init(
    dsn="https://242076c45c627b58d1b4254f28c0606a@o4510415052931072.ingest.us.sentry.io/4510415138586624",
    send_default_pii=True,
    traces_sample_rate=1.0,
    _experiments={"profiles_sample_rate": 1.0},
)

app = Flask(__name__)
# Cargar configuración desde config.py
app.config.from_object(Config)
CORS(app)

# INICIALIZACIÓN DE BASE DE DATOS
try:
    with app.app_context():
        session = get_session()
        session.execute(text("SELECT 1"))
        print("✅ Base de datos conectada correctamente")
except Exception as e:
    sentry_sdk.capture_exception(e)
    print(f"❌ Error conectando a la base de datos: {e}")
    raise

# ---------- AUTH DECORATOR ----------
def token_required(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        token = request.headers.get('Authorization')
        if not token:
            return jsonify({'error': 'Token es requerido'}), 401
        try:
            if token.startswith('Bearer '):
                token = token[7:]
            # Usamos la clave secreta desde la configuración
            data = jwt.decode(token, app.config['SECRET_KEY'], algorithms=['HS256'])
            current_user_id = data['user_id']
            
            sentry_sdk.set_user({"id": current_user_id, "email": data.get('email')})
            
        except jwt.ExpiredSignatureError:
            return jsonify({'error': 'Token expirado'}), 401
        except jwt.InvalidTokenError:
            return jsonify({'error': 'Token inválido'}), 401
        return f(current_user_id, *args, **kwargs)
    return decorated

# ---------- HEALTH CHECK ----------
@app.route('/api/health', methods=['GET'])
def health_check():
    return jsonify({
        'status': 'OK', 
        'message': 'CyberLearn API Operativa',
        'version': '3.1.0',
        'database': 'Conectada',
        'environment': 'Producción - Servidor'
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

# ==========================================
# 👤 AUTH (LOGIN / REGISTER / REFRESH)
# ==========================================

@app.route('/api/auth/register', methods=['POST'])
def register():
    try:
        data = request.get_json()
        
        if not data or not data.get('email') or not data.get('password'):
            return jsonify({"error": "Email y password son requeridos"}), 400
        
        auth_service = AuthService()
        result = auth_service.register(data)
        
        return jsonify({
            "success": True,
            "message": "Usuario registrado",
            "access_token": result['access_token'],
            "refresh_token": result['refresh_token'],
            "user": result['user']
        }), 201
        
    except ValueError as ve:
        return jsonify({"error": str(ve)}), 400
    except Exception as e:
        sentry_sdk.capture_exception(e)
        print(f"❌ Error en register: {e}")
        return jsonify({"error": "Error interno"}), 500

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
            # 1. Buscar la lección
            lesson = session.query(Lesson).filter_by(id=lesson_id).first()
            if not lesson:
                return jsonify({"error": "Lección no encontrada"}), 404
            
            # 2. Buscar lección anterior (Bloqueo Secuencial)
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
            
            # 3. Retornar contenido
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
            return jsonify({"success": True, "user": {
                "id": user.id, "email": user.email, "name": user.name,
                "created_at": user.created_at.isoformat() if user.created_at else None
            }})
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
        act_service = ActivityService()
        streak_service = StreakService()

        total_xp = act_service.get_total_xp(current_user_id)
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

        session.close()

        return jsonify({
            "success": True,
            "dashboard": {
                "total_xp": total_xp,
                "level": (total_xp // 100) + 1,
                "current_streak": current_streak,
                "streak_bonus": streak_bonus,
                "badges_count": user_badges_count,
                "courses_progress": course_progress_list,
                "next_course": next_course,
                "completed_courses": sum(1 for c in course_progress_list if c["completed"]),
                "total_courses": len(course_progress_list)
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
# 📖 GLOSARIO (TÉRMINO DIARIO Y BÚSQUEDA)
# ==========================================

@app.route('/api/glossary', methods=['GET'])
def get_glossary_route():
    try:
        terms = get_all_glossary_terms()
        return jsonify({"success": True, "terms": terms})
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({"error": "Error cargando glosario"}), 500

@app.route('/api/glossary/search', methods=['GET'])
def search_glossary_route():
    query = request.args.get('q', '')
    if len(query) < 2:
        return jsonify({"success": True, "terms": []})
    try:
        results = search_glossary(query)
        return jsonify({"success": True, "terms": results})
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({"error": "Error en búsqueda"}), 500

@app.route('/api/daily-term', methods=['GET'])
@token_required
def daily_term_v2(current_user_id):
    """Obtiene el término del día con XP si es la primera visita."""
    try:
        result = get_daily_term_for_user(current_user_id)
        if not result:
             return jsonify({"error": "No hay términos disponibles"}), 404
        return jsonify({
            "success": True,
            "daily_term": result["term"],
            "xp_earned": result.get("xp_reward", 0),
            "already_viewed_today": result["already_viewed"]
        })
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({"error": "Error cargando término del día"}), 500

@app.route('/api/glossary/stats', methods=['GET'])
def glossary_stats():
    try:
        stats = get_glossary_stats()
        return jsonify({"success": True, "stats": stats})
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({"error": "Error obteniendo estadísticas"}), 500

@app.route('/api/glossary/favorites', methods=['GET'])
@token_required
def get_favorites(current_user_id):
    try:
        favorites = get_user_favorites(current_user_id)
        return jsonify({"success": True, "favorites": favorites})
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({"error": "Error cargando favoritos"}), 500

@app.route('/api/glossary/<int:glossary_id>/favorite', methods=['POST'])
@token_required
def toggle_glossary_favorite(current_user_id, glossary_id):
    try:
        result = toggle_favorite(current_user_id, glossary_id)
        return jsonify({
            "success": True,
            "glossary_id": glossary_id,
            "is_favorite": result["is_favorite"],
            "action": result["action"]
        })
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({"error": "Error al marcar favorito"}), 500

@app.route('/api/glossary/term/<int:glossary_id>', methods=['GET'])
@token_required
def get_term_with_favorite(current_user_id, glossary_id):
    session = get_session()
    try:
        term = session.query(Glossary).get(glossary_id)
        if not term:
            return jsonify({"error": "Término no encontrado"}), 404

        is_fav = is_favorite(current_user_id, glossary_id)

        return jsonify({
            "success": True,
            "term": term.to_dict(),
            "is_favorite": is_fav
        })
    finally:
        session.close()

# ==========================================
# 🎓 EXÁMENES Y APTITUD (EVALUACIONES)
# ==========================================

# --- EXAMEN FINAL ---
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

# --- TEST DE PREFERENCIAS (VOCACIONAL) ---
@app.route('/api/preference-test/questions', methods=['GET'])
@token_required
def get_pref_questions(current_user_id):
    session = get_session()
    try:
        questions = session.query(PreferenceQuestion).filter_by(is_active=True).order_by(PreferenceQuestion.question_number).all()
        if not questions:
            return jsonify({"error": "No hay preguntas disponibles."}), 500
        
        output = []
        for q in questions:
            output.append({
                "id": q.id,
                "number": q.question_number,
                "section": q.section,
                "text": q.question_text,
                "subtext": q.question_subtext,
                "options": q.options,
                "image_url": q.image_url
            })
        return jsonify({"success": True, "total": len(output), "questions": output})
    finally:
        session.close()

@app.route('/api/preference-test/submit', methods=['POST'])
@token_required
def submit_pref_test(current_user_id):
    data = request.get_json()
    answers = data.get('answers')
    time_taken = data.get('time_taken')
    
    if not answers or not isinstance(answers, dict):
        return jsonify({"error": "Respuestas requeridas (dict)"}), 400
    
    try:
        engine = PreferenceEngine()
        result = engine.calculate_and_save(current_user_id, answers, time_taken)
        return jsonify({"success": True, "result": result})
    except Exception as e:
        print(f"Error submitting test: {e}")
        return jsonify({"error": str(e)}), 500

@app.route('/api/preference-test/result', methods=['GET'])
@token_required
def get_pref_result(current_user_id):
    try:
        engine = PreferenceEngine()
        result = engine.get_user_result(current_user_id)
        if not result:
            return jsonify({"success": True, "has_result": False})
        return jsonify({"success": True, "has_result": True, "result": result})
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/api/preference-test/retake', methods=['POST'])
@token_required
def retake_pref_test(current_user_id):
    session = get_session()
    try:
        result = session.query(UserPreferenceResult).filter_by(user_id=current_user_id).first()
        if result:
            result.retaken = True
            session.commit()
        return jsonify({"success": True, "message": "Listo para retomar"})
    except Exception as e:
        return jsonify({"error": str(e)}), 500
    finally:
        session.close()

@app.route('/api/admin/preference-test/stats', methods=['GET'])
@token_required
def get_pref_test_stats(current_user_id):
    session = get_session()
    try:
        results = session.query(UserPreferenceResult).all()
        distribution = {"Red Team": 0, "Blue Team": 0, "Purple Team": 0}
        total_time = 0
        for r in results:
            if r.assigned_profile in distribution:
                distribution[r.assigned_profile] += 1
            if r.time_taken:
                total_time += r.time_taken
        
        count = len(results)
        avg_time = round(total_time / count) if count > 0 else 0
        
        return jsonify({
            "success": True, 
            "total_completed": count, 
            "distribution": distribution, 
            "avg_time_seconds": avg_time
        })
    finally:
        session.close()

# ==========================================
# 🚀 INICIO
# ==========================================
@app.route('/')
def home():
    return jsonify({
        "message": "🚀 CyberLearn API - Servidor de Producción",
        "status": "activo", 
        "version": "3.1.0",
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