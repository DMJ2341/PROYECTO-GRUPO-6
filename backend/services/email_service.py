# backend/services/email_service.py
import smtplib
import random
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
from config import Config

class EmailService:
    """Servicio para envío de correos electrónicos."""
    
    def __init__(self):
        self.smtp_server = "smtp.gmail.com"
        self.smtp_port = 587
        self.sender_email = Config.GMAIL_USER
        self.sender_password = Config.GMAIL_APP_PASSWORD
    
    @staticmethod
    def generate_verification_code():
        """Genera un código de 6 dígitos aleatorio."""
        return str(random.randint(100000, 999999))
    
    def send_verification_email(self, to_email: str, code: str, user_name: str = "Usuario"):
        """Envía email con código de verificación."""
        try:
            subject = "Verifica tu cuenta de CyberLearn 🔐"
            
            # Cuerpo del email en HTML
            html_body = f"""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body {{ font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }}
                    .container {{ max-width: 600px; margin: 40px auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); overflow: hidden; }}
                    .header {{ background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; }}
                    .content {{ padding: 40px 30px; }}
                    .code-box {{ background-color: #f8f9fa; border: 2px dashed #667eea; border-radius: 8px; padding: 20px; text-align: center; margin: 30px 0; }}
                    .code {{ font-size: 36px; font-weight: bold; color: #667eea; letter-spacing: 8px; font-family: 'Courier New', monospace; }}
                    .footer {{ background-color: #f8f9fa; padding: 20px; text-align: center; font-size: 12px; color: #6c757d; }}
                    .warning {{ color: #dc3545; font-size: 14px; margin-top: 20px; }}
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎓 CyberLearn</h1>
                        <p>Aprende Ciberseguridad</p>
                    </div>
                    <div class="content">
                        <h2>¡Hola, {user_name}! 👋</h2>
                        <p>Gracias por registrarte en CyberLearn. Para completar tu registro, verifica tu correo electrónico.</p>
                        <p><strong>Tu código de verificación es:</strong></p>
                        <div class="code-box"><div class="code">{code}</div></div>
                        <p>Ingresa este código en la aplicación para activar tu cuenta.</p>
                        <p class="warning">⏱️ Este código expira en <strong>10 minutos</strong>.</p>
                    </div>
                    <div class="footer">
                        <p>CyberLearn - Plataforma de Educación en Ciberseguridad</p>
                    </div>
                </div>
            </body>
            </html>
            """
            
            message = MIMEMultipart("alternative")
            message["Subject"] = subject
            message["From"] = f"CyberLearn <{self.sender_email}>"
            message["To"] = to_email
            
            html_part = MIMEText(html_body, "html")
            message.attach(html_part)
            
            with smtplib.SMTP(self.smtp_server, self.smtp_port) as server:
                server.starttls()
                server.login(self.sender_email, self.sender_password)
                server.send_message(message)
            
            print(f"✅ Email de verificación enviado a {to_email}")
            return True
            
        except Exception as e:
            print(f"❌ Error enviando email: {e}")
            raise Exception(f"No se pudo enviar el email: {str(e)}")
    
    def send_welcome_email(self, to_email: str, user_name: str):
        """Envía email de bienvenida."""
        try:
            subject = "¡Bienvenido a CyberLearn! 🎉"
            html_body = f"""
            <!DOCTYPE html>
            <html>
            <body>
                <h2>Hola {user_name},</h2>
                <p>Tu cuenta ha sido verificada exitosamente. ¡Bienvenido a CyberLearn!</p>
            </body>
            </html>
            """
            message = MIMEMultipart("alternative")
            message["Subject"] = subject
            message["From"] = f"CyberLearn <{self.sender_email}>"
            message["To"] = to_email
            message.attach(MIMEText(html_body, "html"))
            
            with smtplib.SMTP(self.smtp_server, self.smtp_port) as server:
                server.starttls()
                server.login(self.sender_email, self.sender_password)
                server.send_message(message)
            return True
        except Exception as e:
            print(f"⚠️ No se pudo enviar email de bienvenida: {e}")
            return False

    # ✅ NUEVO MÉTODO
    def send_password_reset_email(self, to_email: str, reset_code: str, user_name: str = "Usuario"):
        """Envía email con el código de recuperación de 6 dígitos."""
        try:
            subject = "🔐 Recupera tu contraseña - CyberLearn"
            
            html_body = f"""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body {{ font-family: Arial, sans-serif; background-color: #0f1419; color: #e5e7eb; margin: 0; padding: 0; }}
                    .container {{ max-width: 600px; margin: 40px auto; background-color: #1a2332; border-radius: 16px; overflow: hidden; }}
                    .header {{ background: linear-gradient(135deg, #8b5cf6 0%, #6d28d9 100%); padding: 40px 20px; text-align: center; }}
                    .header h1 {{ margin: 0; color: #ffffff; font-size: 32px; font-weight: bold; }}
                    .content {{ padding: 40px 30px; }}
                    .code-box {{ background: rgba(139, 92, 246, 0.1); border: 2px solid #8b5cf6; border-radius: 12px; padding: 30px; text-align: center; margin: 30px 0; }}
                    .code {{ font-size: 48px; font-weight: bold; letter-spacing: 8px; color: #8b5cf6; font-family: 'Courier New', monospace; }}
                    .warning {{ background: rgba(239, 68, 68, 0.1); border-left: 4px solid #ef4444; padding: 15px; margin: 20px 0; border-radius: 4px; }}
                    .footer {{ text-align: center; padding: 20px; color: #6b7280; font-size: 12px; border-top: 1px solid #374151; }}
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🔐 Recuperación</h1>
                    </div>
                    <div class="content">
                        <h2 style="color: #8b5cf6;">Hola, {user_name}</h2>
                        <p>Recibimos una solicitud para restablecer tu contraseña en <strong>CyberLearn</strong>.</p>
                        <p>Usa el siguiente código:</p>
                        
                        <div class="code-box">
                            <div class="code">{reset_code}</div>
                        </div>
                        
                        <p style="text-align: center; color: #9ca3af;">Este código expira en 10 minutos.</p>
                        
                        <div class="warning">
                            <strong>⚠️ Importante:</strong> Si NO solicitaste esto, ignora este correo.
                        </div>
                    </div>
                    <div class="footer">
                        <p>© 2025 CyberLearn</p>
                    </div>
                </div>
            </body>
            </html>
            """
            
            message = MIMEMultipart("alternative")
            message["Subject"] = subject
            message["From"] = f"CyberLearn <{self.sender_email}>"
            message["To"] = to_email
            message.attach(MIMEText(html_body, "html"))
            
            with smtplib.SMTP(self.smtp_server, self.smtp_port) as server:
                server.starttls()
                server.login(self.sender_email, self.sender_password)
                server.send_message(message)
            
            print(f"✅ Email de recuperación enviado a {to_email}")
            return True
            
        except Exception as e:
            print(f"❌ Error enviando email de recuperación: {e}")
            return False