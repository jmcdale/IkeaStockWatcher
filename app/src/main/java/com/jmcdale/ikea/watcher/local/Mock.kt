package com.jmcdale.ikea.watcher.local

import com.jmcdale.ikea.watcher.remote.IkeaItemStock
import com.jmcdale.ikea.watcher.remote.IkeaWatcherClient
import com.jmcdale.ikea.watcher.remote.StockForecast
import java.time.LocalDate

object Mock {
    val mockIkeaItemStock = IkeaItemStock(
        itemNumber = "555.555.5555",
        availableStock = 1,
        stockType = "",
        inStockProbability = "",
        inStockRangeCode = "",
        inCustomerOrderRange = "",
        restockDateTime = LocalDate.now(),
        availabilityDetails = listOf(),
        stockForecast = listOf()
    )
    val mockMainStockItem = MainStockItem(
        itemNumber = "002.654.39".cleanItemNumber(),
        itemName = "SEKTION High cabinet frame, white18x24x90",
        imageUrl = "https://www.ikea.com/us/en/images/products/sektion-high-cabinet-frame-white__0268551_pe406649_s5.jpg?f=xxxs",
        numberDesired = 2,
        itemStocks = mapOf("STL" to mockIkeaItemStock, "KC" to mockIkeaItemStock)
    )

    object MockIkeaWatcherClient : IkeaWatcherClient

    val mockStockItemForecastList = listOf(
        StockForecast(
            date = LocalDate.now().plusDays(1),
            probability = "HIGH",
            availableStock = 0,
            stockType = "STORE"
        ),
        StockForecast(
            date = LocalDate.now().plusDays(2),
            probability = "HIGH",
            availableStock = 1,
            stockType = "STORE"
        ),
        StockForecast(
            date = LocalDate.now().plusDays(3),
            probability = "HIGH",
            availableStock = 1,
            stockType = "STORE"
        ),
        StockForecast(
            date = LocalDate.now().plusDays(4),
            probability = "HIGH",
            availableStock = 100,
            stockType = "STORE"
        )
    )
}