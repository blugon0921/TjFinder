package kr.blugon.tjfinder.ui.layout

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kr.blugon.tjfinder.MainActivity
import kr.blugon.tjfinder.R
import kr.blugon.tjfinder.module.LoginManager
import kr.blugon.tjfinder.module.SettingManager
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
        composable(DefaultScreen.Login, navController, mainActivity)

        composable(BottomScreen.NewSongs, navController, mainActivity)

        composable(BottomScreen.Search, navController, mainActivity).let {
            composable(ChildScreen.SearchPlaylist, navController, mainActivity)
            composable(ChildScreen.SearchOtherUser, navController, mainActivity)
        }

        composable(BottomScreen.Home, navController, mainActivity)

        composable(BottomScreen.Playlist, navController, mainActivity).let {
            composable(ChildScreen.PlaylistItem, navController, mainActivity)
            composable(ChildScreen.CreatePlaylist, navController, mainActivity)
            composable(ChildScreen.EditPlaylist, navController, mainActivity)
        }

        composable(BottomScreen.User, navController, mainActivity).let {
            composable(ChildScreen.OtherUserItem, navController, mainActivity)
            composable(ChildScreen.EditUser, navController, mainActivity)
            composable(ChildScreen.Setting, navController, mainActivity)
        }
    }
}
fun NavGraphBuilder.composable(screen: Screen, navController: NavController, mainActivity: MainActivity) {
    this.composable(screen.name) {
        if(screen == BottomScreen.Home) {
            val isSuggestPlaylist = SettingManager.getSetting(mainActivity, SettingType.suggestPlaylist)
            if(isSuggestPlaylist) {
                PlaylistHome(navController)
                return@composable
            }
        }
//        if(screen is BottomScreen) {
//            SwipeableScreen(navController, mainActivity)
//            return@composable
//        }
        screen.compose(navController, mainActivity)
    }
}
fun NavController.navigateScreen(screen: Screen) {
//    if(screen is BottomScreen || screen is ChildScreen) {
//        selectedTab = if(screen is BottomScreen) screen else (screen as ChildScreen).parent
//        if(screen is BottomScreen) return
//    }
    if(this.currentBackStackEntry?.destination?.route == screen.name) return
    this.navigate(screen.name) {
        popUpTo(this@navigateScreen.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}


@Composable
fun BottomNav(navController: NavHostController, mainActivity: MainActivity) {
    val context = LocalContext.current
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = Screen.valueOf(if(LoginManager.getSavedUid(context) == null) DefaultScreen.Login.name else backStackEntry?.destination?.route ?: BottomScreen.Home.name)

    BackHandler {
        if(currentScreen == DefaultScreen.Login) {
            mainActivity.finish()
            return@BackHandler
        }
        if(currentScreen !is BottomScreen) return@BackHandler
        else if (currentScreen == BottomScreen.Home) {
            mainActivity.finish()
            return@BackHandler
        }
        navController.navigateScreen(BottomScreen.Home)
    }
    if(Screen.valueOf(backStackEntry?.destination?.route?: "")?.isFullScreen == true) return BottomNavHost(navController = navController, mainActivity)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ThemeColor.Background,
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.075f)
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
//                        selected = selectedTab.number == screen.number || selected[screen] == true,
                        selected = currentScreen!!.number == screen.number || selected[screen] == true,
                        screen = screen,
                        navController = navController,
                    )
                }
            }
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
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
    val compose: @Composable (NavController, MainActivity) -> Unit

    val name: String

    companion object {
        fun valueOf(name: String): Screen? {
//        fun valueOf(name: String): Screen {
            DefaultScreen.entries.forEach { if(it.name == name) return it }
            BottomScreen.entries.forEach { if(it.name == name) return it }
            ChildScreen.entries.forEach { if(it.name == name) return it }
            return null
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
    override val compose: @Composable (NavController, MainActivity) -> Unit,
): Screen {
    Login(true, 2, { navController, mainActivity -> LoginScreen(navController)});
}

enum class BottomScreen(
    @StringRes val title: Int,
    val icon: Int,
    override val number: Int,
    override val compose: @Composable (NavController, MainActivity) -> Unit,
    override val isFullScreen: Boolean = false
): Screen {
    NewSongs(R.string.text_newsongs, R.drawable.starlight, 0, { navController, mainActivity -> NewSongs(navController)}),
    Search(R.string.text_search, R.drawable.search, 1, { navController, mainActivity -> Search(navController)}),
    Home(R.string.text_home, R.drawable.home, 2, { navController, mainActivity -> Home(navController) }),
    Playlist(R.string.text_playlist, R.drawable.playlist, 3, { navController, mainActivity ->PlaylistScreen(navController) }),
    User(R.string.text_user, R.drawable.user, 4, { navController, mainActivity -> UserScreen(navController, mainActivity)})
}


enum class ChildScreen(
    val parent: BottomScreen,
    override val compose: @Composable (NavController, MainActivity) -> Unit,
    override val isFullScreen: Boolean = false
): Screen {
    PlaylistItem(BottomScreen.Playlist, { navController, mainActivity -> InPlaylistScreen(navController)}),
    CreatePlaylist(BottomScreen.Playlist, { navController, mainActivity -> CreatePlaylist(navController)}, true),
    EditPlaylist(BottomScreen.Playlist, { navController, mainActivity -> EditPlaylistScreen(navController)}, true),

    SearchPlaylist(BottomScreen.Search, { navController, mainActivity -> InPlaylistScreen(navController)}),
    SearchOtherUser(BottomScreen.Search, { navController, mainActivity -> InOtherUserScreen(navController)}),

    OtherUserItem(BottomScreen.User, { navController, mainActivity -> InOtherUserScreen(navController)}),

    EditUser(BottomScreen.User, { navController, mainActivity -> EditUserScreen(navController)}, true),
    Setting(BottomScreen.User, { navController, mainActivity -> SettingScreen(navController)}, true);

    override val number: Int get() = this.parent.number
}