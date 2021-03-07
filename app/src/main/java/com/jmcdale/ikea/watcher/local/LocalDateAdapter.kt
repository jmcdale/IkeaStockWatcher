package com.jmcdale.ikea.watcher.local

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.LocalDate

object LocalDateAdapter {
    @FromJson
    fun fromJson(json: String): LocalDate {
        return LocalDate.parse(json)
    }

    @ToJson
    fun toJson(localDate: LocalDate): String {
        return localDate.toString()
    }
}