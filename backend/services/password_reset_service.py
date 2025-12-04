# backend/services/password_reset_service.py
import secrets
import string
import bcrypt
from database.db import get_session
from models.password_reset_code import PasswordResetCode
from models.user import User
from models.refresh_token import RefreshToken  
from datetime import datetime, timedelta
from services.email_service import EmailService
from sqlalchemy import func  # ✅ IMPORTANTE: Para búsquedas case-insensitive

def create_reset_token(email: str):
    """Genera código de 6 dígitos y lo envía por email."""
    session = get_session()
    try:
        # ✅ Normalizar email
        clean_email = email.strip().lower()
        
        # ✅ Búsqueda case-insensitive
        user = session.query(User).filter(func.lower(User.email) == clean_email).first()
        
        if not user:
            print(f"⚠️ Intento de recuperación para email no encontrado: '{clean_email}'")
            return {"success": True, "message": "Si el correo existe, recibirás un código"}

        # Invalidar códigos anteriores
        session.query(PasswordResetCode).filter_by(user_id=user.id).update({"used": True})
        session.commit()
        
        # Generar código 6 dígitos
        code = ''.join(secrets.choice(string.digits) for _ in range(6))
        
        reset_code = PasswordResetCode(
            user_id=user.id,
            code=code
        )
        session.add(reset_code)
        session.commit()

        # Enviar email
        email_service = EmailService()
        email_sent = email_service.send_password_reset_email(
            to_email=user.email,
            reset_code=code,
            user_name=user.name
        )
        
        if not email_sent:
            print(f"⚠️ Error al enviar correo a {user.email}")

        print(f"✅ Código generado para: {user.email}")
        return {
            "success": True,
            "message": "Código enviado a tu correo"
        }
    except Exception as e:
        session.rollback()
        print(f"❌ Error en create_reset_token: {str(e)}")
        return {"success": True, "message": "Proceso iniciado"}
    finally:
        session.close()

def validate_reset_token(email: str, code: str):
    """Valida el código de 6 dígitos."""
    session = get_session()
    try:
        # ✅ Normalizar email
        clean_email = email.strip().lower()
        
        print(f"🔍 [SERVICE] Buscando usuario: '{clean_email}'")
        
        # ✅ Búsqueda case-insensitive
        user = session.query(User).filter(func.lower(User.email) == clean_email).first()
        
        if not user:
            print(f"❌ [SERVICE] Usuario NO encontrado: '{clean_email}'")
            raise ValueError("Código inválido")
        
        print(f"✅ [SERVICE] Usuario encontrado: {user.id} ({user.email})")
        
        # Buscar código válido
        reset_code = session.query(PasswordResetCode).filter_by(
            user_id=user.id,
            code=code,
            used=False
        ).order_by(PasswordResetCode.created_at.desc()).first()

        if not reset_code:
            print(f"❌ [SERVICE] Código NO encontrado para user_id={user.id}, code='{code}'")
            raise ValueError("Código inválido")
        
        print(f"✅ [SERVICE] Código encontrado: id={reset_code.id}, created_at={reset_code.created_at}")

        if reset_code.is_expired():
            print(f"❌ [SERVICE] Código expirado (expires_at={reset_code.expires_at})")
            raise ValueError("El código ha expirado")

        print(f"✅ [SERVICE] Código válido!")
        return {"valid": True, "user_id": user.id}
    finally:
        session.close()

def reset_password(email: str, code: str, new_password: str):
    """Cambia la contraseña después de validar el código."""
    session = get_session()
    try:
        # ✅ Normalizar datos
        clean_email = email.strip().lower()
        clean_code = code.strip()
        
        print(f"🔐 [SERVICE] Cambiando contraseña para: '{clean_email}'")
        
        # ✅ Búsqueda case-insensitive
        user = session.query(User).filter(func.lower(User.email) == clean_email).first()
        
        if not user:
            print(f"❌ [SERVICE] Usuario NO encontrado: '{clean_email}'")
            raise ValueError("Usuario no encontrado")
        
        print(f"✅ [SERVICE] Usuario encontrado: {user.id} ({user.email})")

        # Buscar código válido
        reset_code = session.query(PasswordResetCode).filter_by(
            user_id=user.id,
            code=clean_code,
            used=False
        ).order_by(PasswordResetCode.created_at.desc()).first()

        if not reset_code:
            print(f"❌ [SERVICE] Código NO encontrado para user_id={user.id}, code='{clean_code}'")
            raise ValueError("Código inválido o expirado")
        
        if reset_code.is_expired():
            print(f"❌ [SERVICE] Código expirado")
            raise ValueError("Código inválido o expirado")

        # Actualizar contraseña
        user.password_hash = bcrypt.hashpw(new_password.encode('utf-8'), bcrypt.gensalt()).decode('utf-8')

        # Marcar código como usado
        reset_code.used = True
        
        # Cerrar sesión en otros dispositivos
        session.query(RefreshToken).filter_by(user_id=user.id).update({"revoked": True})

        session.commit()
        print(f"✅ Contraseña actualizada exitosamente para: {user.email}")
        return {"success": True, "message": "Contraseña actualizada correctamente"}
    except ValueError:
        raise  # Re-lanzar ValueError para que el endpoint lo capture
    except Exception as e:
        session.rollback()
        print(f"❌ Error en reset_password: {str(e)}")
        raise ValueError("Error al cambiar contraseña")
    finally:
        session.close()