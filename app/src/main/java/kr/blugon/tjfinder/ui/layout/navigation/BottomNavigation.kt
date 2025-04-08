package kr.blugon.tjfinder.ui.layout.navigation

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kr.blugon.tjfinder.MainActivity
import kr.blugon.tjfinder.R
import kr.blugon.tjfinder.module.LoginManager
import kr.blugon.tjfinder.module.SettingManager
import kr.blugon.tjfinder.ui.layout.PretendardText
import kr.blugon.tjfinder.ui.screen.*
import kr.blugon.tjfinder.ui.screen.child.playlist.CreatePlaylist
import kr.blugon.tjfinder.ui.screen.child.playlist.EditPlaylistScreen
import kr.blugon.tjfinder.ui.screen.child.playlist.InPlaylistScreen
import kr.blugon.tjfinder.ui.screen.child.user.EditUserScreen
import kr.blugon.tjfinder.ui.screen.child.user.InOtherUserScreen
import kr.blugon.tjfinder.ui.screen.child.user.SettingScreen
import kr.blugon.tjfinder.ui.screen.child.user.SettingType
import kr.blugon.tjfinder.ui.theme.ThemeColor


@Composable
fun BottomNavHost(navController: NavHostController, mainActivity: MainActivity) {
    NavHost(
        navController = navController,
        startDestination = DefaultScreen.Main.name,
//        startDestination = BottomScreen.Home.name,
        enterTransition = { //등장 애니메이션
            val before = Screen.valueOf(this.initialState.destination.route!!)
            val target = Screen.valueOf(this.targetState.destination.route!!)
            if(before == DefaultScreen.Main && target is ChildScreen) { //Main -> Child
                slideInHorizontally { it }
            } else if(before is ChildScreen && target == DefaultScreen.Main) { // Child -> Main
                scaleIn()
            } else slideInHorizontally { it }
        },
        exitTransition = { //퇴장 애니메이션
            val before = Screen.valueOf(this.initialState.destination.route!!)
            val target = Screen.valueOf(this.targetState.destination.route!!)
            if(before == DefaultScreen.Main && target is ChildScreen) { //Main -> Child
                scaleOut()
            } else if(before is ChildScreen && target == DefaultScreen.Main) { // Child -> Main
                slideOutHorizontally { it }
            } else slideOutHorizontally { it }
        }
    ) {
        composable(DefaultScreen.Login, navController) { LoginScreen(navController) }

        composable(ChildScreen.SearchPlaylist, navController) { InPlaylistScreen(navController) }
        composable(ChildScreen.SearchOtherUser, navController) { InOtherUserScreen(navController) }

//        composable(BottomScreen.Home, navController) {
//            val isSuggestPlaylist = SettingManager[mainActivity, SettingType.suggestPlaylist]
//            if(isSuggestPlaylist) PlaylistHome(navController)
//            else Home(navController)
//        }

        composable(ChildScreen.PlaylistItem, navController) { InPlaylistScreen(navController) }
        composable(ChildScreen.CreatePlaylist, navController) { CreatePlaylist(navController) }
        composable(ChildScreen.EditPlaylist, navController) { EditPlaylistScreen(navController) }

        composable(ChildScreen.OtherUserItem, navController) { InOtherUserScreen(navController) }
        composable(ChildScreen.EditUser, navController) { EditUserScreen(navController) }
        composable(ChildScreen.Setting, navController) { SettingScreen(navController) }

        composable(DefaultScreen.Main, navController) { MainScreen(navController) }
    }
}
fun NavGraphBuilder.composable(
    screen: Screen,
    navController: NavController,
    compose: @Composable () -> Unit
) = composable(screen.name) {
    onBack(navController)
    compose()
}
fun NavController.navigateScreen(screen: Screen, coroutineScope: CoroutineScope? = null) {
//    if(screen is BottomScreen || screen is ChildScreen) {
//        selectedTab = if(screen is BottomScreen) screen else (screen as ChildScreen).parent
//        if(screen is BottomScreen) return
//    }
    if(this.currentBackStackEntry?.destination?.route == screen.name) return
    if(screen is BottomScreen) {
        if(this.currentScreen != DefaultScreen.Main) {
            this.navigate(DefaultScreen.Main.name) {
                launchSingleTop = true
                restoreState = true
            }
        }
        if(coroutineScope == null) return
        coroutineScope.launch {
            mainStates.pagerState!!.animateScrollToPage(
                screen.number,
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            )
        }
        return
    }
    this.navigate(screen.name) {
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun NavController.navigateMainScreen(screen: BottomScreen) {
    val coroutineScope = rememberCoroutineScope()
    if(this.currentScreen != DefaultScreen.Main) {
        this.navigate(DefaultScreen.Main.name) {
            launchSingleTop = true
            restoreState = true
        }
    }
    coroutineScope.launch {
        mainStates.pagerState!!.animateScrollToPage(screen.number)
    }
}
fun NavController.navigateMainScreen(screen: BottomScreen, coroutineScope: CoroutineScope) {
    if(this.currentScreen != DefaultScreen.Main) {
        this.navigate(DefaultScreen.Main.name) {
            launchSingleTop = true
            restoreState = true
        }
    }
    coroutineScope.launch {
        mainStates.pagerState!!.animateScrollToPage(screen.number)
    }
}


val NavController.currentScreen: Screen?
    get() {
        return Screen.valueOf(
            if (LoginManager.getSavedUid(context) == null) DefaultScreen.Login.name
            else this.currentBackStackEntry?.destination?.route ?: DefaultScreen.Main.name
        )
    }
@Composable
fun BottomNav(navController: NavHostController, mainActivity: MainActivity) {
    val backStackEntry by navController.currentBackStackEntryAsState()

    fun isFullScreen(): Boolean = Screen.valueOf(backStackEntry?.destination?.route ?: "")?.isFullScreen == true

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ThemeColor.Background,
        bottomBar = {
            if(isFullScreen()) return@Scaffold
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp)
                    .clip(RoundedCornerShape(14.dp, 14.dp, 0.dp, 0.dp)),
                containerColor = ThemeColor.Navigation
            ) {
                val selected = HashMap<Screen, Boolean>()
                BottomScreen.entries.forEach { screen ->
                    Item(
                        selected = mainStates.pagerState?.currentPage == screen.number || selected[screen] == true,
                        screen = screen,
                        navController = navController,
                    )
                }
            }
        }
    ) {
        Box(
            modifier = if(!isFullScreen()) Modifier.padding(it) else Modifier
        ) {
            BottomNavHost(navController = navController, mainActivity)
        }
    }
}

