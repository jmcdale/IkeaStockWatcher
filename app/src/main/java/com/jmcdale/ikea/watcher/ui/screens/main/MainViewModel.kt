package com.jmcdale.ikea.watcher.ui.screens.main

import androidx.lifecycle.*
import com.jmcdale.ikea.watcher.local.IkeaStockRepository
import com.jmcdale.ikea.watcher.local.MainStockItem
import com.jmcdale.ikea.watcher.local.StockFilters
import com.jmcdale.ikea.watcher.remote.IkeaStore
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class MainViewModel() : ViewModel() {

    //TODO make this private. Gotta fix the viemodel factory and DI
    var _ikeaStockRepository: IkeaStockRepository? by Delegates.observable<IkeaStockRepository?>(
        null
    ) { _, _, _ -> reinit() }
    private val ikeaStockRepository: IkeaStockRepository
        get() = _ikeaStockRepository!!

    lateinit var isLoading: LiveData<Boolean>
        private set
    lateinit var stockItems: LiveData<List<MainStockItem>>
        private set
    lateinit var filters: LiveData<StockFilters>
        private set

    private val _selectedItem = MutableLiveData<MainStockItem?>(null)
    val selectedItem: LiveData<MainStockItem?>
        get() = _selectedItem

    private val _shouldShowSettings = MutableLiveData<Boolean>()
    val shouldShowSettings: LiveData<Boolean>
        get() = _shouldShowSettings

    //TODO this is only needed bc the repo is not provided via constructor.
    private fun reinit() {
        stockItems = ikeaStockRepository.items.asLiveData()
        filters = ikeaStockRepository.filters.asLiveData()
        isLoading = ikeaStockRepository.isRefreshing.asLiveData()
    }

    fun onRefreshRequested() {
        viewModelScope.launch {
            ikeaStockRepository.refreshItems()
        }
    }

    fun onItemClicked(item: MainStockItem) {
        _selectedItem.value = item
    }

    fun onSettingsClicked(){
        _shouldShowSettings.value = true
    }

    fun onItemDialogDismissRequest() {
        _selectedItem.value = null
    }

    fun onSettingsDialogDismissRequest() {
        _shouldShowSettings.value = false
    }

    fun onStoreFilterChangeRequested(store: IkeaStore, shouldUse: Boolean) {
        viewModelScope.launch {
            if (shouldUse) ikeaStockRepository.addStoreFilter(store)
            else ikeaStockRepository.removeStoreFilter(store)
        }
    }
}