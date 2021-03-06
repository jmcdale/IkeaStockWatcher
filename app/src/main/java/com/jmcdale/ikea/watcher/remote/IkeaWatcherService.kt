package com.jmcdale.ikea.watcher.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface IkeaWatcherService {
    //eg: https://iows.ikea.com/retail/iows/us/en/stores/410/availability/ART/40487419
    @Headers(
        "Accept: application/vnd.ikea.iows+json;version=1.0",
        "Contract: 37249",
        "Consumer: MAMMUT"
    )
    @GET("retail/iows/{region}/{locale}/stores/{storeId}/availability/{itemType}/{itemCode}")
    suspend fun checkItemStock(
        @Path("region") region: String,
        @Path("locale") locale: String,
        @Path("storeId") storeId: String,
        @Path("itemType") itemType: String,
        @Path("itemCode") itemCode: String
    ): Response<IkeaItemStockResponse>
}
