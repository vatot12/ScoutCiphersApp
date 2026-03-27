package com.scoutcipher.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import com.scoutcipher.MainViewModel
import com.scoutcipher.cipher.*
import com.scoutcipher.ui.components.*

@Composable
fun EncodeDecodeScreen(viewModel: MainViewModel, isEncode: Boolean) {
    val scrollState = rememberScrollState()
    val selectedCipher by viewModel.selectedCipher.observeAsState(CipherType.CAESAR)
    val params by viewModel.cipherParams.observeAsState(CipherParams())
    val cipherInfo = ALL_CIPHERS.first { it.type == selectedCipher }

    val inputText = if (isEncode) viewModel.encodeInput.observeAsState("").value
                    else viewModel.decodeInput.observeAsState("").value
    val outputText = if (isEncode) viewModel.encodeOutput.observeAsState("").value
                     else viewModel.decodeOutput.observeAsState("").value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Cipher selector
        ScoutCard {
            SectionLabel(if (isEncode) "اختر شفرة التشفير" else "اختر شفرة فك التشفير")
            CipherSelectorGrid(
                selected = selectedCipher,
                onSelect = {
                    viewModel.selectedCipher.value = it
                    if (isEncode) viewModel.clearEncode() else viewModel.clearDecode()
                }
            )
        }

        // Cipher description
        ScoutCard {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(cipherInfo.emoji, style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        cipherInfo.nameAr,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        cipherInfo.descAr,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDirection = TextDirection.Rtl
                        ),
                        modifier = Modifier.padding(top = 3.dp)
                    )
                }
            }

            // Cipher-specific settings
            AnimatedVisibility(visible = cipherInfo.hasShiftParam || cipherInfo.hasKeyParam || cipherInfo.hasRailsParam) {
                Column {
                    ScoutDivider()
                    SectionLabel("إعدادات الشفرة")

                    if (cipherInfo.hasShiftParam) {
                        Row(
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "قيمة الإزاحة:",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = params.shift.toString(),
                                onValueChange = { s ->
                                    val v = s.toIntOrNull()?.coerceIn(1, 27) ?: 1
                                    viewModel.cipherParams.value = params.copy(shift = v)
                                },
                                modifier = Modifier.width(100.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(10.dp),
                                label = { Text("1-27") }
                            )
                        }
                    }

                    if (cipherInfo.hasKeyParam) {
                        Spacer(Modifier.height(8.dp))
                        ArabicTextField(
                            value = params.keyword,
                            onValueChange = { viewModel.cipherParams.value = params.copy(keyword = it) },
                            label = "كلمة المفتاح",
                            placeholder = "مثال: كشاف",
                            minLines = 1,
                            singleLine = true
                        )
                    }

                    if (cipherInfo.hasRailsParam) {
                        Spacer(Modifier.height(8.dp))
                        Row(
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "عدد الأسيجة:",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = params.rails.toString(),
                                onValueChange = { s ->
                                    val v = s.toIntOrNull()?.coerceIn(2, 5) ?: 2
                                    viewModel.cipherParams.value = params.copy(rails = v)
                                },
                                modifier = Modifier.width(100.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(10.dp),
                                label = { Text("2-5") }
                            )
                        }
                    }
                }
            }
        }

        // Input / Output
        ScoutCard {
            ArabicTextField(
                value = inputText,
                onValueChange = {
                    if (isEncode) viewModel.encodeInput.value = it
                    else viewModel.decodeInput.value = it
                },
                label = if (isEncode) "النص الأصلي" else "النص المشفر",
                placeholder = if (isEncode) "اكتب رسالتك هنا..." else "الصق النص المشفر هنا..."
            )

            Spacer(Modifier.height(14.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { if (isEncode) viewModel.encode() else viewModel.decode() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(if (isEncode) "تشفير ←" else "فك التشفير ←")
                }
                OutlinedButton(
                    onClick = { if (isEncode) viewModel.clearEncode() else viewModel.clearDecode() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("مسح")
                }
            }

            ScoutDivider()

            OutputBox(
                text = outputText,
                label = if (isEncode) "النص المشفر" else "النص المفكوك"
            )
        }

        Spacer(Modifier.height(80.dp))
    }
}
