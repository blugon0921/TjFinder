package kr.blugon.tjfinder.ui.layout.navigation

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.animation.*
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
        startDestination = BottomScreen.Home.name,
        enterTransition = { //등장 애니메이션
            val before = Screen.valueOf(this.initialState.destination.route!!)
            val target = Screen.valueOf(this.targetState.destination.route!!)
            if(before is BottomScreen && target is ChildScreen) { //Bottom -> Child
                slideInHorizontally { it }
            } else if(before is ChildScreen && target is BottomScreen) { // Child -> Bottom
                scaleIn()
            } else if(before is BottomScreen && target is BottomScreen) { // Bottom -> Bottom
                slideInHorizontally {
                    if (BottomScreen.entries.indexOf(before) < BottomScreen.entries.indexOf(target)) it
                    else if(BottomScreen.entries.indexOf(before) == BottomScreen.entries.indexOf(target)) 0
                    else -it
                }
            } else slideInHorizontally { -it }
        },
        exitTransition = { //퇴장 애니메이션
            val before = Screen.valueOf(this.initialState.destination.route!!)
            val target = Screen.valueOf(this.targetState.destination.route!!)
            if(before is BottomScreen && target is ChildScreen) { //Bottom -> Child
                scaleOut()
            } else if(before is ChildScreen && target is BottomScreen) { // Child -> Bottom
                slideOutHorizontally { it }
            } else if(before is BottomScreen && target is BottomScreen) { // Bottom -> Bottom
                slideOutHorizontally {
                    if (BottomScreen.entries.indexOf(before) < BottomScreen.entries.indexOf(target)) -it
                    else if(BottomScreen.entries.indexOf(before) == BottomScreen.entries.indexOf(target)) 0
                    else it
                }
            } else slideOutHorizontally { -it }
        }
    ) {
        composable(DefaultScreen.Login, navController) { LoginScreen(navController) }

        composable(BottomScreen.NewSongs, navController) { NewSongs(navController) }

        composable(BottomScreen.Search, navController) { Search(navController) }.let {
            composable(ChildScreen.SearchPlaylist, navController) { InPlaylistScreen(navController) }
            composable(ChildScreen.SearchOtherUser, navController) { InOtherUserScreen(navController) }
        }

        composable(BottomScreen.Home, navController) {
            val isSuggestPlaylist = SettingManager[mainActivity, SettingType.suggestPlaylist]
            if(isSuggestPlaylist) PlaylistHome(navController)
            else Home(navController)
        }

        composable(BottomScreen.Playlist, navController) { PlaylistScreen(navController) }.let {
            composable(ChildScreen.PlaylistItem, navController) { InPlaylistScreen(navController) }
            composable(ChildScreen.CreatePlaylist, navController) { CreatePlaylist(navController) }
            composable(ChildScreen.EditPlaylist, navController) { EditPlaylistScreen(navController) }
        }

        composable(BottomScreen.User, navController) { UserScreen(navController) }.let {
            composable(ChildScreen.OtherUserItem, navController) { InOtherUserScreen(navController) }
            composable(ChildScreen.EditUser, navController) { EditUserScreen(navController) }
            composable(ChildScreen.Setting, navController) { SettingScreen(navController) }
        }
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
fun NavController.navigateScreen(screen: Screen) {
//    if(screen is BottomScreen || screen is ChildScreen) {
//        selectedTab = if(screen is BottomScreen) screen else (screen as ChildScreen).parent
//        if(screen is BottomScreen) return
//    }
    if(this.currentBackStackEntry?.destination?.route == screen.name) return
    this.navigate(screen.name) {
        launchSingleTop = true
        restoreState = true
    }
}


@Composable
fun onBack(navController: NavController) {
    val activity = LocalContext.current as Activity
    BackHandler {
        when (navController.currentScreen) {
            DefaultScreen.Login -> activity.finish()
            BottomScreen.Home -> activity.finish()

            is BottomScreen -> navController.navigateScreen(BottomScreen.Home)
            else -> navController.navigateUp()
        }
    }
}


val NavController.currentScreen: Screen?
    get() {
        return Screen.valueOf(
            if (LoginManager.getSavedUid(context) == null) DefaultScreen.Login.name
            else this.currentBackStackEntry?.destination?.route ?: BottomScreen.Home.name
        )
    }
@Composable
fun BottomNav(navController: NavHostController, mainActivity: MainActivity) {
    val context = LocalContext.current
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = Screen.valueOf(
        if (LoginManager.getSavedUid(context) == null) DefaultScreen.Login.name else backStackEntry?.destination?.route
            ?: BottomScreen.Home.name
    )

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
                ChildScreen.entries.forEach { child ->
                    val parent = child.parent
                    if(currentScreen !is ChildScreen) return@forEach
                    if(currentScreen.parent.number == parent.number) selected[parent] = true
                }
                BottomScreen.entries.forEach { screen ->
                    Item(
                        selected = currentScreen!!.number == screen.number || selected[screen] == true,
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
fun RowScope.Item(
    selected: Boolean,
    screen: BottomScreen,
    navController: NavHostController,
) {
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
            navController.navigateScreen(screen)
        }
    )
}

interface Screen {
    val isFullScreen: Boolean
    val number: Int

    val name: String

    companion object {
        fun valueOf(name: String): Screen? {
//        fun valueOf(name: String): Screen {
            return entries.firstOrNull { it.name == name }
//            return BottomScreen.Home
        }

        val entries: List<Screen>
            get() = ArrayList<Screen>().apply {
                addAll(DefaultScreen.entries)
                addAll(BottomScreen.entries)
                addAll(ChildScreen.entries)
            }
    }
}

enum class DefaultScreen(
    override val isFullScreen: Boolean,
    override val number: Int,
): Screen {
    Login(true, 2);
}

enum class BottomScreen(
    @StringRes val title: Int,
    val icon: Int,
    override val number: Int,
    override val isFullScreen: Boolean = false
): Screen {
    NewSongs(R.string.text_newsongs, R.drawable.starlight, 0),
    Search(R.string.text_search, R.drawable.search, 1),
    Home(R.string.text_home, R.drawable.home, 2),
    Playlist(R.string.text_playlist, R.drawable.playlist, 3),
    User(R.string.text_user, R.drawable.user, 4)
}


enum class ChildScreen(
    val parent: BottomScreen,
    override val isFullScreen: Boolean = false
): Screen {
    PlaylistItem(BottomScreen.Playlist),
    CreatePlaylist(BottomScreen.Playlist, true),
    EditPlaylist(BottomScreen.Playlist, true),

    SearchPlaylist(BottomScreen.Search),
    SearchOtherUser(BottomScreen.Search),

    OtherUserItem(BottomScreen.User),

    EditUser(BottomScreen.User, true),
    Setting(BottomScreen.User, true);

    override val number: Int get() = this.parent.number
}