# backend/scripts/create_badge_table.py
import psycopg2

def create_badge_table():
    conn = psycopg2.connect(
        host="localhost",
        port=5432,
        database="cyberlearn_db",
        user="cyberlearn_user",
        password="CyberLearn2024!"
    )
    cursor = conn.cursor()
    
    # Crear tabla badges con la estructura completa
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS badges (
            id SERIAL PRIMARY KEY,
            name VARCHAR(100) NOT NULL,
            description TEXT NOT NULL,
            icon VARCHAR(50) NOT NULL,
            rarity VARCHAR(20) DEFAULT 'common',
            xp_value INTEGER DEFAULT 0,
            category VARCHAR(50) NOT NULL,
            condition_type VARCHAR(50) NOT NULL,
            condition_value INTEGER NOT NULL,
            created_at TIMESTAMP DEFAULT NOW()
        );
    """)
    
    print("✅ Tabla badges creada exitosamente")
    
    conn.commit()
    cursor.close()
    conn.close()

if __name__ == "__main__":
    create_badge_table()