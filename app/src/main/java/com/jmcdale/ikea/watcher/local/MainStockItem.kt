package com.jmcdale.ikea.watcher.local

import com.jmcdale.ikea.watcher.remote.IkeaItemStock
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class MainStockItem(
    val itemNumber: String,
    val itemName: String,
    val imageUrl: String?,
    val itemStock: IkeaItemStock? = null,
    val lastRefreshTime: LocalDateTime? = null
)

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