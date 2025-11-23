# backend/app.py - VERSIÓN COMPLETA CON MODIFICACIONES PARA PYDANTIC
from flask import Flask, request, jsonify
from flask_cors import CORS
from database.db import db, Session
from models.user import User, UserBadge
from models.course import Course
from models.lesson import Lesson
from models.activity import Activity
from models.badge import Badge
from services.activity_service import ActivityService
from services.course_service import CourseService
from services.badge_service import BadgeService
from services.lesson_service import create_lesson  # ✅ Import para crear lección con validación
import jwt
import datetime
from functools import wraps
from sqlalchemy import text

# -------------------------------------------------------------------
# 🟢 SENTRY CONFIG (YA INTEGRADO)
# -------------------------------------------------------------------
import sentry_sdk

sentry_sdk.init(
    dsn="https://242076c45c627b58d1b4254f28c0606a@o4510415052931072.ingest.us.sentry.io/4510415138586624",
    send_default_pii=True,
    traces_sample_rate=1.0,
    _experiments={"profiles_sample_rate": 1.0},
)
# -------------------------------------------------------------------

app = Flask(__name__)
CORS(app)
app.config['SECRET_KEY'] = 'cyberlearn_super_secret_key_2024_change_in_production'

# INICIALIZACIÓN DE BASE DE DATOS
try:
    with app.app_context():
        db.session.execute(text("SELECT 1"))
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
            data = jwt.decode(token, app.config['SECRET_KEY'], algorithms=['HS256'])
            current_user_id = data['user_id']
            
            sentry_sdk.set_user({"id": current_user_id, "email": data.get('email')})
            
        except jwt.ExpiredSignatureError:
            return jsonify({'error': 'Token expirado'}), 401
        except jwt.InvalidTokenError:
            return jsonify({'error': 'Token inválido'}), 401
        return f(current_user_id, *args, **kwargs)
    return decorated

# ---------- HEALTH & DEBUG ----------
@app.route('/api/health', methods=['GET'])
def health_check():
    return jsonify({
        'status': 'OK', 
        'message': 'Servidor funcionando con Sentry y Pydantic',
        'database': 'Conectada',
        'environment': 'Producción - Servidor'
    })

# ---------- AUTH ----------
@app.route('/api/auth/register', methods=['POST'])
def register():
    try:
        data = request.get_json()
        print(f"📨 Datos recibidos: {data}")
        
        if not data or not data.get('email') or not data.get('password'):
            return jsonify({"error": "Email y password son requeridos"}), 400
        
        session = db.get_session()
        try:
            existing = session.query(User).filter_by(email=data['email']).first()
            if existing:
                return jsonify({"error": "El usuario ya existe"}), 400
            
            new_user = User(email=data['email'], name=data.get('name', ''), password_hash=data['password'])
            session.add(new_user)
            session.commit()
            
            print(f"✅ Usuario creado: ID={new_user.id}, Email={new_user.email}")
            
            token = jwt.encode({
                'user_id': new_user.id, 
                'email': new_user.email, 
                'exp': datetime.datetime.utcnow() + datetime.timedelta(days=30)
            }, app.config['SECRET_KEY'], algorithm='HS256')
            
            print(f"🔑 Token generado: {token}")
            
            response_data = {
                "success": True, 
                "message": "Usuario registrado", 
                "token": token,
                "user": {
                    "id": new_user.id, 
                    "email": new_user.email, 
                    "name": new_user.name
                }
            }
            
            return jsonify(response_data), 201
            
        finally:
            session.close()
            
    except Exception as e:
        sentry_sdk.capture_exception(e) 
        print(f"❌ Error en registro: {e}")
        import traceback
        traceback.print_exc()
        return jsonify({"error": "Error interno"}), 500

