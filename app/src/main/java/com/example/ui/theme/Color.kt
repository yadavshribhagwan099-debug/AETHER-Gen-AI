package com.example.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

object AetherThemeManager {
    // Current active theme name
    var currentThemeName by mutableStateOf("nebula_solaris")
        private set

    // Colors backed by Compose state so that reading them triggers dynamic recomposition
    var spaceDark by mutableStateOf(Color(0xFF09040F))
    var nebulaBlue by mutableStateOf(Color(0xFF160924))
    var cyberCyan by mutableStateOf(Color(0xFFFF5E00))
    var plasmaPurple by mutableStateOf(Color(0xFFBD00FF))
    var solarGold by mutableStateOf(Color(0xFFFFD700))
    var hotPink by mutableStateOf(Color(0xFFFF007F))
    var darkGray by mutableStateOf(Color(0xFF220D36))
    var emerald by mutableStateOf(Color(0xFF00FF88))
    var electricOrange by mutableStateOf(Color(0xFFFF5E00))
    var slateObsidian by mutableStateOf(Color(0xFF040207))
    var textLight by mutableStateOf(Color(0xFFF2E6FF))
    var textMuted by mutableStateOf(Color(0xFFD2BBE8))
    var textCyber by mutableStateOf(Color(0xCCFF5E00))
    var glassBorder by mutableStateOf(Color(0x2AFFFF5E))
    var holoGridLine by mutableStateOf(Color(0x0CFF5E00))
    var overlaySolid by mutableStateOf(Color(0xE6160924))

