import os

class Config:
    """Configuración de la aplicación Flask."""
    
    # 1. Lee la variable de entorno DATABASE_URL que Docker le da.
    # 2. Si no la encuentra (ej. si corres 'python app.py' sin Docker),
    #    usará None.
    SQLALCHEMY_DATABASE_URI = os.environ.get('DATABASE_URL')
    
    # Si no se define DATABASE_URL, SQLAlchemy no se iniciará,
    # y tu app.py entrará en modo JSON (DB_AVAILABLE = False)
    
    SQLALCHEMY_TRACK_MODIFICATIONS = False