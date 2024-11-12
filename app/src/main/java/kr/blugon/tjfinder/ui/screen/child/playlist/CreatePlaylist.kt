package kr.blugon.tjfinder.ui.screen.child.playlist

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import kr.blugon.tjfinder.module.BlugonTJApi.createPlaylist
import kr.blugon.tjfinder.module.BlugonTJApi.playlists
import kr.blugon.tjfinder.module.Playlist
import kr.blugon.tjfinder.module.State
import kr.blugon.tjfinder.module.User
import kr.blugon.tjfinder.module.isInternetAvailable
import kr.blugon.tjfinder.ui.layout.state.Loading
import kr.blugon.tjfinder.ui.layout.state.NotConnectedNetwork
import kr.blugon.tjfinder.ui.theme.Pretendard
import kr.blugon.tjfinder.ui.theme.ThemeColor


@Composable
fun CreatePlaylist(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var user by remember { mutableStateOf<User?>(null) }
    val playlistCollection = remember { mutableStateListOf<Playlist>() }
    var state by remember { mutableStateOf(State.SUCCESS) }

    var defaultTitle by remember { mutableStateOf("플레이리스트#") }
    var title by remember { mutableStateOf(defaultTitle) }

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        if(!isInternetAvailable(context)) {
            state = State.NOT_INTERNET_AVAILABLE
            return@LaunchedEffect
        }
        user = User.login(context)?: return@LaunchedEffect
        val playlists = user!!.playlists(context) ?: return@LaunchedEffect
        playlistCollection.addAll(playlists)
        defaultTitle = "플레이리스트#${playlistCollection.size+1}"
        title = defaultTitle
    }

    if(state == State.DEFAULT || state == State.NOT_INTERNET_AVAILABLE) return NotConnectedNetwork()
    if(state == State.LOADING) return Loading("플레이리스트 생성중")

    val keyboardManager = LocalSoftwareKeyboardController.current
    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(bottom = 30.dp),
            text = "새 플레이리스트 만들기",
            fontFamily = Pretendard,
            fontSize = TextUnit(27f, TextUnitType.Sp),
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        TextField( //플레이리스트 타이틀 인풋
            modifier = Modifier
                .widthIn(170.dp, 350.dp)
                .focusRequester(focusRequester),
            value = title,
            onValueChange = {
                if(100 < it.length) {
                    Toast.makeText(context, "입력 가능한 최대 길이는 100자입니다", Toast.LENGTH_SHORT).show()
                    return@TextField
                }
                title = it
            },
            textStyle = TextStyle(
                color = Color.White,
                fontFamily = Pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = TextUnit(20f, TextUnitType.Sp),
                textAlign = TextAlign.Center
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = ThemeColor.ItemBackground,
                unfocusedContainerColor = ThemeColor.ItemBackground,
                cursorColor = ThemeColor.LightGray,
                focusedIndicatorColor = ThemeColor.LightGray,
                unfocusedIndicatorColor = ThemeColor.LightGray
            ),
            singleLine = true,
            placeholder = { //Place Holder
                Text(
                    modifier = Modifier.width(150.dp),
                    text = defaultTitle,
                    color = ThemeColor.LightGray,
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Medium,
                    fontSize = TextUnit(18f, TextUnitType.Sp),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { keyboardManager?.hide() })
        )
        Row(
            modifier = Modifier
                .padding(0.dp, 20.dp)
                .fillMaxWidth(0.5f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) { //버튼
            Button( //취소
                modifier = Modifier,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(20.dp, 10.dp),
                shape = RoundedCornerShape(100.dp),
                onClick = { navController.popBackStack() },
                border = BorderStroke(1.dp, Color.Gray)
            ) {
                Text(
                    modifier = Modifier,
                    text = "취소",
                    color = Color.White,
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Bold,
                    fontSize = TextUnit(21f, TextUnitType.Sp),
                )
            }
            Button( //확인
                modifier = Modifier,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ThemeColor.Main
                ),
                contentPadding = PaddingValues(20.dp, 10.dp),
                shape = RoundedCornerShape(100.dp),
                onClick = {
                    coroutineScope.launch {
                        if(user!!.playlists(context) == null) return@launch
                        state = State.LOADING
                        val new = user!!.createPlaylist(title.ifBlank { defaultTitle }) //서버 업로드
                        if(new == null) Toast.makeText(context, "플레이리스트 생성에 실패했습니다", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                },
            ) {
                Text(
                    modifier = Modifier,
                    text = "확인",
                    color = Color.White,
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Bold,
                    fontSize = TextUnit(21f, TextUnitType.Sp),
                )
            }
        }
    }
}