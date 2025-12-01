# backend/services/auth_service.py
from database.db import get_session
from models.user import User
from models.refresh_token import RefreshToken
from models.email_verification import EmailVerificationCode
from services.email_service import EmailService
import bcrypt
import jwt
import re
from datetime import datetime, timedelta, timezone
from config import Config

class AuthService:
    def __init__(self):
        self.secret_key = Config.SECRET_KEY 
        self.access_expires = timedelta(minutes=30)
        self.refresh_expires = timedelta(days=30)
        self.email_service = EmailService()

    def _create_token(self, user_id: int, expires_delta: timedelta, is_refresh=False):
        expire = datetime.now(timezone.utc) + expires_delta
        payload = {
            "user_id": user_id,
            "exp": expire,
            "iat": datetime.now(timezone.utc),
        }
        if is_refresh:
            payload["type"] = "refresh"
        
        return jwt.encode(payload, self.secret_key, algorithm="HS256"), expire

    def _validate_email(self, email: str):
        """Valida formato de email."""
        pattern = r'^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$'
        if not re.match(pattern, email):
            raise ValueError("Formato de email inválido")
        return email.lower()

    def _validate_password(self, password: str):
        """Valida seguridad de la contraseña."""
        if len(password) < Config.PASSWORD_MIN_LENGTH:
            raise ValueError(f"La contraseña debe tener al menos {Config.PASSWORD_MIN_LENGTH} caracteres")
        
        # Verificar contra lista negra
        if password.lower() in Config.WEAK_PASSWORDS:
            raise ValueError("Esta contraseña es muy común. Elige una más segura.")
        
        # Opcional: Verificar que no sea solo números
        if password.isdigit():
            raise ValueError("La contraseña no puede ser solo números")
        
        return password

    def register(self, user_data):
        """Registro de usuario con envío de código de verificación."""
        session = get_session()
        try:
            # 1. Validar datos
            email = self._validate_email(user_data['email'])
            password = self._validate_password(user_data['password'])
            name = user_data.get('name', '').strip()
            
            if not name:
                raise ValueError("El nombre es requerido")
            
            # 2. Verificar si el usuario ya existe
            existing = session.query(User).filter_by(email=email).first()
            if existing:
                raise ValueError("Este correo ya está registrado")

            # 3. Hash de la contraseña
            password_hash = bcrypt.hashpw(password.encode('utf-8'), bcrypt.gensalt())

            # 4. Crear usuario (sin verificar todavía)
            user = User(
                email=email,
                password_hash=password_hash.decode('utf-8'),
                name=name,
                email_verified=False,  # ✅ No verificado inicialmente
                terms_accepted_at=datetime.utcnow()  # ✅ Acepta términos al registrarse
            )
            session.add(user)
            session.commit()
            session.refresh(user)

            # 5. Generar código de verificación
            code = self.email_service.generate_verification_code()
            verification = EmailVerificationCode(
                user_id=user.id,
                code=code
            )
            session.add(verification)
            session.commit()

            # 6. Enviar email con código
            try:
                self.email_service.send_verification_email(email, code, name)
            except Exception as e:
                print(f"⚠️ Error enviando email: {e}")
                # No fallar el registro si el email falla
                # En producción, podrías reenviar después

            # 7. NO generar tokens todavía (usuario debe verificar primero)
            return {
                "success": True,
                "message": "Usuario registrado. Verifica tu email.",
                "user_id": user.id,
                "email": user.email,
                "requires_verification": True
            }
        finally:
            session.close()

    def verify_email(self, email: str, code: str):
        """Verifica el código de email y activa la cuenta."""
        session = get_session()
        try:
            email = email.lower()
            
            # 1. Buscar usuario
            user = session.query(User).filter_by(email=email).first()
            if not user:
                raise ValueError("Usuario no encontrado")
            
            if user.email_verified:
                raise ValueError("Este email ya está verificado")
            
            # 2. Buscar código válido
            verification = session.query(EmailVerificationCode).filter_by(
                user_id=user.id,
                code=code,
                used=False
            ).order_by(EmailVerificationCode.created_at.desc()).first()
            
            if not verification:
                raise ValueError("Código inválido")
            
            if verification.is_expired():
                raise ValueError("El código ha expirado. Solicita uno nuevo.")
            
            # 3. Marcar email como verificado
            user.email_verified = True
            user.email_verified_at = datetime.utcnow()
            verification.used = True
            session.commit()
            session.refresh(user)
            
            # 4. Generar tokens ahora que está verificado
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
            
            # 5. Enviar email de bienvenida
            try:
                self.email_service.send_welcome_email(user.email, user.name)
            except:
                pass  # No es crítico
            
            return {
                "success": True,
                "access_token": access_token,
                "refresh_token": refresh_token,
                "user": {
                    "id": user.id,
                    "email": user.email,
                    "name": user.name,
                    "institution": user.get_institution(),
                    "is_academic": user.is_academic_email()
                }
            }
        finally:
            session.close()

    def resend_verification_code(self, email: str):
        """Reenvía código de verificación."""
        session = get_session()
        try:
            email = email.lower()
            user = session.query(User).filter_by(email=email).first()
            
            if not user:
                raise ValueError("Usuario no encontrado")
            
            if user.email_verified:
                raise ValueError("Este email ya está verificado")
            
            # Invalidar códigos anteriores
            old_codes = session.query(EmailVerificationCode).filter_by(
                user_id=user.id,
                used=False
            ).all()
            for old in old_codes:
                old.used = True
            
            # Generar nuevo código
            code = self.email_service.generate_verification_code()
            verification = EmailVerificationCode(
                user_id=user.id,
                code=code
            )
            session.add(verification)
            session.commit()
            
            # Enviar email
            self.email_service.send_verification_email(email, code, user.name)
            
            return {
                "success": True,
                "message": "Código reenviado a tu email"
            }
        finally:
            session.close()

    def login(self, email, password):
        """Login - Solo permite usuarios verificados."""
        session = get_session()
        try:
            email = email.lower()
            user = session.query(User).filter_by(email=email).first()
            
            if not user or not bcrypt.checkpw(password.encode('utf-8'), user.password_hash.encode('utf-8')):
                raise ValueError("Credenciales inválidas")
            
            # ✅ VERIFICAR QUE EL EMAIL ESTÉ CONFIRMADO
            if not user.email_verified:
                raise ValueError("Debes verificar tu email antes de iniciar sesión")

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
                "user": {
                    "id": user.id,
                    "email": user.email,
                    "name": user.name,
                    "institution": user.get_institution(),
                    "is_academic": user.is_academic_email()
                }
            }
        finally:
            session.close()

    def refresh(self, refresh_token_str: str):
        """Renovar tokens."""
        session = get_session()
        try:
            rt = session.query(RefreshToken).filter_by(token=refresh_token_str).first()
            
            if not rt:
                raise ValueError("Token no encontrado")
            if rt.is_revoked():
                raise ValueError("Token revocado")
            if rt.is_expired():
                raise ValueError("Token expirado")

            try:
                payload = jwt.decode(refresh_token_str, self.secret_key, algorithms=["HS256"])
            except:
                rt.revoked = True
                session.commit()
                raise ValueError("Token inválido")

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
        """Revocar token de refresco."""
        session = get_session()
        try:
            rt = session.query(RefreshToken).filter_by(token=token_str).first()
            if rt:
                rt.revoked = True
                session.commit()
        finally:
            session.close()