    fun applyTheme(themeName: String) {
        currentThemeName = themeName
        when (themeName) {
            "hologram_grid" -> {
                spaceDark = Color(0xFF050B14)
                nebulaBlue = Color(0xFF0C1625)
                cyberCyan = Color(0xFF00E5FF)
                plasmaPurple = Color(0xFF00B0FF)
                solarGold = Color(0xFF76FF03)
                hotPink = Color(0xFFFF1744)
                darkGray = Color(0xFF132237)
                emerald = Color(0xFF00E676)
                electricOrange = Color(0xFFFF3D00)
                slateObsidian = Color(0xFF080F1B)
                textLight = Color(0xFFE0F7FA)
                textMuted = Color(0xFF8A9FB4)
                textCyber = Color(0xCC00E5FF)
                glassBorder = Color(0x2A00E5FF)
                holoGridLine = Color(0x0C00E5FF)
                overlaySolid = Color(0xE60C1625)
            }
            "slate_luxury" -> {
                spaceDark = Color(0xFF050505)
                nebulaBlue = Color(0xFF121212)
                cyberCyan = Color(0xFFFFFFFF)
                plasmaPurple = Color(0xFFFFD700)
                solarGold = Color(0xFF8E8E93)
                hotPink = Color(0xFFD4AF37)
                darkGray = Color(0xFF1C1C1E)
                emerald = Color(0xFFD4AF37)
                electricOrange = Color(0xFFE5E5EA)
                slateObsidian = Color(0xFF0A0A0A)
                textLight = Color(0xFFF2F2F7)
                textMuted = Color(0xFF9E9E9E)
                textCyber = Color(0xCCFFFFFF)
                glassBorder = Color(0x21FFFFFF)
                holoGridLine = Color(0x0CFFFFFF)
                overlaySolid = Color(0xE6121212)
            }
            "matrix_terminal" -> {
                spaceDark = Color(0xFF020603)
                nebulaBlue = Color(0xFF071209)
                cyberCyan = Color(0xFF39FF14)
                plasmaPurple = Color(0xFF00FF88)
                solarGold = Color(0xFFFFB300)
                hotPink = Color(0xFFFF3D00)
                darkGray = Color(0xFF0D2411)
                emerald = Color(0xFF39FF14)
                electricOrange = Color(0xFFFF9100)
                slateObsidian = Color(0xFF010301)
                textLight = Color(0xFFD4FAD6)
                textMuted = Color(0xFF88B38E)
                textCyber = Color(0xCC39FF14)
                glassBorder = Color(0x2B39FF14)
                holoGridLine = Color(0x0F39FF14)
                overlaySolid = Color(0xE6071209)
            }
            "nebula_solaris" -> {
                spaceDark = Color(0xFF09040F)
                nebulaBlue = Color(0xFF160924)
                cyberCyan = Color(0xFFFF5E00)
                plasmaPurple = Color(0xFFBD00FF)
                solarGold = Color(0xFFFFD700)
                hotPink = Color(0xFFFF007F)
                darkGray = Color(0xFF220D36)
                emerald = Color(0xFF00FF88)
                electricOrange = Color(0xFFFF5E00)
                slateObsidian = Color(0xFF040207)
                textLight = Color(0xFFF2E6FF)
                textMuted = Color(0xFFD2BBE8)
                textCyber = Color(0xCCFF5E00)
                glassBorder = Color(0x2AFFFF5E)
                holoGridLine = Color(0x0CFF5E00)
                overlaySolid = Color(0xE6160924)
            }
            "chrono_pink" -> {
                spaceDark = Color(0xFF090408)
                nebulaBlue = Color(0xFF1A0A13)
                cyberCyan = Color(0xFFED4B82)
                plasmaPurple = Color(0xFFFF5E00)
                solarGold = Color(0xFFBD00FF)
                hotPink = Color(0xFFFF2E93)
                darkGray = Color(0xFF2E1222)
                emerald = Color(0xFF00FF88)
                electricOrange = Color(0xFFFF5E00)
                slateObsidian = Color(0xFF040104)
                textLight = Color(0xFFFFEBF2)
                textMuted = Color(0xFFDFBACD)
                textCyber = Color(0xCCED4B82)
                glassBorder = Color(0x2AED4B82)
                holoGridLine = Color(0x0CED4B82)
                overlaySolid = Color(0xE61A0A13)
            }
            else -> { // "cyberpunk" (Default)
                spaceDark = Color(0xFF03050C)
                nebulaBlue = Color(0xFF0A0F24)
                cyberCyan = Color(0xFF00FFF0)
                plasmaPurple = Color(0xFFBD00FF)
                solarGold = Color(0xFFFFD700)
                hotPink = Color(0xFFFF007F)
                darkGray = Color(0xFF11162B)
                emerald = Color(0xFF00FF88)
                electricOrange = Color(0xFFFF5E00)
                slateObsidian = Color(0xFF070914)
                textLight = Color(0xFFE8EFFF)
                textMuted = Color(0xFFB5C9EB)
                textCyber = Color(0xCC00FFF0)
                glassBorder = Color(0x2A00FFF0)
                holoGridLine = Color(0x0C00FFF0)
                overlaySolid = Color(0xE60A0F24)
            }
        }
    }
}

// --- Dynamic getters backporting original static colors ---
val AetherSpaceDark: Color get() = AetherThemeManager.spaceDark
val AetherNebulaBlue: Color get() = AetherThemeManager.nebulaBlue
val AetherCyberCyan: Color get() = AetherThemeManager.cyberCyan
val AetherPlasmaPurple: Color get() = AetherThemeManager.plasmaPurple
val AetherSolarGold: Color get() = AetherThemeManager.solarGold
val AetherHotPink: Color get() = AetherThemeManager.hotPink
val AetherDarkGray: Color get() = AetherThemeManager.darkGray
val AetherEmerald: Color get() = AetherThemeManager.emerald
val AetherElectricOrange: Color get() = AetherThemeManager.electricOrange
val AetherSlateObsidian: Color get() = AetherThemeManager.slateObsidian
val AetherTextLight: Color get() = AetherThemeManager.textLight
val AetherTextMuted: Color get() = AetherThemeManager.textMuted
val AetherTextCyber: Color get() = AetherThemeManager.textCyber
val AetherGlassBorder: Color get() = AetherThemeManager.glassBorder
val AetherHoloGridLine: Color get() = AetherThemeManager.holoGridLine
val AetherOverlaySolid: Color get() = AetherThemeManager.overlaySolid
