# backend/app.py - VERSIÓN 3.3.0 - FINAL (LIMPIO)
from flask import Flask, request, jsonify
from flask_cors import CORS
from database.db import get_session, create_all
from sqlalchemy import text, desc
import jwt
from functools import wraps
import sentry_sdk
from config import Config

# --- MODELOS ---
# (Solo importamos lo necesario para que SQLAlchemy registre las relaciones)
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

# ✅ NUEVOS MODELOS DEL TEST
from models.test_preference import (
    TestQuestion, Certification, Lab, LearningPath,
    RoleSkill, AcademicReference, TestResult, TestAnswer
)

# --- SERVICIOS ---
from services.activity_service import ActivityService
from services.glossary_service import (
    get_all_glossary_terms, search_glossary, get_glossary_stats,
    mark_term_as_learned, get_learned_terms, record_quiz_attempt
)
from services.password_reset_service import create_reset_token, validate_reset_token, reset_password
from services.course_service import CourseService
from services.badge_service import BadgeService
from services.progress_service import mark_lesson_completed, get_user_course_progress
from services.auth_service import AuthService
from services.lesson_service import create_lesson, get_course_lessons_with_status
from services.streak_service import StreakService
from services.glossary_favorite_service import toggle_favorite, get_user_favorites, is_favorite
from services.daily_term_service import get_daily_term_for_user, complete_daily_term
from services.test_preference_service import TestPreferenceService

# -------------------------------------------------------------------
# CONFIGURACIÓN
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

# INICIALIZACIÓN BD
try:
    with app.app_context():
        create_all()
        session = get_session()
        session.execute(text("SELECT 1"))
        print("✅ Base de datos conectada correctamente")
except Exception as e:
    sentry_sdk.capture_exception(e)
    print(f"❌ Error conectando a la base de datos: {e}")

# ---------- DECORADOR DE AUTH ----------
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
        else:
            return jsonify({'error': 'Formato de token inválido'}), 401

        try:
            data = jwt.decode(token, app.config['SECRET_KEY'], algorithms=['HS256'])
            current_user_id = data['user_id']
            sentry_sdk.set_user({"id": current_user_id, "email": data.get('email')})
        except Exception as e:
            return jsonify({'error': 'Token inválido o expirado'}), 401
            
        return f(current_user_id, *args, **kwargs)
    return decorated

@app.route('/api/health', methods=['GET'])
def health_check():
    return jsonify({'status': 'OK', 'version': '3.3.0'})

# ==========================================
# 🔐 AUTH
# ==========================================

@app.route('/api/auth/register', methods=['POST'])
def register():
    try:
        data = request.get_json()
        auth_service = AuthService()
        result = auth_service.register(data)
        return jsonify(result), 201
    except ValueError as ve:
        return jsonify({"error": str(ve)}), 400
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({"error": "Error interno"}), 500

@app.route('/api/auth/login', methods=['POST'])
def login():
    try:
        data = request.get_json()
        auth_service = AuthService()
        result = auth_service.login(data.get('email'), data.get('password'))
        return jsonify(result)
    except ValueError as ve:
        return jsonify({"error": str(ve)}), 401
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({"error": "Error interno"}), 500

@app.route('/api/auth/verify-email', methods=['POST'])
def verify_email_route():
    try:
        data = request.get_json()
        auth_service = AuthService()
        result = auth_service.verify_email(data.get('email'), data.get('code'))
        return jsonify(result), 200
    except ValueError as ve:
        return jsonify({"error": str(ve)}), 400
    except Exception as e:
        return jsonify({"error": "Error interno"}), 500

@app.route('/api/auth/resend-code', methods=['POST'])
def resend_code_route():
    try:
        data = request.get_json()
        auth_service = AuthService()
        result = auth_service.resend_verification_code(data.get('email'))
        return jsonify(result), 200
    except ValueError as ve:
        return jsonify({"error": str(ve)}), 400
    except Exception as e:
        return jsonify({"error": "Error interno"}), 500

