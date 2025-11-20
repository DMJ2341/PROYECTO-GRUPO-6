# backend/clean_titles.py
import psycopg2

DB_CONFIG = {
    "host": "172.232.188.183",
    "database": "cyberlearn_db",
    "user": "app_cyberlearn",
    "password": "CyberLearn2025*"
}

def clean_titles():
    try:
        conn = psycopg2.connect(**DB_CONFIG)
        cursor = conn.cursor()
        
        # Títulos LIMPIOS (Sin emojis pegados al texto)
        updates = [
            ("1", "CURSO 1: Fundamentos de Ciberseguridad"),
            ("2", "CURSO 2: Seguridad de Redes y Comunicaciones"),
            ("3", "CURSO 3: Seguridad de Sistemas Operativos"),
            ("4", "CURSO 4: Ciberseguridad Avanzada y Cloud"),
            ("5", "CURSO 5: Operaciones de Ciberseguridad")
        ]

        print("🧹 Limpiando títulos...")
        for cid, title in updates:
            cursor.execute("UPDATE courses SET title = %s WHERE id = %s", (title, cid))
        
        conn.commit()
        print("✅ ¡Títulos limpios! Ahora el texto no tendrá el emoji pegado.")

    except Exception as e:
        print(f"❌ Error: {e}")
    finally:
        if conn: conn.close()

if __name__ == "__main__":
    clean_titles()