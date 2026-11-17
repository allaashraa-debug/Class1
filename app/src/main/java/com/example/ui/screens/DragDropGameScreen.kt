package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.ui.theme.CardBlue
import com.example.ui.theme.KidsBlue
import com.example.ui.theme.KidsGreen
import com.example.ui.theme.KidsOrange
import com.example.ui.theme.KidsPurple
import com.example.ui.theme.KidsTeal
import com.example.ui.theme.KidsYellow
import com.example.ui.viewmodel.QuizViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class GameElement(
    val id: String,
    val value: String,
    val targetLabel: String
)

class DragItemState(
    val id: String,
    val value: String,
    val targetId: String,
    val scope: CoroutineScope
) {
    val offsetX = Animatable(0f)
    val offsetY = Animatable(0f)
    var isDragging by mutableStateOf(false)
    var isMatched by mutableStateOf(false)

    fun snapBack() {
        scope.launch { offsetX.animateTo(0f, spring()) }
        scope.launch { offsetY.animateTo(0f, spring()) }
    }

    fun snapTo(targetOffsetX: Float, targetOffsetY: Float) {
        scope.launch { offsetX.animateTo(targetOffsetX, spring()) }
        scope.launch { offsetY.animateTo(targetOffsetY, spring()) }
    }
}

