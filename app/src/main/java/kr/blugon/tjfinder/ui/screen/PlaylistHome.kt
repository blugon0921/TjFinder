package kr.blugon.tjfinder.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import kr.blugon.tjfinder.R
import kr.blugon.tjfinder.module.*
import kr.blugon.tjfinder.utils.api.TjFinderApi.searchPlaylist
import kr.blugon.tjfinder.module.State
import kr.blugon.tjfinder.ui.layout.*
import kr.blugon.tjfinder.ui.layout.card.playlist.PlaylistCard
import kr.blugon.tjfinder.ui.layout.navigation.DefaultScreen
import kr.blugon.tjfinder.ui.layout.navigation.navigateScreen
import kr.blugon.tjfinder.ui.layout.state.CenterText
import kr.blugon.tjfinder.ui.theme.ThemeColor
import kr.blugon.tjfinder.utils.isApiServerOpened
import kr.blugon.tjfinder.utils.isInternetAvailable
import my.nanihadesuka.compose.LazyColumnScrollbar


private val playlists = mutableStateListOf<Playlist>()
private lateinit var listState: LazyListState

private var sort by mutableStateOf(PlaylistSortType.TITLE)
private val sortExpanded = mutableStateOf(false)
@Composable
fun PlaylistHome(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    if(!(::listState.isInitialized)) listState = rememberLazyListState()

    var user by remember { mutableStateOf<User?>(null) }
    var state by remember { mutableStateOf(State.SUCCESS) }
    var isApiServerOpened by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        if(!isInternetAvailable(context)) {
            state = State.NOT_INTERNET_AVAILABLE
            return@LaunchedEffect
        }
        isApiServerOpened = isApiServerOpened()
        if(!isApiServerOpened) return@LaunchedEffect
        user = User.login(context)?: return@LaunchedEffect
        if (playlists.isEmpty()) {
            state = State.LOADING
            CoroutineScope(IO).launch {
                val data = user!!.searchPlaylist("", context)
                if(data.isEmpty()) {
                    state = State.FAIL
                    return@launch
                }
                playlists.addAll(data)
                state = State.SUCCESS
            }
        }
    }

    if(!isApiServerOpened) return CenterText(text = "서버 연결에 실패했습니다")
    if(user == null) return CenterText(text = "로그인 후 이용 가능합니다")

    val reload: () -> Unit = {
        if(!isInternetAvailable(context)) state = State.NOT_INTERNET_AVAILABLE
        else {
            coroutineScope.launch {
                state = State.LOADING
                val results = user!!.searchPlaylist("", context)
                if(results.isEmpty()) {
                    state = State.FAIL
                    return@launch
                }
                playlists.clear()
                playlists.addAll(results)
                when(sort) {
                    PlaylistSortType.SONG_COUNT -> playlists.sortBy { it.songIdList.size }
                    PlaylistSortType.TITLE -> playlists.sortBy { it.title }
                    PlaylistSortType.ID -> playlists.sortBy { it.id }
                    PlaylistSortType.CREATOR -> playlists.sortBy { it.creator }
                }
                state = State.SUCCESS
            }
        }
    }
    Column {
        SortableTopBar(
            title = "추천 플레이리스트",
            iconId = R.drawable.star,
            isExpanded = sortExpanded,
            items = ArrayList<PlaylistSortType>().apply {
                PlaylistSortType.entries.forEach { this.add(it) }
            }
        ) { //아이템
            if(playlists.isEmpty()) return@SortableTopBar
            sort = it
            sortExpanded.value = false
            when(it) {
                PlaylistSortType.SONG_COUNT -> playlists.sortBy { it.songIdList.size }
                PlaylistSortType.TITLE -> playlists.sortBy { it.title }
                PlaylistSortType.ID -> playlists.sortBy { it.id }
                PlaylistSortType.CREATOR -> playlists.sortBy { it.creator }
            }
        }

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
                    enableScroll = 6 <= playlists.size
                    items(playlists.size) {
                        val playlist = playlists[it]
                        PlaylistCard(playlist = playlist, reload = reload, it == 0, isLast = it == playlists.lastIndex, navController = navController)
                    }
                }
            }
        }
    }
}