@Composable
private fun RowScope.Item(
    selected: Boolean,
    screen: BottomScreen,
    navController: NavHostController,
) {
    val coroutineScope = rememberCoroutineScope()

    NavigationBarItem(
        modifier = Modifier.padding(0.dp, 8.dp, 0.dp, 0.dp),
        selected = selected,
        alwaysShowLabel = selected,
        colors = NavigationBarItemDefaults.colors(
            unselectedIconColor = ThemeColor.Icon,
            selectedIconColor = ThemeColor.Main,
            selectedTextColor = ThemeColor.Main,
            indicatorColor = ThemeColor.Navigation
        ),
        icon = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = ImageVector.vectorResource(screen.icon),
                    contentDescription = screen.name,
                    modifier = Modifier.size(24.dp),
                    tint = when(selected) {
                        true -> ThemeColor.Main
                        false ->ThemeColor.Icon
                    }
                )
                AnimatedVisibility(visible = selected, modifier = Modifier.padding(0.dp, 2.5.dp, 0.dp, 0.dp)) {
                    PretendardText(
                        text = stringResource(id = screen.title),
                        color = ThemeColor.Main,
                        fontSize = 12f
                    )
                }
            }
        },
        onClick = {
            if(navController.graph.route == screen.name) return@NavigationBarItem
//            if(selectedTab.name == screen.name) return@NavigationBarItem
            navController.navigateScreen(screen, coroutineScope)
        }
    )
}