# scripts/create_missing_tables.py
import psycopg2

def create_missing_tables():
    conn = psycopg2.connect(
        host="172.232.188.183",
        port=5432,
        database="cyberlearn_db",
        user="app_cyberlearn", 
        password="CyberLearn2025*"
    )
    cursor = conn.cursor()
    
    print("🔧 CREANDO TABLAS FALTANTES...")
    
    # 1. TABLA COURSE (simplificada como mencionaste)
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS course (
            id SERIAL PRIMARY KEY,
            title VARCHAR(200) NOT NULL,
            description TEXT,
            level VARCHAR(50),
            xp_reward INTEGER DEFAULT 0,
            image_url VARCHAR(500),
            category VARCHAR(100),
            duration_hours INTEGER,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
    """)
    print("✅ Tabla 'course' creada")
    
    # 2. TABLA LESSON (compatible con user_lesson_progress)
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS lesson (
            id VARCHAR(100) PRIMARY KEY,
            course_id INTEGER REFERENCES course(id) ON DELETE CASCADE,
            title VARCHAR(200) NOT NULL,
            description TEXT,
            content TEXT,
            order_index INTEGER NOT NULL,
            type VARCHAR(20) DEFAULT 'interactive',
            duration_minutes INTEGER,
            xp_reward INTEGER DEFAULT 0,
            total_screens INTEGER DEFAULT 1,
            screens JSONB,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
    """)
    print("✅ Tabla 'lesson' creada")
    
    conn.commit()
    
    # Verificar que se crearon
    cursor.execute("""
        SELECT table_name FROM information_schema.tables 
        WHERE table_schema = 'public' 
        AND table_name IN ('course', 'lesson')
    """)
    created_tables = cursor.fetchall()
    
    cursor.close()
    conn.close()
    
    print(f"🎉 Tablas creadas: {[t[0] for t in created_tables]}")
    print("=" * 50)
    print("📊 ESTRUCTURA COMPLETA:")
    print("• course     (nueva)")
    print("• lesson     (nueva)") 
    print("• user       (existente)")
    print("• badge      (existente)")
    print("• user_badge (existente)")
    print("• user_activity (existente)")
    print("• user_lesson_progress (existente)")
    print("=" * 50)

if __name__ == "__main__":
    create_missing_tables()