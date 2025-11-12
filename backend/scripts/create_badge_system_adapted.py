# backend/scripts/create_badge_system.py
import psycopg2

def create_badge_system_adapted():
    conn = psycopg2.connect(
        host="localhost",
        port=5432,
        database="cyberlearn_db",
        user="cyberlearn_user",
        password="CyberLearn2024!"
    )
    cursor = conn.cursor()
    
    # Medallas adaptadas a la estructura existente
    badges = [
        {
            "name": "Primer Respondedor",
            "description": "Completaste tu primera lección sobre amenazas reales",
            "icon": "🛡️",
            "condition": "Completar 1 lección",
            "points_required": 10
        },
        {
            "name": "Cazador de Phishing",
            "description": "Detectaste correctamente casos de phishing",
            "icon": "🔍",
            "condition": "Detectar 3 phishing correctamente",
            "points_required": 30
        },
        {
            "name": "Contenedor de Ransomware",
            "description": "Contuviste exitosamente un ataque simulado",
            "icon": "🦠",
            "condition": "Contener ataque simulado",
            "points_required": 50
        },
        {
            "name": "Estratega CIA",
            "description": "Aplicaste correctamente los principios de seguridad",
            "icon": "🎯",
            "condition": "Aplicar principios CIA correctamente",
            "points_required": 45
        },
        {
            "name": "Detective de Redes",
            "description": "Identificaste correctamente redes peligrosas",
            "icon": "📶",
            "condition": "Identificar 5 redes peligrosas",
            "points_required": 35
        },
        {
            "name": "Fundamentos Completados",
            "description": "Terminaste el curso de fundamentos",
            "icon": "🎖️",
            "condition": "Completar todas las lecciones del curso 1",
            "points_required": 100
        }
    ]
    
    for badge in badges:
        cursor.execute("""
            INSERT INTO badges (
                name, description, icon, condition, points_required, created_at
            ) VALUES (
                %s, %s, %s, %s, %s, NOW()
            )
        """, (
            badge["name"], badge["description"], badge["icon"],
            badge["condition"], badge["points_required"]
        ))
        
        print(f"✅ Medalla creada: {badge['name']}")
    
    conn.commit()
    cursor.close()
    conn.close()
    print("🎉 Sistema de medallas adaptado exitosamente!")

if __name__ == "__main__":
    create_badge_system_adapted()