package com.jmcdale.ikea.watcher.local

import com.jmcdale.ikea.watcher.remote.IkeaItemStock
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class MainStockItem(
    val itemNumber: String,
    val itemName: String,
    val imageUrl: String?,
    val itemStocks: Map<String, IkeaItemStock> = mapOf(),
    val lastRefreshTime: LocalDateTime? = null,
    val numberDesired:Int
)

fun MainStockItem.anyUpcomingStock(): Int {
    return this.itemStocks.flatMap { it.value.stockForecast }.maxOfOrNull { it.availableStock } ?: 0
//    return this.itemStocks.keys.maxOfOrNull { upcomingStock(it) } ?: 0
}

fun MainStockItem.upcomingStock(storeId:String): Int {
    return this.itemStocks[storeId]?.upcomingStock() ?: 0
}

fun IkeaItemStock.upcomingStock(): Int {
    return this.stockForecast.maxOfOrNull { it.availableStock } ?: 0
}

fun MutableList<MainStockItem>.replaceItem(
    item: MainStockItem
) {
    val idx = this.indexOfFirst { it.itemNumber == item.itemNumber }
    this[idx] = item
}

fun String.cleanItemNumber(): String {
    return this.replace("[^A-Za-z0-9 ]".toRegex(), "")
}

fun String.formatItemNumber(): String {
    val cleaned = this.cleanItemNumber()
    return if (cleaned.length == 8) {
        return "${cleaned.substring(0, 3)}.${cleaned.substring(3, 6)}.${cleaned.substring(6, 8)}"
    } else {
        this
    }
}