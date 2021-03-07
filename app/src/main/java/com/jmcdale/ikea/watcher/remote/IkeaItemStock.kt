package com.jmcdale.ikea.watcher.remote

import com.squareup.moshi.JsonClass
import java.time.LocalDate

@JsonClass(generateAdapter = true)
data class IkeaItemStock(
    val itemNumber: String,
    val availableStock: Int,
    val stockType: String,
    val inStockProbability: String,
    val inStockRangeCode: String,
    val inCustomerOrderRange: String,
    val restockDateTime: LocalDate?,
    val availabilityDetails: List<AvailabilityDetail>,
    val stockForecast: List<StockForecast>
)

@JsonClass(generateAdapter = true)
data class AvailabilityDetail(val code: String, val message: String)

@JsonClass(generateAdapter = true)
data class StockForecast(
    val date: LocalDate,
    val probability: String,
    val availableStock: Int,
    val stockType: String
)

fun IkeaItemStockResponse.toIkeaItemStock(): IkeaItemStock {
    val itemNumber = this.stockAvailability.ItemKey.ItemNo.value
    val availableStock = this.stockAvailability.RetailItemAvailability.AvailableStock.value.toInt()
    val stockType: String = this.stockAvailability.RetailItemAvailability.AvailableStockType.value
    val inStockProbability =
        this.stockAvailability.RetailItemAvailability.InStockProbabilityCode.value
    val inStockRangeCode: String =
        this.stockAvailability.RetailItemAvailability.InStockRangeCode.value
    val inCustomerOrderRange: String =
        this.stockAvailability.RetailItemAvailability.InCustomerOrderRangeCode.value
    val restockDateTime: LocalDate? =
        this.stockAvailability.RetailItemAvailability.RestockDateTime?.value?.let {
            LocalDate.parse(
                it
            )
        }
    val availabilityDetails: List<AvailabilityDetail> =
        this.stockAvailability.RetailItemAvailability.StockAvailabilityInfoList?.StockAvailabilityInfo.toAvailabilityDetails()
    val stockForecast: List<StockForecast> =
        this.stockAvailability.AvailableStockForecastList.toStockForecasts()

    return IkeaItemStock(
        itemNumber,
        availableStock,
        stockType,
        inStockProbability,
        inStockRangeCode,
        inCustomerOrderRange,
        restockDateTime,
        availabilityDetails,
        stockForecast
    )
}

fun StockAvailabilityInfo.toAvailabilityDetail(): AvailabilityDetail {
    return AvailabilityDetail(this.StockAvailInfoCode.value, this.StockAvailInfoText.value)
}

fun List<StockAvailabilityInfo>?.toAvailabilityDetails(): List<AvailabilityDetail> {
    return this?.let { list -> list.map { it.toAvailabilityDetail() } } ?: listOf()
}

fun AvailableStockForecastList.toStockForecasts(): List<StockForecast> {
    return this.AvailableStockForecast.map { it.toStockForecast() }
}

fun AvailableStockForecast.toStockForecast(): StockForecast {
    val date: LocalDate = LocalDate.parse(this.ValidDateTime.value)
    val probability: String = this.InStockProbabilityCode.value
    val availableStock: Int = this.AvailableStock.value.toInt()
    val stockType: String = this.AvailableStockType.value

    return StockForecast(date, probability, availableStock, stockType)
}
