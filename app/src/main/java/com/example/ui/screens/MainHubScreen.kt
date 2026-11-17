package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Science
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RewardsCalculator
import com.example.ui.theme.CardBlue
import com.example.ui.theme.CardTeal
import com.example.ui.theme.CardYellow
import com.example.ui.theme.FredokaFontFamily
import com.example.ui.theme.KidsBlue
import com.example.ui.theme.KidsOrange
import com.example.ui.theme.KidsPurple
import com.example.ui.theme.KidsRed
import com.example.ui.theme.KidsTeal
import com.example.ui.theme.KidsYellow
import com.example.ui.viewmodel.QuizViewModel

data class SubjectCardData(
    val id: String,
    val icon: ImageVector,
    val iconTint: Color,
    val topSymbol: String,
    val englishTitle: String,
    val arabicTitle: String,
    val totalLessons: Int,
    val gradientColors: List<Color>,
    val watermarkEmoji: String,
    val quizSubject: String
)

@Composable
fun MainHubScreen(
    viewModel: QuizViewModel,
    onNavigateToQuiz: (String) -> Unit,
    onNavigateToDailyChallenge: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToRewards: () -> Unit,
    onNavigateToDragDrop: () -> Unit,
    onNavigateToMemoryGame: () -> Unit,
    onNavigateToStory: () -> Unit
) {
    val name by viewModel.studentName.collectAsState()
    val avatar by viewModel.studentAvatar.collectAsState()
    val attempts by viewModel.allAttempts.collectAsState()
    val dailyStreak by viewModel.dailyStreak.collectAsState()
    val soundEnabled by viewModel.soundEffectsEnabled.collectAsState()
    val isDailyChallengeDone by viewModel.isDailyChallengeDoneToday.collectAsState()

    val totalStars = attempts.sumOf { it.starsEarned }
    val heroRank = RewardsCalculator.getHeroRank(totalStars)
    val badges = RewardsCalculator.calculateBadges(attempts, dailyStreak)
    val unlockedBadgesCount = badges.count { it.isUnlocked }

    // Primary 4 core subjects grid: Math, English, Science, Arabic
    val coreSubjects = listOf(
        SubjectCardData(
            id = "math",
            icon = Icons.Rounded.Calculate,
            iconTint = Color(0xFF1B8E3E),
            topSymbol = "123",
            englishTitle = "Math",
            arabicTitle = "الرياضيات",
            totalLessons = 10,
            gradientColors = listOf(Color(0xFF4CD964), Color(0xFF22A94E)),
            watermarkEmoji = "🧮",
            quizSubject = "Mathematics"
        ),
        SubjectCardData(
            id = "english",
            icon = Icons.AutoMirrored.Rounded.MenuBook,
            iconTint = Color(0xFF1565C0),
            topSymbol = "ABC",
            englishTitle = "English",
            arabicTitle = "الإنجليزية",
            totalLessons = 10,
            gradientColors = listOf(Color(0xFF42A5F5), Color(0xFF1976D2)),
            watermarkEmoji = "🦉",
            quizSubject = "English"
        ),
        SubjectCardData(
            id = "science",
            icon = Icons.Rounded.Science,
            iconTint = Color(0xFFE65100),
            topSymbol = "🔬",
            englishTitle = "Science",
            arabicTitle = "العلوم",
            totalLessons = 10,
            gradientColors = listOf(Color(0xFFFF9838), Color(0xFFF46016)),
            watermarkEmoji = "🌱",
            quizSubject = "Science"
        ),
        SubjectCardData(
            id = "arabic",
            icon = Icons.Rounded.Translate,
            iconTint = Color(0xFF7B1FA2),
            topSymbol = "أبجد",
            englishTitle = "Arabic",
            arabicTitle = "اللغة العربية",
            totalLessons = 10,
            gradientColors = listOf(Color(0xFFBA68C8), Color(0xFF8E24AA)),
            watermarkEmoji = "🕌",
            quizSubject = "اللغة العربية"
        )
    )

    val extraSubjects = listOf(
        SubjectCardData(
            id = "social",
            icon = Icons.Rounded.Public,
            iconTint = Color(0xFF00838F),
            topSymbol = "🌍",
            englishTitle = "Social Studies",
            arabicTitle = "الدراسات",
            totalLessons = 10,
            gradientColors = listOf(Color(0xFF26C6DA), Color(0xFF0097A7)),
            watermarkEmoji = "🧭",
            quizSubject = "Social Studies"
        ),
        SubjectCardData(
            id = "islamic",
            icon = Icons.Rounded.AutoStories,
            iconTint = Color(0xFF00695C),
            topSymbol = "قرآن",
            englishTitle = "Islamic Studies",
            arabicTitle = "الإسلامية",
            totalLessons = 10,
            gradientColors = listOf(Color(0xFF26A69A), Color(0xFF00796B)),
            watermarkEmoji = "🕋",
            quizSubject = "Islamic Studies"
        )
    )

    fun getCompletedCount(subject: SubjectCardData): Int {
        val solved = attempts.filter {
            val matches = when (it.subject) {
                subject.quizSubject, subject.englishTitle, subject.arabicTitle -> true
                else -> false
            }
            matches && it.score > 0
        }.sumOf { it.score }
        return solved.coerceAtMost(subject.totalLessons)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Upper Greeting Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = KidsBlue.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "Hello, $name! 👋",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = KidsBlue,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        color = KidsRed.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = KidsRed,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "$dailyStreak",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = KidsRed
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Grade 1 Learning Hub",
                    fontFamily = FredokaFontFamily,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "اختر مادتك المفضلة وابدأ المراجعة الممتعة ✨",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    onClick = { viewModel.toggleSoundEffects() },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("hub_sound_toggle")
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (soundEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                            contentDescription = "Toggle Sound",
                            tint = if (soundEnabled) KidsTeal else Color.Gray,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    border = BorderStroke(2.dp, KidsBlue.copy(alpha = 0.45f)),
                    onClick = {
                        viewModel.playClickSound()
                        onNavigateToProfile()
                    },
                    modifier = Modifier
                        .size(52.dp)
                        .testTag("top_avatar_button")
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = avatar, fontSize = 30.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Stats, Hero Rank, Rewards & Parent Report Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        viewModel.playClickSound()
                        onNavigateToRewards()
                    }
                    .padding(4.dp)
            ) {
                Surface(
                    color = KidsYellow.copy(alpha = 0.22f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = KidsOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$totalStars ⭐",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = KidsOrange
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${heroRank.emoji} ${heroRank.titleAr}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = KidsPurple
                )
            }

            TextButton(
                onClick = {
                    viewModel.playClickSound()
                    onNavigateToDashboard()
                },
                modifier = Modifier.testTag("dashboard_quick_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Analytics,
                    contentDescription = null,
                    tint = KidsBlue,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "التقرير 📊",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = KidsBlue
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ==================== CORE SUBJECTS GRID (Math, English, Science, Arabic) ====================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Subjects • المواد الدراسية 📚",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "اختر مادة",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = KidsBlue
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val isWide = maxWidth >= 600.dp
            val rows = if (isWide) listOf(coreSubjects) else coreSubjects.chunked(2)

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                rows.forEach { rowSubjects ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        rowSubjects.forEach { subject ->
                            Grade1SubjectCard(
                                subject = subject,
                                completedLessons = getCompletedCount(subject),
                                onClick = {
                                    viewModel.playClickSound()
                                    onNavigateToQuiz(subject.quizSubject)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Additional Subjects + Trophy Room Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            extraSubjects.forEach { subject ->
                Grade1SubjectCard(
                    subject = subject,
                    completedLessons = getCompletedCount(subject),
                    onClick = {
                        viewModel.playClickSound()
                        onNavigateToQuiz(subject.quizSubject)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Daily Challenge & Trophy Room Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                onClick = {
                    viewModel.playClickSound()
                    onNavigateToDailyChallenge()
                },
                modifier = Modifier
                    .weight(1.25f)
                    .height(112.dp)
                    .testTag("daily_challenge_banner")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                if (isDailyChallengeDone) listOf(Color(0xFF26A69A), Color(0xFF00897B))
                                else listOf(Color(0xFF7E57C2), Color(0xFF5E35B1))
                            )
                        )
                        .padding(14.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = if (isDailyChallengeDone) "✅" else "⚡", fontSize = 24.sp)
                            Surface(
                                color = Color.White.copy(alpha = 0.24f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = if (isDailyChallengeDone) "Done" else "+5 ⭐",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Daily Challenge",
                                fontFamily = FredokaFontFamily,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "تحدي اليوم الشامل",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.88f)
                            )
                        }
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                onClick = {
                    viewModel.playClickSound()
                    onNavigateToRewards()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(112.dp)
                    .testTag("trophy_room_grid_card")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFFFF7043), Color(0xFFE64A19))
                            )
                        )
                        .padding(14.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Trophy Room",
                                tint = KidsYellow,
                                modifier = Modifier.size(26.dp)
                            )
                            Text(
                                text = "$unlockedBadgesCount/${badges.size}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                        Column {
                            Text(
                                text = "Trophies",
                                fontFamily = FredokaFontFamily,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "غرفة الجوائز",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.88f)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ==================== INTERACTIVE GAMES & ADVENTURES ====================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "المغامرات والألعاب التفاعلية 🎮✨",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = KidsPurple
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 1. Featured Interactive Story Game Banner
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardBlue),
            border = BorderStroke(2.5.dp, KidsBlue),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            onClick = {
                viewModel.playClickSound()
                onNavigateToStory()
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("interactive_story_banner")
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(62.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White)
                        .border(BorderStroke(2.dp, KidsBlue), RoundedCornerShape(18.dp))
                ) {
                    Text(text = "🦉📖", fontSize = 28.sp)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(KidsBlue)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "قصة تفاعلية شيقة 🌟",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "مغامرة بطلنا مع مُفَكِّر 🦉",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = KidsBlue
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "اتخذ القرارات الذكية بنفسك وصمم نهاية القصة السعيدة!",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }

                Text(text = "👈", fontSize = 20.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Memory Game Banner
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardTeal),
            border = BorderStroke(2.5.dp, KidsTeal),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            onClick = {
                viewModel.playClickSound()
                onNavigateToMemoryGame()
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("memory_game_banner")
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(62.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White)
                        .border(BorderStroke(2.dp, KidsTeal), RoundedCornerShape(18.dp))
                ) {
                    Text(text = "🧠✨", fontSize = 28.sp)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(KidsTeal)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "لعبة تنشيط الذاكرة 🏆",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "لعبة الذاكرة والمطابقة 🃏",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = KidsTeal
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "اقلب البطاقات وطابق الحروف، الأرقام والكلمات لتربح النجوم!",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }

                Text(text = "👈", fontSize = 20.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Drag & Drop Game Banner
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardYellow),
            border = BorderStroke(2.5.dp, KidsOrange),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            onClick = {
                viewModel.playClickSound()
                onNavigateToDragDrop()
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("drag_drop_game_banner")
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(62.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White)
                        .border(BorderStroke(2.dp, KidsOrange), RoundedCornerShape(18.dp))
                ) {
                    Text(text = "🧩🎯", fontSize = 28.sp)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(KidsOrange)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "لعبة السحب والإفلات 🚀",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "تحدي السحب السحري ✨",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = KidsOrange
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "اسحب الحروف، الكلمات الإنجليزية وأرقام الرياضيات لمكانها الصحيح!",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }

                Text(text = "👈", fontSize = 20.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Bottom Parent Dashboard Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.5.dp, KidsOrange.copy(alpha = 0.25f)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    viewModel.playClickSound()
                    onNavigateToDashboard()
                }
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(KidsOrange.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Analytics,
                        contentDescription = null,
                        tint = KidsOrange,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "قسم الوالدين والمعلمين 👨‍👩‍👦",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = KidsOrange
                    )
                    Text(
                        text = "اضغط لمشاهدة التحصيل الدراسي، إضافة أسئلة مخصصة، والتحكم بالصوت.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun Grade1SubjectCard(
    subject: SubjectCardData,
    completedLessons: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        onClick = onClick,
        modifier = modifier
            .height(178.dp)
            .testTag("subject_card_${subject.id}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(subject.gradientColors))
        ) {
            // Decorative playful circles & watermark emoji
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 24.dp, y = (-24).dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
            )

            Text(
                text = subject.watermarkEmoji,
                fontSize = 64.sp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 8.dp, y = 12.dp)
                    .alpha(0.26f)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 15.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Colorful Child-Friendly Icon Badge + Top Symbol
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = subject.icon,
                            contentDescription = subject.englishTitle,
                            tint = subject.iconTint,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Text(
                        text = subject.topSymbol,
                        fontFamily = if (subject.id == "english" || subject.id == "math") FredokaFontFamily else null,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.92f)
                    )
                }

                Column {
                    Text(
                        text = subject.englishTitle,
                        fontFamily = FredokaFontFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = subject.arabicTitle,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                Surface(
                    color = Color.White.copy(alpha = 0.26f),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.clip(RoundedCornerShape(50))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$completedLessons/${subject.totalLessons} mastered",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
