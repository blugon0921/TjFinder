package kr.blugon.tjfinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.launch
import kr.blugon.tjfinder.module.LoginManager
import kr.blugon.tjfinder.utils.api.TjFinderApi.loadMemoList
import kr.blugon.tjfinder.module.User
import kr.blugon.tjfinder.module.database.NewCacheDB
import kr.blugon.tjfinder.module.database.PopularCacheDB
import kr.blugon.tjfinder.module.database.SongCacheDB
import kr.blugon.tjfinder.ui.layout.PretendardText
import kr.blugon.tjfinder.ui.layout.TextConfirmCancelModal
import kr.blugon.tjfinder.ui.layout.navigation.BottomNav
import kr.blugon.tjfinder.ui.theme.Pretendard
import kr.blugon.tjfinder.ui.theme.ThemeColor
import kr.blugon.tjfinder.ui.theme.TjFinderTheme
import kr.blugon.tjfinder.utils.*


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

const val VERSION = "Beta 1.7.1"
@Composable
fun Greeting(mainActivity: MainActivity) {
    val context = LocalContext.current
    SongCacheDB(context).close()
    PopularCacheDB(context).close()
    NewCacheDB(context).close()
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()

    var latestVersion by remember { mutableStateOf<Version?>(null) }
    var isLatestVersion by remember { mutableStateOf(true) }
    var showUpdateModal by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if(!isInternetAvailable(context)) return@LaunchedEffect
        latestVersion = latestVersion()?: return@LaunchedEffect
        isLatestVersion = isLatestVersion(latestVersion!!)
        if(!isLatestVersion) {
            showUpdateModal = true
        }
        if(!isApiServerOpened()) return@LaunchedEffect
        val user = User.login(context)?: return@LaunchedEffect
        user.loadMemoList()
    }

    if(!isLatestVersion) {
        if(showUpdateModal) AlertDialog(
            modifier = Modifier,
            containerColor = ThemeColor.ItemBackground,
            shape = RoundedCornerShape(12.dp),
            onDismissRequest = {},
            text = {
                PretendardText(
                    "${latestVersion?.version}버전 업데이트가 있습니다.\n업데이트 파일을 다운로드하시겠습니까?",
                    fontSize = 15f,
                    fontWeight = FontWeight.SemiBold
                )
            },
            dismissButton = {
                Button(
                    modifier = Modifier,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues(15.dp, 10.dp),
                    shape = RoundedCornerShape(100.dp),
                    onClick = { showUpdateModal = false },
                    border = BorderStroke(1.dp, Color.Gray)
                ) {
                    Text(
                        modifier = Modifier,
                        text = "다음에",
                        color = Color.White,
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.Bold,
                        fontSize = TextUnit(15f, TextUnitType.Sp),
                    )
                }
            },
            confirmButton = { //확인버튼
                Button(
                    modifier = Modifier,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ThemeColor.Main
                    ),
                    contentPadding = PaddingValues(15.dp, 10.dp),
                    shape = RoundedCornerShape(100.dp),
                    onClick = {
                        //다운로드
                        showUpdateModal = false
                        coroutineScope.launch {
                            downloadApk(context, latestVersion?: return@launch)
                        }
                    },
                ) {
                    Text(
                        modifier = Modifier,
                        text = "확인",
                        color = Color.White,
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.Bold,
                        fontSize = TextUnit(15f, TextUnitType.Sp),
                    )
                }
            }
        )
    }
//    User.logout(context)
    BottomNav(navController = navController, mainActivity)
}