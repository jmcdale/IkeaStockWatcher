package com.jmcdale.ikea.watcher.local

import com.jmcdale.ikea.watcher.remote.*
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class IkeaStockRepository(
    private val client: IkeaWatcherClient,
    private val storage: LocalStorage
) {
    private val defaultFilters = StockFilters(listOf(IkeaStore.SAINT_LOUIS, IkeaStore.KANSAS_CITY, IkeaStore.SCHAUMBURG))

    private val _filters = MutableStateFlow(defaultFilters)
    val filters = _filters.asStateFlow()

    private val itemList = DEFAULT_ITEMS.toMutableList()
    private val _items = MutableStateFlow(itemList.toList())
    val items = _items.asStateFlow()

    private var storageWatchJob: Job? = null

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        GlobalScope.launch { loadItems() }
        GlobalScope.launch {
            storage.flowJsonable(KEY_STOCK_FILTERS, defaultFilters).stateIn(GlobalScope).collect {
                _filters.value = it
            }
        }
    }

    private suspend fun updateFilters(newFilters: StockFilters): Boolean {
        return storage.saveJsonable(KEY_STOCK_FILTERS, newFilters)
    }

    suspend fun addStoreFilter(store: IkeaStore) {
        val newStores = filters.value.stores.toMutableList().apply { add(store) }
        updateFilters(filters.value.copy(stores = newStores))
    }

    suspend fun removeStoreFilter(store: IkeaStore) {
        val newStores = filters.value.stores.toMutableList().apply { remove(store) }
        updateFilters(filters.value.copy(stores = newStores))
    }

    suspend fun refreshItems() {
        if (isRefreshing.value) return
        //TODO fix isRefreshing with parallelism
        _isRefreshing.value = true
        //TODO parallel
        itemList.forEach { refreshItem(it) }
        _isRefreshing.value = false
    }

    private suspend fun refreshItem(item: MainStockItem) {
        filters.value.stores.forEach { store -> refreshItemForStore(item, store) }
    }

    private suspend fun refreshItemForStore(item: MainStockItem, store: IkeaStore) {
        return refreshItemForStore(item, store.id)
    }

    private suspend fun refreshItemForStore(item: MainStockItem, storeId: String) {
        val store = IkeaStore.fromStoreId(storeId)
        //TODO allow isRefreshing to understand individual items being refreshed
        val updatedItem = client.checkItemStock(
            itemCode = item.itemNumber,
            ikeaStore = store,
            itemType = ItemType.ART,
            region = "us",
            locale = "en"
        )

        when (updatedItem) {
            is IkeaWatcherResult.Success -> {
                saveItem(store.id, updatedItem.result)
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
                    if (updatedItem != null) {
                        val stocksWithFilteredStores = updatedItem.itemStocks.filter {
                            filters.value.stores.map { store -> store.id }.contains(it.key)
                        }
                        val withFilteredStores =
                            updatedItem.copy(itemStocks = stocksWithFilteredStores)
                        itemList.replaceAndEmit(withFilteredStores)
                    }
                }
            }
        }
        emitItems()
    }

    private suspend fun emitItems() {
        _items.emitList(itemList.sortedWith(MainStockItemAvailabilityComparator))
    }

    private fun MainStockItem.withIkeaItemStock(
        storeId: String,
        item: IkeaItemStock
    ): MainStockItem {
        val itemStocks = this.itemStocks.toMutableMap()
        itemStocks[storeId] = item
        return this.copy(itemStocks = itemStocks, lastRefreshTime = LocalDateTime.now())
    }

    private suspend fun saveItem(storeId: String, updatedItemStock: IkeaItemStock) {
        itemList.firstOrNull { it.itemNumber == updatedItemStock.itemNumber }?.let {
            val updatedItem = it.withIkeaItemStock(storeId, updatedItemStock)
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
        private const val KEY_STOCK_FILTERS = "KEY_STOCK_FILTERS"
        private const val KEY_MAIN_STOCK_ITEM_PREFIX = "KEY_MAIN_STOCK_ITEM_"
    }
}

