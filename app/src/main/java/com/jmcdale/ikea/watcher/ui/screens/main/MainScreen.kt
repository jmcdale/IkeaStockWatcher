package com.jmcdale.ikea.watcher.ui.screens.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jmcdale.ikea.watcher.ui.pullrefresh.PullToRefresh
import com.jmcdale.ikea.watcher.ui.theme.IkeaWatcherTheme

@ExperimentalAnimationApi
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val isLoading by viewModel.isLoading.observeAsState(initial = false)
    val stockItems by viewModel.stockItems.observeAsState(initial = listOf())

    Surface(color = MaterialTheme.colors.background, modifier = Modifier.fillMaxSize()) {
        Column {
            TopAppBar(
                title = { Text(text = "Ikea Watcher") },
                backgroundColor = MaterialTheme.colors.primaryVariant,
                contentColor = MaterialTheme.colors.onPrimary
            )
            Column(Modifier.padding(16.dp)) {
                PullToRefresh(
                    isRefreshing = isLoading,
                    onRefresh = viewModel::onRefreshRequested
                ) {
                    LazyColumn {
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
