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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.jmcdale.ikea.watcher.R
import com.jmcdale.ikea.watcher.local.StockFilters
import com.jmcdale.ikea.watcher.ui.pullrefresh.PullToRefresh
import com.jmcdale.ikea.watcher.ui.theme.IkeaWatcherTheme

@ExperimentalAnimationApi
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val isLoading by viewModel.isLoading.observeAsState(initial = false)
    val stockItems by viewModel.stockItems.observeAsState(initial = listOf())
    val selectedItem by viewModel.selectedItem.observeAsState(initial = null)
    val shouldShowSettings by viewModel.shouldShowSettings.observeAsState(initial = false)
    val filters by viewModel.filters.observeAsState(StockFilters(listOf()))

    Surface(color = MaterialTheme.colors.background, modifier = Modifier.fillMaxSize()) {
        Column {
            TopAppBar(
                title = { Text(text = "Ikea Watcher") },
                backgroundColor = MaterialTheme.colors.primaryVariant,
                contentColor = MaterialTheme.colors.onPrimary,
                actions = {
                    Box(modifier = Modifier
                        .clickable { viewModel.onSettingsClicked() }
                        .fillMaxHeight()
                        .aspectRatio(1f)) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_settings_24),
                            contentDescription = null,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            )
            Column(Modifier.padding(IkeaWatcherTheme.dimens.margin, IkeaWatcherTheme.dimens.none)) {
                selectedItem?.let {
                    ItemDialog(
                        item = it,
                        onDismissRequest = viewModel::onItemDialogDismissRequest
                    )
                }
                if (shouldShowSettings) {
                    SettingsDialog(
                        filters = filters,
                        onStoreFilterChangeRequested = viewModel::onStoreFilterChangeRequested,
                        onDismissRequest = viewModel::onSettingsDialogDismissRequest
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
