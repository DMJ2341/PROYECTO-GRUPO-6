package com.example.cyberlearnapp.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

// ==========================================
// 📦 MODELO BASE DE LA LECCIÓN
// ==========================================

@Serializable
data class LessonDetailResponse(
    val id: String,
    val title: String,
    val description: String,
    val type: String, // "interactive", "crisis_simulator", etc.
    @SerialName("duration_minutes") val durationMinutes: Int,
    @SerialName("xp_reward") val xpReward: Int,
    @SerialName("total_screens") val totalScreens: Int,
    val theme: LessonTheme? = null,
    val screens: List<LessonScreen> // Lista polimórfica
)

@Serializable
data class LessonTheme(
    @SerialName("primary_color") val primaryColor: String,
    @SerialName("secondary_color") val secondaryColor: String,
    @SerialName("accent_color") val accentColor: String,
    val gradient: List<String>
)

// ==========================================
// 🎮 CLASES SELLADAS (POLIMORFISMO)
// ==========================================

// Configuración automática: el campo "type" en el JSON decide qué subclase usar
@Serializable
sealed class LessonScreen {
    abstract val screenId: Int
    abstract val title: String
}

@Serializable
@SerialName("story_intro")
data class StoryIntro(
    @SerialName("screen_id") override val screenId: Int,
    override val title: String,
    val content: StoryIntroContent
) : LessonScreen()

@Serializable
@SerialName("story_slide")
data class StorySlide(
    @SerialName("screen_id") override val screenId: Int,
    override val title: String,
    val content: StorySlideContent
) : LessonScreen()

@Serializable
@SerialName("crisis_timeline")
data class CrisisTimeline(
    @SerialName("screen_id") override val screenId: Int,
    override val title: String,
    val content: CrisisTimelineContent
) : LessonScreen()

@Serializable
@SerialName("crisis_decision")
data class CrisisDecision(
    @SerialName("screen_id") override val screenId: Int,
    override val title: String,
    val content: CrisisDecisionContent
) : LessonScreen()

@Serializable
@SerialName("flip_card_full")
data class FlipCard(
    @SerialName("screen_id") override val screenId: Int,
    override val title: String,
    val content: FlipCardContent
) : LessonScreen()

@Serializable
@SerialName("swipe_cards")
data class SwipeCards(
    @SerialName("screen_id") override val screenId: Int,
    override val title: String,
    val content: SwipeCardsContent
) : LessonScreen()

@Serializable
@SerialName("drag_drop_match")
data class DragDropMatch(
    @SerialName("screen_id") override val screenId: Int,
    override val title: String,
    val content: DragDropMatchContent
) : LessonScreen()

@Serializable
@SerialName("drag_drop_builder")
data class DragDropBuilder(
    @SerialName("screen_id") override val screenId: Int,
    override val title: String,
    val content: DragDropBuilderContent
) : LessonScreen()

@Serializable
@SerialName("log_hunter")
data class LogHunter(
    @SerialName("screen_id") override val screenId: Int,
    override val title: String,
    val content: LogHunterContent
) : LessonScreen()

@Serializable
@SerialName("code_practice")
data class CodePractice(
    @SerialName("screen_id") override val screenId: Int,
    override val title: String,
    val content: CodePracticeContent
) : LessonScreen()

@Serializable
@SerialName("budget_allocation")
data class BudgetAllocation(
    @SerialName("screen_id") override val screenId: Int,
    override val title: String,
    val content: BudgetAllocationContent
) : LessonScreen()

@Serializable
@SerialName("evidence_lab")
data class EvidenceLab(
    @SerialName("screen_id") override val screenId: Int,
    override val title: String,
    val content: EvidenceLabContent
) : LessonScreen()

@Serializable
@SerialName("slider_balance")
data class SliderBalance(
    @SerialName("screen_id") override val screenId: Int,
    override val title: String,
    val content: SliderBalanceContent
) : LessonScreen()

@Serializable
@SerialName("timed_quiz")
data class TimedQuiz(
    @SerialName("screen_id") override val screenId: Int,
    override val title: String,
    val content: TimedQuizContent
) : LessonScreen()

@Serializable
@SerialName("achievement_unlock")
data class AchievementUnlock(
    @SerialName("screen_id") override val screenId: Int,
    override val title: String,
    val content: AchievementUnlockContent
) : LessonScreen()

@Serializable
@SerialName("lesson_complete")
data class LessonComplete(
    @SerialName("screen_id") override val screenId: Int,
    override val title: String,
    val content: LessonCompleteContent
) : LessonScreen()

@Serializable
@SerialName("summary_stats")
data class SummaryStats(
    @SerialName("screen_id") override val screenId: Int,
    override val title: String,
    val content: SummaryStatsContent
) : LessonScreen()

// Fallback para tipos desconocidos (útil para desarrollo)
@Serializable
@SerialName("unknown")
data class UnknownScreen(
    @SerialName("screen_id") override val screenId: Int,
    override val title: String = "Desconocido",
    val content: JsonObject? = null
) : LessonScreen()


// ==========================================
// 📝 CONTENIDOS ESPECÍFICOS (DATA CLASSES)
// ==========================================

@Serializable
data class StoryIntroContent(
    val headline: String = "",
    val subheadline: String = "",
    @SerialName("background_image") val backgroundImage: String = "",
    val stats: List<StatItem> = emptyList(),
    @SerialName("cta_text") val ctaText: String = "Continuar"
)
@Serializable
data class StatItem(val value: String, val label: String)

@Serializable
data class StorySlideContent(
    val icon: String = "info",
    @SerialName("icon_color") val iconColor: String? = null,
    val headline: String = "",
    val body: String = "",
    @SerialName("highlight_box") val highlightBox: HighlightBox? = null
)
@Serializable
data class HighlightBox(val type: String, val text: String)