@app.route('/api/auth/login', methods=['POST'])
def login():
    try:
        data = request.get_json()
        if not data or not data.get('email') or not data.get('password'):
            return jsonify({"error": "Email y password son requeridos"}), 400
        
        session = db.get_session()
        try:
            user = session.query(User).filter_by(email=data['email']).first()
            if not user or user.password_hash != data['password']:
                return jsonify({"error": "Credenciales inválidas"}), 401
            
            token = jwt.encode({
                'user_id': user.id, 
                'email': user.email, 
                'exp': datetime.datetime.utcnow() + datetime.timedelta(days=30)
            }, app.config['SECRET_KEY'], algorithm='HS256')
            
            return jsonify({
                "success": True, 
                "token": token, 
                "user": {
                    "id": user.id, 
                    "email": user.email, 
                    "name": user.name
                }
            })
        finally:
            session.close()
            
    except Exception as e:
        sentry_sdk.capture_exception(e)
        print(f"❌ Error en login: {e}")
        return jsonify({"error": "Error interno"}), 500

# ---------- CURSOS ----------
@app.route('/api/courses', methods=['GET'])
def get_courses():
    try:
        print("🔍 Obteniendo cursos...")
        session = db.get_session()
        try:
            courses = session.query(Course).all()
            print(f"✅ Encontrados {len(courses)} cursos")
            
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
        print(f"❌ Error en /api/courses: {e}")
        return jsonify({"error": "Error interno al obtener cursos"}), 500

# ---------- LISTAR LECCIONES DE UN CURSO ----------
@app.route('/api/courses/<int:course_id>/lessons', methods=['GET'])
def get_course_lessons(course_id):
    try:
        print(f"🔍 Obteniendo lecciones del curso {course_id}")
        session = db.get_session()
        try:
            lessons = session.query(Lesson).filter_by(course_id=course_id).order_by(Lesson.order_index).all()
            
            print(f"✅ Encontradas {len(lessons)} lecciones")
            
            if not lessons:
                return jsonify([]), 200
            
            result = []
            for l in lessons:
                result.append({
                    'id': l.id,  # ✅ String: "fundamentos_leccion_1"
                    'course_id': l.course_id,
                    'title': l.title,
                    'description': l.description,
                    'type': l.type,
                    'duration_minutes': l.duration_minutes,
                    'xp_reward': getattr(l, 'xp_reward', 10),
                    'order_index': l.order_index,
                    'is_completed': False  # ✅ Agregar lógica de completado después
                })
            return jsonify(result)
        finally:
            session.close()
    except Exception as e:
        sentry_sdk.capture_exception(e)
        print(f"❌ Error en /api/courses/{course_id}/lessons: {e}")
        return jsonify({"error": "Error interno"}), 500

# ---------- OBTENER LECCIÓN ESPECÍFICA ----------
@app.route('/api/lessons/<lesson_id>', methods=['GET'])
def get_lesson_public(lesson_id):
    try:
        print(f"🔍 Buscando lección: {lesson_id}")
        session = db.get_session()
        try:
            lesson = session.query(Lesson).filter_by(id=lesson_id).first()
            
            if not lesson:
                return jsonify({
                    "error": "Lección no encontrada",
                    "searched_id": lesson_id
                }), 404
            
            print(f"✅ Lección encontrada: {lesson.title}")
            
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
                "order_index": lesson.order_index,
                "created_at": lesson.created_at.isoformat() if lesson.created_at else None
            })
        finally:
            session.close()
    except Exception as e:
        sentry_sdk.capture_exception(e)
        print(f"❌ Error en /api/lessons/{lesson_id}: {e}")
        return jsonify({"error": "Error interno"}), 500

