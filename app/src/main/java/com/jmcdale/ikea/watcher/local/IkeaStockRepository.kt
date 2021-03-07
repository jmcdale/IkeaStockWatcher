package com.jmcdale.ikea.watcher.local

import com.jmcdale.ikea.watcher.remote.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class IkeaStockRepository(
    private val client: IkeaWatcherClient,
    private val storage: LocalStorage
) {

    private val itemList = DEFAULT_ITEMS.toMutableList()
    private val _items = MutableStateFlow(itemList.toList())
    val items = _items.asStateFlow()

    private var storageWatchJob: Job? = null

    var isRefreshing = false
        private set

    init {
        GlobalScope.launch { loadItems() }
    }

    suspend fun refreshItems() {
//        val items = storage.loadJsonableList(KEY_MAIN_STOCK_ITEMS, listOf<MainStockItem>())
//        items?.forEach { refreshItem(it) }
        //TODO fix isRefreshing with parallelism
        isRefreshing = true
        //TODO parallel
        itemList.forEach { refreshItem(it) }
        isRefreshing = false
    }

    suspend fun refreshItem(item: MainStockItem) {
        //TODO allow isRefreshing to understand individual items being refreshed
        val updatedItem = client.checkItemStock(
            itemCode = item.itemNumber,
            ikeaStore = IkeaStore.SAINT_LOUIS, //TODO
            itemType = ItemType.ART,
            region = "us",
            locale = "en"
        )

        when (updatedItem) {
            is IkeaWatcherResult.Success -> {
                saveItem(updatedItem.result)
            }
            else -> {
                //TODO
            }
        }


    }

    private suspend fun loadItems() {
        storageWatchJob?.cancel()
        itemList.forEach { item ->
            val storedItem = storage.loadJsonable(item.storageKey, item) ?: item
            itemList.replaceItem(storedItem)

            storageWatchJob = GlobalScope.launch {
                storage.flowJsonable<MainStockItem>(item.storageKey).collect { updatedItem ->
                    if (updatedItem != null) itemList.replaceAndEmit(updatedItem)
                }
            }
        }
        emitItems()
    }

    private suspend fun emitItems() {
        _items.emitList(itemList)
    }

    private fun MainStockItem.withIkeaItemStock(item: IkeaItemStock): MainStockItem {
        return this.copy(itemStock = item)
    }

    private suspend fun saveItem(updatedItemStock: IkeaItemStock) {
        itemList.firstOrNull { it.itemNumber == updatedItemStock.itemNumber }?.let {
            val updatedItem = it.withIkeaItemStock(updatedItemStock)
            storage.saveJsonable(updatedItem.storageKey, updatedItem)
        }
    }

    private suspend fun MutableList<MainStockItem>.replaceAndEmit(updatedItem: MainStockItem) {
        this.replaceItem(updatedItem)
        emitItems()
    }

    private val MainStockItem.storageKey
        get() = "$KEY_MAIN_STOCK_ITEM_PREFIX$itemNumber"

    private suspend fun <T> MutableStateFlow<List<T>>.emitList(l: List<T>) {
        emit(l.toList())
    }

    companion object {
        private const val KEY_MAIN_STOCK_ITEMS = "KEY_MAIN_STOCK_ITEMS"
        private const val KEY_MAIN_STOCK_ITEM_PREFIX = "KEY_MAIN_STOCK_ITEM_"
    }
}


