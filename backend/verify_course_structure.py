#!/usr/bin/env python3
# backend/verify_course_structure.py

import psycopg2
from tabulate import tabulate

def verify_course_structure():
    """Verifica la estructura completa de cursos y lecciones"""
    
    conn = psycopg2.connect(
        host="172.232.188.183",
        port=5432,
        database="cyberlearn_db",
        user="app_cyberlearn",
        password="CyberLearn2025*"
    )
    cursor = conn.cursor()
    
    print("=" * 80)
    print("🔍 VERIFICACIÓN COMPLETA DE ESTRUCTURA DE CURSOS")
    print("=" * 80)
    
    # ========== 1. VERIFICAR CURSOS ==========
    print("\n📚 PASO 1: CURSOS EN LA BASE DE DATOS")
    print("-" * 80)
    
    cursor.execute("""
        SELECT id, title, level, xp_reward, duration_hours, category
        FROM courses
        ORDER BY id
    """)
    
    courses = cursor.fetchall()
    
    if courses:
        print(f"\n✅ Total de cursos encontrados: {len(courses)}\n")
        
        courses_data = []
        for course in courses:
            courses_data.append([
                course[0],  # id
                course[1][:50],  # title (truncado)
                course[2],  # level
                course[3],  # xp_reward
                course[4],  # duration_hours
                course[5]   # category
            ])
        
        print(tabulate(
            courses_data,
            headers=["ID", "Título", "Nivel", "XP", "Horas", "Categoría"],
            tablefmt="grid"
        ))
    else:
        print("❌ NO SE ENCONTRARON CURSOS")
    
    # ========== 2. VERIFICAR CURSO 1 Y SUS LECCIONES ==========
    print("\n\n📖 PASO 2: CURSO 1 - FUNDAMENTOS DE CIBERSEGURIDAD")
    print("-" * 80)
    
    cursor.execute("""
        SELECT id, title 
        FROM courses 
        WHERE title ILIKE '%fundamento%' OR title ILIKE '%concientización%'
        LIMIT 1
    """)
    
    curso_1 = cursor.fetchone()
    
    if curso_1:
        curso_1_id = curso_1[0]
        curso_1_title = curso_1[1]
        print(f"✅ Curso 1 encontrado: ID={curso_1_id}, Título='{curso_1_title}'")
        
        # Verificar lecciones del Curso 1
        print(f"\n🎯 LECCIONES DEL CURSO 1:")
        print("-" * 80)
        
        cursor.execute("""
            SELECT id, title, order_index, duration_minutes, xp_reward, type, total_screens
            FROM lessons
            WHERE course_id = %s
            ORDER BY order_index
        """, (curso_1_id,))
        
        lecciones_curso_1 = cursor.fetchall()
        
        if lecciones_curso_1:
            print(f"\n✅ Total de lecciones: {len(lecciones_curso_1)}\n")
            
            lecciones_data = []
            for leccion in lecciones_curso_1:
                lecciones_data.append([
                    leccion[2],  # order_index
                    leccion[0],  # id
                    leccion[1][:60],  # title (truncado)
                    leccion[3],  # duration_minutes
                    leccion[4],  # xp_reward
                    leccion[5],  # type
                    leccion[6]   # total_screens
                ])
            
            print(tabulate(
                lecciones_data,
                headers=["#", "ID", "Título", "Min", "XP", "Tipo", "Screens"],
                tablefmt="grid"
            ))
            
            # Verificar que las 6 lecciones esperadas existan
            print("\n📋 VERIFICACIÓN DE LECCIONES ESPERADAS:")
            lecciones_esperadas = [
                "INTRODUCCIÓN A LAS AMENAZAS CIBERNÉTICAS",
                "INGENIERÍA SOCIAL Y ENGAÑO",
                "ATAQUES CIBERNÉTICOS BÁSICOS",
                "DISPOSITIVOS MÓVILES E INALÁMBRICOS",
                "PRINCIPIOS DE LA CIBERSEGURIDAD",
                "EVALUACIÓN FINAL"
            ]
            
            titulos_actuales = [l[1] for l in lecciones_curso_1]
            
            for i, esperada in enumerate(lecciones_esperadas, 1):
                encontrada = any(esperada.lower() in titulo.lower() for titulo in titulos_actuales)
                status = "✅" if encontrada else "❌"
                print(f"{status} Lección {i}: {esperada}")
        else:
            print("❌ NO SE ENCONTRARON LECCIONES PARA EL CURSO 1")
    else:
        print("❌ NO SE ENCONTRÓ EL CURSO 1 (Fundamentos de Ciberseguridad)")
    
    # ========== 3. VERIFICAR CURSOS 2-5 ==========
    print("\n\n📚 PASO 3: CURSOS 2-5")
    print("-" * 80)
    
    cursos_esperados = [
        (2, "Seguridad de Redes y Comunicaciones"),
        (3, "Seguridad de Sistemas Operativos"),
        (4, "Ciberseguridad Avanzada y Cloud"),
        (5, "Operaciones de Ciberseguridad")
    ]
    
    for num_curso, titulo_esperado in cursos_esperados:
        cursor.execute("""
            SELECT id, title 
            FROM courses 
            WHERE id = %s OR title ILIKE %s
            LIMIT 1
        """, (num_curso, f"%{titulo_esperado.split()[0]}%"))
        
        curso = cursor.fetchone()
        
        if curso:
            print(f"\n✅ CURSO {num_curso}: {curso[1]}")
            
            # Contar lecciones
            cursor.execute("""
                SELECT COUNT(*) 
                FROM lessons 
                WHERE course_id = %s
            """, (curso[0],))
            
            num_lecciones = cursor.fetchone()[0]
            print(f"   📖 Lecciones: {num_lecciones}")
        else:
            print(f"\n❌ CURSO {num_curso} NO ENCONTRADO: '{titulo_esperado}'")
    
    # ========== 4. DETECTAR CURSOS EXTRA ==========
    print("\n\n⚠️  PASO 4: DETECTAR CURSOS NO ESPERADOS")
    print("-" * 80)
    
    cursor.execute("""
        SELECT id, title 
        FROM courses 
        WHERE id > 5
        ORDER BY id
    """)
    
    cursos_extra = cursor.fetchall()
    
    if cursos_extra:
        print(f"\n❌ ENCONTRADOS {len(cursos_extra)} CURSOS EXTRA (deberían eliminarse):\n")
        for curso in cursos_extra:
            print(f"   ID {curso[0]}: {curso[1]}")
    else:
        print("\n✅ NO HAY CURSOS EXTRA")
    
    # ========== 5. VERIFICAR TABLAS DUPLICADAS ==========
    print("\n\n🔍 PASO 5: DETECTAR TABLAS DUPLICADAS O INCORRECTAS")
    print("-" * 80)
    
    cursor.execute("""
        SELECT table_name 
        FROM information_schema.tables 
        WHERE table_schema = 'public'
        ORDER BY table_name
    """)
    
    tablas = [row[0] for row in cursor.fetchall()]
    
    print(f"\n📊 Tablas en la base de datos ({len(tablas)}):\n")
    
    tablas_esperadas = ['courses', 'lessons', 'users', 'badges', 'user_badges', 'activities']
    tablas_extra = [t for t in tablas if t not in tablas_esperadas]
    
    for tabla in tablas:
        if tabla in tablas_esperadas:
            print(f"   ✅ {tabla}")
        else:
            print(f"   ⚠️  {tabla} (tabla no estándar)")
    
    if tablas_extra:
        print(f"\n⚠️  Tablas no esperadas encontradas: {tablas_extra}")
    
    # ========== 6. RESUMEN FINAL ==========
    print("\n\n" + "=" * 80)
    print("📊 RESUMEN FINAL")
    print("=" * 80)
    
    cursor.execute("SELECT COUNT(*) FROM courses")
    total_cursos = cursor.fetchone()[0]
    
    cursor.execute("SELECT COUNT(*) FROM lessons")
    total_lecciones = cursor.fetchone()[0]
    
    cursor.execute("SELECT COUNT(*) FROM courses WHERE id <= 5")
    cursos_validos = cursor.fetchone()[0]
    
    print(f"\n✅ Cursos totales: {total_cursos}")
    print(f"✅ Cursos válidos (1-5): {cursos_validos}")
    print(f"❌ Cursos extra (>5): {total_cursos - cursos_validos}")
    print(f"📖 Lecciones totales: {total_lecciones}")
    
    # Recomendaciones
    print("\n\n💡 RECOMENDACIONES:")
    print("-" * 80)
    
    if total_cursos > 5:
        print("❌ ACCIÓN REQUERIDA: Eliminar cursos con ID > 5")
    
    if cursos_validos < 5:
        print("⚠️  ACCIÓN REQUERIDA: Faltan cursos (deberían ser 5)")
    
    if curso_1 and len(lecciones_curso_1) != 6:
        print(f"⚠️  ACCIÓN REQUERIDA: Curso 1 debería tener 6 lecciones (tiene {len(lecciones_curso_1)})")
    
    if total_cursos == 5 and cursos_validos == 5:
        if curso_1 and len(lecciones_curso_1) == 6:
            print("✅ ¡ESTRUCTURA CORRECTA! No se requieren cambios.")
    
    cursor.close()
    conn.close()
    
    print("\n" + "=" * 80)
    print("🏁 VERIFICACIÓN COMPLETADA")
    print("=" * 80)

if __name__ == "__main__":
    try:
        verify_course_structure()
    except Exception as e:
        print(f"\n💥 ERROR: {e}")
        import traceback
        traceback.print_exc()