object MainStockItemAvailabilityComparator : Comparator<MainStockItem> {
    override fun compare(obj1: MainStockItem?, obj2: MainStockItem?): Int {
        return when {
            // One or the other is null
            obj1 == null && obj2 != null -> OBJ2_COMES_FIRST
            obj1 != null && obj2 == null -> OBJ1_COMES_FIRST
            obj1 == null && obj2 == null -> BOTH_ARE_EQUAL
            // Same item
            obj1!!.itemNumber == obj2!!.itemNumber -> BOTH_ARE_EQUAL
            // One or the other doesn't have itemStocks
            obj1.itemStocks.isEmpty() && obj2.itemStocks.isNotEmpty() -> OBJ2_COMES_FIRST
            obj1.itemStocks.isNotEmpty() && obj2.itemStocks.isEmpty() -> OBJ1_COMES_FIRST
            obj1.itemStocks.isEmpty() && obj2.itemStocks.isEmpty() -> BOTH_ARE_EQUAL
            // One or the other has more stock
            obj1.itemStocks.highestAvailableStock() > obj2.itemStocks.highestAvailableStock() -> OBJ1_COMES_FIRST
            obj1.itemStocks.highestAvailableStock() < obj2.itemStocks.highestAvailableStock() -> OBJ2_COMES_FIRST
            // Both have same non-zero stock levels
            (obj1.itemStocks.highestAvailableStock() == obj2.itemStocks.highestAvailableStock()) && obj1.itemStocks.highestAvailableStock() != 0 -> BOTH_ARE_EQUAL
            // Both have zero stock levels, One or the other has upcoming stock availability
            (obj1.itemStocks.highestAvailableStock() == obj2.itemStocks.highestAvailableStock()) && obj1.itemStocks.highestAvailableStock() == 0
                    && (obj1.anyUpcomingStock() > obj2.anyUpcomingStock()) -> OBJ1_COMES_FIRST
            (obj1.itemStocks.highestAvailableStock() == obj2.itemStocks.highestAvailableStock()) && obj1.itemStocks.highestAvailableStock() == 0
                    && (obj1.anyUpcomingStock() < obj2.anyUpcomingStock()) -> OBJ2_COMES_FIRST
            else -> BOTH_ARE_EQUAL
        }
    }

    fun Map<String, IkeaItemStock>.highestAvailableStock(): Int {
        return this.values.maxOfOrNull { it.availableStock } ?: 0
    }

    private const val OBJ1_COMES_FIRST = -1
    private const val BOTH_ARE_EQUAL = 0
    private const val OBJ2_COMES_FIRST = 1
}


object IkeaItemStockAvailabilityComparator : Comparator<IkeaItemStock> {
    override fun compare(obj1: IkeaItemStock?, obj2: IkeaItemStock?): Int {
        return when {
            // One or the other is null
            obj1 == null && obj2 != null -> OBJ2_COMES_FIRST
            obj1 != null && obj2 == null -> OBJ1_COMES_FIRST
            obj1 == null && obj2 == null -> BOTH_ARE_EQUAL
            // Same item
            obj1!!.itemNumber == obj2!!.itemNumber -> BOTH_ARE_EQUAL
            // One or the other has more stock
            obj1.availableStock > obj2.availableStock -> OBJ1_COMES_FIRST
            obj1.availableStock < obj2.availableStock -> OBJ2_COMES_FIRST
            // Both have same non-zero stock levels
            (obj1.availableStock == obj2.availableStock) && obj1.availableStock != 0 -> BOTH_ARE_EQUAL
            // Both have zero stock levels, One or the other has upcoming stock availability
            (obj1.availableStock == obj2.availableStock) && obj1.availableStock == 0
                    && (obj1.upcomingStock() > obj2.upcomingStock()) -> OBJ1_COMES_FIRST
            (obj1.availableStock == obj2.availableStock) && obj1.availableStock == 0
                    && (obj1.upcomingStock() < obj2.upcomingStock()) -> OBJ2_COMES_FIRST
            else -> BOTH_ARE_EQUAL
        }
    }

    private const val OBJ1_COMES_FIRST = -1
    private const val BOTH_ARE_EQUAL = 0
    private const val OBJ2_COMES_FIRST = 1
}

@JsonClass(generateAdapter = true)
data class StockFilters(val stores: List<IkeaStore>)