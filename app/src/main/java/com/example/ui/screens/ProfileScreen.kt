package com.example.ui.screens

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.KidsBlue
import com.example.ui.theme.KidsOrange
import com.example.ui.viewmodel.QuizViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    viewModel: QuizViewModel,
    onNavigateToHub: () -> Unit
) {
    val name by viewModel.studentName.collectAsState()
    val avatar by viewModel.studentAvatar.collectAsState()
    val isProfileCompleted by viewModel.isProfileCompleted.collectAsState()

    var tempName by remember { mutableStateOf(if (name == "بطلنا المستقبلي") "" else name) }
    var tempAvatar by remember { mutableStateOf(avatar) }

    LaunchedEffect(name, avatar) {
        if (tempName.isBlank() && name != "بطلنا المستقبلي") {
            tempName = name
        }
        tempAvatar = avatar
    }

    val avatarOptions = listOf("🌟", "🚀", "🐸", "🦄", "🐼", "🦊", "⚽", "🎨", "🏎️", "🦁", "🦖", "🧁")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = KidsOrange,
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "المراجع الصغير",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = KidsBlue,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.size(8.dp))
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = KidsOrange,
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "مرحباً بك يا بطل! اختر اسمك وشخصيتك المفضلة للدخول وعيش تجربة المراجعة الممتعة!",
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(3.dp, KidsOrange.copy(alpha = 0.5f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier.size(124.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = tempAvatar,
                    fontSize = 68.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        OutlinedTextField(
            value = tempName,
            onValueChange = { tempName = it },
            label = { Text("اكتب اسمك الرائع هنا (مثلاً: آدم)") },
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            ),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = KidsBlue,
                unfocusedBorderColor = KidsBlue.copy(alpha = 0.4f),
                focusedLabelColor = KidsBlue
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .testTag("student_name_input")
        )

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "اختر رمزك المفضل:",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        FlowRow(
            maxItemsInEachRow = 4,
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            avatarOptions.forEach { item ->
                val isSelected = tempAvatar == item
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) KidsOrange.copy(alpha = 0.22f)
                            else MaterialTheme.colorScheme.surface
                        )
                        .clickable {
                            viewModel.playClickSound()
                            tempAvatar = item
                        }
                        .testTag("avatar_option_$item")
                ) {
                    Text(text = item, fontSize = 32.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = {
                viewModel.playClickSound()
                viewModel.updateStudentProfile(tempName, tempAvatar)
                onNavigateToHub()
            },
            colors = ButtonDefaults.buttonColors(containerColor = KidsBlue),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .testTag("submit_profile_button")
        ) {
            Text(
                text = if (isProfileCompleted) "حفظ التعديلات والعودة 🚀" else "انطلاق لرحلة اللعب والتعلم 🚀",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
