package cn.wangyiheng.autopy.modules.mainscreen.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import cn.wangyiheng.autopy.modules.mainscreen.controllers.Main_Controller
import cn.wangyiheng.autopy.ui.theme.AutopyTheme
import org.koin.core.component.inject
import org.koin.java.KoinJavaComponent.inject
import cn.wangyiheng.autopy.services.auto.Auto
@Composable
fun MainScreen(navController: NavHostController) {

//    Text(text = "MainScreen")
    val main_controller = Main_Controller()
    Button(onClick = { main_controller.start() }) {
        Text(text = "Start")
    }

}