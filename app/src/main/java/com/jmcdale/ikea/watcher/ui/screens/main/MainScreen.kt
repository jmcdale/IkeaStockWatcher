package com.jmcdale.ikea.watcher.ui.screens.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jmcdale.ikea.watcher.R
import com.jmcdale.ikea.watcher.local.MainStockItem
import com.jmcdale.ikea.watcher.local.formatItemNumber
import com.jmcdale.ikea.watcher.ui.FullScreenDialog
import com.jmcdale.ikea.watcher.ui.pullrefresh.PullToRefresh
import com.jmcdale.ikea.watcher.ui.theme.IkeaWatcherTheme
import dev.chrisbanes.accompanist.coil.CoilImage
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@ExperimentalAnimationApi
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val isLoading by viewModel.isLoading.observeAsState(initial = false)
    val stockItems by viewModel.stockItems.observeAsState(initial = listOf())
    val selectedItem by viewModel.selectedItem.observeAsState(initial = null)

    Surface(color = MaterialTheme.colors.background, modifier = Modifier.fillMaxSize()) {
        Column {
            TopAppBar(
                title = { Text(text = "Ikea Watcher") },
                backgroundColor = MaterialTheme.colors.primaryVariant,
                contentColor = MaterialTheme.colors.onPrimary
            )
            Column(Modifier.padding(IkeaWatcherTheme.dimens.margin, IkeaWatcherTheme.dimens.none)) {
                selectedItem?.let {
                    ItemDialog(
                        item = it,
                        onDismissRequest = viewModel::onItemDialogDismissRequest
                    )
                }
                PullToRefresh(
                    isRefreshing = isLoading,
                    onRefresh = viewModel::onRefreshRequested
                ) {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            IkeaWatcherTheme.dimens.none,
                            IkeaWatcherTheme.dimens.margin
                        ),
                        verticalArrangement = Arrangement.spacedBy(IkeaWatcherTheme.dimens.halfMargin)
                    ) {
                        items(items = stockItems, { it.itemNumber }) {
                            StockItem(
                                item = it,
                                onClick = viewModel::onItemClicked
                            )
                        }
                    }
                }
            }
        }
    }
}

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
                    }
                    Spacer(modifier = Modifier.height(IkeaWatcherTheme.dimens.margin))
                    StockItemForecast(item.itemStock.stockForecast)
                }

                val refreshTime =
                    item.lastRefreshTime?.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
                        ?: "Never"
                Text(text = "Last Update: $refreshTime")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenLightPreview() {
    IkeaWatcherTheme {
//        MainScreen(MainViewModel(MockIkeaWatcherClient))
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenDarkPreview() {
    IkeaWatcherTheme(darkTheme = true) {
//        MainScreen(MainViewModel(MockIkeaWatcherClient))
    }
}
