import multiprocessing

# Dónde escuchar (Solo localmente, Nginx se encarga de lo de fuera)
bind = "127.0.0.1:8000"

# Trabajadores (Workers)
workers = multiprocessing.cpu_count() * 2 + 1

# Configuración de logs (Guardamos en temporales para evitar problemas de permisos)
accesslog = "/tmp/gunicorn_access.log"
errorlog = "/tmp/gunicorn_error.log"
loglevel = "info"

# Tiempos de espera
timeout = 120
