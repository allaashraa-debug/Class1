package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AchievementBadge
import com.example.data.model.RewardsCalculator
import com.example.ui.theme.KidsBlue
import com.example.ui.theme.KidsGreen
import com.example.ui.theme.KidsOrange
import com.example.ui.theme.KidsPurple
import com.example.ui.theme.KidsTeal
import com.example.ui.theme.KidsYellow
import com.example.ui.viewmodel.QuizViewModel

@Composable
fun RewardsScreen(
    viewModel: QuizViewModel,
    onNavigateBack: () -> Unit
) {
    val studentName by viewModel.studentName.collectAsState()
    val studentAvatar by viewModel.studentAvatar.collectAsState()
    val attempts by viewModel.allAttempts.collectAsState()
    val dailyStreak by viewModel.dailyStreak.collectAsState()

    val totalStars = attempts.sumOf { it.starsEarned }
    val heroRank = RewardsCalculator.getHeroRank(totalStars)
    val badges = RewardsCalculator.calculateBadges(attempts, dailyStreak)
    val unlockedCount = badges.count { it.isUnlocked }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable {
                        viewModel.playClickSound()
                        onNavigateBack()
                    }
                    .testTag("rewards_back_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = KidsBlue,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "غرفة الجوائز والأوسمة 🏆",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Trophy Room & Sticker Album ($unlockedCount/${badges.size})",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Hero Rank Banner Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFFFA726), Color(0xFFFB8C00), Color(0xFFF57C00))
                        )
                    )
                    .padding(18.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = studentAvatar, fontSize = 38.sp)
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = studentName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "${heroRank.emoji} ${heroRank.titleAr} • ${heroRank.titleEn}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.92f)
                            )
                        }

                        Surface(
                            color = Color.White.copy(alpha = 0.22f),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = KidsYellow,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$totalStars",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val progressInLevel = if (heroRank.nextRankStars > heroRank.minStars) {
                        ((totalStars - heroRank.minStars).toFloat() /
                            (heroRank.nextRankStars - heroRank.minStars).toFloat()).coerceIn(0f, 1f)
                    } else 1f

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "التقدم للمستوى التالي:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            text = "$totalStars / ${heroRank.nextRankStars} ⭐",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { progressInLevel },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape),
                        color = KidsYellow,
                        trackColor = Color.White.copy(alpha = 0.3f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "ملصقات الإنجازات والأوسمة ✨",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = KidsPurple,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(badges) { badge ->
                BadgeGridCard(badge = badge)
            }
        }
    }
}

@Composable
private fun BadgeGridCard(badge: AchievementBadge) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (badge.isUnlocked) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(alpha = 0.65f)
        ),
        border = BorderStroke(
            width = if (badge.isUnlocked) 2.5.dp else 1.dp,
            color = if (badge.isUnlocked) badge.accentColor else Color.LightGray.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (badge.isUnlocked) 4.dp else 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (badge.isUnlocked) 1f else 0.65f)
            .testTag("badge_card_${badge.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .clip(CircleShape)
                    .background(
                        if (badge.isUnlocked) badge.accentColor.copy(alpha = 0.16f)
                        else Color.LightGray.copy(alpha = 0.25f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (badge.isUnlocked) {
                    Text(text = badge.emoji, fontSize = 34.sp)
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color.Gray,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = badge.titleAr,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (badge.isUnlocked) badge.accentColor else Color.Gray,
                textAlign = TextAlign.Center
            )

            Text(
                text = badge.titleEn,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = badge.descriptionAr,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = if (badge.isUnlocked) KidsGreen.copy(alpha = 0.15f) else Color.LightGray.copy(alpha = 0.2f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = if (badge.isUnlocked) "مكتمل ✅" else badge.progressText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (badge.isUnlocked) KidsGreen else Color.Gray,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}