@Composable
fun DragDropGameScreen(
    viewModel: QuizViewModel,
    onNavigateBack: () -> Unit
) {
    val studentName by viewModel.studentName.collectAsState()
    val scope = rememberCoroutineScope()

    var activeTab by remember { mutableStateOf("LETTERS") } // "LETTERS", "MATH", "ENGLISH"
    val isEnglishTab = activeTab == "MATH" || activeTab == "ENGLISH"

    val lettersPack = remember {
        listOf(
            GameElement("أ", "أ", "🐰 أرنب ممتع"),
            GameElement("ب", "ب", "🦆 بطة رشيقة"),
            GameElement("ت", "ت", "🍎 تفاحة شهية"),
            GameElement("ث", "ث", "🦊 ثعلب ذكي")
        )
    }

    val mathPack = remember {
        listOf(
            GameElement("2", "2", "1 + 1 = ? 🍬"),
            GameElement("3", "3", "5 - 2 = ? 🍎"),
            GameElement("4", "4", "2 + 2 = ? ⭐"),
            GameElement("5", "5", "3 + 2 = ? 🦋")
        )
    }

    val englishPack = remember {
        listOf(
            GameElement("A", "A", "🍎 Apple"),
            GameElement("B", "B", "🐝 Bee"),
            GameElement("C", "C", "🐱 Cat"),
            GameElement("S", "S", "☀️ Sun")
        )
    }

    val currentElements = when (activeTab) {
        "MATH" -> mathPack
        "ENGLISH" -> englishPack
        else -> lettersPack
    }

    var leftItems by remember { mutableStateOf(currentElements.shuffled()) }
    var rightTargets by remember { mutableStateOf(currentElements.shuffled()) }
    val dragStates = remember { mutableStateMapOf<String, DragItemState>() }

    var containerCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    val restingCenters = remember { mutableStateMapOf<String, Offset>() }
    val targetRects = remember { mutableStateMapOf<String, Rect>() }

    var selectedTileId by remember { mutableStateOf<String?>(null) }
    var isVictory by remember { mutableStateOf(false) }

    fun checkVictory() {
        val allFinished = dragStates.values.isNotEmpty() && dragStates.values.all { it.isMatched }
        if (allFinished && !isVictory) {
            isVictory = true
            viewModel.soundEngine.playCelebrationSound(isEnglishTab)
            val attemptLabel = when (activeTab) {
                "MATH" -> "Drag & Drop: Math 🔢"
                "ENGLISH" -> "Drag & Drop: English 🔤"
                else -> "سحب وإفلات: الحروف 🔠"
            }
            viewModel.saveDragDropAttempt(
                subject = attemptLabel,
                score = 4,
                total = 4,
                stars = 10
            )
        }
    }

    fun performMatch(itemId: String) {
        val state = dragStates[itemId] ?: return
        state.isMatched = true
        selectedTileId = null
        viewModel.soundEngine.playCorrectSound(isEnglishTab)

        val restCenter = restingCenters[itemId]
        val targetRect = targetRects[itemId]
        if (restCenter != null && targetRect != null) {
            state.snapTo(targetRect.center.x - restCenter.x, targetRect.center.y - restCenter.y)
        }
        checkVictory()
    }

    fun initGame() {
        isVictory = false
        selectedTileId = null
        restingCenters.clear()
        targetRects.clear()
        dragStates.clear()

        val shuffledLeft = currentElements.shuffled()
        leftItems = shuffledLeft
        rightTargets = currentElements.shuffled()

        shuffledLeft.forEach { item ->
            dragStates[item.id] = DragItemState(
                id = item.id,
                value = item.value,
                targetId = item.id,
                scope = scope
            )
        }
    }

    LaunchedEffect(activeTab) {
        initGame()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
                        .testTag("drag_drop_back_button"),
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

                Text(
                    text = if (isEnglishTab) "Magic Drag & Match 🎮" else "لعبة السحب والمطابقة 🎮",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = KidsBlue,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(KidsYellow.copy(alpha = 0.18f))
                        .clickable {
                            viewModel.playClickSound()
                            initGame()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restart Game",
                        tint = KidsOrange,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3 Pack Selector Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(5.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    Triple("LETTERS", "حروف 🔠", KidsTeal),
                    Triple("MATH", "Math 🔢", KidsOrange),
                    Triple("ENGLISH", "English 🔤", KidsPurple)
                ).forEach { (tabId, label, activeColor) ->
                    val isSelected = activeTab == tabId
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) activeColor else Color.Transparent)
                            .clickable {
                                viewModel.playClickSound()
                                activeTab = tabId
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBlue),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isEnglishTab) {
                        "Hey $studentName! Drag (or tap) each tile to its matching card to win 10 stars! ⭐"
                    } else {
                        "يا $studentName! اسحب كل بطاقة (أو اضغط عليها ثم على مكانها الصحيح) لتربح ١٠ نجوم! ⭐"
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = KidsBlue,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                    .padding(14.dp)
                    .onGloballyPositioned { containerCoordinates = it }
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Draggable / Tappable Items Column
                    Column(
                        modifier = Modifier
                            .weight(0.9f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        leftItems.forEach { item ->
                            val state = dragStates[item.id]
                            val isMatched = state?.isMatched == true
                            val isDragging = state?.isDragging == true
                            val isSelected = selectedTileId == item.id

                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .onGloballyPositioned { coordinates ->
                                        if (state != null && !state.isDragging && !state.isMatched) {
                                            containerCoordinates?.let { container ->
                                                val bounds = container.localBoundingBoxOf(coordinates)
                                                restingCenters[item.id] = bounds.center
                                            }
                                        }
                                    }
                                    .offset {
                                        val ox = state?.offsetX?.value?.roundToInt() ?: 0
                                        val oy = state?.offsetY?.value?.roundToInt() ?: 0
                                        IntOffset(ox, oy)
                                    }
                                    .zIndex(if (isDragging) 10f else 1f)
                                    .clickable(enabled = !isMatched) {
                                        viewModel.playClickSound()
                                        selectedTileId = if (isSelected) null else item.id
                                    }
                                    .pointerInput(item.id) {
                                        if (isMatched) return@pointerInput
                                        detectDragGestures(
                                            onDragStart = {
                                                state?.isDragging = true
                                                selectedTileId = item.id
                                                viewModel.soundEngine.playClickSound()
                                            },
                                            onDrag = { change, dragAmount ->
                                                change.consume()
                                                if (state != null) {
                                                    scope.launch {
                                                        state.offsetX.snapTo(state.offsetX.value + dragAmount.x)
                                                        state.offsetY.snapTo(state.offsetY.value + dragAmount.y)
                                                    }
                                                }
                                            },
                                            onDragEnd = {
                                                if (state != null) {
                                                    state.isDragging = false
                                                    val restCenter = restingCenters[item.id] ?: Offset.Zero
                                                    val activeCenter = restCenter + Offset(state.offsetX.value, state.offsetY.value)

                                                    var targetMatchedId: String? = null
                                                    for ((targetId, rect) in targetRects) {
                                                        if (rect.contains(activeCenter)) {
                                                            targetMatchedId = targetId
                                                            break
                                                        }
                                                    }

                                                    if (targetMatchedId == item.id) {
                                                        performMatch(item.id)
                                                    } else {
                                                        if (targetMatchedId != null) {
                                                            viewModel.soundEngine.playIncorrectSound()
                                                        }
                                                        state.snapBack()
                                                    }
                                                }
                                            },
                                            onDragCancel = {
                                                state?.isDragging = false
                                                state?.snapBack()
                                            }
                                        )
                                    }
                            ) {
                                Card(
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = when {
                                            isMatched -> KidsGreen
                                            isDragging || isSelected -> KidsOrange
                                            else -> KidsBlue
                                        }
                                    ),
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = if (isDragging || isSelected) 12.dp else 4.dp
                                    ),
                                    border = BorderStroke(if (isSelected) 4.dp else 3.dp, Color.White),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Text(
                                            text = item.value,
                                            fontSize = 30.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    // Targets Drop Slots Column
                    Column(
                        modifier = Modifier
                            .weight(1.4f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        rightTargets.forEach { target ->
                            val matchedState = dragStates[target.id]
                            val isMatched = matchedState?.isMatched == true

                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isMatched) KidsGreen.copy(alpha = 0.15f) else Color.White
                                ),
                                border = BorderStroke(
                                    width = 3.dp,
                                    color = if (isMatched) KidsGreen else KidsBlue.copy(alpha = 0.35f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(72.dp)
                                    .clickable(enabled = !isMatched && selectedTileId != null) {
                                        if (selectedTileId == target.id) {
                                            performMatch(target.id)
                                        } else {
                                            viewModel.soundEngine.playIncorrectSound()
                                            selectedTileId = null
                                        }
                                    }
                                    .onGloballyPositioned { coordinates ->
                                        containerCoordinates?.let { container ->
                                            val bounds = container.localBoundingBoxOf(coordinates)
                                            targetRects[target.id] = bounds
                                        }
                                    }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 12.dp)
                                ) {
                                    Text(
                                        text = target.targetLabel,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isMatched) KidsGreen else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (isMatched) KidsGreen.copy(alpha = 0.2f) else KidsBlue.copy(alpha = 0.08f)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isMatched) {
                                            Text(text = "⭐", fontSize = 20.sp)
                                        } else {
                                            Text(
                                                text = if (isEnglishTab) "?" else "؟",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = KidsBlue.copy(alpha = 0.55f)
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

        // Victory Overlay
        AnimatedVisibility(
            visible = isVictory,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier
                .fillMaxSize()
                .zIndex(100f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.75f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .padding(16.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(4.dp, KidsYellow)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isEnglishTab) "🎉 SUPER JOB! 🎉" else "🎉 ممتـاز جـداً 🎉",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            color = KidsOrange,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(KidsYellow.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🏆", fontSize = 60.sp)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = if (isEnglishTab) {
                                "You matched every card like a true champion and earned 10 bright stars! ⭐⭐⭐"
                            } else {
                                "لقد طابقت العناصر كلها بشكل رائع يا بطل! كسبت ١٠ نجوم جديدة! ⭐⭐⭐"
                            },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )

                        Spacer(modifier = Modifier.height(22.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    viewModel.playClickSound()
                                    initGame()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = KidsOrange),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (isEnglishTab) "Play Again" else "العب مجدداً",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Button(
                                onClick = {
                                    viewModel.playClickSound()
                                    onNavigateBack()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = KidsBlue),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (isEnglishTab) "Home 🏠" else "الرئيسية 🏠",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
