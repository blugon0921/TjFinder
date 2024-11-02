package kr.blugon.tjfinder.ui.layout

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import kr.blugon.tjfinder.MainActivity
import kr.blugon.tjfinder.R
import kr.blugon.tjfinder.module.LoginManager
import kr.blugon.tjfinder.ui.screen.*
import kr.blugon.tjfinder.ui.screen.child.playlist.CreatePlaylist
import kr.blugon.tjfinder.ui.screen.child.playlist.EditPlaylistScene
import kr.blugon.tjfinder.ui.screen.child.playlist.InPlaylistScene
import kr.blugon.tjfinder.ui.screen.child.user.Setting
import kr.blugon.tjfinder.ui.theme.Pretendard
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
        composable(DefaultScreen.Login.name) {
            LoginScreen(navController)
        }

        composable(BottomScreen.NewSongs.name) {
            NewSongs(navController)
        }
        composable(BottomScreen.Search.name) {
            Search(navController)
        }
            composable(ChildScreen.SearchPlaylist.name) {
                InPlaylistScene(navController)
            }
        composable(BottomScreen.Home.name) {
            Home(navController)
        }

        composable(BottomScreen.Playlist.name) {
            PlaylistScene(navController)
        }.let {
            composable(ChildScreen.PlaylistItem.name) {
                InPlaylistScene(navController)
            }
            composable(ChildScreen.CreatePlaylist.name) {
                CreatePlaylist(navController)
            }
            composable(ChildScreen.EditPlaylist.name) {
                EditPlaylistScene(navController)
            }
        }

        composable(BottomScreen.User.name) {
            UserScreen(navController, mainActivity)
        }.let {
            composable(ChildScreen.Setting.name) {
                Setting()
            }
        }
    }
}


@Composable
fun BottomNav(navController: NavHostController, mainActivity: MainActivity) {
    val context = LocalContext.current
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = Screen.valueOf(if(LoginManager.getSavedUid(context) == null) DefaultScreen.Login.name else backStackEntry?.destination?.route ?: BottomScreen.Home.name)


    BackHandler {
        if(currentScreen == DefaultScreen.Login) {
            (context as Activity).finish()
            return@BackHandler
        }
        if(currentScreen !is BottomScreen) return@BackHandler
        else if (currentScreen == BottomScreen.Home) {
            (context as Activity).finish()
            return@BackHandler
        }
        navController.navigate(BottomScreen.Home.name) {
            launchSingleTop = true
            popUpTo(navController.graph.id) {
                inclusive = true
            }
        }
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
                    if(currentScreen is ChildScreen) {
                        if(currentScreen.parent.name == parent.name) selected[parent] = true
                    }
                }
                BottomScreen.entries.forEach { screen ->
                    Item(
                        selected = currentScreen!!.getName() == screen.name || selected[screen] == true,
                        icon = screen.icon,
                        label = screen.title,
                        name = screen.name,
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
    @StringRes label: Int,
    @DrawableRes icon: Int,
    name: String,
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
                    imageVector = ImageVector.vectorResource(icon),
                    contentDescription = name,
                    modifier = Modifier.size(24.dp),
                    tint = when(selected) {
                        true -> ThemeColor.Main
                        false ->ThemeColor.Icon
                    }
                )
                AnimatedVisibility(visible = selected, modifier = Modifier.padding(0.dp, 2.5.dp, 0.dp, 0.dp)) {
                    Text(
                        text = stringResource(id = label),
                        color = ThemeColor.Main,
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.Medium,
                        fontSize = TextUnit(12f, TextUnitType.Sp)
                    )
                }
            }
        },
        onClick = {
            if(navController.currentDestination?.route == name) return@NavigationBarItem
            navController.navigate(name) {
                launchSingleTop = true
                popUpTo(navController.graph.id) {
                    inclusive = true
                }
            }
        }
    )
}

interface Screen {
    val isFullScreen: Boolean

    fun getName(): String {
        return when (this) {
            is DefaultScreen -> this.name
            is BottomScreen -> this.name
            is ChildScreen -> this.name
            else -> ""
        }
    }
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
): Screen {
    Login(true);
}

enum class BottomScreen(
    @StringRes val title: Int,
    val icon: Int,
    override val isFullScreen: Boolean = false,
): Screen {
    NewSongs(R.string.text_newsongs, R.drawable.starlight),
    Search(R.string.text_search, R.drawable.search),
    Home(R.string.text_home, R.drawable.home),
    Playlist(R.string.text_playlist, R.drawable.playlist),
    User(R.string.text_user, R.drawable.user)
}


enum class ChildScreen(
    val parent: BottomScreen,
    override val isFullScreen: Boolean = false
): Screen {
    PlaylistItem(BottomScreen.Playlist),
    CreatePlaylist(BottomScreen.Playlist, true),
    EditPlaylist(BottomScreen.Playlist, true),

    SearchPlaylist(BottomScreen.Search),

    Setting(BottomScreen.User)
}