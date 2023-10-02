package cn.wangyiheng.autopy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cn.wangyiheng.autopy.services.MyForegroundService
import cn.wangyiheng.autopy.shared.utils.InfoManager
import cn.wangyiheng.autopy.shared.utils.NavGraph
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenManager: InfoManager by inject()

        // Start the foreground service when the app launches
        startForegroundService()


        setContent {
            NavGraph(tokenManager)
        }
    }

    private fun startForegroundService() {
        MyForegroundService.start(this)
    }
}

@Preview
@Composable
fun PreviewMainScreen() {
    // MainScreen()
}