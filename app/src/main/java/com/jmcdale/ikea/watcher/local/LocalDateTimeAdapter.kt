package com.jmcdale.ikea.watcher.local

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.LocalDateTime

object LocalDateTimeAdapter {
    @FromJson
    fun fromJson(json: String): LocalDateTime {
        return LocalDateTime.parse(json)
    }

    @ToJson
    fun toJson(localDateTime: LocalDateTime): String {
        return localDateTime.toString()
    }
}