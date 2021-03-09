package com.jmcdale.ikea.watcher.ui.screens.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.jmcdale.ikea.watcher.R
import com.jmcdale.ikea.watcher.local.MainStockItem
import com.jmcdale.ikea.watcher.local.formatItemNumber
import com.jmcdale.ikea.watcher.local.upcomingStock
import com.jmcdale.ikea.watcher.ui.FullScreenDialog
import com.jmcdale.ikea.watcher.ui.theme.IkeaWatcherTheme
import dev.chrisbanes.accompanist.coil.CoilImage
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


@Composable
fun ItemDialog(item: MainStockItem, onDismissRequest: () -> Unit) {
    FullScreenDialog(onDismissRequest = onDismissRequest) {
        Surface(color = IkeaWatcherTheme.colors.background, modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(IkeaWatcherTheme.dimens.margin)) {
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close_24),
                        contentDescription = null,
                        modifier = Modifier.clickable(onClick = onDismissRequest)
                    )
                }
                ItemDetails(item = item)
            }
        }
    }
}

@Composable
fun ColumnScope.ItemDetails(item: MainStockItem) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        CoilImage(
            data = item.imageUrl ?: "",
            contentDescription = null,
            fadeIn = true,
            modifier = Modifier.size(128.dp)
        )
    }
    Text(text = item.itemName)
    Text(text = item.itemNumber.formatItemNumber())
    Spacer(modifier = Modifier.height(IkeaWatcherTheme.dimens.margin))
    if (item.itemStock != null) {
        if (item.itemStock.availableStock > 0) {
            Text(text = "${item.itemStock.availableStock} Available")
        } else {
            Text(text = "Unavailable")
        }
        Spacer(modifier = Modifier.height(IkeaWatcherTheme.dimens.margin))
        if (item.itemStock.availabilityDetails.isNotEmpty()) {
            Text(text = "Notes:")
        }
        item.itemStock.availabilityDetails.forEach {
            Text(text = "- ${it.message}")
        }
        Spacer(modifier = Modifier.height(IkeaWatcherTheme.dimens.margin))
        if (item.itemStock.restockDateTime != null) {
            Text(text = "Estimated Restock Date:")
            val restockDate = item.itemStock.restockDateTime.format(
                DateTimeFormatter.ofLocalizedDate(
                    FormatStyle.SHORT
                )
            )
            Text(text = restockDate)
            Text(text = "Estimated Restock Amount: ${item.upcomingStock().toString()}")
        }
        Spacer(modifier = Modifier.height(IkeaWatcherTheme.dimens.margin))
        StockItemForecast(item.itemStock.stockForecast)
    }

    val refreshTime =
        item.lastRefreshTime?.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
            ?: "Never"
    Text(text = "Last Update: $refreshTime")
}