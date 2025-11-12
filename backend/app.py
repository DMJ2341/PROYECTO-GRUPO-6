# backend/app.py - versión completa, limpia y sin duplicados
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
import jwt
import datetime
import os
from functools import wraps

app = Flask(__name__)
CORS(app)
app.config['SECRET_KEY'] = os.getenv('SECRET_KEY', 'fallback-secret-key')

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
        except jwt.ExpiredSignatureError:
            return jsonify({'error': 'Token expirado'}), 401
        except jwt.InvalidTokenError:
            return jsonify({'error': 'Token inválido'}), 401
        return f(current_user_id, *args, **kwargs)
    return decorated

# ---------- HEALTH ----------
@app.route('/api/health', methods=['GET'])
def health_check():
    return jsonify({'status': 'OK', 'message': 'Servidor funcionando'})

# ---------- AUTH ----------
@app.route('/api/auth/register', methods=['POST'])
def register():
    try:
        data = request.get_json()
        if not data or not data.get('email') or not data.get('password'):
            return jsonify({"error": "Email y password son requeridos"}), 400
        existing = db.session.query(User).filter_by(email=data['email']).first()
        if existing:
            return jsonify({"error": "El usuario ya existe"}), 400
        new_user = User(email=data['email'], name=data.get('name', ''), password_hash=data['password'])
        db.session.add(new_user)
        db.session.commit()
        return jsonify({"success": True, "message": "Usuario registrado", "user_id": new_user.id}), 201
    except Exception as e:
        db.session.rollback()
        return jsonify({"error": "Error interno"}), 500

@app.route('/api/auth/login', methods=['POST'])
def login():
    try:
        data = request.get_json()
        if not data or not data.get('email') or not data.get('password'):
            return jsonify({"error": "Email y password son requeridos"}), 400
        user = db.session.query(User).filter_by(email=data['email']).first()
        if not user or user.password_hash != data['password']:
            return jsonify({"error": "Credenciales inválidas"}), 401
        token = jwt.encode({'user_id': user.id, 'email': user.email, 'exp': datetime.datetime.utcnow() + datetime.timedelta(days=30)},
                           app.config['SECRET_KEY'], algorithm='HS256')
        return jsonify({"success": True, "token": token, "user": {"id": user.id, "email": user.email, "name": user.name}})
    except Exception as e:
        return jsonify({"error": "Error interno"}), 500

# ---------- CURSOS ----------
@app.route('/api/courses', methods=['GET'])
def get_courses():
    try:
        print("🔍 Obteniendo cursos...")
        courses = db.session.query(Course).all()
        print(f"✅ Encontrados {len(courses)} cursos")
        out = []
        for c in courses:
            out.append({
                'id': c.id,
                'title': c.title,
                'description': c.description,
                'category': c.category,
                'difficulty': c.difficulty,
                'duration_hours': c.duration_hours,
                'instructor': c.instructor,
                'price': float(c.price) if c.price else 0.0,
                'rating': c.rating,
                'students_count': c.students_count,
                'language': c.language,
                'image_url': c.image_url or ""
            })
        print("✅ JSON armado")
        return jsonify(out)
    except Exception as e:
        print(f"❌ Error en /api/courses: {e}")
        return jsonify({"error": "Error interno al obtener cursos"}), 500

# ---------- LECCIONES (público) ----------
@app.route('/api/lessons/<int:lesson_id>', methods=['GET'])
def get_lesson_public(lesson_id):
    try:
        lesson = db.session.query(Lesson).filter_by(id=lesson_id).first()
        if not lesson:
            return jsonify({"error": "Lección no encontrada"}), 404
        return jsonify({
            "success": True,
            "lesson": {
                'id': lesson.id, 'course_id': lesson.course_id, 'title': lesson.title,
                'description': lesson.description, 'content': lesson.content, 'type': lesson.type,
                'duration_minutes': lesson.duration_minutes, 'order_index': lesson.order_index,
                'created_at': lesson.created_at.isoformat() if lesson.created_at else None
            }
        })
    except Exception as e:
        return jsonify({"error": "Error interno"}), 500

@app.route('/api/courses/<int:course_id>/lessons/<int:lesson_id>', methods=['GET'])
def get_course_lesson_detail(course_id, lesson_id):
    try:
        lesson = db.session.query(Lesson).filter_by(course_id=course_id, id=lesson_id).first()
        if not lesson:
            return jsonify({"error": "Lección no encontrada"}), 404
        return jsonify({
            'id': lesson.id, 'course_id': lesson.course_id, 'title': lesson.title,
            'description': lesson.description, 'content': lesson.content, 'type': lesson.type,
            'duration_minutes': lesson.duration_minutes, 'order_index': lesson.order_index,
            'created_at': lesson.created_at.isoformat() if lesson.created_at else None
        })
    except Exception as e:
        return jsonify({"error": "Error interno"}), 500

# ---------- USUARIO ----------
@app.route('/api/user/profile', methods=['GET'])
@token_required
def get_user_profile(current_user_id):
    try:
        user = db.session.query(User).filter_by(id=current_user_id).first()
        if not user:
            return jsonify({"error": "Usuario no encontrado"}), 404
        return jsonify({"success": True, "user": {
            "id": user.id, "email": user.email, "name": user.name,
            "created_at": user.created_at.isoformat() if user.created_at else None
        }})
    except Exception as e:
        return jsonify({"error": "Error interno"}), 500

