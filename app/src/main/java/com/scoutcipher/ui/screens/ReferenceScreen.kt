package com.scoutcipher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scoutcipher.cipher.ArabicAlphabet
import com.scoutcipher.cipher.PolybiusCipher
import com.scoutcipher.ui.components.ScoutCard
import com.scoutcipher.ui.components.ScoutDivider
import com.scoutcipher.ui.components.SectionLabel
import com.scoutcipher.ui.theme.GoldAccent

@Composable
fun ReferenceScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item { ArabicAlphabetRef() }
        item { CaesarRef() }
        item { AtbashRef() }
        item { MorseRef() }
        item { PolybiusRef() }
        item { RailFenceRef() }
        item { NumberSubRef() }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
fun ArabicAlphabetRef() {
    ScoutCard {
        SectionLabel("🔤 الأبجدية العربية (28 حرف)")
        val letters = ArabicAlphabet.letters
        val rows = letters.chunked(7)
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                row.forEach { letter ->
                    val idx = ArabicAlphabet.charToIndex[letter]!!
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(6.dp))
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(letter.toString(), style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary), textAlign = TextAlign.Center)
                            Text("${idx + 1}", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp), textAlign = TextAlign.Center)
                        }
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
        }
    }
}

@Composable
fun CaesarRef() {
    ScoutCard {
        SectionLabel("🏛️ قيصر — مثال بإزاحة +3")
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(androidx.compose.foundation.rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ArabicAlphabet.letters.take(10).forEachIndexed { i, letter ->
                val shifted = ArabicAlphabet.letters[(i + 3) % ArabicAlphabet.size]
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(letter.toString(), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                    Text("↓", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(shifted.toString(), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Text("... وهكذا لجميع الحروف الـ28", style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, textDirection = TextDirection.Rtl))
    }
}

@Composable
fun AtbashRef() {
    ScoutCard {
        SectionLabel("🔄 المرآة — عكس الأبجدية")
        val pairs = (0 until 14).map { ArabicAlphabet.letters[it] to ArabicAlphabet.letters[27 - it] }
        pairs.chunked(7).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                row.forEach { (a, b) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(6.dp))
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "${a}↔${b}",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary, fontSize = 11.sp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
        }
    }
}

@Composable
fun MorseRef() {
    val morseMap = mapOf(
        'أ' to ".-", 'ب' to "-...", 'ت' to "-.-.", 'ث' to "-.-",
        'ج' to ".---", 'ح' to "....", 'خ' to "---", 'د' to "-..",
        'ذ' to "---..", 'ر' to ".-.", 'ز' to "--.", 'س' to "...",
        'ش' to "----", 'ص' to "-..-", 'ض' to "...-", 'ط' to "..-",
        'ظ' to "-.--", 'ع' to ".-.-", 'غ' to "--.", 'ف' to "..-.",
        'ق' to "--.-", 'ك' to "-.-", 'ل' to ".-..", 'م' to "--",
        'ن' to "-.", 'ه' to ".....", 'و' to ".--", 'ي' to ".."
    )
    ScoutCard {
        SectionLabel("📡 مورس عربي")
        Text(
            "افصل بين الأحرف بمسافة، وبين الكلمات بـ /",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, textDirection = TextDirection.Rtl),
            modifier = Modifier.padding(bottom = 10.dp)
        )
        val rows = morseMap.entries.toList().chunked(4)
        rows.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                row.forEach { (letter, code) ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                            .padding(vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(letter.toString(), style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary))
                        Text(code, style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, color = GoldAccent, fontSize = 10.sp))
                    }
                }
                repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
            }
            Spacer(Modifier.height(4.dp))
        }
    }
}

@Composable
fun PolybiusRef() {
    ScoutCard {
        SectionLabel("🔲 مربع بوليبيوس (5×6)")
        Text(
            "الرقم الأول = الصف، الرقم الثاني = العمود",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            modifier = Modifier.padding(bottom = 10.dp)
        )
        val grid = PolybiusCipher.getGrid()
        // Header
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Spacer(Modifier.width(28.dp))
            (1..PolybiusCipher.colCount).forEach { c ->
                Text(c.toString(), style = MaterialTheme.typography.labelSmall.copy(color = GoldAccent), modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            }
        }
        grid.forEachIndexed { r, row ->
            Spacer(Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("${r + 1}", style = MaterialTheme.typography.labelSmall.copy(color = GoldAccent), modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
                row.forEach { letter ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (letter != null) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.background)
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = if (letter != null) 1f else 0.3f), RoundedCornerShape(6.dp))
                            .padding(vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            letter?.toString() ?: "",
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RailFenceRef() {
    ScoutCard {
        SectionLabel("🚂 السياج — مثال 3 أسيجة")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            Column {
                Text("أ · · · ذ · · · ص · · · م", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.primary, letterSpacing = 1.sp))
                Text("· ب · ج · ر · ع · ف · ن", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace, color = GoldAccent, letterSpacing = 1.sp))
                Text("· · ت · · · ز · · · ق · ·", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.secondary, letterSpacing = 1.sp))
                Spacer(Modifier.height(6.dp))
                Text("اقرأ الصفوف من اليمين إلى اليسار", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
            }
        }
    }
}

@Composable
fun NumberSubRef() {
    ScoutCard {
        SectionLabel("🔢 الأرقام — كل حرف له رقم")
        val examples = listOf("أ=1", "ب=2", "ت=3", "ث=4", "ج=5", "ح=6", "خ=7", "د=8", "ذ=9", "ر=10", "ز=11", "س=12", "ش=13", "ص=14")
        val rows = examples.chunked(7)
        rows.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                row.forEach { pair ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(6.dp))
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(pair, style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary, fontSize = 10.sp), textAlign = TextAlign.Center)
                    }
                }
                repeat(7 - row.size) { Spacer(Modifier.weight(1f)) }
            }
            Spacer(Modifier.height(4.dp))
        }
        Text("... وهكذا حتى ي=28 | الكلمات تُفصل بـ 0", style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant), modifier = Modifier.padding(top = 6.dp))
    }
}
