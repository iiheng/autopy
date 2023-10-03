package cn.wangyiheng.autopy

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.wangyiheng.autopy.services.MyForegroundService
import com.chaquo.python.PyException
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"

    @OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val tokenManager: InfoManager by inject()

        // Start the foreground service when the app launches
        startForegroundService()


        setContent {
            if (!Python.isStarted()) {
                Python.start(AndroidPlatform(this))
            }
            val py = Python.getInstance()
            val module = py.getModule("plot")
//            NavGraph(tokenManager)
            var x by remember { mutableStateOf("") }
            var y by remember { mutableStateOf("") }
            var image by remember { mutableStateOf<ImageBitmap?>(null) }

            val context = LocalContext.current
            val keyboardController = LocalSoftwareKeyboardController.current

            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                TextField(
                    value = x,
                    onValueChange = { x = it },
                    label = { Text("X Coordinate") },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                        }
                    )
                )
                Spacer(Modifier.height(16.dp))
                TextField(
                    value = y,
                    onValueChange = { y = it },
                    label = { Text("Y Coordinate") },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                        }
                    )
                )
                Spacer(Modifier.height(16.dp))
                Button(onClick = {
                    try {
                        val bytes = module.callAttr("plot", x, y).toJava(ByteArray::class.java)
                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        image = bitmap.asImageBitmap()
                    } catch (e: PyException) {
                        // Handle the exception (e.g., show a toast)
                    }
                }) {
                    Text("Plot")
                }
                Spacer(Modifier.height(16.dp))
                image?.let {
                    androidx.compose.foundation.Image(
                        bitmap = it,
                        contentDescription = null,
                        modifier = Modifier.size(200.dp)
                    )
                }
            }
//            MainScreen()
        }
    }

    private fun startForegroundService() {
        MyForegroundService.start(this)
    }
}
class MainScreen : ComponentActivity() {
    @OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if (!Python.isStarted()) {
//            Python.start(AndroidPlatform(this))
//        }
//        val py = Python.getInstance()
//        val module = py.getModule("plot")

        setContent {
            var x by remember { mutableStateOf("") }
            var y by remember { mutableStateOf("") }
            var image by remember { mutableStateOf<ImageBitmap?>(null) }

            val context = LocalContext.current
            val keyboardController = LocalSoftwareKeyboardController.current

            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                TextField(
                    value = x,
                    onValueChange = { x = it },
                    label = { Text("X Coordinate") },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                        }
                    )
                )
                Spacer(Modifier.height(16.dp))
//                TextField(
//                    value = y,
//                    onValueChange = { y = it },
//                    label = { Text("Y Coordinate") },
//                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
//                    keyboardActions = KeyboardActions(
//                        onDone = {
//                            keyboardController?.hide()
//                        }
//                    )
//                )
                Spacer(Modifier.height(16.dp))
//                Button(onClick = {
//                    try {
//                        val bytes = module.callAttr("plot", x, y).toJava(ByteArray::class.java)
//                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
//                        image = bitmap.asImageBitmap()
//                    } catch (e: PyException) {
//                        // Handle the exception (e.g., show a toast)
//                    }
//                }) {
//                    Text("Plot")
//                }
                Spacer(Modifier.height(16.dp))
//                image?.let {
//                    androidx.compose.foundation.Image(
//                        bitmap = it,
//                        contentDescription = null,
//                        modifier = Modifier.size(200.dp)
//                    )
//                }
            }
        }
    }
}
@Preview
@Composable
fun PreviewMainScreen() {
    MainActivity()
}