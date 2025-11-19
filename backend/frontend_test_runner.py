#!/usr/bin/env python3
"""
CYBERLEARN - TESTER COMPLETO DEL FRONTEND
Script para probar todas las funcionalidades del frontend contra el backend
"""

import requests
import json
import time
import sys
from datetime import datetime

class FrontendTester:
    def __init__(self, base_url="http://localhost:8000"):
        self.base_url = base_url
        self.token = None
        self.user_id = None
        self.session = requests.Session()
        
    def print_header(self, title):
        print(f"\n{'='*60}")
        print(f"🧪 {title}")
        print(f"{'='*60}")
    
    def print_success(self, message):
        print(f"✅ {message}")
    
    def print_error(self, message):
        print(f"❌ {message}")
    
    def print_warning(self, message):
        print(f"⚠️  {message}")
    
    def print_info(self, message):
        print(f"🔹 {message}")
    
    def test_health(self):
        """Prueba el endpoint de salud"""
        self.print_header("1. PRUEBA DE SALUD DEL SERVIDOR")
        try:
            response = self.session.get(f"{self.base_url}/api/health")
            if response.status_code == 200:
                data = response.json()
                self.print_success(f"Servidor activo: {data}")
                return True
            else:
                self.print_error(f"Error en health: {response.status_code}")
                return False
        except Exception as e:
            self.print_error(f"No se pudo conectar al servidor: {e}")
            return False
    
    def test_register(self):
        """Prueba el registro de usuario"""
        self.print_header("2. PRUEBA DE REGISTRO")
        
        # Generar email único para evitar conflictos
        timestamp = int(time.time())
        test_email = f"test_user_{timestamp}@cyberlearn.com"
        
        register_data = {
            "email": test_email,
            "password": "test123456",
            "name": "Usuario de Prueba"
        }
        
        try:
            response = self.session.post(
                f"{self.base_url}/api/auth/register",
                json=register_data,
                headers={"Content-Type": "application/json"}
            )
            
            print(f"📨 Datos enviados: {register_data}")
            print(f"📥 Respuesta: {response.status_code}")
            
            if response.status_code == 201:
                data = response.json()
                if data.get("success"):
                    self.token = data.get("token")
                    self.user_id = data.get("user", {}).get("id")
                    self.print_success(f"Registro exitoso - User ID: {self.user_id}")
                    self.print_info(f"Token: {self.token[:50]}...")
                    return True
                else:
                    self.print_error(f"Error en registro: {data}")
                    return False
            else:
                self.print_error(f"Error HTTP: {response.status_code} - {response.text}")
                return False
                
        except Exception as e:
            self.print_error(f"Excepción en registro: {e}")
            return False
    
    def test_login(self):
        """Prueba el login de usuario"""
        self.print_header("3. PRUEBA DE LOGIN")
        
        login_data = {
            "email": "test@example.com",  # Usuario de prueba existente
            "password": "password123"
        }
        
        try:
            response = self.session.post(
                f"{self.base_url}/api/auth/login",
                json=login_data,
                headers={"Content-Type": "application/json"}
            )
            
            print(f"📨 Login con: {login_data['email']}")
            print(f"📥 Respuesta: {response.status_code}")
            
            if response.status_code == 200:
                data = response.json()
                if data.get("success"):
                    self.token = data.get("token")
                    self.user_id = data.get("user", {}).get("id")
                    self.print_success(f"Login exitoso - User ID: {self.user_id}")
                    self.print_info(f"Token: {self.token[:50]}...")
                    return True
                else:
                    self.print_error(f"Error en login: {data}")
                    return False
            else:
                self.print_error(f"Error HTTP: {response.status_code} - {response.text}")
                return False
                
        except Exception as e:
            self.print_error(f"Excepción en login: {e}")
            return False
    
    def test_user_profile(self):
        """Prueba obtener perfil de usuario"""
        self.print_header("4. PRUEBA DE PERFIL DE USUARIO")
        
        if not self.token:
            self.print_warning("Sin token - omitiendo prueba de perfil")
            return False
        
        try:
            headers = {"Authorization": f"Bearer {self.token}"}
            response = self.session.get(
                f"{self.base_url}/api/user/profile",
                headers=headers
            )
            
            print(f"📥 Respuesta: {response.status_code}")
            
            if response.status_code == 200:
                data = response.json()
                self.print_success(f"Perfil obtenido: {data.get('user', {}).get('email')}")
                return True
            else:
                self.print_error(f"Error obteniendo perfil: {response.status_code} - {response.text}")
                return False
                
        except Exception as e:
            self.print_error(f"Excepción en perfil: {e}")
            return False
    
    def test_courses(self):
        """Prueba obtener lista de cursos"""
        self.print_header("5. PRUEBA DE LISTA DE CURSOS")
        
        try:
            response = self.session.get(f"{self.base_url}/api/courses")
            
            print(f"📥 Respuesta: {response.status_code}")
            
            if response.status_code == 200:
                courses = response.json()
                self.print_success(f"Se obtuvieron {len(courses)} cursos")
                
                # Mostrar primeros 3 cursos como ejemplo
                for i, course in enumerate(courses[:3]):
                    self.print_info(f"Curso {i+1}: {course.get('title')} - {course.get('difficulty')}")
                
                return True
            else:
                self.print_error(f"Error obteniendo cursos: {response.status_code} - {response.text}")
                return False
                
        except Exception as e:
            self.print_error(f"Excepción en cursos: {e}")
            return False
    
    def test_user_dashboard(self):
        """Prueba el dashboard del usuario"""
        self.print_header("6. PRUEBA DE DASHBOARD")
        
        if not self.token:
            self.print_warning("Sin token - omitiendo dashboard")
            return False
        
        try:
            headers = {"Authorization": f"Bearer {self.token}"}
            response = self.session.get(
                f"{self.base_url}/api/user/dashboard",
                headers=headers
            )
            
            print(f"📥 Respuesta: {response.status_code}")
            
            if response.status_code == 200:
                data = response.json()
                dashboard = data.get('dashboard', {})
                
                self.print_success("Dashboard obtenido correctamente")
                self.print_info(f"Total XP: {dashboard.get('total_xp', 0)}")
                self.print_info(f"Racha actual: {dashboard.get('current_streak', 0)} días")
                self.print_info(f"Medallas: {dashboard.get('badges_count', 0)}")
                
                # Mostrar progreso de cursos
                courses_progress = dashboard.get('courses_progress', [])
                self.print_info(f"Progreso en {len(courses_progress)} cursos")
                
                for course in courses_progress[:2]:  # Mostrar primeros 2
                    progress_pct = course.get('progress_percent', 0)
                    self.print_info(f"  - {course.get('course_title')}: {progress_pct}%")
                
                return True
            else:
                self.print_error(f"Error en dashboard: {response.status_code} - {response.text}")
                return False
                
        except Exception as e:
            self.print_error(f"Excepción en dashboard: {e}")
            return False
    
    def test_user_badges(self):
        """Prueba obtener medallas del usuario"""
        self.print_header("7. PRUEBA DE MEDALLAS")
        
        if not self.token:
            self.print_warning("Sin token - omitiendo medallas")
            return False
        
        try:
            headers = {"Authorization": f"Bearer {self.token}"}
            response = self.session.get(
                f"{self.base_url}/api/user/badges",
                headers=headers
            )
            
            print(f"📥 Respuesta: {response.status_code}")
            
            if response.status_code == 200:
                data = response.json()
                badges = data.get('badges', [])
                
                self.print_success(f"Se obtuvieron {len(badges)} medallas")
                
                for badge in badges[:3]:  # Mostrar primeras 3 medallas
                    self.print_info(f"Medalla: {badge.get('name')} - {badge.get('description')}")
                
                return True
            else:
                self.print_error(f"Error obteniendo medallas: {response.status_code} - {response.text}")
                return False
                
        except Exception as e:
            self.print_error(f"Excepción en medallas: {e}")
            return False
    
    def test_available_badges(self):
        """Prueba obtener medallas disponibles"""
        self.print_header("8. PRUEBA DE MEDALLAS DISPONIBLES")
        
        try:
            response = self.session.get(f"{self.base_url}/api/badges/available")
            
            print(f"📥 Respuesta: {response.status_code}")
            
            if response.status_code == 200:
                data = response.json()
                badges = data.get('badges', [])
                
                self.print_success(f"Hay {len(badges)} medallas disponibles en el sistema")
                
                for badge in badges[:3]:  # Mostrar primeras 3
                    self.print_info(f"Disponible: {badge.get('name')} - {badge.get('condition')}")
                
                return True
            else:
                self.print_error(f"Error en medallas disponibles: {response.status_code} - {response.text}")
                return False
                
        except Exception as e:
            self.print_error(f"Excepción en medallas disponibles: {e}")
            return False
    
    def test_user_streak(self):
        """Prueba obtener racha del usuario"""
        self.print_header("9. PRUEBA DE RACHA")
        
        if not self.token:
            self.print_warning("Sin token - omitiendo racha")
            return False
        
        try:
            headers = {"Authorization": f"Bearer {self.token}"}
            response = self.session.get(
                f"{self.base_url}/api/user/streak",
                headers=headers
            )
            
            print(f"📥 Respuesta: {response.status_code}")
            
            if response.status_code == 200:
                data = response.json()
                streak = data.get('streak', {})
                
                self.print_success("Datos de racha obtenidos")
                self.print_info(f"Días consecutivos: {streak.get('current_days', 0)}")
                self.print_info(f"Bonus XP: {streak.get('bonus_xp', 0)}")
                
                return True
            else:
                self.print_error(f"Error en racha: {response.status_code} - {response.text}")
                return False
                
        except Exception as e:
            self.print_error(f"Excepción en racha: {e}")
            return False
    
    def test_lessons(self):
        """Prueba obtener lecciones de un curso"""
        self.print_header("10. PRUEBA DE LECCIONES")
        
        # Primero obtener un curso para luego obtener sus lecciones
        try:
            response = self.session.get(f"{self.base_url}/api/courses")
            if response.status_code == 200:
                courses = response.json()
                if courses:
                    course_id = courses[0].get('id')  # Tomar el primer curso
                    
                    # Obtener lecciones del curso
                    response = self.session.get(f"{self.base_url}/api/courses/{course_id}/lessons")
                    
                    if response.status_code == 200:
                        lessons = response.json()
                        self.print_success(f"Se obtuvieron {len(lessons)} lecciones para el curso {course_id}")
                        
                        for i, lesson in enumerate(lessons[:2]):  # Mostrar primeras 2
                            self.print_info(f"Lección {i+1}: {lesson.get('title')}")
                        
                        return True
                    else:
                        self.print_error(f"Error obteniendo lecciones: {response.status_code}")
                        return False
                else:
                    self.print_warning("No hay cursos disponibles")
                    return False
            else:
                self.print_error("No se pudieron obtener cursos")
                return False
                
        except Exception as e:
            self.print_error(f"Excepción en lecciones: {e}")
            return False
    
    def test_lesson_progress(self):
        """Prueba actualizar progreso de lección"""
        self.print_header("11. PRUEBA DE PROGRESO DE LECCIÓN")
        
        if not self.token:
            self.print_warning("Sin token - omitiendo progreso")
            return False
        
        # Usar una lección de prueba (ID 1)
        lesson_id = 1
        
        try:
            headers = {
                "Authorization": f"Bearer {self.token}",
                "Content-Type": "application/json"
            }
            
            progress_data = {
                "completed": True
            }
            
            response = self.session.post(
                f"{self.base_url}/api/lessons/{lesson_id}/progress",
                json=progress_data,
                headers=headers
            )
            
            print(f"📨 Marcando lección {lesson_id} como completada")
            print(f"📥 Respuesta: {response.status_code}")
            
            if response.status_code == 200:
                data = response.json()
                self.print_success(f"Progreso actualizado - XP ganado: {data.get('points_earned', 0)}")
                return True
            else:
                self.print_error(f"Error en progreso: {response.status_code} - {response.text}")
                return False
                
        except Exception as e:
            self.print_error(f"Excepción en progreso: {e}")
            return False
    
    def run_complete_test_suite(self):
        """Ejecuta todas las pruebas en secuencia"""
        self.print_header("🚀 INICIANDO PRUEBAS COMPLETAS DEL FRONTEND")
        print(f"📅 {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
        print(f"🌐 Servidor: {self.base_url}")
        
        test_results = []
        
        # Ejecutar pruebas en orden
        tests = [
            ("Health Check", self.test_health),
            ("Registro", self.test_register),
            ("Login", self.test_login),
            ("Perfil Usuario", self.test_user_profile),
            ("Lista Cursos", self.test_courses),
            ("Dashboard", self.test_user_dashboard),
            ("Medallas Usuario", self.test_user_badges),
            ("Medallas Disponibles", self.test_available_badges),
            ("Racha", self.test_user_streak),
            ("Lecciones", self.test_lessons),
            ("Progreso Lección", self.test_lesson_progress),
        ]
        
        for test_name, test_func in tests:
            try:
                success = test_func()
                test_results.append((test_name, success))
                time.sleep(1)  # Pequeña pausa entre pruebas
            except Exception as e:
                self.print_error(f"Error ejecutando {test_name}: {e}")
                test_results.append((test_name, False))
        
        # Mostrar resumen final
        self.print_header("📊 RESUMEN FINAL DE PRUEBAS")
        
        passed = sum(1 for _, result in test_results if result)
        total = len(test_results)
        
        print(f"✅ Pruebas pasadas: {passed}/{total}")
        print(f"📈 Tasa de éxito: {(passed/total)*100:.1f}%")
        
        # Mostrar detalles
        for test_name, result in test_results:
            status = "✅ PASÓ" if result else "❌ FALLÓ"
            print(f"  {status} - {test_name}")
        
        # Recomendaciones
        self.print_header("🎯 RECOMENDACIONES")
        
        if passed == total:
            self.print_success("¡Todas las pruebas pasaron! El frontend debería funcionar correctamente.")
        elif passed >= total * 0.7:
            self.print_warning("La mayoría de las pruebas pasaron. Revisar las que fallaron.")
        else:
            self.print_error("Muchas pruebas fallaron. Revisar la configuración del backend.")
        
        return passed, total

def main():
    """Función principal"""
    # Verificar si se proporcionó una URL diferente
    base_url = "http://localhost:8000"
    if len(sys.argv) > 1:
        base_url = sys.argv[1]
    
    tester = FrontendTester(base_url)
    passed, total = tester.run_complete_test_suite()
    
    # Código de salida para scripts
    sys.exit(0 if passed == total else 1)

if __name__ == "__main__":
    main()