@app.route('/api/auth/refresh', methods=['POST'])
def refresh_token_route():
    try:
        data = request.get_json()
        auth_service = AuthService()
        result = auth_service.refresh(data.get('refresh_token'))
        return jsonify(result), 200
    except ValueError as e:
        return jsonify({"error": str(e)}), 401
    except Exception:
        return jsonify({"error": "Error interno"}), 500

@app.route('/api/auth/logout', methods=['POST'])
@token_required
def logout_route(current_user_id):
    try:
        data = request.get_json()
        auth_service = AuthService()
        auth_service.revoke_refresh_token(data.get('refresh_token'))
        return jsonify({"success": True})
    except Exception:
        return jsonify({"error": "Error logout"}), 500

@app.route('/api/auth/forgot-password', methods=['POST'])
def forgot_password_route():
    data = request.get_json()
    try:
        result = create_reset_token(data.get('email'))
        return jsonify(result), 200
    except Exception:
        return jsonify({"error": "Error interno"}), 500

@app.route('/api/auth/validate-reset-token', methods=['POST'])
def validate_reset_token_route():
    data = request.get_json()
    try:
        result = validate_reset_token(data.get('token'), data.get('email'))
        return jsonify(result), 200
    except ValueError as e:
        return jsonify({"error": str(e)}), 400
    except Exception:
        return jsonify({"error": "Error interno"}), 500

@app.route('/api/auth/reset-password', methods=['POST'])
def reset_password_route():
    data = request.get_json()
    try:
        result = reset_password(data.get('token'), data.get('email'), data.get('password'))
        return jsonify(result), 200
    except ValueError as e:
        return jsonify({"error": str(e)}), 400
    except Exception:
        return jsonify({"error": "Error interno"}), 500

# ==========================================
# 👤 USUARIO
# ==========================================

