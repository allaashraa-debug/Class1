package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.KidsBlue
import com.example.ui.theme.KidsGreen
import com.example.ui.theme.KidsOrange
import com.example.ui.theme.KidsPurple
import com.example.ui.theme.KidsTeal
import com.example.ui.theme.KidsYellow

data class AchievementBadge(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val descriptionAr: String,
    val emoji: String,
    val accentColor: Color,
    val isUnlocked: Boolean,
    val progressText: String
)

data class HeroRank(
    val titleAr: String,
    val titleEn: String,
    val emoji: String,
    val minStars: Int,
    val nextRankStars: Int
)

object RewardsCalculator {
    fun getHeroRank(totalStars: Int): HeroRank {
        return when {
            totalStars >= 120 -> HeroRank("أسطورة المعرفة", "Knowledge Legend", "👑", 120, 200)
            totalStars >= 70 -> HeroRank("عبقري الصف الأول", "Grade 1 Genius", "🚀", 70, 120)
            totalStars >= 35 -> HeroRank("مستكشف ذكي", "Smart Explorer", "🦉", 35, 70)
            totalStars >= 10 -> HeroRank("نجم صاعد", "Rising Star", "🌟", 10, 35)
            else -> HeroRank("بطل البداية", "Beginner Hero", "🌱", 0, 10)
        }
    }

    fun calculateBadges(attempts: List<Attempt>, streakDays: Int): List<AchievementBadge> {
        val totalStars = attempts.sumOf { it.starsEarned }
        val totalAttempts = attempts.size
        val perfectScores = attempts.count { it.totalQuestions > 0 && it.score == it.totalQuestions }
        val subjectsPlayed = attempts.map { it.subject }.distinct().size
        val mathWins = attempts.count { it.subject == "Mathematics" && it.score >= 3 }
        val englishWins = attempts.count { it.subject == "English" && it.score >= 3 }
        val arabicWins = attempts.count { it.subject == "اللغة العربية" && it.score >= 3 }
        val gamesPlayed = attempts.count {
            it.subject.contains("الذاكرة") || it.subject.contains("سحب") || it.subject.contains("القصة")
        }

        return listOf(
            AchievementBadge(
                id = "first_step",
                titleAr = "خطوة البطل الأولى",
                titleEn = "First Step",
                descriptionAr = "أكمل أول تحدٍ أو لعبة بنجاح",
                emoji = "🌟",
                accentColor = KidsYellow,
                isUnlocked = totalAttempts >= 1,
                progressText = "${totalAttempts.coerceAtMost(1)} / 1"
            ),
            AchievementBadge(
                id = "perfect_crown",
                titleAr = "التاج الذهبي",
                titleEn = "Perfect Score",
                descriptionAr = "احصل على العلامة الكاملة في أي اختبار",
                emoji = "👑",
                accentColor = KidsOrange,
                isUnlocked = perfectScores >= 1,
                progressText = "${perfectScores.coerceAtMost(1)} / 1"
            ),
            AchievementBadge(
                id = "math_whiz",
                titleAr = "عبقري الرياضيات",
                titleEn = "Math Whiz",
                descriptionAr = "اجتز اختبار الرياضيات بنجاح (٣ إجابات فأكثر)",
                emoji = "🧮",
                accentColor = KidsGreen,
                isUnlocked = mathWins >= 1,
                progressText = "${mathWins.coerceAtMost(1)} / 1"
            ),
            AchievementBadge(
                id = "english_star",
                titleAr = "نجم الإنجليزية",
                titleEn = "English Star",
                descriptionAr = "اجتز اختبار اللغة الإنجليزية بنجاح",
                emoji = "🔤",
                accentColor = KidsBlue,
                isUnlocked = englishWins >= 1,
                progressText = "${englishWins.coerceAtMost(1)} / 1"
            ),
            AchievementBadge(
                id = "arabic_knight",
                titleAr = "فارس الضاد",
                titleEn = "Arabic Knight",
                descriptionAr = "اجتز اختبار اللغة العربية بنجاح",
                emoji = "🕌",
                accentColor = KidsPurple,
                isUnlocked = arabicWins >= 1,
                progressText = "${arabicWins.coerceAtMost(1)} / 1"
            ),
            AchievementBadge(
                id = "game_explorer",
                titleAr = "مستكشف الألعاب",
                titleEn = "Game Explorer",
                descriptionAr = "أكمل ٣ أنشطة من الألعاب أو القصص التفاعلية",
                emoji = "🎮",
                accentColor = KidsTeal,
                isUnlocked = gamesPlayed >= 3,
                progressText = "${gamesPlayed.coerceAtMost(3)} / 3"
            ),
            AchievementBadge(
                id = "star_collector",
                titleAr = "جامع النجوم",
                titleEn = "Star Collector",
                descriptionAr = "اجمع ٥٠ نجمة ذهبية في رصيدك",
                emoji = "🏆",
                accentColor = KidsOrange,
                isUnlocked = totalStars >= 50,
                progressText = "${totalStars.coerceAtMost(50)} / 50"
            ),
            AchievementBadge(
                id = "all_rounder",
                titleAr = "البطل الشامل",
                titleEn = "All-Rounder",
                descriptionAr = "جرب ٤ مواد أو أنشطة مختلفة على الأقل",
                emoji = "🌈",
                accentColor = KidsBlue,
                isUnlocked = subjectsPlayed >= 4 || streakDays >= 3,
                progressText = "${subjectsPlayed.coerceAtMost(4)} / 4"
            )
        )
    }
}