@Serializable
data class CrisisTimelineContent(val events: List<TimelineEvent> = emptyList())
@Serializable
data class TimelineEvent(val time: String, val icon: String, val title: String, val description: String, val severity: String = "info")

@Serializable
data class CrisisDecisionContent(
    val scenario: String,
    @SerialName("timer_seconds") val timerSeconds: Int = 30,
    val options: List<DecisionOption> = emptyList()
)
@Serializable
data class DecisionOption(
    val id: String, val text: String, val icon: String,
    @SerialName("is_correct") val isCorrect: Boolean,
    val consequence: DecisionConsequence
)
@Serializable
data class DecisionConsequence(val title: String, val description: String, @SerialName("impact_score") val impactScore: Int)

@Serializable
data class FlipCardContent(val card: CardSide)
@Serializable
data class CardSide(val front: CardFace, val back: CardBack)
@Serializable
data class CardFace(val icon: String, val term: String, val subtitle: String? = null, val color: String)
@Serializable
data class CardBack(val definition: String? = null, val color: String)

@Serializable
data class SwipeCardsContent(
    val instruction: String,
    val cards: List<SwipeCardItem>,
    val labels: Map<String, String> = emptyMap()
)
@Serializable
data class SwipeCardItem(val id: Int, val scenario: String, @SerialName("correct_answer") val correctAnswer: String)

@Serializable
data class DragDropMatchContent(
    val instruction: String,
    val scenarios: List<MatchScenario>,
    val targets: List<MatchTarget>
)
@Serializable
data class MatchScenario(val id: Int, val text: String, @SerialName("correct_target") val correctTarget: String)
@Serializable
data class MatchTarget(val id: String, val label: String, val color: String, val icon: String? = null)

@Serializable
data class DragDropBuilderContent(
    val instruction: String,
    @SerialName("available_components") val availableComponents: List<BuilderComponent>,
    @SerialName("success_criteria") val successCriteria: String
)
@Serializable
data class BuilderComponent(val id: String, val name: String, val layer: String)

@Serializable
data class LogHunterContent(
    val instruction: String? = null,
    val scenario: String? = null,
    @SerialName("log_lines") val logLines: List<LogLine>? = null,
    val logs: List<LogLine>? = null
) {
    // Propiedad computada (ignorada por serialización) para unificar logs
    val allLogs: List<LogLine>
        get() = logLines ?: logs ?: emptyList()
}
@Serializable
data class LogLine(
    val id: Int,
    val text: String,
    @SerialName("is_suspicious") val isSuspicious: Boolean = false,
    @SerialName("suspicious") val suspicious: Boolean = false,
    val reason: String? = null
) {
    val isSuspiciousCombined: Boolean get() = isSuspicious || suspicious
}

@Serializable
data class CodePracticeContent(
    val instruction: String,
    @SerialName("code_snippets") val codeSnippets: List<CodeSnippet>
)
@Serializable
data class CodeSnippet(val id: Int, val language: String, val code: String, @SerialName("is_vulnerable") val isVulnerable: Boolean, val explanation: String? = null)

@Serializable
data class BudgetAllocationContent(
    @SerialName("total_budget") val totalBudget: Int,
    val categories: List<BudgetCategory>,
    @SerialName("optimal_allocation") val optimalAllocation: List<BudgetSelection>
)
@Serializable
data class BudgetCategory(val id: String, val name: String, val cost: Int, val mandatory: Boolean, val impact: String)
@Serializable
data class BudgetSelection(val id: String, val selected: Boolean)

@Serializable
data class EvidenceLabContent(
    val instruction: String,
    @SerialName("evidence_items") val evidenceItems: List<EvidenceItem>,
    @SerialName("drop_zones") val dropZones: List<DropZone>
)
@Serializable
data class EvidenceItem(val id: String, val name: String, val icon: String, @SerialName("correct_order") val correctOrder: Int)
@Serializable
data class DropZone(val id: String? = null, val name: String? = null, val description: String? = null, val order: Int? = null, val label: String? = null)

@Serializable
data class SliderBalanceContent(
    val scenario: String,
    @SerialName("left_option") val leftOption: SliderOption,
    @SerialName("right_option") val rightOption: SliderOption,
    val explanation: String
)
@Serializable
data class SliderOption(val label: String, val icon: String, val consequences: List<String>)

@Serializable
data class TimedQuizContent(
    @SerialName("timer_seconds") val timerSeconds: Int,
    val question: String,
    val options: List<QuizOption>,
    val explanation: String
)
@Serializable
data class QuizOption(val id: String, val text: String, @SerialName("is_correct") val isCorrect: Boolean)

@Serializable
data class AchievementUnlockContent(val achievement: AchievementItem)
@Serializable
data class AchievementItem(val icon: String, val name: String, val description: String, val rarity: String, val color: String)

@Serializable
data class LessonCompleteContent(
    @SerialName("xp_earned") val xpEarned: Int,
    @SerialName("key_takeaways") val keyTakeaways: List<String>,
    @SerialName("next_lesson") val nextLesson: NextLessonItem? = null
)
@Serializable
data class NextLessonItem(val id: String, val title: String, val preview: String)

@Serializable
data class SummaryStatsContent(
    val stats: List<StatDetail>,
    @SerialName("key_insight") val keyInsight: String? = null,
    @SerialName("root_causes") val rootCauses: List<String>? = null,
    val lessons: List<String>? = null
)
@Serializable
data class StatDetail(val icon: String, val value: String, val label: String, val color: String? = null)