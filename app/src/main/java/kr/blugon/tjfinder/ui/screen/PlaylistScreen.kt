package kr.blugon.tjfinder.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import kr.blugon.tjfinder.R
import kr.blugon.tjfinder.module.*
import kr.blugon.tjfinder.module.State
import kr.blugon.tjfinder.ui.layout.LoadingStateScreen
import kr.blugon.tjfinder.ui.layout.SortableTopBar
import kr.blugon.tjfinder.ui.layout.card.playlist.PlaylistCard
import kr.blugon.tjfinder.ui.layout.navigation.ChildScreen
import kr.blugon.tjfinder.ui.layout.navigation.navigateScreen
import kr.blugon.tjfinder.ui.layout.state.CenterText
import kr.blugon.tjfinder.ui.theme.Pretendard
import kr.blugon.tjfinder.ui.theme.ThemeColor
import kr.blugon.tjfinder.utils.api.TjFinderApi
import kr.blugon.tjfinder.utils.api.finder.libraries
import kr.blugon.tjfinder.utils.api.finder.playlists
import kr.blugon.tjfinder.utils.isApiServerOpened
import kr.blugon.tjfinder.utils.isInternetAvailable
import my.nanihadesuka.compose.LazyColumnScrollbar
import org.json.JSONArray
import org.json.JSONObject


class PlaylistScreenViewModel: ViewModel() {
    var listState: LazyListState? = null
    val sortExpanded = mutableStateOf(false)
    var sort by mutableStateOf(PlaylistSortType.TITLE)

    val playlists = mutableStateListOf<Playlist>()
    val libraries = mutableStateListOf<Playlist>()

    var user by mutableStateOf<User?>(null)

    var state by mutableStateOf(State.DEFAULT)

    @Composable
    fun init() {
        if(listState != null) return
        listState = rememberLazyListState()
    }
}
private val states = PlaylistScreenViewModel()
@Composable
fun PlaylistScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    states.init()

//    var user by remember { mutableStateOf<User?>(null) }
//    var state by remember { mutableStateOf(State.DEFAULT) }

    val reload: () -> Unit = {
        if(!isInternetAvailable(context)) states.state = State.NOT_INTERNET_AVAILABLE
        else {
            coroutineScope.launch {
                val playlists = states.user!!.playlists(context)
                if(playlists == null) {
                    states.state = State.FAIL
                    return@launch
                }
                var isPlaylistSameBefore = true
                val beforePlaylist = JSONArray().apply {
                    states.playlists.sortedBy { it.id }.forEach {
                        it.songList = null
                        put(JSONObject(it.toString()))
                    }
                }.toString()
                val afterPlaylist = JSONArray().apply {
                    playlists.sortedBy { it.id }.forEach {
                        it.songList = null
                        put(JSONObject(it.toString()))
                    }
                }.toString()
                if(beforePlaylist != afterPlaylist) { //전이랑 같지 않으면 reload
                    states.playlists.clear()
                    states.playlists.addAll(playlists)
                    isPlaylistSameBefore = false
                }
                val libraries = states.user!!.libraries(context)
                if(libraries == null) {
                    states.state = State.FAIL
                    return@launch
                }
                var isLibrarySameBefore = true
                val beforeLibrary = JSONArray().apply {
                    states.libraries.sortedBy { it.id }.forEach {
                        put(JSONObject(it.toString()))
                    }
                }.toString()
                val afterLibrary = JSONArray().apply {
                    libraries.sortedBy { it.id }.forEach {
                        put(JSONObject(it.toString()))
                    }
                }.toString()
                if(beforeLibrary != afterLibrary) { //전이랑 같지 않으면 reload
                    states.libraries.clear()
                    states.libraries.addAll(libraries)
                    isLibrarySameBefore = false
                }
                if(isPlaylistSameBefore && isLibrarySameBefore) {
                    states.state = State.SUCCESS
                    return@launch
                }
                when(states.sort) {
                    PlaylistSortType.SONG_COUNT -> states.playlists.sortBy { it.songIdList.size }
                    PlaylistSortType.TITLE -> states.playlists.sortBy { it.title }
                    PlaylistSortType.ID -> states.playlists.sortBy { it.id }
                    PlaylistSortType.CREATOR -> states.playlists.sortBy { it.owner.name }
                }
                when(states.sort) {
                    PlaylistSortType.SONG_COUNT -> states.libraries.sortBy { it.songIdList.size }
                    PlaylistSortType.TITLE -> states.libraries.sortBy { it.title }
                    PlaylistSortType.ID -> states.libraries.sortBy { it.id }
                    PlaylistSortType.CREATOR -> states.libraries.sortBy { it.owner.name }
                }
                states.state = State.SUCCESS
            }
        }
    }

    var isApiServerOpened by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        if(!isInternetAvailable(context)) {
            states.state = State.NOT_INTERNET_AVAILABLE
            return@LaunchedEffect
        }
        isApiServerOpened = isApiServerOpened()
        if(!isApiServerOpened) return@LaunchedEffect
        states.user = TjFinderApi.User.login(LoginManager.getSavedUid(context)?: return@LaunchedEffect)?: return@LaunchedEffect
        if(states.playlists.isEmpty() && states.libraries.isEmpty()) {
            states.state = State.LOADING
        }
        reload()
    }

    Column {
        SortableTopBar(
            title = "플레이리스트",
            iconId = R.drawable.playlist,
            isExpanded = states.sortExpanded,
            items = ArrayList<PlaylistSortType>().apply {
                PlaylistSortType.entries.forEach { this.add(it) }
            }
        ) { //아이템
            if(states.playlists.isEmpty()) return@SortableTopBar
            states.sort = it
            states.sortExpanded.value = false
            when(it) {
                PlaylistSortType.SONG_COUNT -> states.playlists.sortBy { it.songIdList.size }
                PlaylistSortType.TITLE -> states.playlists.sortBy { it.title }
                PlaylistSortType.ID -> states.playlists.sortBy { it.id }
                PlaylistSortType.CREATOR -> states.playlists.sortBy { it.owner.name }
            }
            when(it) {
                PlaylistSortType.SONG_COUNT -> states.libraries.sortBy { it.songIdList.size }
                PlaylistSortType.TITLE -> states.libraries.sortBy { it.title }
                PlaylistSortType.ID -> states.libraries.sortBy { it.id }
                PlaylistSortType.CREATOR -> states.libraries.sortBy { it.owner.name }
            }
        }

        if(!isApiServerOpened) return CenterText(text = "서버 연결에 실패했습니다")
        if(states.user == null) return CenterText(text = "로그인 후 이용 가능합니다")

        var enableScroll by remember { mutableStateOf(false) }
        LazyColumnScrollbar(
            modifier = Modifier.fillMaxSize(),
            listState = states.listState!!,
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
                state = states.listState!!
            ) {
                LoadingStateScreen(states.state, fail = {enableScroll = false}, loadingMessage = "플레이리스트 로딩중") {
                    enableScroll = 6 <= states.playlists.size+states.libraries.size
                    items(states.playlists.size) { i ->
                        val playlist = states.playlists[i]
                        PlaylistCard(playlist = playlist, reload = reload, i == 0, navController = navController)
                    }
                    items(states.libraries.size) { i ->
                        val playlist = states.libraries[i]
                        PlaylistCard(playlist = playlist, reload = reload, states.playlists.isEmpty() && i == 0, navController = navController)
                    }
                    items(1) {
                        Box( //플레이 리스트 추가
                            modifier = Modifier
                                .padding(vertical = if(!states.playlists.isEmpty()) 17.5.dp else 0.dp)
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