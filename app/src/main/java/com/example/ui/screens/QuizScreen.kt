package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Question
import com.example.ui.theme.KidsBlue
import com.example.ui.theme.KidsGreen
import com.example.ui.theme.KidsOrange
import com.example.ui.theme.KidsPurple
import com.example.ui.theme.KidsRed
import com.example.ui.theme.KidsTeal
import com.example.ui.theme.KidsYellow
import com.example.ui.viewmodel.QuizViewModel

@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    onNavigateBack: () -> Unit
) {
    val activeQuestions by viewModel.activeQuestions.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    val score by viewModel.score.collectAsState()
    val starsCount by viewModel.starsCount.collectAsState()
    val comboStreak by viewModel.comboStreak.collectAsState()
    val quizFinished by viewModel.quizFinished.collectAsState()
    val selectedSubject by viewModel.selectedSubject.collectAsState()
    val isDailyChallenge by viewModel.isDailyChallenge.collectAsState()

    val showFeedbackDialog by viewModel.showFeedbackDialog.collectAsState()
    val isCurrentAnswerCorrect by viewModel.isCurrentAnswerCorrect.collectAsState()
    val currentExplanation by viewModel.currentExplanation.collectAsState()
    val currentCorrectAnswer by viewModel.currentCorrectAnswer.collectAsState()
    val hintUsed by viewModel.hintUsed.collectAsState()

    val isSubjectEnglish = selectedSubject != "اللغة العربية" && selectedSubject != "Arabic"

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopAudio()
        }
    }

    if (activeQuestions.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isSubjectEnglish) "Loading quiz adventure questions... 🌟" else "جاري تحميل أسئلة المغامرة المشوقة... 🌟",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        return
    }

    if (quizFinished) {
        QuizSummaryScreen(
            score = score,
            total = activeQuestions.size,
            stars = starsCount,
            subject = selectedSubject ?: "",
            isEnglish = isSubjectEnglish,
            isDailyChallenge = isDailyChallenge,
            onRetry = {
                viewModel.playClickSound()
                if (isDailyChallenge) {
                    viewModel.startDailyChallenge()
                } else {
                    viewModel.startQuiz(selectedSubject ?: "")
                }
            },
            onHome = {
                viewModel.playClickSound()
                onNavigateBack()
            }
        )
        return
    }

    val question = activeQuestions[currentIndex]
    val isQuestionEnglish = question.subject != "اللغة العربية" && question.subject != "Arabic"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Core header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable {
                        viewModel.playClickSound()
                        viewModel.stopAudio()
                        onNavigateBack()
                    }
                    .testTag("exit_quiz_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = KidsBlue,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Combo Streak Pill
            AnimatedVisibility(visible = comboStreak >= 2, enter = scaleIn() + fadeIn()) {
                Surface(
                    color = KidsRed.copy(alpha = 0.14f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = if (isQuestionEnglish) "🔥 ${comboStreak}x Combo!" else "🔥 $comboStreak متتالية!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = KidsRed,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            // Stars tracker
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(KidsYellow.copy(alpha = 0.18f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = KidsOrange,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isSubjectEnglish) "$starsCount Stars" else "$starsCount نجمة",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = KidsOrange
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Animated Progress bar with sliding star
        val targetProgress = (currentIndex + 1).toFloat() / activeQuestions.size.toFloat()
        val animatedProgress by animateFloatAsState(
            targetValue = targetProgress,
            animationSpec = spring(),
            label = "quizProgress"
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .padding(horizontal = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .align(Alignment.CenterStart)
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = 0.3f))
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress.coerceIn(0.05f, 1f))
                    .height(10.dp)
                    .align(Alignment.CenterStart)
                    .clip(CircleShape)
                    .background(KidsBlue)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress.coerceIn(0.08f, 0.98f))
                    .align(Alignment.CenterStart)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(KidsYellow)
                        .align(Alignment.CenterEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${question.subject} • ${question.unit}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = if (isQuestionEnglish) "Question ${currentIndex + 1} / ${activeQuestions.size}" else "السؤال ${currentIndex + 1} من ${activeQuestions.size}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = KidsBlue
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Large question display card
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Sound TTS Button, Representing Emoji, and Helper Hint Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(KidsBlue.copy(alpha = 0.15f))
                            .clickable { viewModel.playQuestionAudio() }
                            .testTag("speaker_sound_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Read out",
                            tint = KidsBlue,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(KidsOrange.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = question.hintImage,
                            fontSize = 42.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    val canUseHint = !hintUsed && question.questionType in listOf("MULTIPLE_CHOICE", "FILL_BLANK", "ARRANGE_LETTERS")
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                if (canUseHint) KidsYellow.copy(alpha = 0.25f)
                                else Color.LightGray.copy(alpha = 0.2f)
                            )
                            .clickable(enabled = canUseHint) { viewModel.useHint() }
                            .testTag("hint_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Hint",
                            tint = if (canUseHint) KidsOrange else Color.Gray.copy(alpha = 0.5f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // The Question Text
                Text(
                    text = question.questionText,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    lineHeight = 31.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                AnsweringWidget(
                    question = question,
                    viewModel = viewModel,
                    isEnglish = isQuestionEnglish
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Main action Submit button
        val isButtonEnabled = isAnsweringValid(question, viewModel)
        Button(
            onClick = { viewModel.submitAnswer() },
            enabled = isButtonEnabled,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = KidsOrange,
                disabledContainerColor = Color.LightGray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .testTag("submit_answer_button")
        ) {
            Text(
                text = if (isQuestionEnglish) "Check My Answer ✔️" else "تحقق من إجابتي الذكية ✔️",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }

    if (showFeedbackDialog) {
        FeedbackPopup(
            isCorrect = isCurrentAnswerCorrect,
            explanation = currentExplanation,
            correctAnswer = currentCorrectAnswer,
            isEnglish = isQuestionEnglish,
            onGoNext = {
                viewModel.playClickSound()
                viewModel.dismissFeedbackAndGoNext()
            }
        )
    }
}

@Composable
private fun isAnsweringValid(question: Question, viewModel: QuizViewModel): Boolean {
    val selectedOption by viewModel.selectedOption.collectAsState()
    val arrangedLetters by viewModel.arrangedLetters.collectAsState()
    val matchedPairs by viewModel.matchedPairs.collectAsState()

    return when (question.questionType) {
        "ARRANGE_LETTERS" -> arrangedLetters.isNotEmpty()
        "MATCH_COLUMNS" -> {
            val pairs = question.options.split(";").filter { it.isNotEmpty() }
            matchedPairs.size == pairs.size
        }
        else -> selectedOption != null
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AnsweringWidget(
    question: Question,
    viewModel: QuizViewModel,
    isEnglish: Boolean = false
) {
    val selectedOption by viewModel.selectedOption.collectAsState()
    val eliminatedOption by viewModel.eliminatedOption.collectAsState()
    val scrambledLetters by viewModel.scrambledLetters.collectAsState()
    val arrangedLetters by viewModel.arrangedLetters.collectAsState()

    val leftMatchItems by viewModel.leftMatchItems.collectAsState()
    val rightMatchItems by viewModel.rightMatchItems.collectAsState()
    val matchedPairs by viewModel.matchedPairs.collectAsState()
    val selectedLeftItem by viewModel.selectedLeftItem.collectAsState()
    val selectedRightItem by viewModel.selectedRightItem.collectAsState()

    when (question.questionType) {
        "MULTIPLE_CHOICE", "FILL_BLANK" -> {
            val options = question.options.split(";").filter { it.isNotEmpty() }
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                options.forEachIndexed { index, option ->
                    val isSelected = selectedOption == option
                    val isEliminated = eliminatedOption == option
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                isEliminated -> Color.LightGray.copy(alpha = 0.25f)
                                isSelected -> KidsBlue
                                else -> MaterialTheme.colorScheme.surface
                            }
                        ),
                        border = BorderStroke(
                            width = if (isSelected) 4.dp else 2.dp,
                            color = when {
                                isEliminated -> Color.LightGray.copy(alpha = 0.5f)
                                isSelected -> KidsBlue
                                else -> KidsBlue.copy(alpha = 0.45f)
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(if (isEliminated) 0.45f else 1f)
                            .clickable(enabled = !isEliminated) {
                                viewModel.playClickSound()
                                viewModel.selectOption(option)
                            }
                            .testTag("option_$option")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) Color.White.copy(alpha = 0.25f) else KidsBlue.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                val numberText = if (isEnglish) "${index + 1}" else when (index) {
                                    0 -> "١"
                                    1 -> "٢"
                                    2 -> "٣"
                                    3 -> "٤"
                                    else -> "${index + 1}"
                                }
                                Text(
                                    text = numberText,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else KidsBlue
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Text(
                                text = option,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                textDecoration = if (isEliminated) TextDecoration.LineThrough else TextDecoration.None,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        "TRUE_FALSE" -> {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("صح", "True", "خطأ", "False").filter { opt ->
                    question.options.split(";").any { it.equals(opt, ignoreCase = true) }
                }.forEachIndexed { index, rawOpt ->
                    val isSelected = selectedOption == rawOpt
                    val isAffirmative = rawOpt == "صح" || rawOpt == "True"
                    val cardBg = if (isSelected) {
                        if (isAffirmative) KidsGreen else KidsRed
                    } else MaterialTheme.colorScheme.surface

                    val borderColor = if (isAffirmative) KidsGreen else KidsRed

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(
                            width = if (isSelected) 4.dp else 2.dp,
                            color = if (isSelected) cardBg else borderColor
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(115.dp)
                            .clickable {
                                viewModel.playClickSound()
                                viewModel.selectOption(rawOpt)
                            }
                            .testTag("option_$rawOpt")
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) Color.White.copy(alpha = 0.2f) else borderColor.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                val numberText = if (isEnglish) "${index + 1}" else if (index == 0) "١" else "٢"
                                Text(
                                    text = numberText,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else borderColor
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Icon(
                                imageVector = if (isAffirmative) Icons.Default.Check else Icons.Default.Close,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else borderColor,
                                modifier = Modifier.size(34.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isEnglish) rawOpt.uppercase() else if (isAffirmative) "صَـح" else "خَـطَـأ",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        "IMAGE_SELECTION" -> {
            val emojis = question.options.split(";").filter { it.isNotEmpty() }
            FlowRow(
                maxItemsInEachRow = 2,
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                emojis.forEachIndexed { index, emoji ->
                    val isSelected = selectedOption == emoji
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) KidsYellow.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(
                            width = if (isSelected) 4.dp else 2.dp,
                            color = if (isSelected) KidsYellow else KidsBlue.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .size(120.dp)
                            .clickable {
                                viewModel.playClickSound()
                                viewModel.selectOption(emoji)
                            }
                            .testTag("option_emoji_$emoji")
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            val numBadge = if (isEnglish) "${index + 1}" else when (index) {
                                0 -> "١"
                                1 -> "٢"
                                2 -> "٣"
                                3 -> "٤"
                                else -> "${index + 1}"
                            }
                            Box(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) KidsYellow else KidsBlue.copy(alpha = 0.15f))
                                    .align(Alignment.TopStart),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = numBadge,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.DarkGray else KidsBlue
                                )
                            }

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(text = emoji, fontSize = 52.sp)
                            }
                        }
                    }
                }
            }
        }

        "ARRANGE_LETTERS" -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isEnglish) "Your Answer:" else "كلمتي المجمعة:",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (arrangedLetters.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(64.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.LightGray.copy(alpha = 0.14f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isEnglish) "Tap tiles below in order!" else "اضغط على الحروف بالترتيب!",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    } else {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            arrangedLetters.forEach { letter ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = KidsBlue),
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Text(
                                            text = letter,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Undo last tile button
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(KidsOrange.copy(alpha = 0.12f))
                            .clickable(enabled = arrangedLetters.isNotEmpty()) {
                                viewModel.playClickSound()
                                viewModel.removeLastArrangedLetter()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Backspace,
                            contentDescription = "Undo",
                            tint = KidsOrange,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Reset button
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(KidsRed.copy(alpha = 0.12f))
                            .clickable {
                                viewModel.playClickSound()
                                viewModel.resetArrangedLetters()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset",
                            tint = KidsRed
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text = if (isEnglish) "Tap Tiles:" else "الحروف المبعثرة:",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    scrambledLetters.forEachIndexed { idx, letter ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(3.dp, KidsBlue),
                            modifier = Modifier
                                .size(56.dp)
                                .clickable {
                                    viewModel.playClickSound()
                                    viewModel.addArrangedLetter(letter)
                                }
                                .testTag("scrambled_letter_$idx")
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = letter,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = KidsBlue
                                )
                            }
                        }
                    }
                }
            }
        }

        "MATCH_COLUMNS" -> {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (isEnglish) "Tap one item on the left, then tap its match on the right!" else "اضغط على عنصر من العمود الأول ثم طابقه مع العمود الثاني!",
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    color = KidsOrange,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Left Column
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (isEnglish) "Items" else "العناصر",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = KidsBlue
                        )
                        leftMatchItems.forEach { left ->
                            val isSelected = selectedLeftItem == left
                            val isMatched = matchedPairs.containsKey(left)
                            val bg = when {
                                isSelected -> KidsOrange
                                isMatched -> KidsGreen.copy(alpha = 0.15f)
                                else -> MaterialTheme.colorScheme.surface
                            }
                            val outlineColor = when {
                                isSelected -> KidsOrange
                                isMatched -> KidsGreen
                                else -> KidsBlue.copy(alpha = 0.5f)
                            }

                            val idx = leftMatchItems.indexOf(left)
                            val indexText = if (isEnglish) "${idx + 1}" else when (idx) {
                                0 -> "١"
                                1 -> "٢"
                                2 -> "٣"
                                3 -> "٤"
                                else -> "${idx + 1}"
                            }

                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = bg),
                                border = BorderStroke(if (isSelected || isMatched) 3.dp else 2.dp, outlineColor),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.playClickSound()
                                        viewModel.selectLeftMatch(left)
                                    }
                                    .testTag("left_match_$left")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(26.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) Color.White.copy(alpha = 0.25f) else KidsBlue.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = indexText,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else KidsBlue
                                        )
                                    }
                                    Text(
                                        text = left,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Start,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    // Right Column
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (isEnglish) "Matches" else "المطابقة",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = KidsTeal
                        )
                        rightMatchItems.forEach { right ->
                            val isSelected = selectedRightItem == right
                            val isMatched = matchedPairs.containsValue(right)
                            val bg = when {
                                isSelected -> KidsOrange
                                isMatched -> KidsGreen.copy(alpha = 0.15f)
                                else -> MaterialTheme.colorScheme.surface
                            }
                            val outlineColor = when {
                                isSelected -> KidsOrange
                                isMatched -> KidsGreen
                                else -> KidsTeal.copy(alpha = 0.5f)
                            }

                            val idx = rightMatchItems.indexOf(right)
                            val letterText = if (isEnglish) ('A' + idx).toString() else when (idx) {
                                0 -> "أ"
                                1 -> "ب"
                                2 -> "ج"
                                3 -> "د"
                                else -> ('A' + idx).toString()
                            }

                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = bg),
                                border = BorderStroke(if (isSelected || isMatched) 3.dp else 2.dp, outlineColor),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.playClickSound()
                                        viewModel.selectRightMatch(right)
                                    }
                                    .testTag("right_match_$right")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(26.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) Color.White.copy(alpha = 0.25f) else KidsTeal.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = letterText,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else KidsTeal
                                        )
                                    }
                                    Text(
                                        text = right,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Start,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }

                if (matchedPairs.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = if (isEnglish) "Current Pairs:" else "روابط التوصيل الحالية:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        matchedPairs.forEach { (key, value) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(KidsGreen.copy(alpha = 0.12f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$key  🔗  $value",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = KidsGreen,
                                    modifier = Modifier.weight(1f)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(KidsRed.copy(alpha = 0.12f))
                                        .clickable {
                                            viewModel.playClickSound()
                                            viewModel.unmatchPair(key)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Unmatch",
                                        tint = KidsRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FeedbackPopup(
    isCorrect: Boolean,
    explanation: String,
    correctAnswer: String = "",
    isEnglish: Boolean = false,
    onGoNext: () -> Unit
) {
    Dialog(onDismissRequest = { }) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
            ),
            border = BorderStroke(
                width = 3.dp,
                color = if (isCorrect) KidsGreen else KidsOrange
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("feedback_card")
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(if (isCorrect) KidsGreen.copy(alpha = 0.15f) else KidsOrange.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isCorrect) "🌟" else "💡",
                        fontSize = 44.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = when {
                        isCorrect && isEnglish -> "Awesome Job, Champion!"
                        isCorrect && !isEnglish -> "رائع جداً يا بطل! أحسنت!"
                        !isCorrect && isEnglish -> "Nice Try! Keep Learning!"
                        else -> "محاولة رائعة! فكر مجدداً"
                    },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCorrect) KidsGreen else KidsOrange,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = when {
                        isCorrect && isEnglish -> "You earned +3 bright stars! ⭐"
                        isCorrect && !isEnglish -> "كسبت +٣ نجوم برّاقة ⭐"
                        !isCorrect && isEnglish -> "Every mistake helps your brain grow!"
                        else -> "أنت تتعلم يا ذكي وستنجح!"
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center
                )

                if (!isCorrect && correctAnswer.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, KidsOrange.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = if (isEnglish) "Correct Answer: $correctAnswer" else "الإجابة الصحيحة: $correctAnswer",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = KidsOrange,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = explanation,
                    fontSize = 14.sp,
                    color = Color.DarkGray.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center,
                    lineHeight = 21.sp
                )

                Spacer(modifier = Modifier.height(22.dp))

                Button(
                    onClick = onGoNext,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCorrect) KidsGreen else KidsOrange
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("go_next_button")
                ) {
                    Text(
                        text = if (isEnglish) "Next Question 🚀" else "السؤال التالي 🚀",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun QuizSummaryScreen(
    score: Int,
    total: Int,
    stars: Int,
    subject: String,
    isEnglish: Boolean = false,
    isDailyChallenge: Boolean = false,
    onRetry: () -> Unit,
    onHome: () -> Unit
) {
    val awardTitle: String
    val awardBadge: String
    val awardSubtitle: String
    val awardColor: Color

    when {
        score == total -> {
            awardTitle = if (isEnglish) "Golden Crown of Geniuses! 👑" else "التاج الذهبي للعباقرة! 👑"
            awardBadge = "👑"
            awardSubtitle = if (isEnglish) {
                "Super smart! You got a perfect score in $subject!"
            } else {
                "أنت خارق الذكاء وحققت العلامة الكاملة! بطل الـ $subject بلا منازع!"
            }
            awardColor = KidsYellow
        }
        score >= 3 -> {
            awardTitle = if (isEnglish) "Champions Trophy! 🏆" else "كأس الأبطال المتميزين! 🏆"
            awardBadge = "🏆"
            awardSubtitle = if (isEnglish) {
                "Great effort! You solved most questions like a star!"
            } else {
                "مستوى متميز وبطل حقيقي! لقد كسبت غالبية الأسئلة عن جدارة!"
            }
            awardColor = KidsOrange
        }
        else -> {
            awardTitle = if (isEnglish) "Persistent Explorer Medal! 🏅" else "وسام المجتهد المثابر! 🏅"
            awardBadge = "🏅"
            awardSubtitle = if (isEnglish) {
                "Good try! Practice again to master every question!"
            } else {
                "أحسنت المحاولة ولديك فضول رائع! أعد الكرة لتصبح من النخبة المتميزين!"
            }
            awardColor = KidsBlue
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "✨", fontSize = 28.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isEnglish) "Challenge Complete!" else "تهانينا! أكملت المراجعة!",
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = KidsBlue,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "✨", fontSize = 28.sp)
        }

        if (isDailyChallenge) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = KidsGreen.copy(alpha = 0.15f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "🎁 +5 Bonus Daily Challenge Stars Included!",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = KidsGreen,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = awardBadge,
                    fontSize = 80.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = awardTitle,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = awardColor,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = awardSubtitle,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isEnglish) "Correct Answers" else "الإجابات الصحيحة",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "$score / $total",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = KidsGreen
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isEnglish) "Stars Earned" else "النجوم المكتسبة",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "+$stars ⭐",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = KidsOrange
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = KidsBlue),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("retry_quiz_button")
        ) {
            Text(
                text = if (isEnglish) "Play Again 🔄" else "إعادة التحدي للتحسن المستمر 🔄",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onHome,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(2.dp, KidsBlue),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("back_to_lobby_button")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    tint = KidsBlue,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isEnglish) "Back to Home 🏠" else "العودة للقائمة الرئيسية 🏠",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = KidsBlue
                )
            }
        }
    }
}
