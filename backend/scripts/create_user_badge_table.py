# backend/scripts/create_user_badge_table.py
import psycopg2

def create_user_badge_table():
    conn = psycopg2.connect(
        host="localhost",
        port=5432,
        database="cyberlearn_db",
        user="cyberlearn_user",
        password="CyberLearn2024!"
    )
    cursor = conn.cursor()
    
    # Crear tabla user_badges
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS user_badges (
            id SERIAL PRIMARY KEY,
            user_id INTEGER NOT NULL REFERENCES users(id),
            badge_id INTEGER NOT NULL REFERENCES badges(id),
            earned_at TIMESTAMP DEFAULT NOW(),
            earned_value INTEGER NOT NULL
        );
    """)
    
    print("✅ Tabla user_badges creada exitosamente")
    
    conn.commit()
    cursor.close()
    conn.close()

if __name__ == "__main__":
    create_user_badge_table()