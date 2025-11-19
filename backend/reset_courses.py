# scripts/reset_courses.py
import psycopg2

def reset_courses():
    conn = psycopg2.connect(
        host="172.232.188.183",
        port=5432,
        database="cyberlearn_db",
        user="app_cyberlearn",
        password="CyberLearn2025*"
    )
    cursor = conn.cursor()
    
    print("🧹 LIMPIANDO TABLA DE CURSOS...")
    
    # 1. Eliminar todos los cursos existentes
    cursor.execute("DELETE FROM course")
    print("✅ Todos los cursos eliminados")
    
    # 2. Reiniciar la secuencia de IDs (opcional pero recomendado)
    cursor.execute("ALTER SEQUENCE course_id_seq RESTART WITH 1")
    print("✅ Secuencia de IDs reiniciada")
    
    conn.commit()
    cursor.close()
    conn.close()
    
    print("🎉 Tabla 'course' limpiada exitosamente!")
    print("📝 Lista para cargar los 10 cursos correctos")

if __name__ == "__main__":
    reset_courses()