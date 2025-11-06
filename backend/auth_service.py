import hashlib
import json
import os
from datetime import datetime, timedelta
import jwt  # ← NUEVO: Necesitas instalar PyJWT

class AuthService:
    # ✅ CONFIGURACIÓN JWT
    SECRET_KEY = "tu_clave_secreta_muy_segura_123456"  # ⚠️ CAMBIAR EN PRODUCCIÓN
    
    def __init__(self):
        self.data_file = 'data/users.json'
        self._ensure_data_file()
        
        # Verificar si tenemos PostgreSQL disponible
        try:
            from models.user import User
            from database.db import db
            self.use_postgres = True
            self.User = User
            self.db = db
            print("🔹 AuthService usando PostgreSQL")
        except ImportError:
            self.use_postgres = False
            print("🔹 AuthService usando JSON")
    
    def _ensure_data_file(self):
        """Solo para modo JSON"""
        if not os.path.exists('data'):
            os.makedirs('data')
        if not os.path.exists(self.data_file):
            with open(self.data_file, 'w') as f:
                json.dump({}, f)
    
    def _load_users(self):
        """Solo para modo JSON"""
        try:
            with open(self.data_file, 'r') as f:
                return json.load(f)
        except:
            return {}
    
    def _save_users(self, users):
        """Solo para modo JSON"""
        with open(self.data_file, 'w') as f:
            json.dump(users, f, indent=2)
    
    def is_strong_password(self, password):
        return (len(password) >= 8 and 
                any(c.isupper() for c in password) and 
                any(c.isdigit() for c in password))
    
    def _hash_password(self, password):
        return hashlib.sha256(password.encode()).hexdigest()
    
    # ========================================
    # ✅ NUEVAS FUNCIONES JWT
    # ========================================
    
    def _generate_token(self, email):
        """
        Genera un token JWT válido con expiración.
        
        Args:
            email (str): Email del usuario
            
        Returns:
            str: Token JWT firmado
        """
        payload = {
            'email': email,
            'exp': datetime.utcnow() + timedelta(hours=24),  # Expira en 24h
            'iat': datetime.utcnow()  # Fecha de emisión
        }
        token = jwt.encode(payload, self.SECRET_KEY, algorithm='HS256')
        print(f"🔑 Token generado para {email}")
        return token
    
    def verify_token(self, token):
        """
        Verifica y decodifica un token JWT.
        
        Args:
            token (str): Token JWT a verificar
            
        Returns:
            dict: {'email': email} si es válido, None si no lo es
        """
        try:
            # Decodificar y verificar el token
            payload = jwt.decode(token, self.SECRET_KEY, algorithms=['HS256'])
            print(f"✅ Token válido para: {payload['email']}")
            return {'email': payload['email']}
        
        except jwt.ExpiredSignatureError:
            print("❌ Token expirado")
            return None
        
        except jwt.InvalidTokenError as e:
            print(f"❌ Token inválido: {e}")
            return None
        
        except Exception as e:
            print(f"❌ Error verificando token: {e}")
            return None
    
    # ========================================
    # ✅ REGISTRO (Actualizado para usar JWT)
    # ========================================
    
    def register(self, email, password, name):
        if not self.is_strong_password(password):
            return {
                'success': False, 
                'message': 'Contraseña débil. Usa mayúsculas, números y mínimo 8 caracteres.'
            }
        
        if self.use_postgres:
            return self._register_postgres(email, password, name)
        else:
            return self._register_json(email, password, name)
    
    def _register_postgres(self, email, password, name):
        """Registro usando PostgreSQL con JWT"""
        try:
            # Verificar si el usuario ya existe
            existing_user = self.User.query.filter_by(email=email).first()
            if existing_user:
                return {
                    'success': False,
                    'message': 'El usuario ya existe'
                }
            
            # Crear nuevo usuario
            new_user = self.User(
                email=email,
                password_hash=self._hash_password(password),
                name=name,
                xp_total=0,
                level=1,
                streak=0
            )
            
            self.db.session.add(new_user)
            self.db.session.commit()
            
            # ✅ GENERAR TOKEN JWT
            token = self._generate_token(email)
            
            return {
                'success': True,
                'message': 'Usuario registrado correctamente',
                'token': token,  # ← Token JWT real
                'user': {
                    'email': email,
                    'name': name
                }
            }
        except Exception as e:
            self.db.session.rollback()
            return {
                'success': False,
                'message': f'Error al registrar: {str(e)}'
            }
    
    def _register_json(self, email, password, name):
        """Registro usando JSON (fallback) con JWT"""
        users = self._load_users()
        
        if email in users:
            return {
                'success': False, 
                'message': 'El usuario ya existe'
            }
        
        users[email] = {
            'email': email,
            'password_hash': self._hash_password(password),
            'name': name,
            'created_at': datetime.now().isoformat(),
            'xp_total': 0,
            'level': 1,
            'streak': 0,
            'last_login': None,
            'badges': [],
            'lessons_completed': 0,
            'courses_completed': 0
        }
        
        self._save_users(users)
        
        # ✅ GENERAR TOKEN JWT
        token = self._generate_token(email)
        
        return {
            'success': True,
            'message': 'Usuario registrado correctamente',
            'token': token,  # ← Token JWT real
            'user': {
                'email': email,
                'name': name
            }
        }
    
    # ========================================
    # ✅ LOGIN (Actualizado para usar JWT)
    # ========================================
    
    def login(self, email, password):
        if self.use_postgres:
            return self._login_postgres(email, password)
        else:
            return self._login_json(email, password)
    
    def _login_postgres(self, email, password):
        """Login usando PostgreSQL con JWT"""
        try:
            user = self.User.query.filter_by(email=email).first()
            
            if not user or user.password_hash != self._hash_password(password):
                return {
                    'success': False,
                    'message': 'Credenciales inválidas'
                }
            
            # Actualizar última conexión
            user.last_login = datetime.utcnow()
            self.db.session.commit()
            
            # ✅ GENERAR TOKEN JWT
            token = self._generate_token(email)
            
            return {
                'success': True,
                'message': 'Autenticación exitosa',
                'token': token,  # ← Token JWT real
                'user': {
                    'email': email,
                    'name': user.name
                }
            }
        except Exception as e:
            return {
                'success': False,
                'message': f'Error en login: {str(e)}'
            }
    
    def _login_json(self, email, password):
        """Login usando JSON (fallback) con JWT"""
        users = self._load_users()
        user = users.get(email)
        
        if not user or user['password_hash'] != self._hash_password(password):
            return {
                'success': False,
                'message': 'Credenciales inválidas'
            }
        
        # Actualizar última conexión
        user['last_login'] = datetime.now().isoformat()
        self._save_users(users)
        
        # ✅ GENERAR TOKEN JWT
        token = self._generate_token(email)
        
        return {
            'success': True,
            'message': 'Autenticación exitosa',
            'token': token,  # ← Token JWT real
            'user': {
                'email': email,
                'name': user['name']
            }
        }