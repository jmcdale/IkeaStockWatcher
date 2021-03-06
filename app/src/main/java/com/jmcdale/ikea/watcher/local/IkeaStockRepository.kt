package com.jmcdale.ikea.watcher.local

import com.jmcdale.ikea.watcher.remote.*

class IkeaStockRepository(private val client:IkeaWatcherClient) {

    val

    suspend fun appLocalItemStock(
        itemCode: String,
        ikeaStore: IkeaStore,
        itemType: ItemType = ItemType.ART,
        region: String = "us",
        locale: String = "en"
    ): IkeaWatcherResult<IkeaItemStock> = throw NotImplementedError()
}