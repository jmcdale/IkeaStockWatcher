package com.jmcdale.ikea.watcher.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class IkeaStore(val id: String, val locationName: String, val abbreviation: String) {
    companion object {
        val BOLINGBROOK = IkeaStore("170", "Bolingbrook, IL", "BLK")
        val FISHERS = IkeaStore("536", "Fishers, IN", "IN")
        val KANSAS_CITY = IkeaStore("374", "Merriam, KS", "KC")
        val MEMPHIS = IkeaStore("508", "Memphis, TN", "TN")
        val SAINT_LOUIS = IkeaStore("410", "St. Louis, MO", "STL")
        val SCHAUMBURG = IkeaStore("210", "Schaumburg, IL", "SCH")

        fun fromStoreId(storeId: String): IkeaStore {
            return when (storeId) {
                BOLINGBROOK.id -> BOLINGBROOK
                FISHERS.id -> FISHERS
                KANSAS_CITY.id -> KANSAS_CITY
                MEMPHIS.id -> MEMPHIS
                SAINT_LOUIS.id -> SAINT_LOUIS
                SCHAUMBURG.id -> SCHAUMBURG
                else -> throw IllegalArgumentException("Unrecognized Store ID")
            }
        }
    }
}