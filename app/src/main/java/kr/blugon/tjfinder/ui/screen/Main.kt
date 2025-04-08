package kr.blugon.tjfinder.ui.screen

import android.util.Log
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import kr.blugon.tjfinder.module.SettingManager
import kr.blugon.tjfinder.ui.screen.child.user.SettingType


class MainViewModel {
    var pagerState: PagerState? = null

    @Composable
    fun init() {
        if(pagerState != null) return
        pagerState = rememberPagerState(2, pageCount = { 5 })
    }
}
val mainStates = MainViewModel()
@Composable
fun MainScreen(
    navController: NavController
) {
    val context = LocalContext.current
    mainStates.init()

    HorizontalPager(
        state = mainStates.pagerState!!,
    ) { page ->
        when (page) {
            0 -> NewSongs(navController)
            1 -> Search(navController)
            2 -> {
                val isSuggestPlaylist = SettingManager[context, SettingType.suggestPlaylist]
                if(isSuggestPlaylist) PlaylistHome(navController)
                else Home(navController)
            }
            3 -> PlaylistScreen(navController)
            4 -> UserScreen(navController)
        }
    }
}