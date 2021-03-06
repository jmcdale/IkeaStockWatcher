package com.jmcdale.ikea.watcher.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class IkeaStore(val id: String, val locationName: String?) {
    companion object {
        val SAINT_LOUIS = IkeaStore("410", "St. Louis, MO")
    }
}