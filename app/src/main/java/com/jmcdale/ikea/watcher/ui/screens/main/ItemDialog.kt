package com.jmcdale.ikea.watcher.ui.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jmcdale.ikea.watcher.R
import com.jmcdale.ikea.watcher.local.MainStockItem
import com.jmcdale.ikea.watcher.local.Mock
import com.jmcdale.ikea.watcher.local.formatItemNumber
import com.jmcdale.ikea.watcher.local.upcomingStock
import com.jmcdale.ikea.watcher.remote.IkeaItemStock
import com.jmcdale.ikea.watcher.remote.IkeaStore
import com.jmcdale.ikea.watcher.ui.FullScreenDialog
import com.jmcdale.ikea.watcher.ui.theme.IkeaWatcherTheme
import com.jmcdale.ikea.watcher.ui.theme.toMedium
import dev.chrisbanes.accompanist.coil.CoilImage
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


@Composable
fun ItemDialog(item: MainStockItem, onDismissRequest: () -> Unit) {
    FullScreenDialog(onDismissRequest = onDismissRequest) {
        Surface(color = IkeaWatcherTheme.colors.background, modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(IkeaWatcherTheme.dimens.margin)
            ) {
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close_24),
                        contentDescription = null,
                        modifier = Modifier.clickable(onClick = onDismissRequest)
                    )
                }
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    ItemDetails(item = item)
                }
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
    Text(text = item.itemName, style = IkeaWatcherTheme.typography.h5)
    Text(text = item.itemNumber.formatItemNumber(), style = IkeaWatcherTheme.typography.subtitle1)
    DividerSpacer()
    if (item.itemStocks.isNotEmpty()) {
        item.itemStocks.forEach {
            ItemDetailsForStore(
                itemStock = it.value,
                store = IkeaStore.fromStoreId(it.key)
            )
            DividerSpacer()
        }
    }

    val refreshTime =
        item.lastRefreshTime?.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
            ?: "Never"
    Text(text = "Last Update: $refreshTime")
}

@Composable
fun ColumnScope.ItemDetailsForStore(itemStock: IkeaItemStock, store: IkeaStore) {
    if (itemStock.availableStock > 0) {
        Text(text = "${itemStock.availableStock} Available at ${store.abbreviation}")
    } else {
        Text(text = "Unavailable at ${store.abbreviation}", style = IkeaWatcherTheme.typography.body1.toMedium())
    }
    Spacer(modifier = Modifier.height(IkeaWatcherTheme.dimens.margin))
    if (itemStock.availabilityDetails.isNotEmpty()) {
        Text(text = "Notes:")
    }
    itemStock.availabilityDetails.forEach {
        Text(text = "- ${it.message}")
    }
    Spacer(modifier = Modifier.height(IkeaWatcherTheme.dimens.margin))
    if (itemStock.restockDateTime != null) {
        Text(text = "Estimated Restock Date:")
        val restockDate = itemStock.restockDateTime.format(
            DateTimeFormatter.ofLocalizedDate(
                FormatStyle.SHORT
            )
        )
        Text(text = restockDate)
        Text(text = "Estimated Restock Amount: ${itemStock.upcomingStock().toString()}")
    }
    Spacer(modifier = Modifier.height(IkeaWatcherTheme.dimens.margin))
    StockItemForecast(itemStock.stockForecast)
}

@Composable
private fun DividerSpacer(){
    Divider(
        modifier = Modifier.padding(
            IkeaWatcherTheme.dimens.none,
            IkeaWatcherTheme.dimens.halfMargin
        )
    )
}

@Preview
@Composable
fun PreviewItemDialogLight() {
    IkeaWatcherTheme(darkTheme = true) {
        Surface(color = Color.White) {
            Column(modifier = Modifier.padding(16.dp)) {
                ItemDetails(item = Mock.mockMainStockItem)
            }
        }
    }
}
