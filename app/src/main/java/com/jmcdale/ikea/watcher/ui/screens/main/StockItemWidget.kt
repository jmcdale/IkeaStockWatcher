package com.jmcdale.ikea.watcher.ui.screens.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jmcdale.ikea.watcher.local.MainStockItem
import com.jmcdale.ikea.watcher.local.Mock
import com.jmcdale.ikea.watcher.local.formatItemNumber
import com.jmcdale.ikea.watcher.local.upcomingStock
import com.jmcdale.ikea.watcher.remote.IkeaItemStock
import com.jmcdale.ikea.watcher.remote.IkeaStore
import com.jmcdale.ikea.watcher.remote.StockForecast
import com.jmcdale.ikea.watcher.ui.theme.IkeaWatcherTheme
import com.jmcdale.ikea.watcher.ui.theme.toMedium
import dev.chrisbanes.accompanist.coil.CoilImage
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


@Composable
fun StockItem(
    item: MainStockItem,
    onClick: (item: MainStockItem) -> Unit
) {
    Column {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick(item) },
            border = BorderStroke(1.dp, Color.Black)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CoilImage(
                        data = item.imageUrl ?: "",
                        contentDescription = null,
                        fadeIn = true,
                        modifier = Modifier.size(64.dp)
                    )
                    Column(modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp)) {
                        Text(
                            text = "${item.numberDesired} ${item.itemName}",
                            style = IkeaWatcherTheme.typography.body1.toMedium()
                        )
                        Text(text = item.itemNumber.formatItemNumber())
                    }
                }

                Column(modifier = Modifier.padding(16.dp, 0.dp, 0.dp, 0.dp)) {
                    if (item.itemStocks.isNotEmpty()) {
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
    val refreshTime =
        item.lastRefreshTime?.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
            ?: "Never"
    Text(
        text = "Last Update: $refreshTime",
        style = IkeaWatcherTheme.typography.caption,
        textAlign = TextAlign.End,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun KnownStockItem(item: MainStockItem, onClick: (item: MainStockItem) -> Unit) {
    item.itemStocks.forEach {
        DividerSpacer()
        StockItemForStore(itemStock = it.value, store = IkeaStore.fromStoreId(it.key))
    }
    val refreshTime =
        item.lastRefreshTime?.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
            ?: "Never"
    Text(
        text = "Last Update: $refreshTime",
        style = IkeaWatcherTheme.typography.caption,
        textAlign = TextAlign.End,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun StockItemForStore(itemStock: IkeaItemStock, store: IkeaStore) {
    if (itemStock.availableStock > 0) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val color =
                if (itemStock.availableStock > 10) IkeaWatcherTheme.colors.greenLight
                else IkeaWatcherTheme.colors.yellowLight
            ColoredCircle(color = color)
            Text(
                text = "${itemStock.availableStock} Available at ${store.locationName}",
                modifier = Modifier.padding(
                    IkeaWatcherTheme.dimens.halfMargin,
                    IkeaWatcherTheme.dimens.none,
                    IkeaWatcherTheme.dimens.none,
                    IkeaWatcherTheme.dimens.none
                )
            )
        }
    } else {
        Text(text = "Unavailable at ${store.locationName}")
    }
    val restockDate =
        itemStock.restockDateTime?.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
            ?: "Unknown"

    if (itemStock.availableStock == 0) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val color =
                if (itemStock.upcomingStock() > 0) IkeaWatcherTheme.colors.yellowLight
                else IkeaWatcherTheme.colors.redLight
            ColoredCircle(color = color, itemStock.upcomingStock().toString())
            Text(
                text = "Estimated Restock Date: $restockDate",
                modifier = Modifier.padding(
                    IkeaWatcherTheme.dimens.halfMargin,
                    IkeaWatcherTheme.dimens.none,
                    IkeaWatcherTheme.dimens.none,
                    IkeaWatcherTheme.dimens.none
                )
            )
        }
    }
}

@Composable
fun StockItemForecast(forecast: List<StockForecast>) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        forecast.forEach {
            StockItemForecastItem(forecast = it)
        }
    }
}

@Composable
fun StockItemForecastItem(forecast: StockForecast) {
    val dayOfMonth = forecast.date.dayOfMonth
    val availability = forecast.availableStock

    val color = when (availability) {
        0 -> IkeaWatcherTheme.colors.redLight
        in 1..9 -> IkeaWatcherTheme.colors.yellowLight
        else -> IkeaWatcherTheme.colors.greenLight
    }
    val painter = remember { ColorPainter(color) }

    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(24.dp)
            .clip(MaterialTheme.shapes.small)
    ) {
        Box(
            modifier = Modifier
                .border(1.dp, MaterialTheme.colors.onSecondary, MaterialTheme.shapes.small)
                .paint(painter),
        )
        Text(
            text = dayOfMonth.toString(),
            modifier = Modifier.align(Alignment.Center),
            color = IkeaWatcherTheme.colors.onSecondary
        )
    }


}


@Composable
fun ColoredCircle(color: Color, text: String? = null) {

    val painter = remember { ColorPainter(color) }

    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(24.dp)
            .clip(CircleShape)
    ) {
        Box(
            modifier = Modifier
                .border(1.dp, MaterialTheme.colors.onSecondary, CircleShape)
                .paint(painter)
        )
        text?.let {
            Text(
                text = text,
                modifier = Modifier.align(Alignment.Center),
                color = IkeaWatcherTheme.colors.onSecondary
            )
        }
    }


}

@Composable
private fun DividerSpacer() {
    TabRowDefaults.Divider(
        modifier = Modifier.padding(
            IkeaWatcherTheme.dimens.none,
            IkeaWatcherTheme.dimens.quarterMargin
        )
    )
}

@Preview
@Composable
fun StockItemForecastLightPreview() {
    IkeaWatcherTheme {
        Surface(color = MaterialTheme.colors.background) {
            Column(modifier = Modifier.fillMaxWidth()) {
                StockItemForecast(forecast = Mock.mockStockItemForecastList)
            }
        }
    }
}
