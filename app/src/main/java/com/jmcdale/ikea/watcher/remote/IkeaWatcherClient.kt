package com.jmcdale.ikea.watcher.remote

interface IkeaWatcherClient {
    suspend fun checkItemStock(
        itemCode: String,
        ikeaStore: IkeaStore,
        itemType: ItemType = ItemType.ART,
        region: String = "us",
        locale: String = "en"
    ): IkeaWatcherResult<IkeaItemStock> = throw NotImplementedError()
}

class IkeaWatcherClientImpl(private val service: IkeaWatcherService) : IkeaWatcherClient {
    override suspend fun checkItemStock(
        itemCode: String,
        ikeaStore: IkeaStore,
        itemType: ItemType,
        region: String,
        locale: String
    ): IkeaWatcherResult<IkeaItemStock> {
        val response = service.checkItemStock(
            region = region,
            locale = locale, storeId = ikeaStore.id, itemType = itemType.name, itemCode = itemCode
        )

        val body = response.body()
        return if (response.isSuccessful && body != null) {
            try {
                val stock = body.toIkeaItemStock()
                IkeaWatcherResult.success(stock)
            } catch (e: Exception) {
                val error = IkeaWatcherError(
                    errorCode = IkeaWatcherError.Codes.PARSING_ERROR,
                    message = "Error converting to IkeaStockItem"
                )
                IkeaWatcherResult.failure(error)
            }
        } else {
            val error = IkeaWatcherError(errorCode = response.code().toString())
            IkeaWatcherResult.failure(error)
        }
    }
}

object MockIkeaWatcherClient : IkeaWatcherClient
