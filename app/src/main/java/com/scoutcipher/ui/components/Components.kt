package com.scoutcipher.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scoutcipher.cipher.ALL_CIPHERS
import com.scoutcipher.cipher.CipherInfo
import com.scoutcipher.cipher.CipherType
import com.scoutcipher.cipher.Difficulty
import com.scoutcipher.ui.theme.RedPrimary
import com.scoutcipher.ui.theme.GoldAccent
import com.scoutcipher.ui.theme.CharcoalBorder

// ── ScoutCard ─────────────────────────────────────────────────────────────────
@Composable
fun ScoutCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

// ── SectionLabel ──────────────────────────────────────────────────────────────
@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.5.sp
        ),
        modifier = modifier.padding(bottom = 8.dp)
    )
}

// ── ArabicTextField ───────────────────────────────────────────────────────────
@Composable
fun ArabicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    minLines: Int = 3,
    singleLine: Boolean = false,
    placeholder: String = ""
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = if (placeholder.isNotEmpty()) {{ Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) }} else null,
        modifier = modifier.fillMaxWidth(),
        minLines = if (singleLine) 1 else minLines,
        maxLines = if (singleLine) 1 else 6,
        singleLine = singleLine,
        textStyle = LocalTextStyle.current.copy(
            textDirection = TextDirection.Rtl,
            fontFamily = FontFamily.Default,
            fontSize = 15.sp
        ),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        )
    )
}

// ── OutputBox ─────────────────────────────────────────────────────────────────
@Composable
fun OutputBox(text: String, label: String, modifier: Modifier = Modifier) {
    val clipboard = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            SectionLabel(label)
            if (text.isNotEmpty()) {
                IconButton(
                    onClick = {
                        clipboard.setText(AnnotatedString(text))
                        copied = true
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = "نسخ",
                        modifier = Modifier.size(16.dp),
                        tint = if (copied) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                .padding(14.dp)
                .defaultMinSize(minHeight = 70.dp)
        ) {
            if (text.isEmpty()) {
                Text(
                    text = "النتيجة ستظهر هنا...",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        textDirection = TextDirection.Rtl
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        LaunchedEffect(copied) {
            if (copied) {
                kotlinx.coroutines.delay(2000)
                copied = false
            }
        }
    }
}

// ── CipherSelectorGrid ────────────────────────────────────────────────────────
@Composable
fun CipherSelectorGrid(
    selected: CipherType,
    onSelect: (CipherType) -> Unit,
    modifier: Modifier = Modifier
) {
    val chunked = ALL_CIPHERS.chunked(2)
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        chunked.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { cipher ->
                    CipherChip(
                        cipher = cipher,
                        selected = cipher.type == selected,
                        onClick = { onSelect(cipher.type) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun CipherChip(
    cipher: CipherInfo,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    val bgColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val diffColor = when (cipher.difficulty) {
        Difficulty.EASY   -> Color(0xFF5CB85C)
        Difficulty.MEDIUM -> GoldAccent
        Difficulty.HARD   -> RedPrimary
    }
    val diffText = when (cipher.difficulty) {
        Difficulty.EASY   -> "سهل"
        Difficulty.MEDIUM -> "متوسط"
        Difficulty.HARD   -> "صعب"
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(cipher.emoji, fontSize = 24.sp)
            Spacer(Modifier.height(4.dp))
            Text(
                cipher.nameAr,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 13.sp
                ),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(3.dp))
            Text(
                diffText,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = diffColor,
                    fontSize = 10.sp
                )
            )
        }
    }
}

// ── StatBox ───────────────────────────────────────────────────────────────────
@Composable
fun StatBox(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            value,
            style = MaterialTheme.typography.displayLarge.copy(color = color, fontSize = 36.sp),
            textAlign = TextAlign.Center
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            ),
            textAlign = TextAlign.Center
        )
    }
}

// ── DividerLine ───────────────────────────────────────────────────────────────
@Composable
fun ScoutDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 14.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
        thickness = 1.dp
    )
}
