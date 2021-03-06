package com.jmcdale.ikea.watcher.local

import com.squareup.moshi.FromJson
import java.time.LocalDate

object LocalDateAdapter {
    @FromJson
    fun fromJson(json: String): LocalDate {
        return LocalDate.parse(json)
    }

    fun toJson(localDate: LocalDate): String {
        return localDate.toString()
    }
}