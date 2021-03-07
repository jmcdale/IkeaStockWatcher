package com.jmcdale.ikea.watcher

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.jmcdale.ikea.watcher.local.IkeaStockRepository
import com.jmcdale.ikea.watcher.ui.screens.main.MainScreen
import com.jmcdale.ikea.watcher.ui.screens.main.MainViewModel
import com.jmcdale.ikea.watcher.ui.theme.IkeaWatcherTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IkeaWatcherTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    val repo = IkeaStockRepository(
                        IkeaWatcherApplication.instance!!.client,
                        IkeaWatcherApplication.instance!!.localStorage
                    )

                    val vm: MainViewModel by viewModels()
                    vm._ikeaStockRepository = repo

                    MainScreen(vm)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    IkeaWatcherTheme {
        Greeting("Android")
    }
}