#!/usr/bin/env python3
# test_streak_service.py

from services.streak_service import StreakService

def test_streak_service():
    print("🧪 PROBANDO STREAK SERVICE")
    print("=" * 40)
    
    try:
        # Probar con usuario de prueba
        test_user_id = 2
        
        print(f"🔍 Probando con usuario ID: {test_user_id}")
        
        # Probar get_current_streak
        streak = StreakService.get_current_streak(test_user_id)
        print(f"✅ Racha actual: {streak} días")
        
        # Probar get_streak_bonus con diferentes valores
        test_streaks = [0, 1, 2, 3, 7, 10]
        print(f"\n💰 Probando bonus de racha:")
        for test_streak in test_streaks:
            bonus = StreakService.get_streak_bonus(test_streak)
            print(f"   {test_streak} días → {bonus} XP bonus")
        
        print(f"\n🎉 StreakService funciona correctamente")
        print(f"💡 Usuario {test_user_id} tiene racha de {streak} días y bonus de {StreakService.get_streak_bonus(streak)} XP")
        
        return True
        
    except Exception as e:
        print(f"❌ Error en StreakService: {e}")
        import traceback
        traceback.print_exc()
        return False

if __name__ == "__main__":
    success = test_streak_service()
    exit(0 if success else 1)