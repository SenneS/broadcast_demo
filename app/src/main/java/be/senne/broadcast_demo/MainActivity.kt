package be.senne.broadcast_demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import be.senne.broadcast_demo.ui.theme.Broadcast_demoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Broadcast_demoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    createMainScreen()
                }
            }
        }
    }
}

@Composable
fun createMainScreen(vm: MainViewModel = viewModel()) {

    val devices = vm.devices

    Column() {
        Button(onClick = {
            vm.start()
        }) {
            Text("Start")
        }
        Button(onClick = {
            vm.start()
        }) {
            Text("Stop")
        }
        LazyColumn() {
            itemsIndexed(devices) { index, device ->
                Text("${index}. ${device.name} (${device.id})")
                Divider()
            }
        }
    }
}


data class Device(
    val name : String,
    val id : Long
)