#!/usr/bin/env python3
"""
🔧 SCRIPT PARA REPARAR BASE DE DATOS
Crea tablas faltantes y verifica estructura
"""

import sys
import os
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

def create_tables():
    """Crear todas las tablas faltantes"""
    print("🔧 CREANDO TABLAS FALTANTES")
    print("=" * 50)
    
    try:
        from app import app
        from database.db import db
        
        with app.app_context():
            # Crear todas las tablas
            db.create_all()
            print("✅ Todas las tablas creadas/verificadas")
            
            # Verificar tablas creadas
            from sqlalchemy import text
            session = db.get_session()
            
            try:
                result = session.execute(text("""
                    SELECT table_name 
                    FROM information_schema.tables 
                    WHERE table_schema = 'public'
                    ORDER BY table_name
                """))
                
                tables = [row[0] for row in result.fetchall()]
                print(f"📊 Tablas en la base de datos: {len(tables)}")
                for table in tables:
                    print(f"   - {table}")
                    
                return True
                
            finally:
                session.close()
                
    except Exception as e:
        print(f"❌ Error creando tablas: {e}")
        return False

def check_table_data():
    """Verificar datos en cada tabla"""
    print("\n🔍 VERIFICANDO DATOS EN TABLAS")
    print("=" * 50)
    
    try:
        from database.db import db
        from sqlalchemy import text
        
        session = db.get_session()
        
        # Usar transacciones separadas para cada tabla
        tables_to_check = [
            ("courses", "SELECT COUNT(*) as count FROM courses"),
            ("lessons", "SELECT COUNT(*) as count FROM lessons"), 
            ("badges", "SELECT COUNT(*) as count FROM badges"),
            ("users", "SELECT COUNT(*) as count FROM users"),
            ("activities", "SELECT COUNT(*) as count FROM activities"),
            ("user_badges", "SELECT COUNT(*) as count FROM user_badges")
        ]
        
        for table_name, query in tables_to_check:
            try:
                # Nueva sesión para cada consulta
                temp_session = db.get_session()
                result = temp_session.execute(text(query))
                count = result.scalar() or 0
                temp_session.close()
                
                print(f"   📈 {table_name}: {count} registros")
                
            except Exception as e:
                print(f"   ❌ {table_name}: Error - {e}")
        
        session.close()
        return True
        
    except Exception as e:
        print(f"❌ Error verificando datos: {e}")
        return False

def create_sample_data():
    """Crear datos de ejemplo si las tablas están vacías"""
    print("\n📝 CREANDO DATOS DE EJEMPLO")
    print("=" * 50)
    
    try:
        from database.db import db
        from sqlalchemy import text
        
        session = db.get_session()
        
        # Verificar si hay badges
        result = session.execute(text("SELECT COUNT(*) FROM badges"))
        badge_count = result.scalar()
        
        if badge_count == 0:
            print("   🏅 Creando badges de ejemplo...")
            badges_data = [
                (1, 'Primer Respondedor', 'Completa tu primera lección', '🛡️', 'Completar lección 1', 10),
                (2, 'Cazador de Phishing', 'Identifica correos phishing', '🎣', 'Completar lección 2', 20),
                (3, 'Contenedor de Ransomware', 'Protege contra ransomware', '🔒', 'Completar lección 3', 30),
                (4, 'Guardián Móvil', 'Seguridad en dispositivos móviles', '📱', 'Completar lección 4', 40),
                (5, 'Guardián CIA', 'Domina la Tríada CIA', '🔐', 'Completar lección 5', 50),
                (6, 'Escudo Ciudadano', 'Completa el curso completo', '🏆', 'Completar todas las lecciones', 100)
            ]
            
            for badge in badges_data:
                session.execute(text("""
                    INSERT INTO badges (id, name, description, icon, condition, points_required, created_at)
                    VALUES (%s, %s, %s, %s, %s, %s, NOW())
                """), badge)
            
            session.commit()
            print("   ✅ 6 badges creados")
        
        # Verificar si hay cursos
        result = session.execute(text("SELECT COUNT(*) FROM courses"))
        course_count = result.scalar()
        
        if course_count == 0:
            print("   📚 Creando curso de ejemplo...")
            session.execute(text("""
                INSERT INTO courses (title, description, category, difficulty, duration_hours, 
                                   instructor, rating, students_count, price, language, created_at)
                VALUES ('Fundamentos de Ciberseguridad', 'Curso introductorio de ciberseguridad', 
                       'Seguridad Básica', 'Principiante', 8, 'CyberLearn Team', 4.8, 0, 0.0, 
                       'Español', NOW())
            """))
            session.commit()
            print("   ✅ Curso de ejemplo creado")
        
        session.close()
        return True
        
    except Exception as e:
        print(f"❌ Error creando datos de ejemplo: {e}")
        return False

def main():
    """Función principal"""
    print("🚀 REPARANDO BASE DE DATOS CYBERLEARN")
    print("=" * 60)
    
    # 1. Crear tablas
    if not create_tables():
        print("💥 No se pudieron crear las tablas")
        return
    
    # 2. Verificar datos
    check_table_data()
    
    # 3. Crear datos de ejemplo
    create_sample_data()
    
    print("\n" + "=" * 60)
    print("🎉 REPARACIÓN COMPLETADA")
    print("=" * 60)
    print("✅ Tablas creadas/verificadas")
    print("✅ Datos de ejemplo insertados")
    print("✅ Base de datos lista para usar")
    
    print("\n📝 PRÓXIMOS PASOS:")
    print("   1. Ejecutar: python app.py")
    print("   2. Probar endpoints con el script de prueba")

if __name__ == "__main__":
    main()