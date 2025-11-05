# 1. La imagen base: Usamos una imagen oficial de Python
FROM python:3.10-slim

# 2. Establecemos el directorio de trabajo DENTRO del contenedor
WORKDIR /app

# 3. Copiamos solo el archivo de requisitos primero (para eficiencia de caché)
COPY requirements.txt .

# 4. Instalamos las librerías de Python (Flask, SQLAlchemy, etc.)
# Usamos --no-cache-dir para mantener la imagen ligera
RUN pip install --no-cache-dir -r requirements.txt

# 5. Copiamos TODO el resto de tu código del backend al contenedor
COPY . .

# 6. Exponemos el puerto 5000 (el que usa Flask en tu app.py)
EXPOSE 5000

# 7. El comando para correr tu aplicación cuando el contenedor inicie
# Esto es lo mismo que "python app.py"
CMD ["python", "app.py"]