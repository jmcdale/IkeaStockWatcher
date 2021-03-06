package com.jmcdale.ikea.watcher.remote

data class IkeaWatcherError(
    val errorCode: String = Codes.UNKNOWN_ERROR,
    val message: String? = null
) {

    object Codes {
        val UNKNOWN_ERROR = "-1000"
        val PARSING_ERROR = "-1001"
    }
}