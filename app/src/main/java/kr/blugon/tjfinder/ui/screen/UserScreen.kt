package kr.blugon.tjfinder.ui.screen

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kr.blugon.tjfinder.MainActivity
import kr.blugon.tjfinder.R
import kr.blugon.tjfinder.module.BlugonTJApi
import kr.blugon.tjfinder.module.LoginManager
import kr.blugon.tjfinder.module.User
import kr.blugon.tjfinder.module.isInternetAvailable
import kr.blugon.tjfinder.ui.theme.Pretendard

@Composable
fun UserScreen(navController: NavHostController, mainActivity: MainActivity) {
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
                    AsyncImage( //프로필사진
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color(255, 255, 255, 64), CircleShape),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(user!!.photoUrl)
                            .build(),
                        contentDescription = "profile",
                    )
                    Column( //프로필 사진 옆
                        modifier = Modifier
                            .padding(start = 5.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.Start,
                    ) {
                        Text(buildAnnotatedString { //닉네임
                            withStyle(style = SpanStyle(
                                color = Color.White,
                                fontFamily = Pretendard,
                                fontWeight = FontWeight.Medium,
                                fontSize = TextUnit(23f, TextUnitType.Sp),
                            )
                            ) { append(user!!.name) }
                            withStyle(style = SpanStyle( //태그
                                color = Color.Gray,
                                fontFamily = Pretendard,
                                fontWeight = FontWeight.Medium,
                                fontSize = TextUnit(17f, TextUnitType.Sp)
                            )
                            ) { append("#${user!!.tag}") }
                        }, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Row(
                            modifier = Modifier
                                .clickable {
                                    //TODO(Eidt Profile)
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
                            Text( //프로필 수정
                                modifier = Modifier
                                    .padding(start = 3.dp),
                                text = "프로필 수정(추후 지원 예정)",
                                color = Color.White,
                                fontFamily = Pretendard,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = TextUnit(12f, TextUnitType.Sp),
                                textDecoration = TextDecoration.LineThrough,
                            )
                        }
                    }
                }
                Icon( //설정 아이콘
                    modifier = Modifier
                        .padding(top = 20.dp, end = 20.dp)
                        .size(25.dp)
                        .clickable {
//                            navController.navigate(NavItem.Setting.screenRoute)
                            Toast.makeText(context, "추가 예정", Toast.LENGTH_SHORT).show()
                        },
                    imageVector = ImageVector.vectorResource(R.drawable.gear),
                    contentDescription = "setting",
                    tint = Color.White
                )
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
                text = "Beta 1.4.1",
                color = Color.White,
                fontFamily = Pretendard,
                fontWeight = FontWeight.SemiBold,
                fontSize = TextUnit(15f, TextUnitType.Sp),
            )
        }
    }
}