# ---------- OBTENER LECCIÓN DE CURSO ESPECÍFICO ----------
@app.route('/api/courses/<int:course_id>/lessons/<lesson_id>', methods=['GET'])
def get_course_lesson_detail(course_id, lesson_id):
    try:
        print(f"🔍 Buscando lección {lesson_id} del curso {course_id}")
        session = db.get_session()
        try:
            lesson = session.query(Lesson).filter_by(
                course_id=course_id, 
                id=lesson_id
            ).first()
            
            if not lesson:
                return jsonify({"error": "Lección no encontrada"}), 404
            
            return jsonify({
                'id': lesson.id,
                'course_id': lesson.course_id,
                'title': lesson.title,
                'description': lesson.description,
                'content': lesson.content,
                'type': lesson.type,
                'screens': lesson.screens,
                'total_screens': getattr(lesson, 'total_screens', 0),
                'duration_minutes': lesson.duration_minutes,
                'xp_reward': getattr(lesson, 'xp_reward', 10),
                'order_index': lesson.order_index,
                'created_at': lesson.created_at.isoformat() if lesson.created_at else None
            })
        finally:
            session.close()
    except Exception as e:
        sentry_sdk.capture_exception(e)
        print(f"❌ Error en /api/courses/{course_id}/lessons/{lesson_id}: {e}")
        return jsonify({"error": "Error interno"}), 500

# ---------- USUARIO ----------
@app.route('/api/user/profile', methods=['GET'])
@token_required
def get_user_profile(current_user_id):
    try:
        session = db.get_session()
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
        print(f"❌ Error en /api/user/profile: {e}")
        return jsonify({"error": "Error interno"}), 500

# ---------- PROGRESO ----------
@app.route('/api/lessons/<lesson_id>/progress', methods=['POST'])
@token_required
def update_lesson_progress(current_user_id, lesson_id):
    try:
        data = request.get_json()
        completed = data.get('completed', False)
        
        print(f"📊 Actualizando progreso - Usuario: {current_user_id}, Lección: {lesson_id}, Completado: {completed}")
        
        session = db.get_session()
        try:
            activity_service = ActivityService()
            if completed:
                activity_service.create_activity(
                    user_id=current_user_id,
                    activity_type='lesson_completed',
                    points=10,
                    lesson_id=lesson_id,
                    description=f"Lección {lesson_id} completada"
                )

            return jsonify({
                "success": True, 
                "lesson_id": lesson_id, 
                "completed": completed, 
                "points_earned": 10 if completed else 0
            })
        finally:
            session.close()
            
    except Exception as e:
        sentry_sdk.capture_exception(e)
        print(f"❌ Error update progress: {e}")
        import traceback
        traceback.print_exc()
        return jsonify({"error": "Error interno"}), 500

# ---------- MEDALLAS ----------
@app.route('/api/badges/available', methods=['GET'])
def get_available_badges():
    try:
        session = db.get_session()
        try:
            badges = session.query(Badge).all()
            return jsonify({"success": True, "badges": [
                {"id": b.id, "name": b.name, "description": b.description, "icon": b.icon,
                 "xp_required": b.xp_required} for b in badges]})
        finally:
            session.close()
    except Exception as e:
        sentry_sdk.capture_exception(e)
        print(f"❌ Error en /api/badges/available: {e}")
        return jsonify({"error": "Error interno"}), 500

@app.route('/api/user/badges', methods=['GET'])
@token_required
def get_user_badges(current_user_id):
    try:
        print(f"🔍 Obteniendo medallas para usuario {current_user_id}")
        session = db.get_session()
        try:
            user_badges = session.query(UserBadge, Badge).join(
                Badge, UserBadge.badge_id == Badge.id
            ).filter(UserBadge.user_id == current_user_id).all()
            print(f"✅ Encontradas {len(user_badges)} medallas")

            return jsonify({"success": True, "badges": [
                {
                    "id": badge.id,
                    "name": badge.name,
                    "description": badge.description,
                    "icon": badge.icon,
                    "xp_required": badge.xp_required,
                    "earned_at": ub.earned_at.isoformat(),
                    "earned_value": ub.earned_value if hasattr(ub, 'earned_value') else 1
                } for ub, badge in user_badges
            ]})
        finally:
            session.close()
    except Exception as e:
        sentry_sdk.capture_exception(e)
        print(f"❌ Error en /api/user/badges: {e}")
        return jsonify({"error": "Error interno al obtener medallas"}), 500
    
