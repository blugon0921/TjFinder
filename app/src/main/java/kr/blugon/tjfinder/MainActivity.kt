package kr.blugon.tjfinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.FirebaseApp
import kr.blugon.tjfinder.module.BlugonTJApi.loadMemoList
import kr.blugon.tjfinder.module.User
import kr.blugon.tjfinder.module.database.NewCacheDB
import kr.blugon.tjfinder.module.database.PopularCacheDB
import kr.blugon.tjfinder.module.database.SongCacheDB
import kr.blugon.tjfinder.module.isInternetAvailable
import kr.blugon.tjfinder.ui.layout.BottomNav
import kr.blugon.tjfinder.ui.theme.ThemeColor
import kr.blugon.tjfinder.ui.theme.TjFinderTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            val systemUiController = rememberSystemUiController()


            TjFinderTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = ThemeColor.Background) {
                    SideEffect {
                        systemUiController.setStatusBarColor(Color.Transparent, false)
                        systemUiController.setSystemBarsColor(ThemeColor.Navigation, false)
                    }
                    Greeting(this)
                }
            }
        }
    }
}

@Composable
fun Greeting(mainActivity: MainActivity) {
    val context = LocalContext.current
    SongCacheDB(context).close()
    PopularCacheDB(context).close()
    NewCacheDB(context).close()
    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        if(!isInternetAvailable(context)) return@LaunchedEffect
        val user = User.login(context)?: return@LaunchedEffect
        user.loadMemoList()
    }


//    User.logout(context)
    BottomNav(navController = navController, mainActivity)
}