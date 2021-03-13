package com.jmcdale.ikea.watcher.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.window.Dialog
import com.jmcdale.ikea.watcher.ui.theme.IkeaWatcherTheme

@Composable
fun LoadingDialog() {
    Dialog(onDismissRequest = { }) {
        CircularProgressIndicator()
    }
}
