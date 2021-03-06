package com.jmcdale.ikea.watcher.ui.screens.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jmcdale.ikea.watcher.local.DEFAULT_ITEMS
import com.jmcdale.ikea.watcher.local.MainStockItem
import com.jmcdale.ikea.watcher.local.replaceItem
import com.jmcdale.ikea.watcher.remote.IkeaStore
import com.jmcdale.ikea.watcher.remote.IkeaWatcherClient
import com.jmcdale.ikea.watcher.remote.IkeaWatcherResult
import com.jmcdale.ikea.watcher.utils.notifyObservers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainViewModel(private val client: IkeaWatcherClient) {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _stockItems =
        MutableLiveData<MutableList<MainStockItem>>(DEFAULT_ITEMS.toMutableList())
    val stockItems: LiveData<List<MainStockItem>>
        get() = _stockItems as LiveData<List<MainStockItem>>

    fun onItemClicked(item: MainStockItem) {
        _isLoading.value = true
        GlobalScope.launch {
            val result = client.checkItemStock(
                itemCode = item.itemNumber,
                IkeaStore.SAINT_LOUIS
            )

            when (result) {
                is IkeaWatcherResult.Success -> {
                    _stockItems.value!!.replaceItem(item.copy(itemStock = result.result))
                    launch(Dispatchers.Main) { _stockItems.notifyObservers() }
                }
                else -> {
                }
            }

            launch(Dispatchers.Main) { _isLoading.value = false }
        }
    }
}