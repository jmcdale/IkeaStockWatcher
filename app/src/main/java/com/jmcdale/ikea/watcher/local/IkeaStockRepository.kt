package com.jmcdale.ikea.watcher.local

import com.jmcdale.ikea.watcher.remote.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class IkeaStockRepository(
    private val client: IkeaWatcherClient,
    private val storage: LocalStorage
) {

    private val itemList = DEFAULT_ITEMS.toMutableList()
    private val _items = MutableStateFlow(itemList.toList())
    val items = _items.asStateFlow()

    private var storageWatchJob: Job? = null

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        GlobalScope.launch { loadItems() }
    }

    suspend fun refreshItems() {
//        val items = storage.loadJsonableList(KEY_MAIN_STOCK_ITEMS, listOf<MainStockItem>())
//        items?.forEach { refreshItem(it) }
        if (isRefreshing.value) return
        //TODO fix isRefreshing with parallelism
        _isRefreshing.value = true
        //TODO parallel
        itemList.forEach { refreshItem(it) }
        _isRefreshing.value = false
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
        _items.emitList(itemList.sortedWith(MainStockItemAvailabilityComparator))
    }

    private fun MainStockItem.withIkeaItemStock(item: IkeaItemStock): MainStockItem {
        return this.copy(itemStock = item, lastRefreshTime = LocalDateTime.now())
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

object MainStockItemAvailabilityComparator : Comparator<MainStockItem> {
    override fun compare(obj1: MainStockItem?, obj2: MainStockItem?): Int {
//        return when{
//            obj1 == null && obj2 != null -> OBJ2_COMES_FIRST
//            obj1 != null && obj2 == null -> OBJ1_COMES_FIRST
//            obj1 == null && obj2 == null -> BOTH_ARE_EQUAL
//            obj1!!.itemNumber == obj2!!.itemNumber -> BOTH_ARE_EQUAL
//            obj1.itemStock == null && obj2.itemStock != null -> OBJ2_COMES_FIRST
//            obj1.itemStock != null && obj2.itemStock == null -> OBJ1_COMES_FIRST
//            obj1.itemStock == null && obj2.itemStock == null -> BOTH_ARE_EQUAL
//            obj1.itemStock!!.availableStock > obj2.itemStock!!.availableStock -> OBJ1_COMES_FIRST
//            obj1.itemStock.availableStock == obj2.itemStock.availableStock -> BOTH_ARE_EQUAL
//            obj1.itemStock.availableStock < obj2.itemStock.availableStock -> OBJ2_COMES_FIRST
//            else -> BOTH_ARE_EQUAL
//        }
        return when {
            // One or the other is null
            obj1 == null && obj2 != null -> OBJ2_COMES_FIRST
            obj1 != null && obj2 == null -> OBJ1_COMES_FIRST
            obj1 == null && obj2 == null -> BOTH_ARE_EQUAL
            // Same item
            obj1!!.itemNumber == obj2!!.itemNumber -> BOTH_ARE_EQUAL
            // One or the other doesn't have itemStock
            obj1.itemStock == null && obj2.itemStock != null -> OBJ2_COMES_FIRST
            obj1.itemStock != null && obj2.itemStock == null -> OBJ1_COMES_FIRST
            obj1.itemStock == null && obj2.itemStock == null -> BOTH_ARE_EQUAL
            // One or the other has more stock
            obj1.itemStock!!.availableStock > obj2.itemStock!!.availableStock -> OBJ1_COMES_FIRST
            obj1.itemStock.availableStock < obj2.itemStock.availableStock -> OBJ2_COMES_FIRST
            // Both have same non-zero stock levels
            (obj1.itemStock.availableStock == obj2.itemStock.availableStock) && obj1.itemStock.availableStock != 0 -> BOTH_ARE_EQUAL
            // Both have zero stock levels, One or the other has upcoming stock availability
            (obj1.itemStock.availableStock == obj2.itemStock.availableStock) && obj1.itemStock.availableStock == 0
                    && (obj1.upcomingStock() > obj2.upcomingStock()) -> OBJ1_COMES_FIRST
            (obj1.itemStock.availableStock == obj2.itemStock.availableStock) && obj1.itemStock.availableStock == 0
                    && (obj1.upcomingStock() < obj2.upcomingStock()) -> OBJ2_COMES_FIRST
            else -> BOTH_ARE_EQUAL
        }
    }

    private fun MainStockItem.upcomingStock(): Int {
        return this.itemStock?.stockForecast?.maxOfOrNull { it.availableStock } ?: 0
    }

    private const val OBJ1_COMES_FIRST = -1
    private const val BOTH_ARE_EQUAL = 0
    private const val OBJ2_COMES_FIRST = 1
}