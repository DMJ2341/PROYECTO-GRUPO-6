import psycopg2

DB_CONFIG = {
    "host": "172.232.188.183",
    "database": "cyberlearn_db",
    "user": "app_cyberlearn",
    "password": "CyberLearn2025*"
}

def fix_activity_table():
    try:
        conn = psycopg2.connect(**DB_CONFIG)
        cursor = conn.cursor()
        print("🔌 Conectado...")

        # 1. Borrar tabla vieja (conflictiva)
        print("🗑️ Borrando tabla 'activities' antigua...")
        cursor.execute("DROP TABLE IF EXISTS activities CASCADE;")
        
        # 2. Crear tabla nueva con lesson_id como TEXTO
        print("✨ Creando tabla 'activities' corregida...")
        cursor.execute("""
            CREATE TABLE activities (
                id SERIAL PRIMARY KEY,
                user_id INTEGER NOT NULL REFERENCES users(id),
                activity_type VARCHAR(50) NOT NULL,
                description VARCHAR(255),
                points INTEGER DEFAULT 0,
                timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                lesson_id VARCHAR(100)  -- ✅ AHORA ES TEXTO
            );
        """)
        
        conn.commit()
        print("🎉 ¡Tabla Activities arreglada! Ahora acepta IDs de texto.")

    except Exception as e:
        print(f"❌ Error: {e}")
        if conn: conn.rollback()
    finally:
        if conn: conn.close()

if __name__ == "__main__":
    fix_activity_table()