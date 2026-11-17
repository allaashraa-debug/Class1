package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Attempt
import com.example.data.model.Question
import com.example.ui.theme.KidsBlue
import com.example.ui.theme.KidsGreen
import com.example.ui.theme.KidsOrange
import com.example.ui.theme.KidsPurple
import com.example.ui.theme.KidsRed
import com.example.ui.theme.KidsTeal
import com.example.ui.viewmodel.QuizViewModel

@Composable
fun DashboardScreen(
    viewModel: QuizViewModel,
    onNavigateBack: () -> Unit
) {
    val attempts by viewModel.allAttempts.collectAsState()
    val allQuestions by viewModel.allQuestions.collectAsState()
    val soundEnabled by viewModel.soundEffectsEnabled.collectAsState()
    val ttsEnabled by viewModel.voiceReadAloudEnabled.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("نشاط الطالب 📈", "بنك الأسئلة 📚", "التحكم ⚙️")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
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
                    .testTag("dashboard_back_button"),
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

            Column {
                Text(
                    text = "بوابة الوالدين والمعلمين 👨‍👩‍👦",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "متابعة مستوى الطفل وإدارة بنك الأسئلة وإعدادات الصوت",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Stats summary bar
        val totalQ = attempts.sumOf { it.totalQuestions }
        val average = if (totalQ == 0) 0 else (attempts.sumOf { it.score } * 100) / totalQ

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(KidsBlue.copy(alpha = 0.08f))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("المحاولات", fontSize = 11.sp, color = Color.Gray)
                Text("${attempts.size} مرات", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = KidsBlue)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("مجموع النجوم", fontSize = 11.sp, color = Color.Gray)
                Text("${attempts.sumOf { it.starsEarned }} ⭐", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = KidsOrange)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("متوسط الدقة", fontSize = 11.sp, color = Color.Gray)
                Text("$average%", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = KidsGreen)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = KidsBlue,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = KidsBlue
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = {
                        viewModel.playClickSound()
                        selectedTab = index
                    },
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .testTag("dashboard_tab_$index")
                ) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedTab == index) KidsBlue else Color.Gray,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> StudentReportTab(attempts = attempts)
                1 -> QuestionsPoolTab(
                    questions = allQuestions,
                    onAddQuestion = { subj, unit, qText, emoji, opts, correct, expl, diff ->
                        viewModel.addCustomQuestion(subj, unit, qText, emoji, opts, correct, expl, diff)
                    },
                    onDeleteQuestion = { id -> viewModel.deleteQuestion(id) },
                    playClickSound = { viewModel.playClickSound() }
                )
                2 -> ParentalSettingsTab(
                    soundEnabled = soundEnabled,
                    ttsEnabled = ttsEnabled,
                    onToggleSound = { viewModel.toggleSoundEffects() },
                    onToggleTts = { viewModel.toggleVoiceReadAloud() },
                    onClearHistory = { viewModel.clearDatabaseHistory() },
                    playClickSound = { viewModel.playClickSound() }
                )
            }
        }
    }
}

