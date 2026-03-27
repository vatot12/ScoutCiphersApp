package com.scoutcipher.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── Charcoal & Red Palette ──────────────────────────────────────────────────
val CharcoalDeep       = Color(0xFF121212)
val CharcoalSurface    = Color(0xFF1E1E1E)
val CharcoalCard       = Color(0xFF252525)
val CharcoalElevated   = Color(0xFF2C2C2C)
val CharcoalBorder     = Color(0xFF3A3A3A)

val RedPrimary         = Color(0xFFE03030)
val RedLight           = Color(0xFFFF5555)
val RedDark            = Color(0xFFB02020)
val RedContainer       = Color(0xFF3D1010)

val GoldAccent         = Color(0xFFFFCC44)
val TextPrimary        = Color(0xFFF0F0F0)
val TextSecondary      = Color(0xFFAAAAAA)
val TextHint           = Color(0xFF666666)

// ── Light theme (fallback) ───────────────────────────────────────────────────
val LightBg            = Color(0xFFF5F5F5)
val LightSurface       = Color(0xFFFFFFFF)
val LightBorder        = Color(0xFFDDDDDD)

private val DarkColorScheme = darkColorScheme(
    primary          = RedPrimary,
    onPrimary        = Color.White,
    primaryContainer = RedContainer,
    onPrimaryContainer = RedLight,
    secondary        = GoldAccent,
    onSecondary      = CharcoalDeep,
    background       = CharcoalDeep,
    onBackground     = TextPrimary,
    surface          = CharcoalSurface,
    onSurface        = TextPrimary,
    surfaceVariant   = CharcoalCard,
    onSurfaceVariant = TextSecondary,
    outline          = CharcoalBorder,
    error            = RedLight,
)

private val LightColorScheme = lightColorScheme(
    primary          = RedDark,
    onPrimary        = Color.White,
    primaryContainer = Color(0xFFFFDAD6),
    background       = LightBg,
    onBackground     = Color(0xFF1A1A1A),
    surface          = LightSurface,
    onSurface        = Color(0xFF1A1A1A),
    surfaceVariant   = Color(0xFFF0EDED),
    outline          = LightBorder,
)

@Composable
fun ScoutCipherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = AppTypography,
        content     = content
    )
}
