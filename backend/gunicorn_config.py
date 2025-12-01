import multiprocessing
import os

# Bind: Solo local (Nginx maneja externo)
bind = "0.0.0.0:8000"

# Workers: Fórmula estándar (CPU * 2 + 1)
try:
    # Usamos gevent para manejar la concurrencia de manera eficiente.
    workers = multiprocessing.cpu_count() * 2 + 1
except NotImplementedError:
    workers = os.cpu_count() * 2 + 1 if os.cpu_count() else 3

# ✅ CAMBIO CRÍTICO: Usar gevent en lugar de sync
worker_class = 'gevent'  # Cambiado de 'sync' a gevent
worker_connections = 1000 # Máximo de conexiones que puede manejar cada worker

# Logs
accesslog = "/var/log/cyberlearn/access.log"
errorlog = "/var/log/cyberlearn/error.log"
loglevel = "info"

# ✅ KEEP-ALIVE: Crítico para que Nginx mantenga la conexión (75 segundos)
# Debe ser menor que el timeout del proxy (Nginx).
keepalive = 75  

# Tiempos de espera
timeout = 120
graceful_timeout = 120

# Preload
preload_app = False

# Daemon
daemon = False

# Usuario
user = "is-maria.gavino.p"
group = "is-maria.gavino.p"

# ✅ Configuración adicional para estabilidad
# Reiniciar workers periódicamente para evitar memory leaks
max_requests = 1000  
max_requests_jitter = 50