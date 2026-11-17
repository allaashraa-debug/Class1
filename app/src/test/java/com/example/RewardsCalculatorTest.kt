package com.example

import com.example.data.model.Attempt
import com.example.data.model.RewardsCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RewardsCalculatorTest {

    @Test
    fun heroRankScalesWithStars() {
        assertEquals("Beginner Hero", RewardsCalculator.getHeroRank(0).titleEn)
        assertEquals("Rising Star", RewardsCalculator.getHeroRank(15).titleEn)
        assertEquals("Smart Explorer", RewardsCalculator.getHeroRank(40).titleEn)
        assertEquals("Grade 1 Genius", RewardsCalculator.getHeroRank(75).titleEn)
        assertEquals("Knowledge Legend", RewardsCalculator.getHeroRank(130).titleEn)
    }

    @Test
    fun badgesUnlockFromAttempts() {
        val emptyBadges = RewardsCalculator.calculateBadges(emptyList(), streakDays = 1)
        assertTrue(emptyBadges.all { !it.isUnlocked })

        val attempts = listOf(
            Attempt(
                studentName = "Adam",
                subject = "Mathematics",
                score = 5,
                totalQuestions = 5,
                starsEarned = 20,
                wrongQuestionsCount = 0,
                dateString = "2026-11-16 10:00"
            ),
            Attempt(
                studentName = "Adam",
                subject = "English",
                score = 4,
                totalQuestions = 5,
                starsEarned = 15,
                wrongQuestionsCount = 1,
                dateString = "2026-11-16 10:15"
            ),
            Attempt(
                studentName = "Adam",
                subject = "اللغة العربية",
                score = 4,
                totalQuestions = 5,
                starsEarned = 16,
                wrongQuestionsCount = 1,
                dateString = "2026-11-16 10:30"
            )
        )

        val badges = RewardsCalculator.calculateBadges(attempts, streakDays = 1)
        val unlockedIds = badges.filter { it.isUnlocked }.map { it.id }.toSet()

        assertTrue("first_step" in unlockedIds)
        assertTrue("perfect_crown" in unlockedIds)
        assertTrue("math_whiz" in unlockedIds)
        assertTrue("english_star" in unlockedIds)
        assertTrue("arabic_knight" in unlockedIds)
        assertTrue("star_collector" in unlockedIds)
        assertFalse("all_rounder" in unlockedIds)
    }
}
