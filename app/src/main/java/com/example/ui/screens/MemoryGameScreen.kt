package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CardBlue
import com.example.ui.theme.CardTeal
import com.example.ui.theme.KidsBlue
import com.example.ui.theme.KidsGreen
import com.example.ui.theme.KidsOrange
import com.example.ui.theme.KidsTeal
import com.example.ui.theme.KidsYellow
import com.example.ui.viewmodel.QuizViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class MemoryPair(
    val pairId: Int,
    val itemA: String,
    val itemB: String,
    val speechA: String = itemA,
    val speechB: String = itemB,
    val isEnglish: Boolean = false
)

data class MemoryCard(
    val cardId: Int,
    val pairId: Int,
    val content: String,
    val speechText: String,
    val isEnglish: Boolean
)

enum class MemoryCategory(val title: String, val emoji: String, val color: Color, val isEnglish: Boolean) {
    ARABIC("الحروف والكلمات", "📖", KidsTeal, false),
    MATH("Math & Counting", "🔢", KidsOrange, true),
    ENGLISH("English Words", "🐝", KidsBlue, true),
    ISLAMIC("Islamic Manners", "🕋", KidsGreen, true)
}

@Composable
fun MemoryGameScreen(
    viewModel: QuizViewModel,
    onNavigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val studentName by viewModel.studentName.collectAsState()

    var selectedCategory by remember { mutableStateOf(MemoryCategory.ARABIC) }

    val cards = remember { mutableStateListOf<MemoryCard>() }
    val flippedCardIndices = remember { mutableStateListOf<Int>() }
    val matchedPairIds = remember { mutableStateListOf<Int>() }

    var movesCount by remember { mutableIntStateOf(0) }
    var isCheckingMatch by remember { mutableStateOf(false) }
    var showVictoryDialog by remember { mutableStateOf(false) }
    var starsEarned by remember { mutableIntStateOf(0) }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopAudio()
        }
    }

    fun getPresetPairs(cat: MemoryCategory): List<MemoryPair> {
        return when (cat) {
            MemoryCategory.ARABIC -> listOf(
                MemoryPair(1, "أ", "أَرْنَب 🐰", "ألف", "أرنب"),
                MemoryPair(2, "ب", "بَطَّة 🦆", "باء", "بطة"),
                MemoryPair(3, "ت", "تُفَّاحَة 🍎", "تاء", "تفاحة"),
                MemoryPair(4, "ث", "ثَعْلَب 🦊", "ثاء", "ثعلب"),
                MemoryPair(5, "س", "سَمَكَة 🐟", "سين", "سمكة"),
                MemoryPair(6, "ل", "لَيْمُون 🍋", "لام", "ليمون")
            )
            MemoryCategory.MATH -> listOf(
                MemoryPair(1, "3 + 2", "5 🖐️", "3 plus 2", "5", isEnglish = true),
                MemoryPair(2, "4 Butterflies", "🦋🦋🦋🦋", "4 butterflies", "4 butterflies", isEnglish = true),
                MemoryPair(3, "7 - 3", "4 🍎", "7 minus 3", "4", isEnglish = true),
                MemoryPair(4, "2 + 2", "4 ⭐", "2 plus 2", "4 stars", isEnglish = true),
                MemoryPair(5, "Pattern 🔴🔵", "🔴 Next", "Red Blue Pattern", "Red is next", isEnglish = true),
                MemoryPair(6, "5 + 1", "6 🎈", "5 plus 1", "6", isEnglish = true)
            )
            MemoryCategory.ENGLISH -> listOf(
                MemoryPair(1, "A", "Apple 🍎", "A", "Apple", isEnglish = true),
                MemoryPair(2, "B", "Bee 🐝", "B", "Bee", isEnglish = true),
                MemoryPair(3, "C", "Cat 🐱", "C", "Cat", isEnglish = true),
                MemoryPair(4, "D", "Dog 🐶", "D", "Dog", isEnglish = true),
                MemoryPair(5, "S", "Sun ☀️", "S", "Sun", isEnglish = true),
                MemoryPair(6, "F", "Fish 🐟", "F", "Fish", isEnglish = true)
            )
            MemoryCategory.ISLAMIC -> listOf(
                MemoryPair(1, "Before Eating 🥛", "Bismillah", "Before Eating", "Bismillah", isEnglish = true),
                MemoryPair(2, "After Eating 🍽️", "Alhamdulillah", "After Eating", "Alhamdulillah", isEnglish = true),
                MemoryPair(3, "Pillars of Islam 🕋", "5 Pillars", "Pillars of Islam", "5 Pillars", isEnglish = true),
                MemoryPair(4, "Daily Prayers 🕌", "5 Salah", "Daily Prayers", "5 Salah", isEnglish = true),
                MemoryPair(5, "Greeting 🤝", "Assalamu Alaikum", "Greeting", "Assalamu Alaikum", isEnglish = true),
                MemoryPair(6, "Holy Book 📖", "The Quran", "Holy Book", "The Quran", isEnglish = true)
            )
        }
    }

    fun startNewGame(cat: MemoryCategory) {
        val pairs = getPresetPairs(cat)
        val generatedCards = mutableListOf<MemoryCard>()
        var idCounter = 0
        pairs.forEach { p ->
            generatedCards.add(MemoryCard(idCounter++, p.pairId, p.itemA, p.speechA, p.isEnglish))
            generatedCards.add(MemoryCard(idCounter++, p.pairId, p.itemB, p.speechB, p.isEnglish))
        }
        cards.clear()
        cards.addAll(generatedCards.shuffled())
        flippedCardIndices.clear()
        matchedPairIds.clear()
        movesCount = 0
        isCheckingMatch = false
        showVictoryDialog = false
        starsEarned = 0
    }

    LaunchedEffect(selectedCategory) {
        startNewGame(selectedCategory)
    }

    fun onCardClicked(index: Int) {
        if (isCheckingMatch) return
        if (index in flippedCardIndices) return
        val card = cards.getOrNull(index) ?: return
        if (card.pairId in matchedPairIds) return

        viewModel.playClickSound()
        viewModel.soundEngine.speak(card.speechText, card.isEnglish)

        flippedCardIndices.add(index)

        if (flippedCardIndices.size == 2) {
            movesCount++
            val firstCard = cards[flippedCardIndices[0]]
            val secondCard = cards[flippedCardIndices[1]]

            if (firstCard.pairId == secondCard.pairId) {
                matchedPairIds.add(firstCard.pairId)
                viewModel.soundEngine.playCorrectSound(selectedCategory.isEnglish)
                flippedCardIndices.clear()

                if (matchedPairIds.size == cards.size / 2) {
                    coroutineScope.launch {
                        delay(600)
                        val totalPairs = cards.size / 2
                        val calculatedStars = when {
                            movesCount <= totalPairs + 2 -> 6
                            movesCount <= totalPairs + 5 -> 4
                            else -> 3
                        }
                        starsEarned = calculatedStars
                        showVictoryDialog = true
                        viewModel.soundEngine.playCelebrationSound(selectedCategory.isEnglish)
                        viewModel.saveMiniGameAttempt(
                            gameName = "لعبة الذاكرة: ${selectedCategory.title}",
                            score = totalPairs,
                            total = totalPairs,
                            stars = calculatedStars
                        )
                    }
                }
            } else {
                isCheckingMatch = true
                viewModel.soundEngine.playIncorrectSound()
                coroutineScope.launch {
                    delay(1000)
                    flippedCardIndices.clear()
                    isCheckingMatch = false
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    viewModel.playClickSound()
                    viewModel.stopAudio()
                    onNavigateBack()
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .testTag("memory_game_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = KidsBlue
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "لعبة الذاكرة والمطابقة 🧠✨",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "اقلب البطاقات وطابق بين كل عنصرين متماثلين!",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }

            IconButton(
                onClick = {
                    viewModel.playClickSound()
                    startNewGame(selectedCategory)
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(KidsYellow.copy(alpha = 0.2f))
                    .testTag("memory_game_restart_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Restart",
                    tint = KidsOrange
                )
            }
        }

        // Category Selector Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(MemoryCategory.entries) { category ->
                val isSelected = category == selectedCategory
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        viewModel.playClickSound()
                        selectedCategory = category
                    },
                    label = {
                        Text(
                            text = "${category.emoji} ${category.title}",
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = category.color,
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }

        // Game Status Bar
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🎯 المحاولات: ", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = "$movesCount",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = KidsOrange
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🌟 المطابقات: ", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = "${matchedPairIds.size} / ${cards.size / 2}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = KidsGreen
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(cards.size) { index ->
                val card = cards[index]
                val isFlipped = index in flippedCardIndices || card.pairId in matchedPairIds
                val isMatched = card.pairId in matchedPairIds

                MemoryCardView(
                    card = card,
                    isFlipped = isFlipped,
                    isMatched = isMatched,
                    themeColor = selectedCategory.color,
                    onClick = { onCardClicked(index) }
                )
            }
        }
    }

    if (showVictoryDialog) {
        Dialog(onDismissRequest = { showVictoryDialog = false }) {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(3.dp, KidsOrange),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("memory_game_victory_dialog")
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(KidsYellow.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🏆", fontSize = 44.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "عَبْقَرِيّ يَا $studentName! 🎉",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = KidsOrange,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "لقد طابقت جميع البطاقات بنجاح مذهل وسرعة بديهة!",
                        fontSize = 13.sp,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val ratingStars = if (starsEarned >= 6) 3 else if (starsEarned >= 4) 2 else 1
                        for (i in 1..3) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (i <= ratingStars) KidsYellow else Color.LightGray,
                                modifier = Modifier
                                    .size(36.dp)
                                    .padding(horizontal = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "أكملتها في $movesCount محاولات وحصلت على +$starsEarned نجوم ذهبية ⭐",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = KidsGreen,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.playClickSound()
                                showVictoryDialog = false
                                startNewGame(selectedCategory)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = KidsOrange)
                        ) {
                            Text("العب ثانية 🔄", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                viewModel.playClickSound()
                                showVictoryDialog = false
                                onNavigateBack()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = KidsBlue)
                        ) {
                            Text("الرئيسية 🏠", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MemoryCardView(
    card: MemoryCard,
    isFlipped: Boolean,
    isMatched: Boolean,
    themeColor: Color,
    onClick: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 380, easing = FastOutSlowInEasing),
        label = "cardFlipRotation"
    )

    val cardBorder = when {
        isMatched -> BorderStroke(2.5.dp, KidsGreen)
        isFlipped -> BorderStroke(2.dp, themeColor)
        else -> BorderStroke(1.5.dp, KidsBlue.copy(alpha = 0.3f))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable(enabled = !isFlipped && !isMatched, onClick = onClick)
            .testTag("memory_card_${card.cardId}")
    ) {
        if (rotation <= 90f) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardBlue),
                border = cardBorder,
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(KidsBlue.copy(alpha = 0.85f), KidsTeal)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "✨", fontSize = 20.sp)
                        Text(
                            text = if (card.isEnglish) "?" else "؟",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }
        } else {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isMatched) CardTeal else Color.White
                ),
                border = cardBorder,
                elevation = CardDefaults.cardElevation(defaultElevation = if (isMatched) 1.dp else 4.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = card.content,
                            fontSize = if (card.content.length > 7) 13.sp else 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isMatched) KidsGreen else Color(0xFF2C3E50),
                            textAlign = TextAlign.Center
                        )

                        if (isMatched) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "✅", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
