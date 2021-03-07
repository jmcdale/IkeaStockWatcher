package com.jmcdale.ikea.watcher.ui.screens.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jmcdale.ikea.watcher.local.MainStockItem
import com.jmcdale.ikea.watcher.local.formatItemNumber
import com.jmcdale.ikea.watcher.remote.StockForecast
import com.jmcdale.ikea.watcher.ui.theme.IkeaWatcherTheme
import dev.chrisbanes.accompanist.coil.CoilImage
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


@Composable
fun StockItem(
    item: MainStockItem,
    onClick: (item: MainStockItem) -> Unit
) {
    Column(modifier = Modifier.padding(0.dp, 4.dp)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick(item) },
            border = BorderStroke(1.dp, Color.Black)
        ) {
            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                CoilImage(
                    data = item.imageUrl ?: "",
                    contentDescription = null,
                    fadeIn = true,
                    modifier = Modifier.size(64.dp)
                )
                Column(modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp)) {
                    if (item.itemStock != null) {
                        KnownStockItem(item, onClick)
                    } else {
                        UnknownStockItem(item, onClick)
                    }
                }
            }
        }
    }
}

@Composable
fun UnknownStockItem(item: MainStockItem, onClick: (item: MainStockItem) -> Unit) {
    Text(text = item.itemName)
    Text(text = item.itemNumber.formatItemNumber())
    val refreshTime =
        item.lastRefreshTime?.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
            ?: "Never"
    Text(text = "Last Update: $refreshTime")
}

@Composable
fun KnownStockItem(item: MainStockItem, onClick: (item: MainStockItem) -> Unit) {
    Text(text = item.itemName)
    Text(text = item.itemNumber.formatItemNumber())
    val refreshTime =
        item.lastRefreshTime?.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
            ?: "Never"
    Text(text = "Last Update: $refreshTime")
    if (item.itemStock!!.availableStock > 0) {
        Text(text = "${item.itemStock.availableStock} Available")
    } else {
        Text(text = "Unavailable")
    }
    StockItemForecast(item.itemStock.stockForecast)
}

@Composable
fun StockItemForecast(forecast: List<StockForecast>) {
    Row(horizontalArrangement = Arrangement.SpaceBetween) {
        forecast.forEach {
            StockItemForecastItem(forecast = it)
        }
    }
//    item.itemStock?.stockForecast
//    Text(text = item.itemName)
//    Text(text = item.itemNumber.formatItemNumber())
//    val refreshTime = item.lastRefreshTime?.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)) ?: "Never"
//    Text(text = "Last Update: $refreshTime")
//    if (item.itemStock!!.availableStock > 0) {
//        Text(text = "${item.itemStock.availableStock} Available")
//    } else {
//        Text(text = "Unavailable")
//    }
}

@Composable
fun StockItemForecastItem(forecast: StockForecast) {
    val dayOfMonth = forecast.date.dayOfMonth
    val availability = forecast.availableStock

    val color = when (availability) {
        0 -> Color.Red
        in 1..9 -> Color.Yellow
        else -> Color.Green
    }
    val painter = remember { ColorPainter(color) }

    Box(modifier = Modifier
        .padding(4.dp)
        .size(24.dp)
        .clip(MaterialTheme.shapes.small)){
        Box(
            modifier = Modifier
                .border(1.dp, MaterialTheme.colors.onSurface, MaterialTheme.shapes.small)
                .paint(painter),
        )
        Text(text = dayOfMonth.toString(), modifier = Modifier.align(Alignment.Center))
    }


}

//@Composable
//fun StockItemForecastItem(forecast: StockForecast) {
//    val dayOfMonth = forecast.date.dayOfMonth
//    val availability = forecast.availableStock
//
//    val color = when (availability) {
//        0 -> Color.Red
//        in 1..9 -> Color.Yellow
//        else -> Color.Green
//    }
//    val painter = remember { ColorPainter(color) }
//    Box(
//        modifier = Modifier
//            .padding(4.dp)
//            .size(24.dp)
//            .clip(MaterialTheme.shapes.small)
//            .border(1.dp, MaterialTheme.colors.onSurface, MaterialTheme.shapes.small)
//            .paint(painter),
//    ) {
//        Text(text = dayOfMonth.toString())
//    }
//
//}

@Preview
@Composable
fun StockItemForecastLightPreview() {
    IkeaWatcherTheme {
        Surface(color = MaterialTheme.colors.background) {
            Column(modifier = Modifier.fillMaxWidth()) {
                StockItemForecast(forecast = MockStockItemForecastList)
            }
        }
    }
}

val MockStockItemForecastList = listOf(
    StockForecast(
        date = LocalDate.now().plusDays(1),
        probability = "HIGH",
        availableStock = 0,
        stockType = "STORE"
    ),
    StockForecast(
        date = LocalDate.now().plusDays(2),
        probability = "HIGH",
        availableStock = 1,
        stockType = "STORE"
    ),
    StockForecast(
        date = LocalDate.now().plusDays(3),
        probability = "HIGH",
        availableStock = 1,
        stockType = "STORE"
    ),
    StockForecast(
        date = LocalDate.now().plusDays(4),
        probability = "HIGH",
        availableStock = 100,
        stockType = "STORE"
    )
)