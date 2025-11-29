from services.email_service import EmailService

email_service = EmailService()
code = email_service.generate_verification_code()
print(f"Código generado: {code}")

try:
    email_service.send_verification_email(
        "TU_EMAIL_PERSONAL@gmail.com",  # Cambia esto
        code,
        "Usuario de Prueba"
    )
    print("✅ Email enviado correctamente!")
except Exception as e:
    print(f"❌ Error: {e}")