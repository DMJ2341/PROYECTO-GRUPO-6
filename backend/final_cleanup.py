import psycopg2

# Configuración de conexión
DB_CONFIG = {
    "host": "172.232.188.183",
    "port": 5432,
    "database": "cyberlearn_db",
    "user": "app_cyberlearn",
    "password": "CyberLearn2025*"
}

def clean_database():
    conn = None
    try:
        conn = psycopg2.connect(**DB_CONFIG)
        cursor = conn.cursor()
        print("🔌 Conectado a la base de datos...")

        # 1. ELIMINAR TABLA BASURA
        print("🗑️ Eliminando tabla innecesaria 'lesson_progress'...")
        cursor.execute("DROP TABLE IF EXISTS lesson_progress;")
        
        # 2. ELIMINAR CURSOS SOBRANTES (6-10)
        print("✂️ Eliminando cursos sobrantes (ID 6-10)...")
        # Usamos una lista explícita de IDs en formato string para evitar errores de tipo
        ids_to_delete = ['6', '7', '8', '9', '10']
        # También intentamos borrar por si fueran enteros
        cursor.execute("DELETE FROM courses WHERE id IN ('6', '7', '8', '9', '10')")
        # Por si acaso hay IDs numéricos mayores a 5 (casteando a int)
        try:
            cursor.execute("DELETE FROM courses WHERE CAST(id AS INTEGER) > 5")
        except:
            pass # Si falla el cast, ignoramos

        # 3. CORREGIR LOS TÍTULOS DE LOS 5 CURSOS (UPDATE)
        print("✏️ Corrigiendo títulos de los Cursos 1-5...")
        
        updates = [
            ("1", "Fundamentos de Ciberseguridad", "Curso introductorio sobre amenazas y defensa digital."),
            ("2", "Seguridad de Redes y Comunicaciones", "Aprende a proteger redes y datos en tránsito."),
            ("3", "Seguridad de Sistemas Operativos", "Hardening y protección de Windows y Linux."),
            ("4", "Ciberseguridad Avanzada y Cloud", "Seguridad en la nube y amenazas persistentes."),
            ("5", "Operaciones de Ciberseguridad", "Gestión de incidentes y centro de operaciones (SOC).")
        ]

        sql_update = "UPDATE courses SET title = %s, description = %s WHERE id = %s"

        for course_id, title, desc in updates:
            cursor.execute(sql_update, (title, desc, course_id))
            print(f"   ✅ Curso {course_id} actualizado a: {title}")

        conn.commit()
        print("\n🎉 ¡LIMPIEZA COMPLETADA!")
        print("✅ Tabla 'lesson_progress' eliminada.")
        print("✅ Cursos extra eliminados.")
        print("✅ Nombres de cursos corregidos.")

    except Exception as e:
        print(f"❌ ERROR: {e}")
        if conn:
            conn.rollback()
    finally:
        if conn: conn.close()

if __name__ == "__main__":
    clean_database()
