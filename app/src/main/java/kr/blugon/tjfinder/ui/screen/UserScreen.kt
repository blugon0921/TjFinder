package kr.blugon.tjfinder.ui.screen

import android.content.Intent
import android.widget.Toast
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import kr.blugon.tjfinder.MainActivity
import kr.blugon.tjfinder.R
import kr.blugon.tjfinder.module.BlugonTJApi
import kr.blugon.tjfinder.module.LoginManager
import kr.blugon.tjfinder.module.User
import kr.blugon.tjfinder.module.isInternetAvailable
import kr.blugon.tjfinder.ui.layout.ChildScreen
import kr.blugon.tjfinder.ui.layout.PretendardText
import kr.blugon.tjfinder.ui.layout.card.user.UserDescription
import kr.blugon.tjfinder.ui.layout.card.user.UserName
import kr.blugon.tjfinder.ui.layout.card.user.UserProfileImage
import kr.blugon.tjfinder.ui.layout.navigateScreen
import kr.blugon.tjfinder.ui.theme.Pretendard

@Composable
fun UserScreen(navController: NavController, mainActivity: MainActivity) {
    val context = LocalContext.current
    val isInternetAvailable = isInternetAvailable(context)
    val coroutineScope = rememberCoroutineScope()

    var user by remember { mutableStateOf<User?>(null) }
    LaunchedEffect(Unit) {
        if(!isInternetAvailable) return@LaunchedEffect
        if(LoginManager.getSavedUid(context) != null) {
            user = BlugonTJApi.login(LoginManager.getSavedUid(context)!!)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        if(user != null) {
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TextButton(onClick = {
                LoginManager.logout(context)
                val intent = Intent(mainActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                mainActivity.startActivity(intent)
                mainActivity.finish()
            }) {
                Text(text = "로그아웃")
            }
            Text( //버전
                text = "Beta 1.6.3",
                color = Color.White,
                fontFamily = Pretendard,
                fontWeight = FontWeight.SemiBold,
                fontSize = TextUnit(15f, TextUnitType.Sp),
            )
        }
    }
}