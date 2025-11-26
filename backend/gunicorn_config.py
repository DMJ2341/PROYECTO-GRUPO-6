import multiprocessing
import os

# Bind: Solo local (Nginx maneja externo)
bind = "0.0.0.0:8000"

# Workers: Fórmula estándar (CPU * 2 + 1)
# Usamos os.cpu_count() si multiprocessing falla en ciertos entornos.
try:
    workers = multiprocessing.cpu_count() * 2 + 1
except NotImplementedError:
    workers = os.cpu_count() * 2 + 1 if os.cpu_count() else 3

# Tipo de worker
worker_class = 'sync'

# Logs: Directorio persistente (¡Asegúrate de haber creado la carpeta /var/log/cyberlearn!)
accesslog = "/var/log/cyberlearn/access.log"
errorlog = "/var/log/cyberlearn/error.log"
loglevel = "info"  # Recomiendo 'info' para ver el tráfico en esta etapa

# Tiempos de espera
timeout = 120
graceful_timeout = 120  # Permite terminar peticiones pendientes antes de reiniciar

# Preload: Carga la app antes de clonar los procesos (ahorra RAM y arranca más rápido)
preload_app = False

# Daemon: Systemd se encarga de esto, así que False es correcto
daemon = False

# Usuario del proceso (Opcional si Systemd ya lo maneja, pero no hace daño)
user = "is-maria.gavino.p"
group = "is-maria.gavino.p"