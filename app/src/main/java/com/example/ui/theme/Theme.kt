package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppThemeMode(val titleAr: String) {
    SYSTEM("تلقائي (حسب النظام)"),
    LIGHT("الوضع النهاري (فاتح)"),
    DARK("الوضع الليلي (داكن)")
}

enum class ColorPalette(
    val titleAr: String,
    val description: String,
    val primaryColor: Color,
    val secondaryColor: Color
) {
    EMERALD_GOLD(
        titleAr = "الزمردي الملكي والذهبي",
        description = "الأخضر الزمردي التراثي مع لمسات ذهبية عريقة",
        primaryColor = Color(0xFF135D46),
        secondaryColor = Color(0xFFB88E28)
    ),
    DEEP_SAPPHIRE(
        titleAr = "الياقوت الكحلي والفضي",
        description = "الأزرق الكحلي الوقور مع درجات الفضة الصافية",
        primaryColor = Color(0xFF1E3A8A),
        secondaryColor = Color(0xFF3B82F6)
    ),
    DAMASCUS_AMBER(
        titleAr = "العنبر الأندلسي والبرونز",
        description = "العنبري الدافئ والبرونز المستوحى من خطوط الحلية",
        primaryColor = Color(0xFF92400E),
        secondaryColor = Color(0xFFD97706)
    ),
    ISLAMIC_OLIVE(
        titleAr = "الزيتوني العتيق والنحاس",
        description = "الأخضر الزيتوني الهادئ مع النحاس العتيق",
        primaryColor = Color(0xFF3F4F28),
        secondaryColor = Color(0xFFB45309)
    )
}

fun createColorScheme(
    isDark: Boolean,
    palette: ColorPalette
): ColorScheme {
    return if (isDark) {
        darkColorScheme(
            primary = when (palette) {
                ColorPalette.EMERALD_GOLD -> Color(0xFF4EDEAE)
                ColorPalette.DEEP_SAPPHIRE -> Color(0xFF93C5FD)
                ColorPalette.DAMASCUS_AMBER -> Color(0xFFFBBF24)
                ColorPalette.ISLAMIC_OLIVE -> Color(0xFFA3E635)
            },
            onPrimary = Color(0xFF003828),
            primaryContainer = palette.primaryColor,
            onPrimaryContainer = Color.White,
            secondary = palette.secondaryColor,
            onSecondary = Color.Black,
            background = Color(0xFF0F1715),
            surface = Color(0xFF162320),
            surfaceVariant = Color(0xFF1E302B),
            onBackground = Color(0xFFE2E8F0),
            onSurface = Color(0xFFE2E8F0),
            onSurfaceVariant = Color(0xFF94A3B8)
        )
    } else {
        lightColorScheme(
            primary = palette.primaryColor,
            onPrimary = Color.White,
            primaryContainer = palette.primaryColor.copy(alpha = 0.15f),
            onPrimaryContainer = palette.primaryColor,
            secondary = palette.secondaryColor,
            onSecondary = Color.White,
            secondaryContainer = palette.secondaryColor.copy(alpha = 0.15f),
            onSecondaryContainer = palette.secondaryColor,
            background = Color(0xFFFAF9F5),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFF1EFEA),
            onBackground = Color(0xFF191C1B),
            onSurface = Color(0xFF191C1B),
            onSurfaceVariant = Color(0xFF475569)
        )
    }
}

@Composable
fun WasiyyahTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    palette: ColorPalette = ColorPalette.EMERALD_GOLD,
    content: @Composable () -> Unit,
) {
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemDark
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }
    val colorScheme = createColorScheme(isDark, palette)
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
