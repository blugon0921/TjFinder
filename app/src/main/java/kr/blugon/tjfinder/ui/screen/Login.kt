package kr.blugon.tjfinder.ui.screen

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.blugon.tjfinder.R
import kr.blugon.tjfinder.utils.api.TjFinderApi
import kr.blugon.tjfinder.module.LoginManager
import kr.blugon.tjfinder.module.User
import kr.blugon.tjfinder.ui.layout.navigation.BottomScreen
import kr.blugon.tjfinder.ui.layout.navigation.navigateMainScreen
import kr.blugon.tjfinder.ui.layout.navigation.navigateScreen
import kr.blugon.tjfinder.ui.theme.Pretendard
import kr.blugon.tjfinder.ui.theme.ThemeColor
import kr.blugon.tjfinder.utils.api.TjFinderApi.loadMemoList

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isLogin by remember { mutableStateOf(LoginManager.getSavedUid(context) != null) }
    var user by remember { mutableStateOf<User?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        if(isLogin) {
            coroutineScope.launch {
                user = TjFinderApi.login(LoginManager.getSavedUid(context)!!)
            }
            return@rememberLauncherForActivityResult
        }
        LoginManager.login(activityResult = it, { result, profileUser -> //onSuccess
            isLogin = true
            Log.d("TjFinder", "Login to ${profileUser.name}")
            coroutineScope.launch(Dispatchers.IO) {
                user = TjFinderApi.registerUser(profileUser.uid, profileUser.email, profileUser.name, profileUser.photoUrl)
            }
            if(LoginManager.getSavedUid(context) == null) LoginManager.saveLoginInfo(context, profileUser.uid)
        }) { task,exception -> //onException
            Log.d("TjFinder", "________________________________________________________________________")
            Log.d("TjFinder", task?.exception.toString())
            Log.e("TjFinder", "Error", Throwable(exception))
            Log.d("TjFinder", "________________________________________________________________________")
        }
    }

    val token = stringResource(id = R.string.default_web_client_id)
    if(isLogin) {
//        navController.navigateScreen(BottomScreen.Home)
        navController.navigateMainScreen(BottomScreen.Home)
        return
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(1) {
            Column(
                modifier = Modifier.fillMaxSize().fillParentMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier
                        .padding(0.dp, 0.dp, 0.dp, 20.dp),
                    text = "로그인",
                    color = Color.White,
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = TextUnit(28f, TextUnitType.Sp),
                    textAlign = TextAlign.Center,
                )
                Button(
                    modifier = Modifier
                        .fillMaxWidth(0.5f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ThemeColor.ItemBackground
                    ),
                    shape = RoundedCornerShape(12.dp),
                    onClick = {
                        val googleSignInOptions = GoogleSignInOptions
                            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(token)
                            .requestEmail()
                            .build()

                        val googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)
                        launcher.launch(googleSignInClient.signInIntent)
                    },
                ) {
                    Image( //구글 아이콘
                        modifier = Modifier
                            .size(24.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.color_google),
                        contentDescription = "search",
                    )
                    Text(
                        modifier = Modifier.padding(start = 10.dp),
                        text = "구글로 계속하기",
                        color = Color.White,
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.Bold,
                        fontSize = TextUnit(14f, TextUnitType.Sp),
                    )
                }
            }
        }
    }
}