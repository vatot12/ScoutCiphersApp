package com.scoutcipher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scoutcipher.MainViewModel
import com.scoutcipher.ui.components.*
import com.scoutcipher.ui.theme.GoldAccent
import com.scoutcipher.ui.theme.RedPrimary

@Composable
fun ChallengeScreen(viewModel: MainViewModel) {
    val scrollState = rememberScrollState()
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val encoded by viewModel.challengeEncoded.observeAsState("")
    val cipherName by viewModel.challengeCipherName.observeAsState("")
    val answer by viewModel.challengeAnswer.observeAsState("")
    val resultText by viewModel.challengeResultText.observeAsState("")
    val resultOk by viewModel.challengeResultOk.observeAsState(null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Score
        ScoutCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatBox(stats.score.toString(), "النقاط 🏅", MaterialTheme.colorScheme.primary)
                StatBox(stats.correct.toString(), "صحيح ✅", MaterialTheme.colorScheme.secondary)
                StatBox(stats.wrong.toString(), "خطأ ❌", RedPrimary)
                StatBox(stats.streak.toString(), "تتالي 🔥", GoldAccent)
            }
        }

        // Challenge
        ScoutCard {
            SectionLabel("🎯 تحدي اليوم")

            // Encoded message box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(2.dp, GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        "النص المشفر:",
                        style = MaterialTheme.typography.labelSmall.copy(color = GoldAccent, letterSpacing = 1.sp),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                    Spacer(Modifier.height(8.dp))
                    if (encoded.isEmpty()) {
                        Text(
                            "اضغط \"تحدي جديد\" للبدء",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Text(
                            encoded,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurface,
                                textDirection = TextDirection.Rtl,
                                lineHeight = 28.sp
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            Text(
                                "الشفرة: $cipherName",
                                style = MaterialTheme.typography.labelSmall.copy(color = GoldAccent)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Button(
                onClick = { viewModel.generateChallenge() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = MaterialTheme.colorScheme.background)
            ) {
                Text("تحدي جديد 🎲", style = MaterialTheme.typography.titleMedium)
            }

            ScoutDivider()

            ArabicTextField(
                value = answer,
                onValueChange = { viewModel.challengeAnswer.value = it },
                label = "إجابتك",
                placeholder = "اكتب النص المفكوك هنا...",
                minLines = 2
            )

            Spacer(Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { viewModel.checkAnswer() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    enabled = encoded.isNotEmpty()
                ) {
                    Text("تحقق من الإجابة")
                }
                OutlinedButton(
                    onClick = { viewModel.revealAnswer() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    enabled = encoded.isNotEmpty()
                ) {
                    Text("اكشف الإجابة")
                }
            }

            // Result
            if (resultText.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            when (resultOk) {
                                true  -> MaterialTheme.colorScheme.primaryContainer
                                false -> MaterialTheme.colorScheme.errorContainer
                                null  -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                        .padding(14.dp)
                ) {
                    Text(
                        resultText,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = when (resultOk) {
                                true  -> MaterialTheme.colorScheme.primary
                                false -> MaterialTheme.colorScheme.error
                                null  -> GoldAccent
                            },
                            textDirection = TextDirection.Rtl,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(Modifier.height(80.dp))
    }
}
