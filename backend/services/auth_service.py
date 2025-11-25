from database.db import get_session
from models.user import User
from models.refresh_token import RefreshToken
import bcrypt
import jwt
from datetime import datetime, timedelta, timezone

class AuthService:
    def __init__(self):
        self.secret_key = 'cyberlearn_super_secret_key_2024_change_in_production'
        self.access_expires = timedelta(minutes=30)      # Token corto (Acceso)
        self.refresh_expires = timedelta(days=30)        # Refresh largo (Sesión)

    def _create_token(self, user_id: int, expires_delta: timedelta, is_refresh=False):
        # Usamos timezone.utc para evitar advertencias de fecha
        expire = datetime.now(timezone.utc) + expires_delta
        payload = {
            "user_id": user_id,
            "exp": expire,
            "iat": datetime.now(timezone.utc),
        }
        if is_refresh:
            payload["type"] = "refresh"
        
        return jwt.encode(payload, self.secret_key, algorithm="HS256"), expire

    def register(self, user_data):
        session = get_session()
        try:
            existing = session.query(User).filter_by(email=user_data['email']).first()
            if existing:
                raise ValueError("El usuario ya existe")

            password_hash = bcrypt.hashpw(user_data['password'].encode('utf-8'), bcrypt.gensalt())

            user = User(
                email=user_data['email'],
                password_hash=password_hash.decode('utf-8'),
                name=user_data.get('name', '')
            )
            session.add(user)
            session.commit()
            session.refresh(user)

            # Generar ambos tokens
            access_token, _ = self._create_token(user.id, self.access_expires)
            refresh_token, refresh_expire = self._create_token(user.id, self.refresh_expires, is_refresh=True)

            # Guardar refresh token en DB
            refresh_expire_naive = refresh_expire.replace(tzinfo=None)

            rt = RefreshToken(
                user_id=user.id,
                token=refresh_token,
                expires_at=refresh_expire_naive
            )
            session.add(rt)
            session.commit()

            # ✅ CORRECCIÓN AQUÍ: Estructura anidada 'user'
            return {
                "access_token": access_token,
                "refresh_token": refresh_token,
                "user": {
                    "id": user.id, 
                    "email": user.email, 
                    "name": user.name
                }
            }
        finally:
            session.close()

    def login(self, email, password):
        session = get_session()
        try:
            user = session.query(User).filter_by(email=email).first()
            if not user or not bcrypt.checkpw(password.encode('utf-8'), user.password_hash.encode('utf-8')):
                raise ValueError("Credenciales inválidas")

            access_token, _ = self._create_token(user.id, self.access_expires)
            refresh_token, refresh_expire = self._create_token(user.id, self.refresh_expires, is_refresh=True)

            refresh_expire_naive = refresh_expire.replace(tzinfo=None)

            rt = RefreshToken(
                user_id=user.id,
                token=refresh_token,
                expires_at=refresh_expire_naive
            )
            session.add(rt)
            session.commit()

            return {
                "access_token": access_token,
                "refresh_token": refresh_token,
                "user": {"id": user.id, "email": user.email, "name": user.name}
            }
        finally:
            session.close()

    def refresh(self, refresh_token_str: str):
        session = get_session()
        try:
            # 1. Buscar token en DB
            rt = session.query(RefreshToken).filter_by(token=refresh_token_str).first()
            
            if not rt:
                raise ValueError("Token no encontrado")
            if rt.is_revoked():
                raise ValueError("Token revocado (posible robo de sesión)")
            if rt.is_expired():
                raise ValueError("Token expirado")

            # 2. Verificar firma criptográfica
            try:
                payload = jwt.decode(refresh_token_str, self.secret_key, algorithms=["HS256"])
            except:
                rt.revoked = True
                session.commit()
                raise ValueError("Token corrupto o inválido")

            # 3. Rotación de tokens (Seguridad Máxima)
            rt.revoked = True
            
            new_access, _ = self._create_token(payload["user_id"], self.access_expires)
            new_refresh, new_expire = self._create_token(payload["user_id"], self.refresh_expires, True)
            
            new_expire_naive = new_expire.replace(tzinfo=None)

            new_rt = RefreshToken(
                user_id=payload["user_id"], 
                token=new_refresh, 
                expires_at=new_expire_naive
            )
            
            session.add(new_rt)
            session.commit()

            return {
                "access_token": new_access,
                "refresh_token": new_refresh
            }
        finally:
            session.close()

    def revoke_refresh_token(self, token_str: str):
        session = get_session()
        try:
            rt = session.query(RefreshToken).filter_by(token=token_str).first()
            if rt:
                rt.revoked = True
                session.commit()
        finally:
            session.close()