# ---------- PROGRESO ----------
@app.route('/api/lessons/<int:lesson_id>/progress', methods=['POST'])
@token_required
def update_lesson_progress(current_user_id, lesson_id):
    try:
        data = request.get_json()
        completed = data.get('completed', False)
        activity_service = ActivityService()
        if completed:
            activity_service.create_activity(user_id=current_user_id,
                                             activity_type='lesson_completed',
                                             description=f'Lección {lesson_id} completada',
                                             points=10)

            # ⭐ OTORGAR MEDALLAS AUTOMÁTICAS ⭐
            from services.badge_service import BadgeService
            BadgeService.award_lesson_badges(current_user_id, lesson_id)

        return jsonify({"success": True, "lesson_id": lesson_id, "completed": completed, "points_earned": 10 if completed else 0})
    except Exception as e:
        print(f"Error update progress: {e}")
        return jsonify({"error": "Error interno"}), 500

# ---------- MEDALLAS ----------
@app.route('/api/badges/available', methods=['GET'])
def get_available_badges():
    try:
        badges = db.session.query(Badge).all()
        return jsonify({"success": True, "badges": [
            {"id": b.id, "name": b.name, "description": b.description, "icon": b.icon,
             "condition": b.condition, "points_required": b.points_required} for b in badges]})
    except Exception as e:
        return jsonify({"error": "Error interno"}), 500

@app.route('/api/user/badges', methods=['GET'])
@token_required
def get_user_badges(current_user_id):
    try:
        print(f"🔍 Obteniendo medallas para usuario {current_user_id}")
        user_badges = db.session.query(UserBadge, Badge).join(
            Badge, UserBadge.badge_id == Badge.id
        ).filter(UserBadge.user_id == current_user_id).all()
        print(f"✅ Encontradas {len(user_badges)} medallas")

        return jsonify({"success": True, "badges": [
            {
                "id": badge.id,
                "name": badge.name,
                "description": badge.description,
                "icon": badge.icon,
                "condition": badge.condition,
                "points_required": badge.points_required,
                "earned_at": ub.earned_at.isoformat(),
                "earned_value": ub.earned_value if hasattr(ub, 'earned_value') else 1
            } for ub, badge in user_badges
        ]})
    except Exception as e:
        print(f"❌ Error en /api/user/badges: {e}")
        return jsonify({"error": "Error interno al obtener medallas"}), 500
    
# ---------- DASHBOARD & RACHA ----------
@app.route('/api/user/dashboard', methods=['GET'])
@token_required
def get_user_dashboard(current_user_id):
    try:
        from services.streak_service import StreakService
        act = ActivityService()
        total_xp = act.get_total_xp(current_user_id)
        streak = StreakService()
        current_streak = streak.get_current_streak(current_user_id)
        streak_bonus = streak.get_streak_bonus(current_streak)
        user_badges = db.session.query(UserBadge).filter_by(user_id=current_user_id).count()
        courses_prog = []
        for course in db.session.query(Course).all():
            total = db.session.query(Lesson).filter_by(course_id=course.id).count()
            completed = db.session.query(Activity).filter(
                Activity.user_id == current_user_id,
                Activity.type == 'lesson_completed',
                Activity.description.like(f'%curso {course.id}%')).count()
            if total:
                courses_prog.append({
                    'course_id': course.id, 'course_title': course.title,
                    'completed_lessons': completed, 'total_lessons': total,
                    'progress_percent': round(completed / total * 100, 1)
                })
        next_badge = db.session.query(Badge).filter(
            Badge.id == user_badges + 1).first() if user_badges < 6 else None
        return jsonify({"success": True, "dashboard": {
            "total_xp": total_xp, "current_streak": current_streak,
            "streak_bonus": streak_bonus, "badges_count": user_badges,
            "courses_progress": courses_prog,
            "next_badge": {"name": next_badge.name, "description": next_badge.description,
                           "icon": next_badge.icon, "condition": next_badge.description} if next_badge else None
        }})
    except Exception as e:
        print(f"Dashboard error: {e}")
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
        return jsonify({"error": "Error interno"}), 500
    
# ---------- OTORGAR MEDALLA FINAL ----------
def award_final_badge(user_id):
    from models.user import UserBadge
    badge = db.session.query(Badge).filter_by(id=6).first()  # Escudo Ciudadano
    if not badge:
        print("❌ Medalla 6 no encontrada")
        return
    exists = db.session.query(UserBadge).filter_by(user_id=user_id, badge_id=6).first()
    if not exists:
        ub = UserBadge(user_id=user_id, badge_id=6, earned_at=datetime.datetime.utcnow(), earned_value=1)
        db.session.add(ub)
        db.session.commit()
        print("🏅 Medalla 'Escudo Ciudadano' otorgada")
        
# ---------- LISTAR TODAS LAS LECCIONES DE UN CURSO (faltante) ----------
@app.route('/api/courses/<int:course_id>/lessons', methods=['GET'])
def get_course_lessons(course_id):
    try:
        lessons = db.session.query(Lesson).filter_by(course_id=course_id).order_by(Lesson.order_index).all()
        if not lessons:
            return jsonify([]), 200  # Lista vacía, no 404
        return jsonify([{
            'id': l.id,
            'course_id': l.course_id,
            'title': l.title,
            'description': l.description,
            'content': l.content,
            'duration_minutes': l.duration_minutes,
            'order_index': l.order_index,
            'type': l.type,
            'created_at': l.created_at.isoformat() if l.created_at else None
        } for l in lessons])
    except Exception as e:
        print(f"❌ Error en /api/courses/{course_id}/lessons: {e}")
        return jsonify({"error": "Error interno"}), 500

# ---------- INICIO ----------
if __name__ == '__main__':
    db.create_all()
    app.run(host='0.0.0.0', port=8000, debug=True)