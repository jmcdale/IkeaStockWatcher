package com.jmcdale.ikea.watcher.ui.theme

import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

object IkeaWatcherColors {
    val ikeaBackgroundLight = Color(0xFFFFFFFF)
    val ikeaBackgroundDark = Color(0xFF242735)

    val ikeaBlue = Color(0xFF0051BA)
    val ikeaYellow = Color(0xFFFFDA1A)

    val ikeaBlueLight = Color(0xFF587CED)
    val ikeaBlueDark = Color(0xFF002A89)

    val ikeaYellowLight = Color(0xFFFFFF5B)
    val ikeaYellowDark = Color(0xFFC7A900)

    val white = Color(0xFFFFFFFF)
    val black = Color(0xFF000000)

    val lightRed = Color(0xFFFF5533)
    val lightGreen = Color(0xFF83EE51)
    val lightYellow = Color(0xFFFFFF5B)

}

interface IkeaWatcherColorPalette {
    val primary: Color
    val primaryVariant: Color
    val secondary: Color
    val secondaryVariant: Color
    val background: Color
    val surface: Color
    val error: Color
    val onPrimary: Color
    val onSecondary: Color
    val onBackground: Color
    val onSurface: Color
    val onError: Color

    val redLight:Color
    val yellowLight:Color
    val greenLight:Color

    val materialColors: Colors
}

object IkeaWatcherDarkColorPalette : IkeaWatcherColorPalette {
    override val primary = IkeaWatcherColors.ikeaBlue
    override val primaryVariant = IkeaWatcherColors.ikeaBlueLight
    override val secondary = IkeaWatcherColors.ikeaYellow
    override val secondaryVariant: Color = IkeaWatcherColors.ikeaYellowLight
    override val background = IkeaWatcherColors.ikeaBackgroundDark
    override val surface = Color(0xFF121212)
    override val error = Color(0xFFCF6679)
    override val onPrimary = IkeaWatcherColors.white
    override val onSecondary = IkeaWatcherColors.black
    override val onBackground = Color.White
    override val onSurface = Color.White
    override val onError = Color.Black

    override val redLight = IkeaWatcherColors.ikeaBackgroundLight
    override val yellowLight = IkeaWatcherColors.lightYellow
    override val greenLight = IkeaWatcherColors.lightGreen

    override val materialColors = darkColors(
        primary = primary,
        primaryVariant = primaryVariant,
        secondary = secondary,
        secondaryVariant = secondaryVariant,
        background = background,
        surface = surface,
        error = error,
        onPrimary = onPrimary,
        onSecondary = onSecondary,
        onBackground = onBackground,
        onSurface = onSurface,
        onError = onError
    )
}

object IkeaWatcherLightColorPalette : IkeaWatcherColorPalette {
    override val primary = IkeaWatcherColors.ikeaBlue
    override val primaryVariant = IkeaWatcherColors.ikeaBlueLight
    override val secondary = IkeaWatcherColors.ikeaYellow
    override val secondaryVariant = IkeaWatcherColors.ikeaYellowLight
    override val background = IkeaWatcherColors.ikeaBackgroundLight
    override val surface = Color.White
    override val error = Color(0xFFB00020)
    override val onPrimary = IkeaWatcherColors.white
    override val onSecondary = IkeaWatcherColors.black
    override val onBackground = Color.Black
    override val onSurface = Color.Black
    override val onError = Color.White

    override val redLight = IkeaWatcherColors.ikeaBackgroundLight
    override val yellowLight = IkeaWatcherColors.lightYellow
    override val greenLight = IkeaWatcherColors.lightGreen

    override val materialColors = lightColors(
        primary = primary,
        primaryVariant = primaryVariant,
        secondary = secondary,
        secondaryVariant = secondaryVariant,
        background = background,
        surface = surface,
        error = error,
        onPrimary = onPrimary,
        onSecondary = onSecondary,
        onBackground = onBackground,
        onSurface = onSurface,
        onError = onError
    )
}