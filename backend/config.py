import os

class Config:
    # 🔥 CONEXIÓN REMOTA AL SERVIDOR
    SQLALCHEMY_DATABASE_URI = 'postgresql://cyberlearn_user:password@172.232.188.183:5432/cyberlearn_db'
    SQLALCHEMY_TRACK_MODIFICATIONS = False