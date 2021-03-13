package com.jmcdale.ikea.watcher.ui.screens.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.jmcdale.ikea.watcher.R
import com.jmcdale.ikea.watcher.local.StockFilters
import com.jmcdale.ikea.watcher.remote.IkeaStore
import com.jmcdale.ikea.watcher.ui.FullScreenDialog
import com.jmcdale.ikea.watcher.ui.theme.IkeaWatcherTheme

@Composable
fun SettingsDialog(
    filters: StockFilters,
    onStoreFilterChangeRequested: (IkeaStore, Boolean) -> Unit,
    onDismissRequest: () -> Unit
) {
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
                    Settings(filters, onStoreFilterChangeRequested)
                }
            }
        }
    }
}

@Composable
fun ColumnScope.Settings(
    filters: StockFilters,
    onStoreFilterChangeRequested: (IkeaStore, Boolean) -> Unit
) {
    Text(text = "Settings", style = IkeaWatcherTheme.typography.h5)
    Spacer(modifier = Modifier.height(IkeaWatcherTheme.dimens.margin))
    Text(text = "Ikea Stores", style = IkeaWatcherTheme.typography.h6)
    IkeaStore.availableStores.forEach { store ->
        val isChecked = filters.stores.map { filterStore -> filterStore.id }.contains(store.id)

        Row(
            Modifier
                .height(IkeaWatcherTheme.dimens.touchTarget)
                .fillMaxWidth()
                .clickable { onStoreFilterChangeRequested(store, !isChecked) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = null
            )
            Text(
                text = store.locationName,
                modifier = Modifier.padding(
                    IkeaWatcherTheme.dimens.margin,
                    IkeaWatcherTheme.dimens.none,
                    IkeaWatcherTheme.dimens.none,
                    IkeaWatcherTheme.dimens.none
                )
            )
        }

    }
}