package kr.blugon.tjfinder.ui.screen

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kr.blugon.tjfinder.MainActivity
import kr.blugon.tjfinder.R
import kr.blugon.tjfinder.VERSION
import kr.blugon.tjfinder.module.LoginManager
import kr.blugon.tjfinder.module.User
import kr.blugon.tjfinder.ui.layout.PretendardText
import kr.blugon.tjfinder.ui.layout.card.user.UserDescription
import kr.blugon.tjfinder.ui.layout.card.user.UserName
import kr.blugon.tjfinder.ui.layout.card.user.UserProfileImage
import kr.blugon.tjfinder.ui.layout.navigation.ChildScreen
import kr.blugon.tjfinder.ui.layout.navigation.DefaultScreen
import kr.blugon.tjfinder.ui.layout.navigation.navigateScreen
import kr.blugon.tjfinder.ui.layout.state.CenterText
import kr.blugon.tjfinder.ui.theme.Pretendard
import kr.blugon.tjfinder.ui.theme.ThemeColor
import kr.blugon.tjfinder.utils.api.TjFinderApi
import kr.blugon.tjfinder.utils.isApiServerOpened
import kr.blugon.tjfinder.utils.isInternetAvailable

@Composable
//fun UserScreen(navController: NavController, mainActivity: MainActivity) {
fun UserScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isApiServerOpened by remember { mutableStateOf(true) }

    var user by remember { mutableStateOf<User?>(null) }
    LaunchedEffect(Unit) {
        isApiServerOpened = isApiServerOpened()
        if(!isInternetAvailable(context) || !isApiServerOpened) return@LaunchedEffect
        if(LoginManager.getSavedUid(context) != null) {
            user = TjFinderApi.login(LoginManager.getSavedUid(context)?: return@LaunchedEffect)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        if(!isApiServerOpened) CenterText(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f),
            text = "서버 연결에 실패했습니다"
        ) else {
            if(user == null) {
                Column(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextButton(onClick = {
                        navController.navigateScreen(DefaultScreen.Login)
                    }) {
                        PretendardText(
                            text = "로그인",
                            color = ThemeColor.HyperLink,
                            fontSize = 30f,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            } else {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(top = 20.dp, start = 20.dp)
                                .fillMaxWidth(0.8f),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            UserProfileImage( //프로필사진
                                user = user!!,
                                size = 80.dp
                            )
                            Column( //프로필 사진 옆
                                modifier = Modifier
                                    .padding(start = 5.dp),
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.Start,
                            ) {
                                UserName(user = user!!) //닉네임#태그
                                Row(
                                    modifier = Modifier
                                        .clickable {
                                            navController.navigate(ChildScreen.EditUser.name)
                                        },
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start,
                                ) {
                                    Icon( //펜 아이콘
                                        modifier = Modifier
                                            .size(18.dp),
                                        imageVector = ImageVector.vectorResource(R.drawable.pen),
                                        contentDescription = "edit",
                                        tint = Color.White
                                    )
                                    PretendardText( //프로필 편집
                                        modifier = Modifier.padding(start = 3.dp),
                                        text = "프로필 편집",
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12f
                                    )
                                }
                            }
                        }
                        Icon( //설정 아이콘
                            modifier = Modifier
                                .padding(top = 20.dp, end = 20.dp)
                                .size(25.dp)
                                .clickable {
                                    navController.navigateScreen(ChildScreen.Setting)
//                                Toast.makeText(context, "추가 예정", Toast.LENGTH_SHORT).show()
                                },
                            imageVector = ImageVector.vectorResource(R.drawable.gear),
                            contentDescription = "setting",
                            tint = Color.White
                        )
                    }
                    if(user!!.description.isNotBlank()) { //소개
                        UserDescription(
                            modifier = Modifier.padding(top = 5.dp, start = 20.dp),
                            user = user!!,
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if(isApiServerOpened && user != null) Arrangement.SpaceBetween else Arrangement.End,
        ) {
            if(isApiServerOpened && user != null) {
                TextButton(onClick = {
                    LoginManager.logout(context)
                    val intent = Intent((context as Activity), MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                    context.finish()
                }) {
                    Text(text = "로그아웃")
                }
            }
            Text( //버전
                text = VERSION,
                color = Color.White,
                fontFamily = Pretendard,
                fontWeight = FontWeight.SemiBold,
                fontSize = TextUnit(15f, TextUnitType.Sp),
            )
        }
    }
}