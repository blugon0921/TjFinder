package kr.blugon.tjfinder.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import kr.blugon.tjfinder.R
import kr.blugon.tjfinder.module.*
import kr.blugon.tjfinder.utils.api.TjFinderApi.libraryList
import kr.blugon.tjfinder.utils.api.TjFinderApi.playlists
import kr.blugon.tjfinder.module.State
import kr.blugon.tjfinder.ui.layout.navigation.ChildScreen
import kr.blugon.tjfinder.ui.layout.LoadingStateScreen
import kr.blugon.tjfinder.ui.layout.SortableTopBar
import kr.blugon.tjfinder.ui.layout.card.playlist.PlaylistCard
import kr.blugon.tjfinder.ui.layout.navigation.navigateScreen
import kr.blugon.tjfinder.ui.layout.state.CenterText
import kr.blugon.tjfinder.ui.theme.Pretendard
import kr.blugon.tjfinder.ui.theme.ThemeColor
import kr.blugon.tjfinder.utils.api.TjFinderApi
import kr.blugon.tjfinder.utils.isApiServerOpened
import kr.blugon.tjfinder.utils.isInternetAvailable
import my.nanihadesuka.compose.LazyColumnScrollbar


private val sortExpanded = mutableStateOf(false)
private var sort by mutableStateOf(PlaylistSortType.TITLE)
@Composable
fun PlaylistScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var user by remember { mutableStateOf<User?>(null) }
    var state by remember { mutableStateOf(State.DEFAULT) }
    val playlistCollection = remember { mutableStateListOf<Playlist>() }
    val libraryList = remember { mutableStateListOf<Playlist>() }

    val reload: () -> Unit = {
        if(!isInternetAvailable(context)) state = State.NOT_INTERNET_AVAILABLE
        else {
            coroutineScope.launch {
                state = State.LOADING
                val playlists = user!!.playlists(context)
                if(playlists == null) {
                    state = State.FAIL
                    return@launch
                }
                playlistCollection.clear()
                playlistCollection.addAll(playlists)
                val libraries = user!!.libraryList(context)
                if(libraries == null) {
                    state = State.FAIL
                    return@launch
                }
                libraryList.clear()
                libraryList.addAll(libraries)
                when(sort) {
                    PlaylistSortType.SONG_COUNT -> playlistCollection.sortBy { it.songIdList.size }
                    PlaylistSortType.TITLE -> playlistCollection.sortBy { it.title }
                    PlaylistSortType.ID -> playlistCollection.sortBy { it.id }
                    PlaylistSortType.CREATOR -> playlistCollection.sortBy { it.creator }
                }
                when(sort) {
                    PlaylistSortType.SONG_COUNT -> libraryList.sortBy { it.songIdList.size }
                    PlaylistSortType.TITLE -> libraryList.sortBy { it.title }
                    PlaylistSortType.ID -> libraryList.sortBy { it.id }
                    PlaylistSortType.CREATOR -> libraryList.sortBy { it.creator }
                }
                state = State.SUCCESS
            }
        }
    }

    var isApiServerOpened by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        if(!isInternetAvailable(context)) {
            state = State.NOT_INTERNET_AVAILABLE
            return@LaunchedEffect
        }
        isApiServerOpened = isApiServerOpened()
        if(!isApiServerOpened) return@LaunchedEffect
        user = TjFinderApi.login(LoginManager.getSavedUid(context)?: return@LaunchedEffect)?: return@LaunchedEffect
        reload()
    }

    Column {
        SortableTopBar(
            title = "플레이리스트",
            iconId = R.drawable.playlist,
            isExpanded = sortExpanded,
            items = ArrayList<PlaylistSortType>().apply {
                PlaylistSortType.entries.forEach { this.add(it) }
            }
        ) { //아이템
            if(playlistCollection.isEmpty()) return@SortableTopBar
            sort = it
            sortExpanded.value = false
            when(it) {
                PlaylistSortType.SONG_COUNT -> playlistCollection.sortBy { it.songIdList.size }
                PlaylistSortType.TITLE -> playlistCollection.sortBy { it.title }
                PlaylistSortType.ID -> playlistCollection.sortBy { it.id }
                PlaylistSortType.CREATOR -> playlistCollection.sortBy { it.creator }
            }
            when(it) {
                PlaylistSortType.SONG_COUNT -> libraryList.sortBy { it.songIdList.size }
                PlaylistSortType.TITLE -> libraryList.sortBy { it.title }
                PlaylistSortType.ID -> libraryList.sortBy { it.id }
                PlaylistSortType.CREATOR -> libraryList.sortBy { it.creator }
            }
        }

        if(!isApiServerOpened) return CenterText(text = "서버 연결에 실패했습니다")
        if(user == null) return CenterText(text = "로그인 후 이용 가능합니다")
        println("asdf")
        println("asdf")
        println("asdf")
        println("asdf")
        println("asdf")
        println("asdf")
        println("asdf")
        println("asdf")
        println("asdf")
        println("asdf")
        println("asdf")
        println("asdf")
        println("asdf")
        println("asdf")
        println("asdf")
        println("asdf")
        println("asdf")

        val listState = rememberLazyListState()
        var enableScroll by remember { mutableStateOf(false) }
        LazyColumnScrollbar(
            modifier = Modifier.fillMaxSize(),
            listState = listState,
            thumbColor = ThemeColor.Gray,
            thumbSelectedColor = ThemeColor.LittleLightGray,
            alwaysShowScrollBar = true,
            enabled = enableScroll
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp, 14.5.dp, 0.dp, 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                state = listState
            ) {
                LoadingStateScreen(state, fail = {enableScroll = false}, loadingMessage = "플레이리스트 로딩중") {
                    enableScroll = 6 <= playlistCollection.size+libraryList.size
                    items(playlistCollection.size) { i ->
                        val playlist = playlistCollection[i]
                        PlaylistCard(playlist = playlist, reload = reload, i == 0, navController = navController)
                    }
                    items(libraryList.size) { i ->
                        val playlist = libraryList[i]
                        PlaylistCard(playlist = playlist, reload = reload, playlistCollection.isEmpty() && i == 0, navController = navController)
                    }
                    items(1) {
                        Box( //플레이 리스트 추가
                            modifier = Modifier
                                .padding(vertical = if(!playlistCollection.isEmpty()) 17.5.dp else 0.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(ThemeColor.AddPlaylist)
                                .clickable { //플레이리스트 생성 씬으로 이동
                                    navController.navigateScreen(ChildScreen.CreatePlaylist)
                                },
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp).fillMaxWidth(0.875f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon( //+아이콘
                                    modifier = Modifier.size(24.dp),
                                    imageVector = ImageVector.vectorResource(R.drawable.plus),
                                    contentDescription = "plusPlaylist",
                                    tint = Color.Gray,
                                )
                                Text(
                                    modifier = Modifier.padding(start = 5.dp),
                                    text = "플레이리스트 추가",
                                    color = Color.Gray,
                                    fontFamily = Pretendard,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = TextUnit(18f, TextUnitType.Sp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}