@Composable
private fun StudentReportTab(attempts: List<Attempt>) {
    if (attempts.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("📭", fontSize = 64.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "لا توجد سجلات محاولات بعد!",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "ابدأ مراجعة أول مادة لرؤية الإحصائيات الفورية هنا.",
                fontSize = 13.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "التحصيل الدراسي التراكمي حسب المادة 🎓",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = KidsPurple
                )
                Spacer(modifier = Modifier.height(12.dp))

                val subjectEntries = listOf(
                    Triple("Math", listOf("Mathematics", "Math", "الرياضيات"), KidsGreen),
                    Triple("English", listOf("English", "الإنجليزية"), KidsBlue),
                    Triple("Science", listOf("Science", "العلوم"), KidsTeal),
                    Triple("اللغة العربية", listOf("اللغة العربية", "Arabic"), KidsPurple),
                    Triple("Social Studies", listOf("Social Studies", "الدراسات الاجتماعية"), KidsOrange),
                    Triple("Islamic Studies", listOf("Islamic Studies", "التربية الإسلامية"), KidsTeal)
                )

                subjectEntries.forEach { (displayTitle, aliases, accentColor) ->
                    val subjAttempts = attempts.filter { it.subject in aliases }
                    val totalQ = subjAttempts.sumOf { it.totalQuestions }
                    val accuracyPct = if (totalQ == 0) 0 else {
                        (subjAttempts.sumOf { it.score } * 100) / totalQ
                    }

                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = displayTitle, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "$accuracyPct% دقة", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = accentColor)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray.copy(alpha = 0.3f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth((accuracyPct.toFloat() / 100f).coerceIn(0f, 1f))
                                    .height(8.dp)
                                    .clip(CircleShape)
                                    .background(accentColor)
                            )
                        }
                    }
                }
            }
        }

        Text(
            text = "تاريخ سجل المحاولات بالتفصيل 📜",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = KidsPurple,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        attempts.forEach { attempt ->
            val badgeBg = when (attempt.subject) {
                "اللغة العربية", "Arabic" -> KidsPurple.copy(alpha = 0.12f)
                "Mathematics", "Math", "الرياضيات" -> KidsGreen.copy(alpha = 0.12f)
                "Science", "العلوم" -> KidsTeal.copy(alpha = 0.12f)
                "Islamic Studies", "التربية الإسلامية" -> KidsTeal.copy(alpha = 0.12f)
                "Social Studies", "الدراسات الاجتماعية" -> KidsOrange.copy(alpha = 0.12f)
                else -> KidsBlue.copy(alpha = 0.12f)
            }
            val badgeIcon = when (attempt.subject) {
                "اللغة العربية", "Arabic" -> "أ"
                "Mathematics", "Math", "الرياضيات" -> "123"
                "Science", "العلوم" -> "🔬"
                "Islamic Studies", "التربية الإسلامية" -> "🕌"
                "Social Studies", "الدراسات الاجتماعية" -> "🌍"
                "Daily Challenge" -> "⚡"
                else -> "ABC"
            }

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(badgeBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = badgeIcon,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = attempt.subject,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "البطل: ${attempt.studentName} | ${attempt.dateString}",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${attempt.score} / ${attempt.totalQuestions}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (attempt.score == attempt.totalQuestions) KidsGreen else KidsBlue
                        )
                        Text(
                            text = "+${attempt.starsEarned} ⭐",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = KidsOrange
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionsPoolTab(
    questions: List<Question>,
    onAddQuestion: (String, String, String, String, List<String>, String, String, String) -> Unit,
    onDeleteQuestion: (Long) -> Unit,
    playClickSound: () -> Unit
) {
    val subjects = listOf("All", "Mathematics", "English", "Science", "اللغة العربية", "Social Studies", "Islamic Studies")
    var selectedFilter by remember { mutableStateOf("All") }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredQuestions = if (selectedFilter == "All") {
        questions
    } else {
        questions.filter { it.subject == selectedFilter }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "بنك الأسئلة (${filteredQuestions.size} سؤال):",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = KidsPurple
            )

            Button(
                onClick = {
                    playClickSound()
                    showAddDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = KidsTeal),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("add_custom_question_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "إضافة سؤال",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(subjects) { subj ->
                val isSelected = selectedFilter == subj
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        playClickSound()
                        selectedFilter = subj
                    },
                    label = {
                        Text(
                            text = if (subj == "All") "الكل" else subj,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = KidsBlue,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            filteredQuestions.forEach { question ->
                var showDetail by remember { mutableStateOf(false) }
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            playClickSound()
                            showDetail = !showDetail
                        }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = question.hintImage, fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = question.questionText,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "${question.subject} • ${question.unit}",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                            Text(
                                text = when (question.difficulty.lowercase()) {
                                    "سهل", "easy" -> "🟢 ${question.difficulty}"
                                    "متوسط", "medium" -> "🟡 ${question.difficulty}"
                                    else -> "🔴 ${question.difficulty}"
                                },
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }

                        AnimatedVisibility(visible = showDetail) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(KidsBlue.copy(alpha = 0.05f))
                                    .padding(8.dp)
                            ) {
                                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "نوع التفاعل: ${question.questionType}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = KidsPurple
                                )
                                Text(
                                    text = "الخيارات: ${question.options.replace(";", " | ")}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "الإجابة الصحيحة: ${question.correctAnswer}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = KidsGreen
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "الشرح: ${question.explanation}",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (question.unit == "Custom Practice") {
                                        IconButton(onClick = { onDeleteQuestion(question.id) }) {
                                            Icon(
                                                imageVector = Icons.Default.DeleteOutline,
                                                contentDescription = "Delete Custom Question",
                                                tint = KidsRed
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
    }

    if (showAddDialog) {
        AddCustomQuestionDialog(
            onDismiss = { showAddDialog = false },
            onSave = { subj, qText, opt1, opt2, opt3, explanation ->
                onAddQuestion(
                    subj,
                    "Custom Practice",
                    qText,
                    "🌟",
                    listOf(opt1, opt2, opt3),
                    opt1,
                    explanation,
                    if (subj == "اللغة العربية") "سهل" else "Easy"
                )
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun AddCustomQuestionDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String) -> Unit
) {
    val subjects = listOf("Mathematics", "English", "Science", "اللغة العربية", "Islamic Studies", "Social Studies")
    var selectedSubject by remember { mutableStateOf(subjects.first()) }
    var questionText by remember { mutableStateOf("") }
    var correctOption by remember { mutableStateOf("") }
    var wrongOption1 by remember { mutableStateOf("") }
    var wrongOption2 by remember { mutableStateOf("") }
    var explanation by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(2.dp, KidsTeal),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "إضافة سؤال مخصص للطفل ➕",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = KidsTeal
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(subjects) { subj ->
                        FilterChip(
                            selected = selectedSubject == subj,
                            onClick = { selectedSubject = subj },
                            label = { Text(subj, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = questionText,
                    onValueChange = { questionText = it },
                    label = { Text("نص السؤال") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = correctOption,
                    onValueChange = { correctOption = it },
                    label = { Text("الإجابة الصحيحة") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = wrongOption1,
                    onValueChange = { wrongOption1 = it },
                    label = { Text("خيار خطأ ١") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = wrongOption2,
                    onValueChange = { wrongOption2 = it },
                    label = { Text("خيار خطأ ٢ (اختياري)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = explanation,
                    onValueChange = { explanation = it },
                    label = { Text("توضيح الإجابة للطفل") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("إلغاء", color = Color.DarkGray)
                    }
                    Button(
                        enabled = questionText.isNotBlank() && correctOption.isNotBlank() && wrongOption1.isNotBlank(),
                        onClick = {
                            onSave(selectedSubject, questionText, correctOption, wrongOption1, wrongOption2, explanation)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = KidsTeal),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("حفظ السؤال", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ParentalSettingsTab(
    soundEnabled: Boolean,
    ttsEnabled: Boolean,
    onToggleSound: () -> Unit,
    onToggleTts: () -> Unit,
    onClearHistory: () -> Unit,
    playClickSound: () -> Unit
) {
    var showConfirmDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Sound & TTS Settings Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "إعدادات الصوت والنطق 🔊",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = KidsTeal
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("المؤثرات الصوتية والتشجيع", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text("أصوات النقر والإجابات الصحيحة", fontSize = 11.sp, color = Color.Gray)
                    }
                    Switch(
                        checked = soundEnabled,
                        onCheckedChange = { onToggleSound() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = KidsTeal)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("القراءة الصوتية التلقائية", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text("نطق الأسئلة والقصص وعبارات التحفيز", fontSize = 11.sp, color = Color.Gray)
                    }
                    Switch(
                        checked = ttsEnabled,
                        onCheckedChange = { onToggleTts() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = KidsBlue)
                    )
                }
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "قسم الحماية وتصفير البيانات 🛡️",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = KidsRed
                )
                Text(
                    text = "من خلال إعدادات السلامة هذه، يمكن لأولياء الأمور أو المعلمين مسح سجل محاولات الطالب بالكامل للبدء مجدداً بنظام نجوم نظيف.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp
                )

                Button(
                    onClick = {
                        playClickSound()
                        showConfirmDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = KidsRed),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("clear_history_trigger")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "مسح وحذف سجل المحاولات بالكامل",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "توجيهات تربوية للصف الأول 🏫",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = KidsBlue
                )
                Text(
                    text = "• المراجعة مخصصة للأطفال من سن 6-7 سنوات.\n" +
                        "• تحرص الأسئلة على تنمية مهارة قراءة الحروف لتكوين الكلمات، والرياضيات باللغة الإنجليزية بالرموز البصرية.\n" +
                        "• زر التلميح 💡 يساعد الطفل على تضييق الخيارات بلطف دون توتر.\n" +
                        "• يمكنك جعل الطفل يستمع للأسئلة بالصوت عبر النقر على زر المكبر 🔊.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    lineHeight = 22.sp
                )
            }
        }

        if (showConfirmDialog) {
            Dialog(onDismissRequest = { showConfirmDialog = false }) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "تأكيد مسح البيانات 🔴",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = KidsRed
                        )
                        Text(
                            text = "هل أنت متأكد من رغبتك بالكامل في حذف تاريخ محاولات ودرجات طفلك وتصفير نجومه؟ هذا الإجراء لا يمكن التراجع عنه.",
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            color = Color.DarkGray
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    playClickSound()
                                    onClearHistory()
                                    showConfirmDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = KidsRed),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("confirm_clear_history_button")
                            ) {
                                Text("نعم، احذف", color = Color.White)
                            }

                            Button(
                                onClick = {
                                    playClickSound()
                                    showConfirmDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("إلغاء", color = Color.DarkGray)
                            }
                        }
                    }
                }
            }
        }
    }
}
