import json
import os
from datetime import datetime

class GamificationService:
    def __init__(self):
        self.data_file = 'data/progress.json'
        self._ensure_data_file()
    
    def _ensure_data_file(self):
        if not os.path.exists('data'):
            os.makedirs('data')
        if not os.path.exists(self.data_file):
            with open(self.data_file, 'w') as f:
                json.dump({}, f)
    
    def _load_users(self):
        try:
            with open(self.data_file, 'r') as f:
                return json.load(f)
        except:
            return {}
    
    def _save_users(self, users):
        with open(self.data_file, 'w') as f:
            json.dump(users, f, indent=2)
    
    def get_user_progress(self, email):
        """Obtener progreso del usuario"""
        users = self._load_users()
        user = users.get(email)
        
        if not user:
            # Usuario nuevo - crear con datos por defecto
            return {
                'email': email,
                'name': email.split('@')[0],
                'xp_total': 0,
                'level': 1,
                'streak': 0,
                'lessons_completed': 0,
                'courses_completed': 0,
                'badges': [],
                'recent_activities': []
            }
        
        return {
            'email': email,
            'name': user.get('name', email.split('@')[0]),
            'xp_total': user.get('xp_total', 0),
            'level': user.get('level', 1),
            'streak': user.get('streak', 0),
            'lessons_completed': user.get('lessons_completed', 0),
            'courses_completed': user.get('courses_completed', 0),
            'badges': user.get('badges', []),
            'recent_activities': user.get('recent_activities', [])
        }
    
    def add_xp(self, email, xp_earned, activity_type='lesson_completed'):
        """Agregar XP al usuario"""
        users = self._load_users()
        
        if email not in users:
            users[email] = {
                'email': email,
                'xp_total': 0,
                'level': 1,
                'streak': 0,
                'lessons_completed': 0,
                'courses_completed': 0,
                'badges': [],
                'recent_activities': []
            }
        
        user = users[email]
        user['xp_total'] += xp_earned
        
        # Calcular nivel (cada 100 XP = 1 nivel)
        user['level'] = (user['xp_total'] // 100) + 1
        
        # Actualizar contador según tipo de actividad
        if activity_type == 'lesson_completed':
            user['lessons_completed'] = user.get('lessons_completed', 0) + 1
        elif activity_type == 'course_completed':
            user['courses_completed'] = user.get('courses_completed', 0) + 1
        
        # Agregar actividad reciente
        if 'recent_activities' not in user:
            user['recent_activities'] = []
        
        user['recent_activities'].append({
            'type': activity_type,
            'xp_earned': xp_earned,
            'timestamp': datetime.now().isoformat()
        })
        
        # Mantener solo las últimas 10 actividades
        user['recent_activities'] = user['recent_activities'][-10:]
        
        users[email] = user
        self._save_users(users)
        
        return user
    
    def get_user_achievements(self, email):
        """Obtener logros del usuario"""
        users = self._load_users()
        user = users.get(email)
        
        if not user:
            return {
                'badges': [],
                'total_badges': 0
            }
        
        badges = user.get('badges', [])
        
        return {
            'badges': badges,
            'total_badges': len(badges)
        }
    
    def award_badge(self, email, badge_id, badge_name):
        """Otorgar insignia al usuario"""
        users = self._load_users()
        
        if email not in users:
            return False
        
        user = users[email]
        
        if 'badges' not in user:
            user['badges'] = []
        
        # Verificar si ya tiene la insignia
        if any(b['id'] == badge_id for b in user['badges']):
            return False
        
        user['badges'].append({
            'id': badge_id,
            'name': badge_name,
            'earned_at': datetime.now().isoformat()
        })
        
        users[email] = user
        self._save_users(users)
        
        return True