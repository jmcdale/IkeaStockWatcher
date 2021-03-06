package com.jmcdale.ikea.watcher

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
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
                    MainScreen(MainViewModel(IkeaWatcherApplication.instance!!.client))
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