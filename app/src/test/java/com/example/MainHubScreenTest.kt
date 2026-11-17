package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import com.example.ui.screens.MainHubScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.QuizViewModel
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class MainHubScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreenDisplaysSubjectGridAndNavigatesToSelectedSubject() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = QuizViewModel(app)
        val navigatedSubjects = mutableListOf<String>()

        composeTestRule.setContent {
            MyApplicationTheme {
                MainHubScreen(
                    viewModel = viewModel,
                    onNavigateToQuiz = { navigatedSubjects.add(it) },
                    onNavigateToDailyChallenge = {},
                    onNavigateToDashboard = {},
                    onNavigateToProfile = {},
                    onNavigateToRewards = {},
                    onNavigateToDragDrop = {},
                    onNavigateToMemoryGame = {},
                    onNavigateToStory = {}
                )
            }
        }

        // Verify Math, English, Science, Arabic subject cards are displayed
        composeTestRule.onNodeWithTag("subject_card_math").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("subject_card_english").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("subject_card_science").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("subject_card_arabic").performScrollTo().assertIsDisplayed()

        composeTestRule.onNodeWithText("Math").assertIsDisplayed()
        composeTestRule.onNodeWithText("English").assertIsDisplayed()
        composeTestRule.onNodeWithText("Science").assertIsDisplayed()
        composeTestRule.onNodeWithText("Arabic").assertIsDisplayed()

        // Tap Science, Math, English, and Arabic cards and verify navigation callbacks
        composeTestRule.onNodeWithTag("subject_card_science").performScrollTo().performClick()
        composeTestRule.onNodeWithTag("subject_card_math").performScrollTo().performClick()
        composeTestRule.onNodeWithTag("subject_card_english").performScrollTo().performClick()
        composeTestRule.onNodeWithTag("subject_card_arabic").performScrollTo().performClick()

        assertEquals(
            listOf("Science", "Mathematics", "English", "اللغة العربية"),
            navigatedSubjects
        )
    }
}
