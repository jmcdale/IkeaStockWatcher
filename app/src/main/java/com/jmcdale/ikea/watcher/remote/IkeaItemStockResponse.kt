package com.jmcdale.ikea.watcher.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class IkeaItemStockResponse(
    @Json(name = "StockAvailability")
    val stockAvailability: StockAvailability
)

@JsonClass(generateAdapter = true)
data class StockAvailability(
    @Json(name = "ClassUnitKey")
    val ClassUnitKey: ClassUnitKey,

    @Json(name = "ItemKey")
    val ItemKey: ItemKey,

    @Json(name = "RetailItemAvailability")
    val RetailItemAvailability: RetailItemAvailability,

    @Json(name = "AvailableStockForecastList")
    val AvailableStockForecastList: AvailableStockForecastList,

    @Json(name = "@xmlns")
    val xmlns: Xmlns
)

@JsonClass(generateAdapter = true)
data class Xmlns(
    @Json(name = "$")
    val value: String
)

@JsonClass(generateAdapter = true)
data class AvailableStockForecastList(
    @Json(name = "AvailableStockForecast")
    val AvailableStockForecast: List<AvailableStockForecast>
)

@JsonClass(generateAdapter = true)
data class AvailableStockForecast(
    @Json(name = "AvailableStock")
    val AvailableStock: AvailableStock,
    @Json(name = "AvailableStockType")
    val AvailableStockType: AvailableStockType,
    @Json(name = "InStockProbabilityCode")
    val InStockProbabilityCode: InStockProbabilityCode,
    @Json(name = "ValidDateTime")
    val ValidDateTime: ValidDateTime,
    @Json(name = "ValidDateTimeUnit")
    val ValidDateTimeUnit: ValidDateTimeUnit,
)

@JsonClass(generateAdapter = true)
data class ValidDateTime(
    @Json(name = "$")
    val value: String
)

@JsonClass(generateAdapter = true)
data class ValidDateTimeUnit(
    @Json(name = "$")
    val value: String
)


@JsonClass(generateAdapter = true)
data class RetailItemAvailability(
    @Json(name = "AvailableStock")
    val AvailableStock: AvailableStock,
    @Json(name = "AvailableStockType")
    val AvailableStockType: AvailableStockType,
    @Json(name = "InStockProbabilityCode")
    val InStockProbabilityCode: InStockProbabilityCode,
    // TODO response was empty, so idk what this could look like yet
//    @Json(name = "RecommendedSalesLocation")
//    val RecommendedSalesLocation: RecommendedSalesLocation,
    @Json(name = "SalesMethodCode")
    val SalesMethodCode: SalesMethodCode,
    @Json(name = "InStockRangeCode")
    val InStockRangeCode: InStockRangeCode,
    @Json(name = "InCustomerOrderRangeCode")
    val InCustomerOrderRangeCode: InCustomerOrderRangeCode,
    @Json(name = "RestockDateTime")
    val RestockDateTime: RestockDateTime,
    @Json(name = "RestockDateTimeType")
    val RestockDateTimeType: RestockDateTimeType,
    @Json(name = "StockAvailabilityInfoList")
    val StockAvailabilityInfoList: StockAvailabilityInfoList,
)

@JsonClass(generateAdapter = true)
data class AvailableStock(
    @Json(name = "$")
    val value: String
)

@JsonClass(generateAdapter = true)
data class AvailableStockType(
    @Json(name = "$")
    val value: String
)

@JsonClass(generateAdapter = true)
data class InStockProbabilityCode(
    @Json(name = "$")
    val value: String
)

// TODO response was empty, so idk what this could look like yet
//@JsonClass(generateAdapter = true)
//data class RecommendedSalesLocation(
//    @Json(name = "$")
//    val value: String
//)

@JsonClass(generateAdapter = true)
data class SalesMethodCode(
    @Json(name = "$")
    val value: String
)

@JsonClass(generateAdapter = true)
data class InStockRangeCode(
    @Json(name = "$")
    val value: String
)

@JsonClass(generateAdapter = true)
data class InCustomerOrderRangeCode(
    @Json(name = "$")
    val value: String
)

@JsonClass(generateAdapter = true)
data class RestockDateTime(
    @Json(name = "$")
    val value: String
)

@JsonClass(generateAdapter = true)
data class RestockDateTimeType(
    @Json(name = "$")
    val value: String
)

@JsonClass(generateAdapter = true)
data class StockAvailabilityInfoList(
    @Json(name = "StockAvailabilityInfo")
    val StockAvailabilityInfo: List<StockAvailabilityInfo>
)

@JsonClass(generateAdapter = true)
data class StockAvailabilityInfo(
    @Json(name = "StockAvailInfoCode")
    val StockAvailInfoCode: StockAvailInfoCode,
    @Json(name = "StockAvailInfoText")
    val StockAvailInfoText: StockAvailInfoText
)

@JsonClass(generateAdapter = true)
data class StockAvailInfoCode(
    @Json(name = "$")
    val value: String
)

@JsonClass(generateAdapter = true)
data class StockAvailInfoText(
    @Json(name = "$")
    val value: String
)


@JsonClass(generateAdapter = true)
data class ItemKey(
    @Json(name = "ItemNo")
    val ItemNo: ItemNo,
    @Json(name = "ItemType")
    val ItemType: ItemTypeResponse
)

@JsonClass(generateAdapter = true)
data class ItemNo(
    @Json(name = "$")
    val value: String
)

@JsonClass(generateAdapter = true)
data class ItemTypeResponse(
    @Json(name = "$")
    val value: String
)


@JsonClass(generateAdapter = true)
data class ClassUnitKey(
    @Json(name = "ClassType")
    val ClassType: ClassType,
    @Json(name = "ClassUnitType")
    val ClassUnitType: ClassUnitType,
    @Json(name = "ClassUnitCode")
    val ClassUnitCode: ClassUnitCode
)

@JsonClass(generateAdapter = true)
data class ClassType(
    @Json(name = "$")
    val value: String
)

@JsonClass(generateAdapter = true)
data class ClassUnitType(
    @Json(name = "$")
    val value: String
)

@JsonClass(generateAdapter = true)
data class ClassUnitCode(
    @Json(name = "$")
    val value: Int
)