package com.jmcdale.ikea.watcher.remote

sealed class IkeaWatcherResult<T> {
    data class Success<T> internal constructor(val result: T) : IkeaWatcherResult<T>()
    data class Failure<T> internal constructor(val error: IkeaWatcherError) : IkeaWatcherResult<T>()

    companion object {
        fun <T> success(it: T): Success<T> = Success(it)
        fun <T> failure(error: IkeaWatcherError): Failure<T> = Failure(error)
    }
}