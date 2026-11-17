package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CardBlue
import com.example.ui.theme.CardCoral
import com.example.ui.theme.CardTeal
import com.example.ui.theme.CardYellow
import com.example.ui.theme.KidsBlue
import com.example.ui.theme.KidsGreen
import com.example.ui.theme.KidsOrange
import com.example.ui.theme.KidsTeal
import com.example.ui.theme.KidsYellow
import com.example.ui.viewmodel.QuizViewModel

@Composable
fun WelcomeScreen(
    viewModel: QuizViewModel,
    onStartClick: () -> Unit,
    onNavigateToSection: (String) -> Unit = {}
) {
    val studentName by viewModel.studentName.collectAsState()
    val isProfileCompleted by viewModel.isProfileCompleted.collectAsState()
    var visible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        visible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = KidsYellow,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isProfileCompleted) "أَهْلاً بعودتك يَا $studentName! 👋" else "أَهْلاً بِكَ يَا بَطَل! 👋",
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Black,
                    color = KidsBlue,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = KidsYellow,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "مَرْحَباً فِي تَطْبِيقِ المُرَاجِعِ الصَّغِيرِ 🎓✨",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(22.dp))

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(600)) + slideInVertically(initialOffsetY = { -50 })
            ) {
                Box(
                    modifier = Modifier
                        .size(156.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(4.dp, KidsOrange, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_welcome_mascot_1782041676705),
                        contentDescription = "Mascot Smart Owl",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(
                    topStart = 24.dp,
                    topEnd = 24.dp,
                    bottomStart = 4.dp,
                    bottomEnd = 24.dp
                ),
                colors = CardDefaults.cardColors(containerColor = CardCoral),
                border = BorderStroke(2.dp, KidsOrange),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "أنا 'مُفَكِّرْ' 🦉",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = KidsOrange,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "رَفِيقُكَ الذَّكِيُّ فِي رِحْلَةِ المُرَاجَعَةِ بِاللَّعِبِ! اكتشف معنا اللغة العربية، Mathematics، English، الدراسات والتربية الإسلامية! 👇",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "🧭 جَوْلَةٌ سَرِيعَةٌ فِي أَقْسَامِ التطبيق:",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = KidsTeal,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                textAlign = TextAlign.Start
            )

            AppSectionCard(
                emoji = "📚✨",
                title = "تحديات المواد وتحدي اليوم",
                description = "٥٠ سؤالاً ممتعاً في العربية، الرياضيات (English)، الإنجليزية، التربية الإسلامية والدراسات الاجتماعية مع تلميحات ذكية وصوت ناطق!",
                containerColor = CardBlue,
                borderColor = KidsBlue,
                textColor = KidsBlue,
                onClick = {
                    viewModel.playClickSound()
                    onNavigateToSection("hub")
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            AppSectionCard(
                emoji = "🎮🧩",
                title = "الألعاب التفاعلية والقصص الذكية",
                description = "لعبة السحب والإفلات، لعبة الذاكرة والمطابقة، وقصة مُفَكِّر التفاعلية لجعل التعلم أكثر حماساً ومتعة!",
                containerColor = CardYellow,
                borderColor = KidsOrange,
                textColor = KidsOrange,
                onClick = {
                    viewModel.playClickSound()
                    onNavigateToSection("drag_drop")
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            AppSectionCard(
                emoji = "🏆🎖️",
                title = "غرفة الجوائز وبوابة الوالدين",
                description = "اجمع النجوم الذهبية، افتح ملصقات الأوسمة الثمانية، وتابع مستوى التحصيل الدراسي لكل مادة بالتفصيل!",
                containerColor = CardTeal,
                borderColor = KidsTeal,
                textColor = KidsTeal,
                onClick = {
                    viewModel.playClickSound()
                    onNavigateToSection("rewards")
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            AppSectionCard(
                emoji = "🌟👤",
                title = "الملف الشخصي والشخصيات",
                description = "اختر اسمك المميز ورمزك الكرتوني المفضل ليظهر في كل تحدياتك وإنجازاتك!",
                containerColor = CardCoral,
                borderColor = KidsGreen,
                textColor = KidsGreen,
                onClick = {
                    viewModel.playClickSound()
                    onNavigateToSection("profile")
                }
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    viewModel.playClickSound()
                    onStartClick()
                },
                colors = ButtonDefaults.buttonColors(containerColor = KidsOrange),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(3.dp, Color.White),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp)
                    .testTag("welcome_start_button")
            ) {
                Text(
                    text = if (isProfileCompleted) "دخول مركز التعلم 🚀" else "ابْدَأْ رِحْلَةَ اللَّعِبِ وَالتَّعَلُّمِ الآنَ 🚀",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun AppSectionCard(
    emoji: String,
    title: String,
    description: String,
    containerColor: Color,
    borderColor: Color,
    textColor: Color,
    onClick: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(2.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
            ) {
                Text(text = emoji, fontSize = 26.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    textAlign = TextAlign.Start,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