@app.route('/api/user/profile', methods=['GET'])
@token_required
def get_user_profile(current_user_id):
    session = get_session()
    try:
        user = session.query(User).filter_by(id=current_user_id).first()
        if not user: return jsonify({"error": "No encontrado"}), 404
        
        completed_courses = session.query(UserCourseProgress).filter_by(user_id=current_user_id, percentage=100).count()
        badges_count = session.query(UserBadge).filter_by(user_id=current_user_id).count()
        
        return jsonify({
            "success": True,
            "user": {
                "id": user.id, "email": user.email, "name": user.name,
                "total_xp": user.total_xp, "level": (user.total_xp // 100) + 1,
                "completed_courses": completed_courses, "badges_count": badges_count
            }
        })
    finally:
        session.close()

@app.route('/api/user/dashboard', methods=['GET'])
@token_required
def get_user_dashboard(current_user_id):
    session = get_session()
    try:
        user = session.query(User).filter_by(id=current_user_id).first()
        streak_service = StreakService()
        
        # Lógica simplificada para dashboard
        courses = session.query(Course).order_by(Course.id).all()
        course_progress_list = []
        
        for course in courses:
            prog = session.query(UserCourseProgress).filter_by(user_id=current_user_id, course_id=course.id).first()
            course_progress_list.append({
                "course_id": course.id, "title": course.title,
                "percentage": prog.percentage if prog else 0,
                "completed": prog.percentage == 100 if prog else False
            })
            
        has_preference_result = session.query(TestResult).filter_by(user_id=current_user_id).first() is not None
        
        return jsonify({
            "success": True,
            "dashboard": {
                "total_xp": user.total_xp,
                "current_streak": streak_service.get_current_streak(current_user_id),
                "courses_progress": course_progress_list,
                "has_preference_result": has_preference_result
            }
        })
    finally:
        session.close()

@app.route('/api/user/badges', methods=['GET'])
@token_required
def get_user_badges(current_user_id):
    session = get_session()
    try:
        user_badges = session.query(UserBadge, Badge).join(Badge).filter(UserBadge.user_id == current_user_id).all()
        return jsonify({"success": True, "badges": [
            {"id": b.id, "name": b.name, "icon": b.icon, "earned_at": ub.earned_at.isoformat()} 
            for ub, b in user_badges
        ]})
    finally:
        session.close()

# ==========================================
# 📚 CURSOS
# ==========================================

@app.route('/api/courses', methods=['GET'])
def get_courses():
    session = get_session()
    try:
        courses = session.query(Course).all()
        return jsonify([{
            "id": c.id, 
            "title": c.title, 
            "description": c.description, 
            "level": c.level, 
            "image_url": c.image_url,
            "xp_reward": c.xp_reward or 0  
        } for c in courses])
    finally:
        session.close()

@app.route('/api/courses/<int:course_id>/lessons', methods=['GET'])
@token_required 
def get_course_lessons(current_user_id, course_id): # <--- Recibir user_id
    try:
        # Usamos el servicio inteligente que calcula bloqueos reales
        lessons = get_course_lessons_with_status(current_user_id, course_id)
        return jsonify(lessons), 200
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({"error": "Error obteniendo lecciones"}), 500

@app.route('/api/lessons/<lesson_id>', methods=['GET'])
@token_required
def get_lesson_secure(current_user_id, lesson_id):
    session = get_session()
    try:
        lesson = session.query(Lesson).filter_by(id=lesson_id).first()
        if not lesson: 
            return jsonify({"error": "No encontrada"}), 404
        
        # Obtener todas las lecciones del curso ordenadas
        all_lessons = session.query(Lesson).filter_by(
            course_id=lesson.course_id
        ).order_by(Lesson.order_index).all()
        
        # Encontrar el índice de la lección actual
        current_index = next(
            (i for i, l in enumerate(all_lessons) if l.id == lesson_id), 
            -1
        )
        
        # Primera lección (índice 0) siempre desbloqueada
        if current_index > 0:
            prev_lesson = all_lessons[current_index - 1]
            prog = session.query(UserLessonProgress).filter_by(
                user_id=current_user_id, 
                lesson_id=prev_lesson.id, 
                completed=True
            ).first()
            if not prog: 
                return jsonify({
                    "error": "Bloqueada", 
                    "message": f"Completa {prev_lesson.title}"
                }), 403
        
        # Extraer screens correctamente
        screens_data = []
        if lesson.screens:
            if isinstance(lesson.screens, dict) and "screens" in lesson.screens:
                screens_data = lesson.screens.get("screens", [])
            elif isinstance(lesson.screens, list):
                screens_data = lesson.screens
        
        # --- 🆕 CAMBIO AQUÍ: Extraer el tema visual del contenido ---
        lesson_theme = lesson.content.get("theme") if lesson.content else None
        
        return jsonify({
            "success": True, 
            "id": lesson.id, 
            "title": lesson.title,
            "description": lesson.description,
            "type": lesson.type,        # <--- 🆕 IMPORTANTE: Tipo de juego (crisis, tycoon, etc.)
            "theme": lesson_theme,      # <--- 🆕 IMPORTANTE: Colores (neón, oscuro, etc.)
            "screens": screens_data, 
            "total_screens": len(screens_data),
            "duration_minutes": lesson.duration_minutes,
            "xp_reward": lesson.xp_reward or 20
        })
    finally:
        session.close()

@app.route('/api/progress/lesson/<lesson_id>', methods=['POST'])
@token_required
def complete_lesson_route(current_user_id, lesson_id):
    try:
        result = mark_lesson_completed(current_user_id, lesson_id)
        return jsonify({"success": True, "data": result})
    except Exception:
        return jsonify({"error": "Error"}), 500

# ==========================================
# 📖 GLOSARIO
# ==========================================

@app.route('/api/glossary', methods=['GET'])
@token_required
def get_glossary_route(current_user_id):
    return jsonify({"success": True, "terms": get_all_glossary_terms(user_id=current_user_id)})

@app.route('/api/glossary/search', methods=['GET'])
@token_required
def search_glossary_route(current_user_id):
    return jsonify({"success": True, "terms": search_glossary(request.args.get('q', ''), user_id=current_user_id)})

@app.route('/api/glossary/<int:glossary_id>/mark-learned', methods=['POST'])
@token_required
def mark_learned_route(current_user_id, glossary_id):
    data = request.get_json()
    return jsonify(mark_term_as_learned(current_user_id, glossary_id, data.get('is_learned', True)))

@app.route('/api/glossary/learned', methods=['GET'])
@token_required
def get_learned_terms_route(current_user_id):
    return jsonify({"success": True, "terms": get_learned_terms(current_user_id)})

@app.route('/api/glossary/stats', methods=['GET'])
@token_required
def glossary_stats_route(current_user_id):
    return jsonify({"success": True, "stats": get_glossary_stats(user_id=current_user_id)})

@app.route('/api/glossary/<int:glossary_id>/quiz-attempt', methods=['POST'])
@token_required
def quiz_attempt_route(current_user_id, glossary_id):
    data = request.get_json()
    return jsonify(record_quiz_attempt(current_user_id, glossary_id, data.get('is_correct', False)))

@app.route('/api/daily-term', methods=['GET'])
@token_required
def get_daily_term_route(current_user_id):
    return jsonify(get_daily_term_for_user(current_user_id))

@app.route('/api/daily-term/complete', methods=['POST'])
@token_required
def complete_daily_term_route(current_user_id):
    data = request.get_json()
    return jsonify(complete_daily_term(current_user_id, data.get('term_id')))

# ==========================================
# 🎯 TEST DE PREFERENCIAS (NUEVO)
# ==========================================

@app.route('/api/test/questions', methods=['GET'])
@token_required
def get_test_questions(current_user_id):
    try:
        service = TestPreferenceService()
        questions = service.get_questions()
        return jsonify({'success': True, 'questions': questions, 'total': len(questions)})
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({'error': 'Error'}), 500

@app.route('/api/test/submit', methods=['POST'])
@token_required
def submit_test_preference(current_user_id):
    try:
        data = request.get_json()
        service = TestPreferenceService()
        result = service.submit_test(current_user_id, data.get('answers'), data.get('time_taken'))
        return jsonify({'success': True, 'result': result})
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({'error': 'Error'}), 500

@app.route('/api/test/recommendations/<role>', methods=['GET'])
@token_required
def get_test_recommendations(current_user_id, role):
    try:
        service = TestPreferenceService()
        return jsonify({'success': True, 'recommendations': service.get_recommendations(role)})
    except Exception:
        return jsonify({'error': 'Error'}), 500

@app.route('/api/test/result', methods=['GET'])
@token_required
def get_user_test_result(current_user_id):
    try:
        service = TestPreferenceService()
        result = service.get_user_result(current_user_id)
        return jsonify({'success': True, 'has_result': result is not None, 'result': result})
    except Exception:
        return jsonify({'error': 'Error'}), 500

@app.route('/api/test/history', methods=['GET'])
@token_required
def get_user_test_history(current_user_id):
    try:
        service = TestPreferenceService()
        history = service.get_test_history(current_user_id)
        return jsonify({'success': True, 'history': history})
    except Exception:
        return jsonify({'error': 'Error'}), 500

@app.route('/api/test/retake', methods=['POST'])
@token_required
def retake_test_preference(current_user_id):
    return jsonify({'success': True, 'message': 'OK'})

# ==========================================
# INICIO
# ==========================================
@app.route('/')
def home():
    return jsonify({"status": "activo", "version": "3.3.0"})

if __name__ == '__main__':
    try:
        create_all()
    except Exception as e:
        print(f"⚠️ Error DB: {e}")
    app.run(host='0.0.0.0', port=8000, debug=False)