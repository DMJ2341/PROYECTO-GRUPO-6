# scripts/reset_database.py
import psycopg2

def reset_database():
    conn = psycopg2.connect(
        host="172.232.188.183",
        port=5432,
        database="cyberlearn_db",
        user="app_cyberlearn",
        password="CyberLearn2025*"
    )
    conn.autocommit = True
    cursor = conn.cursor()
    
    print("🗑️  REINICIANDO BASE DE DATOS...")
    
    # Eliminar todas las tablas en orden correcto (evitar foreign key constraints)
    tables = ['user_badges', 'activities', 'lessons', 'courses', 'badges', 'users']
    
    for table in tables:
        try:
            cursor.execute(f"DROP TABLE IF EXISTS {table} CASCADE")
            print(f"   ✅ {table} eliminada")
        except Exception as e:
            print(f"   ⚠️  {table}: {e}")
    
    cursor.close()
    conn.close()
    print("🎉 Base de datos reiniciada completamente")

if __name__ == "__main__":
    reset_database()