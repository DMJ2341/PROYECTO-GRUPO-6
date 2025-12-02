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
                    body {{
                        font-family: Arial, sans-serif;
                        background-color: #f4f4f4;
                        margin: 0;
                        padding: 0;
                    }}
                    .container {{
                        max-width: 600px;
                        margin: 40px auto;
                        background-color: #ffffff;
                        border-radius: 8px;
                        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                        overflow: hidden;
                    }}
                    .header {{
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        color: white;
                        padding: 30px;
                        text-align: center;
                    }}
                    .content {{
                        padding: 40px 30px;
                    }}
                    .code-box {{
                        background-color: #f8f9fa;
                        border: 2px dashed #667eea;
                        border-radius: 8px;
                        padding: 20px;
                        text-align: center;
                        margin: 30px 0;
                    }}
                    .code {{
                        font-size: 36px;
                        font-weight: bold;
                        color: #667eea;
                        letter-spacing: 8px;
                        font-family: 'Courier New', monospace;
                    }}
                    .footer {{
                        background-color: #f8f9fa;
                        padding: 20px;
                        text-align: center;
                        font-size: 12px;
                        color: #6c757d;
                    }}
                    .warning {{
                        color: #dc3545;
                        font-size: 14px;
                        margin-top: 20px;
                    }}
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
                        
                        <div class="code-box">
                            <div class="code">{code}</div>
                        </div>
                        
                        <p>Ingresa este código en la aplicación para activar tu cuenta.</p>
                        
                        <p class="warning">⏱️ Este código expira en <strong>10 minutos</strong>.</p>
                        
                        <p style="margin-top: 30px; font-size: 14px; color: #6c757d;">
                            Si no creaste esta cuenta, puedes ignorar este correo de forma segura.
                        </p>
                    </div>
                    <div class="footer">
                        <p>CyberLearn - Plataforma de Educación en Ciberseguridad</p>
                        <p>Este es un correo automático, por favor no respondas.</p>
                    </div>
                </div>
            </body>
            </html>
            """
            
            # Crear mensaje
            message = MIMEMultipart("alternative")
            message["Subject"] = subject
            message["From"] = f"CyberLearn <{self.sender_email}>"
            message["To"] = to_email
            
            # Agregar versión HTML
            html_part = MIMEText(html_body, "html")
            message.attach(html_part)
            
            # Enviar email
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
        """Envía email de bienvenida después de verificar."""
        try:
            subject = "¡Bienvenido a CyberLearn! 🎉"
            
            html_body = f"""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body {{ font-family: Arial, sans-serif; }}
                    .container {{ max-width: 600px; margin: 0 auto; padding: 20px; }}
                    .header {{ background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; border-radius: 8px; text-align: center; }}
                    .content {{ padding: 30px 0; }}
                    .feature {{ margin: 15px 0; padding: 15px; background: #f8f9fa; border-radius: 5px; }}
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>¡Cuenta verificada! ✅</h1>
                    </div>
                    <div class="content">
                        <h2>Hola {user_name},</h2>
                        <p>Tu cuenta ha sido verificada exitosamente. ¡Ya puedes empezar a aprender!</p>
                        
                        <h3>Lo que encontrarás en CyberLearn:</h3>
                        <div class="feature">🎓 5 cursos completos de ciberseguridad</div>
                        <div class="feature">📖 Glosario con 262 términos técnicos</div>
                        <div class="feature">🏆 Sistema de insignias y niveles</div>
                        <div class="feature">📊 Test vocacional (Red Team vs Blue Team vs Purple Team)</div>
                        
                        <p style="margin-top: 30px;">¡Comienza tu viaje en la ciberseguridad hoy mismo!</p>
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
            
            print(f"✅ Email de bienvenida enviado a {to_email}")
            return True
            
        except Exception as e:
            print(f"⚠️ No se pudo enviar email de bienvenida: {e}")
            return False