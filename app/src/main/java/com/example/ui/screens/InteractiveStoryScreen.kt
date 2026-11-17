package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CardBlue
import com.example.ui.theme.CardCoral
import com.example.ui.theme.CardTeal
import com.example.ui.theme.CardYellow
import com.example.ui.theme.KidsBlue
import com.example.ui.theme.KidsGreen
import com.example.ui.theme.KidsOrange
import com.example.ui.theme.KidsPurple
import com.example.ui.theme.KidsTeal
import com.example.ui.theme.KidsYellow
import com.example.ui.viewmodel.QuizViewModel

data class StoryChoice(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val isOptimal: Boolean,
    val feedbackMessage: String,
    val nextNodeId: String
)

data class StoryNode(
    val id: String,
    val stepNumber: Int,
    val chapterTitle: String,
    val sceneEmoji: String,
    val narrativeText: String,
    val speaker: String,
    val speakerEmoji: String,
    val choices: List<StoryChoice>,
    val isFinalScene: Boolean = false
)

@Composable
fun InteractiveStoryScreen(
    viewModel: QuizViewModel,
    onNavigateBack: () -> Unit
) {
    val studentName by viewModel.studentName.collectAsState()

    val storyNodes = remember(studentName) {
        listOf(
            StoryNode(
                id = "start",
                stepNumber = 1,
                chapterTitle = "المحطة الأولى: مفترق الطرق السحري 🌲",
                sceneEmoji = "🏰🦉✨",
                speaker = "مُفَكِّر البومة الحكيمة",
                speakerEmoji = "🦉",
                narrativeText = "مرحباً يا بطلنا الغالي $studentName! أنا صديقك 'مُفَكِّر'. نحتاج اليوم للعثور على مفتاح حديقة المعرفة الذهبي. أمامنا طريقان، إلى أين تريد أن ننطلق أولاً؟",
                choices = listOf(
                    StoryChoice(
                        id = "c_letters",
                        title = "بستان الحروف والأزهار 🌸",
                        description = "طريق مليء بالكلمات والحيوانات اللطيفة!",
                        emoji = "📖",
                        isOptimal = true,
                        feedbackMessage = "اختيار رائع! لننطلق إلى بستان الحروف بحثاً عن أصدقائنا!",
                        nextNodeId = "letters_scene"
                    ),
                    StoryChoice(
                        id = "c_numbers",
                        title = "وادي الأرقام والجسور 🌉",
                        description = "طريق ممتع لحل الألغاز والعد مع الفراشات!",
                        emoji = "🔢",
                        isOptimal = true,
                        feedbackMessage = "يا لها من فكرة ذكية! وادي الأرقام ينتظر عبقريتنا!",
                        nextNodeId = "numbers_scene"
                    )
                )
            ),
            StoryNode(
                id = "letters_scene",
                stepNumber = 2,
                chapterTitle = "المحطة الثانية: شجرة الحروف وأرنوب 🐰",
                sceneEmoji = "🌳🐰🍎",
                speaker = "أَرْنُوب الصغير",
                speakerEmoji = "🐰",
                narrativeText = "أهلاً يا $studentName! أنا أرنوب، وأنا جائع جداً بعد جولة طويلة. أريد ثمرة شهية تبدأ بـحرف (التاء - ت) لأقوى وأعطيكم المفتاح الأخضر!",
                choices = listOf(
                    StoryChoice(
                        id = "c_apple_bismillah",
                        title = "نقطف تفاحة ونقول: (بِسْمِ اللَّه) 🍎",
                        description = "تبدأ بحرف التاء ونتبع آداب الطعام الإسلامية!",
                        emoji = "🍎",
                        isOptimal = true,
                        feedbackMessage = "ممتاز يا بطل! (تُفَّاحَة) تبدأ بالتاء، والتسمية تجلب البركة!",
                        nextNodeId = "river_scene"
                    ),
                    StoryChoice(
                        id = "c_carrot_no_name",
                        title = "نقطف وردة ملونة دون تسمية 🌸",
                        description = "نأكلها سريعاً دون ذكر اسم الله.",
                        emoji = "🌸",
                        isOptimal = false,
                        feedbackMessage = "انتبه يا بطل: التفاحة ألذ وتبدأ بحرف التاء، ودائماً نسمي الله قبل الأكل!",
                        nextNodeId = "river_scene"
                    )
                )
            ),
            StoryNode(
                id = "numbers_scene",
                stepNumber = 2,
                chapterTitle = "المحطة الثانية: جسر الفراشات العجيب 🦋",
                sceneEmoji = "🌉🦋✨",
                speaker = "مُفَكِّر البومة الحكيمة",
                speakerEmoji = "🦉",
                narrativeText = "انظر يا $studentName إلى هذا الجسر السحري! تطير فوقه 3 فراشات زرقاء، ثم انضمت إليها فراشتان ورديتان (3 + 2). لنعبر الجسر، يجب أن نضغط على الرقم الصحيح لمجموع الفراشات!",
                choices = listOf(
                    StoryChoice(
                        id = "c_num_5",
                        title = "الضغط على حجر الرقم (5) 🖐️",
                        description = "Because 3 + 2 equals 5!",
                        emoji = "5️⃣",
                        isOptimal = true,
                        feedbackMessage = "أنت عبقري رياضيات حقيقي! 5 butterflies opened the glowing bridge!",
                        nextNodeId = "river_scene"
                    ),
                    StoryChoice(
                        id = "c_num_3",
                        title = "الضغط على حجر الرقم (3) 3️⃣",
                        description = "نختار الرقم الأول فقط.",
                        emoji = "3️⃣",
                        isOptimal = false,
                        feedbackMessage = "تذكر يا بطل: 3 + 2 = 5! لكن الجسر فتح لعزيمتك!",
                        nextNodeId = "river_scene"
                    )
                )
            ),
            StoryNode(
                id = "river_scene",
                stepNumber = 3,
                chapterTitle = "المحطة الثالثة: ملتقى النهر ولغز القطة 🌊",
                sceneEmoji = "🌊🐱🐟",
                speaker = "الْقِطَّة الذَّكِيَّة",
                speakerEmoji = "🐱",
                narrativeText = "Meow! Hello $studentName! I live near the river, I have cute whiskers, and I love drinking milk. Which English word describes me?",
                choices = listOf(
                    StoryChoice(
                        id = "c_cat",
                        title = "The word is: (CAT) 🐱",
                        description = "القطة اللطيفة ذات الشوارب والمواء!",
                        emoji = "🐱",
                        isOptimal = true,
                        feedbackMessage = "Good job! C-A-T spells CAT! هاك مفتاح النهر الفضي!",
                        nextNodeId = "elder_scene"
                    ),
                    StoryChoice(
                        id = "c_dog",
                        title = "The word is: (DOG) 🐶",
                        description = "صديقنا الكلب الوفي.",
                        emoji = "🐶",
                        isOptimal = false,
                        feedbackMessage = "الكلب هو DOG وصوته هُو-هُو، أما القطة فهي CAT وصوتها مواء! أحسنت المحاولة!",
                        nextNodeId = "elder_scene"
                    )
                )
            ),
            StoryNode(
                id = "elder_scene",
                stepNumber = 4,
                chapterTitle = "المحطة الرابعة: تلة الحكمة والجد الطيب 👵",
                sceneEmoji = "⛰️👴📚",
                speaker = "الْجَدُّ حَكِيم",
                speakerEmoji = "👴",
                narrativeText = "السلام عليكم يا أبنائي الأعزاء! أنا الجد حكيم، أحمل سلة ثقيلة من كتب العلم إلى قمة التل وأشعر ببعض التعب. ماذا ستفعل يا $studentName؟",
                choices = listOf(
                    StoryChoice(
                        id = "c_help_elder",
                        title = "أسرع لمساعدته وأدعو له بالشفاء والعافية 🤝",
                        description = "احترام الكبير ومساعدة المحتاج من أعظم أخلاق المسلم!",
                        emoji = "💐",
                        isOptimal = true,
                        feedbackMessage = "بارك الله فيك يا بني! رسولنا الكريم علمنا أن نرحم الصغير ونوقر الكبير!",
                        nextNodeId = "final_scene"
                    ),
                    StoryChoice(
                        id = "c_ignore_elder",
                        title = "أركض مسرعاً إلى القمة لأصل أولاً 🏃",
                        description = "المهم أن أكون الأسرع في الوصول.",
                        emoji = "🏃",
                        isOptimal = false,
                        feedbackMessage = "البطولة الحقيقية هي في مساعدة الآخرين والتعاون مع كبار السن!",
                        nextNodeId = "final_scene"
                    )
                )
            ),
            StoryNode(
                id = "final_scene",
                stepNumber = 5,
                chapterTitle = "النهاية السعيدة: تتويج البطل الذكي 🏆",
                sceneEmoji = "🏰🎉⭐",
                speaker = "مُفَكِّر والجد حكيم",
                speakerEmoji = "🦉",
                narrativeText = "مبارك يا بطلنا $studentName! لقد اجتزت كل التحديات: عرفت الحروف وسميت الله، وحللت لغز الأرقام، وتحدثت الإنجليزية، وقدمت المساعدة لكبار السن! لقد أصبحت رسمياً: مستكشف المعرفة الذهبي!",
                choices = emptyList(),
                isFinalScene = true
            )
        )
    }

    var currentNodeId by remember { mutableStateOf("start") }
    var hasSavedFinale by remember { mutableStateOf(false) }
    val currentNode = storyNodes.firstOrNull { it.id == currentNodeId } ?: storyNodes.first()

    var showChoiceFeedback by remember { mutableStateOf(false) }
    var currentFeedbackMessage by remember { mutableStateOf("") }
    var nextPendingNodeId by remember { mutableStateOf("") }
    val userDecisionsLog = remember { mutableStateListOf<String>() }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopAudio()
        }
    }

    fun playSceneAudio(force: Boolean = false) {
        val isEnglishScene = currentNode.id == "river_scene"
        val speech = if (isEnglishScene) currentNode.narrativeText else "${currentNode.speaker} يقول: ${currentNode.narrativeText}"
        viewModel.soundEngine.speak(speech, isEnglish = isEnglishScene, force = force)
    }

    LaunchedEffect(currentNodeId) {
        if (currentNode.isFinalScene) {
            viewModel.soundEngine.playCelebrationSound()
            if (!hasSavedFinale) {
                hasSavedFinale = true
                viewModel.saveStoryAttempt("القصة التفاعلية: مغامرة بطلنا ومُفكر", stars = 6)
            }
        } else {
            playSceneAudio(force = false)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App bar
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
                    .testTag("story_back_button")
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
                    text = "القصة التفاعلية: مغامرة مُفَكِّر 📖✨",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "المحطة ${currentNode.stepNumber} من 5 • صمم مسار مغامرتك!",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }

            IconButton(
                onClick = {
                    viewModel.playClickSound()
                    playSceneAudio(force = true)
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(KidsTeal.copy(alpha = 0.15f))
                    .testTag("story_audio_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Listen",
                    tint = KidsTeal
                )
            }
        }

        // Story Chapter Progress Bar
        LinearProgressIndicator(
            progress = { currentNode.stepNumber / 5f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = KidsPurple,
            trackColor = KidsPurple.copy(alpha = 0.15f)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(KidsPurple.copy(alpha = 0.1f))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = currentNode.chapterTitle,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = KidsPurple
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                border = BorderStroke(2.dp, KidsBlue.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(listOf(CardYellow, CardBlue))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = currentNode.sceneEmoji, fontSize = 40.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardCoral)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(text = currentNode.speakerEmoji, fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = currentNode.speaker,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = KidsOrange
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = currentNode.narrativeText,
                        fontSize = 16.sp,
                        lineHeight = 26.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (!currentNode.isFinalScene) {
                Text(
                    text = "👇 ماذا تختار يا بطلنا الذكي؟",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = KidsOrange
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    currentNode.choices.forEachIndexed { index, choice ->
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (index == 0) CardTeal else CardYellow
                            ),
                            border = BorderStroke(
                                2.dp,
                                if (index == 0) KidsTeal else KidsOrange.copy(alpha = 0.7f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.playClickSound()
                                    currentFeedbackMessage = choice.feedbackMessage
                                    nextPendingNodeId = choice.nextNodeId
                                    userDecisionsLog.add(choice.title)
                                    showChoiceFeedback = true

                                    if (choice.isOptimal) {
                                        viewModel.soundEngine.playCorrectSound()
                                    } else {
                                        viewModel.soundEngine.speak(choice.feedbackMessage)
                                    }
                                }
                                .testTag("story_choice_${choice.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = choice.emoji, fontSize = 28.sp)
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = choice.title,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = choice.description,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                                    )
                                }

                                Text(text = "👈", fontSize = 20.sp)
                            }
                        }
                    }
                }
            } else {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = CardYellow),
                    border = BorderStroke(2.5.dp, KidsYellow),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🎖️ وسام الحكمة والإبداع 🎖️",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = KidsOrange
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row {
                            repeat(5) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = KidsYellow,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "تمت إضافة ٦ نجوم ذهبية لمجموع نقاطك! 🌟",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = KidsGreen
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    viewModel.playClickSound()
                                    hasSavedFinale = false
                                    currentNodeId = "start"
                                    userDecisionsLog.clear()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = KidsOrange)
                            ) {
                                Text("إعادة المغامرة 🔄", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    viewModel.playClickSound()
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
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showChoiceFeedback) {
        Dialog(onDismissRequest = { showChoiceFeedback = false }) {
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(3.dp, KidsTeal),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("story_decision_feedback_dialog")
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(KidsTeal.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "💡✨", fontSize = 36.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "قرارك التفاعلي!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = KidsTeal
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = currentFeedbackMessage,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF2C3E50)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            viewModel.playClickSound()
                            showChoiceFeedback = false
                            currentNodeId = nextPendingNodeId
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = KidsTeal)
                    ) {
                        Text(
                            text = "متابعة المغامرة 🚀",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
