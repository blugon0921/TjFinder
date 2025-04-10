package kr.blugon.tjfinder.ui.screen.child.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kr.blugon.tjfinder.module.OtherUser
import kr.blugon.tjfinder.module.Playlist
import kr.blugon.tjfinder.module.State
import kr.blugon.tjfinder.module.User
import kr.blugon.tjfinder.ui.layout.LoadingStateScreen
import kr.blugon.tjfinder.ui.layout.PretendardText
import kr.blugon.tjfinder.ui.layout.card.playlist.PlaylistCard
import kr.blugon.tjfinder.ui.layout.card.user.UserDescription
import kr.blugon.tjfinder.ui.layout.card.user.UserName
import kr.blugon.tjfinder.ui.layout.card.user.UserProfileImage
import kr.blugon.tjfinder.ui.screen.child.user.InOtherUser.otherUser
import kr.blugon.tjfinder.ui.theme.ThemeColor
import kr.blugon.tjfinder.utils.api.finder.playlists
import kr.blugon.tjfinder.utils.isInternetAvailable


object InOtherUser {
    lateinit var otherUser: OtherUser
}
@Composable
fun InOtherUserScreen(navController: NavController) {
    val context = LocalContext.current

    val playlists = remember { mutableStateListOf<Playlist>() }
    var user by remember { mutableStateOf<User?>(null) }
    var state by remember { mutableStateOf(State.DEFAULT) }

    LaunchedEffect(Unit) {
        if(!isInternetAvailable(context)) {
            state = State.NOT_INTERNET_AVAILABLE
            return@LaunchedEffect
        }
        user = User.login(context)?: return@LaunchedEffect
        CoroutineScope(IO).launch {
            state = State.LOADING
            val playlistCollection = otherUser.playlists(context, true, user!!.uid)
            if(playlistCollection == null) {
                state = State.FAIL
                return@launch
            }
            playlists.addAll(playlistCollection)
            state = State.SUCCESS
        }
    }
    val listState = rememberLazyListState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp, 14.5.dp, 0.dp, 0.dp),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(1) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .padding(top = 20.dp, start = 20.dp)
                        .fillMaxWidth(0.8f),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    UserProfileImage( //프로필사진
                        user = otherUser,
                        size = 70.dp
                    )
                    Column( //프로필 사진 옆
                        modifier = Modifier
                            .padding(start = 5.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.Start,
                    ) {
                        UserName(user = otherUser) //닉네임#태그
                    }
                }
                if(otherUser.description.isNotBlank()) { //소개
                    UserDescription(
                        modifier = Modifier.padding(top = 5.dp, start = 20.dp),
                        user = otherUser,
                    )
                }
                PretendardText(
                    modifier = Modifier.padding(top = 50.dp, start = 20.dp, bottom = 5.dp),
                    text = "공개 플레이리스트",
                    color = ThemeColor.LightGray,
                    fontSize = 20f
                )
                Box(modifier = Modifier.fillMaxWidth().height(3.dp).background(ThemeColor.Gray)) //구분선
            }
        }
        LoadingStateScreen(state, fail = {}, loadingMessage = "플레이리스트 로딩중") {
            if(playlists.isEmpty()) {
                items(1) {
                    Column (
                        modifier = Modifier
                            .fillMaxSize()
                            .fillParentMaxHeight(0.75f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        PretendardText(
                            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 5.dp),
                            text = "공개 플레이리스트 없음",
                            color = ThemeColor.Main
                        )
                    }
                }
                return@LoadingStateScreen
            }
            items(1) { Box(modifier = Modifier.height(17.5.dp)) }
            items(playlists.size) {
                val playlist = playlists[it]
                PlaylistCard(
                    playlist = playlist,
                    reload = {},
                    isFirst = it == 0,
                    isLast = it == playlists.lastIndex,
                    isFromSearch = true,
                    navController
                )
            }
        }
    }
}