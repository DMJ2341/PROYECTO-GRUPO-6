# backend/auth_service.py
from database.db import Session
from models.user import User
import bcrypt
import jwt
from datetime import datetime, timedelta
import os

class AuthService:
    def __init__(self):
        self.db = Session()
        self.secret_key = os.getenv('SECRET_KEY', '3f8b2a1e5d9c4f6a8b1e2d3c4f5a6b7c8d9e0f1a2b3c4d5e6f7a8b9c0d1e2f3')
    
    def register(self, user_data):
        """Registrar nuevo usuario"""
        try:
            # Verificar si el usuario existe
            existing_user = self.db.query(User).filter_by(email=user_data['email']).first()
            if existing_user:
                raise ValueError("El usuario ya existe")
            
            # Hash de contraseña con bcrypt
            password_hash = bcrypt.hashpw(user_data['password'].encode('utf-8'), bcrypt.gensalt())
            
            # Crear usuario
            user = User(
                email=user_data['email'],
                password_hash=password_hash.decode('utf-8'),
                name=user_data['name']
            )
            
            self.db.add(user)
            self.db.commit()
            self.db.refresh(user)
            
            return {
                "id": user.id,
                "email": user.email,
                "name": user.name,
                "message": "Usuario registrado exitosamente"
            }
        except Exception as e:
            self.db.rollback()
            raise e
    
    def login(self, email, password):
        """Iniciar sesión"""
        user = self.db.query(User).filter_by(email=email).first()
        if not user:
            raise ValueError("Usuario no encontrado")
        
        # Verificar contraseña con bcrypt
        if bcrypt.checkpw(password.encode('utf-8'), user.password_hash.encode('utf-8')):
            # Generar token JWT
            token = jwt.encode({
                'user_id': user.id,
                'email': user.email,
                'exp': datetime.utcnow() + timedelta(hours=24)
            }, self.secret_key, algorithm='HS256')
            
            return {
                "token": token,
                "user": {
                    "id": user.id,
                    "email": user.email,
                    "name": user.name
                }
            }
        else:
            raise ValueError("Contraseña incorrecta")
    
    def verify_token(self, token):
        """Verificar token JWT y retornar datos del usuario"""
        try:
            payload = jwt.decode(token, self.secret_key, algorithms=['HS256'])
            return {
                'user_id': payload['user_id'],
                'email': payload['email']
            }
        except jwt.ExpiredSignatureError:
            print("Token expirado")
            return None
        except jwt.InvalidTokenError:
            print("Token inválido")
            return None
        except Exception as e:
            print(f"Error verificando token: {e}")
            return None
    
    def __del__(self):
        if hasattr(self, 'db'):
            self.db.close()