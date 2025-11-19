# scripts/upload_badges.py
import psycopg2

def upload_badges():
    conn = psycopg2.connect(
        host="172.232.188.183",
        port=5432,
        database="cyberlearn_db",
        user="app_cyberlearn",
        password="CyberLearn2025*"
    )
    cursor = conn.cursor()
    
    # PRIMERO: Ver la estructura real de la tabla badge
    print("🔍 Verificando estructura de la tabla 'badge'...")
    cursor.execute("""
        SELECT column_name, data_type 
        FROM information_schema.columns 
        WHERE table_name = 'badge' 
        ORDER BY ordinal_position
    """)
    columns = cursor.fetchall()
    print("📋 Columnas de 'badge':", [col[0] for col in columns])
    
    # Limpiar badges existentes primero
    cursor.execute("DELETE FROM badge")
    print("🧹 Badges existentes eliminados")
    
    # Badges del sistema
    badges = [
        {
            "id": "1",
            "name": "Primer Respondedor",
            "description": "Completaste tu primera lección sobre amenazas reales",
            "icon": "🛡️",
            "xp_required": 10
        },
        {
            "id": "2",
            "name": "Cazador de Phishing", 
            "description": "Identificas correos de phishing como un experto",
            "icon": "🎣",
            "xp_required": 20
        },
        {
            "id": "3",
            "name": "Contenedor de Ransomware", 
            "description": "Proteges sistemas contra ransomware",
            "icon": "🔒",
            "xp_required": 30
        },
        {
            "id": "4",
            "name": "Guardián Móvil",
            "description": "Dominas la seguridad en dispositivos móviles", 
            "icon": "📱",
            "xp_required": 40
        },
        {
            "id": "5",
            "name": "Guardián CIA", 
            "description": "Domina los principios de la Tríada CIA",
            "icon": "🔐",
            "xp_required": 50
        },
        {
            "id": "6",
            "name": "Escudo Ciudadano",
            "description": "Completaste el curso completo de fundamentos",
            "icon": "🏆", 
            "xp_required": 100
        }
    ]
    
    for badge in badges:
        cursor.execute("""
            INSERT INTO badge (
                id, name, description, icon, xp_required
            ) VALUES (
                %s, %s, %s, %s, %s
            )
        """, (
            badge["id"], badge["name"], badge["description"], 
            badge["icon"], badge["xp_required"]
        ))
        
        print(f"✅ Badge creado: {badge['name']} (ID: {badge['id']})")
    
    conn.commit()
    cursor.close()
    conn.close()
    print("🎉 Badges cargados exitosamente!")
    print("📊 Resumen: 6 badges creados con estructura simplificada")

if __name__ == "__main__":
    upload_badges()