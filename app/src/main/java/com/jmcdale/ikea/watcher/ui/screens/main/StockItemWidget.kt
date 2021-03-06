package com.jmcdale.ikea.watcher.ui.screens.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jmcdale.ikea.watcher.local.MainStockItem
import com.jmcdale.ikea.watcher.local.formatItemNumber
import dev.chrisbanes.accompanist.coil.CoilImage


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
}

@Composable
fun KnownStockItem(item: MainStockItem, onClick: (item: MainStockItem) -> Unit) {
    Text(text = item.itemName)
    Text(text = item.itemNumber.formatItemNumber())
    if (item.itemStock!!.availableStock > 0) {
        Text(text = "${item.itemStock.availableStock} Available")
    } else {
        Text(text = "Unavailable")
    }
}