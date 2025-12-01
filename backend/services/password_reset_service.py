# backend/services/password_reset_service.py
import secrets
import string
import hashlib
import bcrypt
from database.db import get_session
from models.password_reset_token import PasswordResetToken
from models.user import User
from models.refresh_token import RefreshToken  # ✅ IMPORTANTE PARA REVOCAR SESIONES
from datetime import datetime, timedelta

def generate_token():
    # Genera un token aleatorio seguro
    alphabet = string.ascii_letters + string.digits
    return ''.join(secrets.choice(alphabet) for _ in range(64))

def create_reset_token(email: str):
    session = get_session()
    try:
        user = session.query(User).filter_by(email=email).first()
        if not user:
            # Seguridad: No revelar si el email existe o no
            return {"success": True, "message": "Si el correo existe, recibirás un enlace"}

        # Invalidar tokens anteriores de recuperación de este usuario
        session.query(PasswordResetToken).filter_by(user_id=user.id).update({"used": True})

        token_raw = generate_token()
        # Guardamos solo el hash en BD (si hackean la BD, no pueden usar los tokens)
        token_hash = hashlib.sha256(token_raw.encode()).hexdigest()

        reset_token = PasswordResetToken(
            user_id=user.id,
            token=token_hash,
            expires_at=datetime.utcnow() + timedelta(hours=1)  # 1 hora de validez
        )
        session.add(reset_token)
        session.commit()

        # En un entorno real, aquí enviarías el email.
        # Para desarrollo, devolvemos el link directo.
        reset_url = f"https://cyberlearn.app/reset-password?token={token_raw}&email={email}"
        
        return {
            "success": True,
            "message": "Enlace generado (Revisar respuesta JSON en desarrollo)",
            "reset_url": reset_url,  # ⚠️ QUITAR EN PRODUCCIÓN CUANDO HAYA EMAIL REAL
            "user_id": user.id
        }
    finally:
        session.close()

def validate_reset_token(token_raw: str, email: str):
    token_hash = hashlib.sha256(token_raw.encode()).hexdigest()
    session = get_session()
    try:
        # Join para verificar que el token pertenece a ese email
        token = session.query(PasswordResetToken).join(User).filter(
            PasswordResetToken.token == token_hash,
            User.email == email
        ).first()

        if not token or not token.is_valid():
            raise ValueError("Token inválido o expirado")

        return {"valid": True, "user_id": token.user_id}
    finally:
        session.close()

def reset_password(token_raw: str, email: str, new_password: str):
    token_hash = hashlib.sha256(token_raw.encode()).hexdigest()
    session = get_session()
    try:
        token = session.query(PasswordResetToken).join(User).filter(
            PasswordResetToken.token == token_hash,
            User.email == email
        ).first()

        if not token or not token.is_valid():
            raise ValueError("Token inválido o expirado")

        user = token.user
        # Actualizar contraseña
        user.password_hash = bcrypt.hashpw(new_password.encode('utf-8'), bcrypt.gensalt()).decode('utf-8')

        # 1. Marcar este token como usado
        token.used = True
        
        # 2. Seguridad Crítica: Revocar TODOS los Refresh Tokens activos
        # Esto cierra sesión en todos los dispositivos del usuario
        session.query(RefreshToken).filter_by(user_id=user.id).update({"revoked": True})

        session.commit()
        return {"success": True, "message": "Contraseña actualizada correctamente"}
    except ValueError:
        raise
    except Exception as e:
        session.rollback()
        raise e
    finally:
        session.close()