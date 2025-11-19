#!/usr/bin/env python3
# test_dashboard_final.py

from database.db import db, Session
from sqlalchemy import text
from services.activity_service import ActivityService
from services.streak_service import StreakService

def test_dashboard_final():
    print("🧪 TEST FINAL - DASHBOARD ENDPOINT")
    print("=" * 45)
    
    try:
        session = Session()
        
        # Usuario de prueba
        test_user_id = 2
        
        print(f"🔍 Probando dashboard para usuario ID: {test_user_id}")
        
        # 1. Verificar que el usuario existe
        result = session.execute(text(f"SELECT email, name FROM users WHERE id = {test_user_id}"))
        user = result.fetchone()
        if not user:
            print("❌ Usuario no encontrado")
            return False
        print(f"✅ Usuario: {user[0]} ({user[1]})")
        
        # 2. Probar ActivityService
        print("\n📊 PROBANDO ACTIVITY SERVICE...")
        try:
            activity_service = ActivityService()
            total_xp = activity_service.get_total_xp(test_user_id)
            print(f"   ✅ Total XP: {total_xp}")
        except Exception as e:
            print(f"   ❌ ActivityService error: {e}")
            return False
        
        # 3. Probar StreakService
        print("\n🔥 PROBANDO STREAK SERVICE...")
        try:
            streak_service = StreakService()
            current_streak = streak_service.get_current_streak(test_user_id)
            streak_bonus = streak_service.get_streak_bonus(current_streak)
            print(f"   ✅ Current Streak: {current_streak} días")
            print(f"   ✅ Streak Bonus: {streak_bonus} XP")
        except Exception as e:
            print(f"   ❌ StreakService error: {e}")
            return False
        
        # 4. Probar conteo de badges
        print("\n🛡️ PROBANDO BADGES COUNT...")
        try:
            result = session.execute(text(f"SELECT COUNT(*) FROM user_badges WHERE user_id = {test_user_id}"))
            badges_count = result.scalar()
            print(f"   ✅ Badges Count: {badges_count}")
        except Exception as e:
            print(f"   ❌ Badges count error: {e}")
            return False
        
        # 5. Probar cursos progreso
        print("\n📚 PROBANDO CURSOS PROGRESO...")
        try:
            result = session.execute(text("""
                SELECT c.id, c.title, 
                       (SELECT COUNT(*) FROM lessons l WHERE l.course_id = c.id) as total_lessons,
                       (SELECT COUNT(*) FROM activities a 
                        WHERE a.user_id = 2 AND a.activity_type = 'lesson_completed') as completed_lessons
                FROM courses c
                LIMIT 3
            """))
            
            courses_data = []
            for row in result:
                courses_data.append({
                    'course_id': row[0],
                    'course_title': row[1],
                    'total_lessons': row[2],
                    'completed_lessons': row[3]
                })
                print(f"   ✅ Curso: {row[1]}, Lecciones: {row[3]}/{row[2]}")
                
        except Exception as e:
            print(f"   ❌ Cursos progreso error: {e}")
            return False
        
        session.close()
        
        print(f"\n🎉 DASHBOARD TEST COMPLETADO EXITOSAMENTE")
        print(f"📈 Resumen para usuario {test_user_id}:")
        print(f"   - Total XP: {total_xp}")
        print(f"   - Racha: {current_streak} días (+{streak_bonus} XP bonus)")
        print(f"   - Badges: {badges_count}")
        print(f"   - Cursos con progreso: {len(courses_data)}")
        
        return True
        
    except Exception as e:
        print(f"❌ ERROR GENERAL: {e}")
        import traceback
        traceback.print_exc()
        return False

if __name__ == "__main__":
    success = test_dashboard_final()
    exit(0 if success else 1)