# ---------- DASHBOARD & RACHA ----------
@app.route('/api/user/dashboard', methods=['GET'])
@token_required
def get_user_dashboard(current_user_id):
    try:
        from services.streak_service import StreakService
        
        session = db.get_session()
        try:
            act = ActivityService()
            total_xp = act.get_total_xp(current_user_id)
            
            streak_service = StreakService()
            current_streak = streak_service.get_current_streak(current_user_id)
            streak_bonus = streak_service.get_streak_bonus(current_streak)
            
            user_badges = session.query(UserBadge).filter_by(user_id=current_user_id).count()
            
            courses_prog = []
            for course in session.query(Course).all():
                total = session.query(Lesson).filter_by(course_id=course.id).count()
                
                completed = session.query(Activity).filter(
                    Activity.user_id == current_user_id,
                    Activity.activity_type == 'lesson_completed',
                    Activity.lesson_id.isnot(None)
                ).count()
                
                if total:
                    courses_prog.append({
                        'course_id': course.id, 
                        'course_title': course.title,
                        'completed_lessons': completed, 
                        'total_lessons': total,
                        'progress_percent': round(completed / total * 100, 1)
                    })
            
            next_badge = session.query(Badge).filter(
                Badge.id == str(user_badges + 1)
            ).first() if user_badges < 6 else None
            
            return jsonify({"success": True, "dashboard": {
                "total_xp": total_xp, 
                "current_streak": current_streak,
                "streak_bonus": streak_bonus, 
                "badges_count": user_badges,
                "courses_progress": courses_prog,
                "next_badge": {
                    "name": next_badge.name, 
                    "description": next_badge.description,
                    "icon": next_badge.icon,
                    "condition": f"Necesitas {next_badge.xp_required} XP"
                } if next_badge else None
            }})
        finally:
            session.close()
            
    except Exception as e:
        sentry_sdk.capture_exception(e)
        print(f"Dashboard error: {e}")
        import traceback
        traceback.print_exc()
        return jsonify({"error": "Error interno"}), 500

@app.route('/api/user/streak', methods=['GET'])
@token_required
def get_user_streak(current_user_id):
    try:
        from services.streak_service import StreakService
        s = StreakService()
        streak_days = s.get_current_streak(current_user_id)
        bonus = s.get_streak_bonus(streak_days)
        return jsonify({"success": True, "streak": {
            "current_days": streak_days, "bonus_xp": bonus,
            "next_milestone": 7 if streak_days < 7 else 30,
            "progress_to_next": streak_days % 7 if streak_days < 7 else streak_days % 30
        }})
    except Exception as e:
        sentry_sdk.capture_exception(e)
        return jsonify({"error": "Error interno"}), 500

# ---------- RUTA RAIZ ----------
@app.route('/')
def home():
    return jsonify({
        "message": "🚀 CyberLearn API - Servidor de Producción",
        "status": "activo", 
        "version": "1.0.0",
        "database": "PostgreSQL en servidor",
        "monitoring": "Sentry Enabled ✅"
    })

# ---------- INICIO ----------
if __name__ == '__main__':
    print("🚀 INICIANDO CYBERLEARN BACKEND - SERVIDOR")
    print("=" * 50)
    print("🔗 Base de datos: PostgreSQL en servidor")
    print("🌐 Host: 0.0.0.0:8000")
    print("⚡ Modo: Producción")
    print("🛡️ Sentry: Activado")
    print("=" * 50)
    
    try:
        db.create_all()
        print("✅ Tablas de base de datos verificadas")
    except Exception as e:
        sentry_sdk.capture_exception(e)
        print(f"⚠️  Error creando tablas: {e}")
    
    app.run(host='0.0.0.0', port=8000, debug=False)