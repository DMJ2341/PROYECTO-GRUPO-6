from flask_sqlalchemy import SQLAlchemy

db = SQLAlchemy()

def init_db(app):
    """
    Inicializa la base de datos.
    NO sobrescribe la configuración - usa la de config.py
    """
    db.init_app(app)
    
    with app.app_context():
        # Crear todas las tablas si no existen
        db.create_all()
        print("✅ Tablas de base de datos verificadas/creadas")