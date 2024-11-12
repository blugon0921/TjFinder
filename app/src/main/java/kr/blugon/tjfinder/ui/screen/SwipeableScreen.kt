package kr.blugon.tjfinder.ui.screen

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.navigation.NavController
import kr.blugon.tjfinder.MainActivity
import kr.blugon.tjfinder.ui.layout.BottomScreen
import kr.blugon.tjfinder.ui.layout.navigateScreen


lateinit var pageState: PagerState
//var selectedTab by mutableIntStateOf(BottomScreen.Home.number)
var selectedTab: BottomScreen by mutableStateOf(BottomScreen.Home)

@Composable
fun SwipeableScreen(navController: NavController, mainActivity: MainActivity) {
//    if(!::pageState.isInitialized) pageState = rememberPagerState(initialPage = selectedTab) { 5 }
    if(!::pageState.isInitialized) pageState = rememberPagerState(initialPage = selectedTab.number) { 5 }

    LaunchedEffect(selectedTab) { //떠날때
        pageState.scrollToPage(selectedTab.number)
    }
    LaunchedEffect(pageState.currentPage) { //돌아올때
        selectedTab = BottomScreen.entries[pageState.currentPage]
    }

    HorizontalPager(state = pageState) { currentPage ->
        BottomScreen.entries.forEach { item ->
            if(item.number != currentPage) return@forEach
            item.compose(navController, mainActivity)
        }
    }
}