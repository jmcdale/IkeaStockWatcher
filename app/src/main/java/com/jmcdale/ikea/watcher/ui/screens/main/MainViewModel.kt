package com.jmcdale.ikea.watcher.ui.screens.main

import androidx.lifecycle.*
import com.jmcdale.ikea.watcher.local.IkeaStockRepository
import com.jmcdale.ikea.watcher.local.MainStockItem
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class MainViewModel() : ViewModel() {

    //TODO make this private. Gotta fix the viemodel factory and DI
    var _ikeaStockRepository: IkeaStockRepository? by Delegates.observable<IkeaStockRepository?>(
        null
    ) { _, _, _ -> reinit() }
    val ikeaStockRepository: IkeaStockRepository
        get() = _ikeaStockRepository!!

    lateinit var isLoading: LiveData<Boolean>
        private set
    lateinit var stockItems: LiveData<List<MainStockItem>>
        private set

    private val _selectedItem = MutableLiveData<MainStockItem?>(null)
        val selectedItem: LiveData<MainStockItem?>
            get() = _selectedItem

    //TODO this is only needed bc the repo is not provided via constructor.
    private fun reinit() {
        stockItems = ikeaStockRepository.items.asLiveData()
        isLoading = ikeaStockRepository.isRefreshing.asLiveData()
    }

    fun onRefreshRequested() {
        viewModelScope.launch {
            ikeaStockRepository.refreshItems()
        }
    }

    fun onItemClicked(item: MainStockItem) {
        _selectedItem.value = item
//        _isLoading.value = true
//        GlobalScope.launch {
//            val result = client.checkItemStock(
//                itemCode = item.itemNumber,
//                IkeaStore.SAINT_LOUIS
//            )
//
//            when (result) {
//                is IkeaWatcherResult.Success -> {
//                    _stockItems.value!!.replaceItem(item.copy(itemStock = result.result))
//                    launch(Dispatchers.Main) { _stockItems.notifyObservers() }
//                }
//                else -> {
//                }
//            }
//
//            launch(Dispatchers.Main) { _isLoading.value = false }
//        }
    }

    fun onItemDialogDismissRequest(){
        _selectedItem.value = null
    }
}