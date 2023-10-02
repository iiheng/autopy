package cn.wangyiheng.autopy.modules.mainscreen.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import cn.wangyiheng.autopy.ui.theme.AutopyTheme

@Composable
fun MainScreen(navController: NavHostController) {

    Text(text = "MainScreen")
}