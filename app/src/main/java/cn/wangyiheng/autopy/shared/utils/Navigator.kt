package cn.wangyiheng.autopy.shared.utils
import cn.wangyiheng.autopy.modules.mainscreen.view.MainScreen
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun NavGraph(tokenManager: InfoManager) {
    val navController = rememberNavController()

    NavHost(navController, "main") {

        composable(ScreenRoutes.MAIN) {
            MainScreen(navController)
        }
    }
}



