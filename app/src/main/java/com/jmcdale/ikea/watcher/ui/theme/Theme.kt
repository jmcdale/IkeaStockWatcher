package com.jmcdale.ikea.watcher.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

// Theming strategy from:
// https://howiezuo.medium.com/building-a-design-system-implementation-using-jetpack-compose-part1-bc1de068a56d

object IkeaWatcherTheme {

    val colors: IkeaWatcherColorPalette
        @Composable get() = LocalIkeaWatcherColorPalette.current

    val typography: IkeaWatcherTypography
        @Composable get() = LocalIkeaWatcherTypography.current

    val dimens: IkeaWatcherDimens
        @Composable get() = LocalIkeaWatcherDimens.current
}

@Composable
fun IkeaWatcherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = if (darkTheme) {
        IkeaWatcherDarkColorPalette
    } else {
        IkeaWatcherLightColorPalette
    }

    val typography = IkeaWatcherTypography

    val dimens = IkeaWatcherDimens

    CompositionLocalProvider(
        LocalIkeaWatcherColorPalette provides colors,
        LocalIkeaWatcherTypography provides typography,
        LocalIkeaWatcherDimens provides dimens
    ) {
        MaterialTheme(
            colors = colors.materialColors,
            typography = typography.materialTypography,
            shapes = IkeaWatcherShapes,
            content = content
        )
    }
}

val LocalIkeaWatcherColorPalette =
    staticCompositionLocalOf<IkeaWatcherColorPalette> { IkeaWatcherLightColorPalette }

val LocalIkeaWatcherTypography = staticCompositionLocalOf { IkeaWatcherTypography }

val LocalIkeaWatcherDimens = staticCompositionLocalOf { IkeaWatcherDimens }