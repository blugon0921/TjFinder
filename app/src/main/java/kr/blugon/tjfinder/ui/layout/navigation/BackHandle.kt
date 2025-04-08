package kr.blugon.tjfinder.ui.layout.navigation

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import kr.blugon.tjfinder.ui.screen.mainStates


@Composable
fun onBack(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()

    val activity = LocalContext.current as Activity
    BackHandler {
        when (navController.currentScreen) {
            DefaultScreen.Login -> activity.finish()
            DefaultScreen.Main -> {
                if(mainStates.pagerState?.currentPage == 2) activity.finish()
                else navController.navigateMainScreen(BottomScreen.Home, coroutineScope)
            }

            else -> navController.navigateUp()
        }
    }
}