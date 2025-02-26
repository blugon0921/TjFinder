package kr.blugon.tjfinder.ui.screen.child.playlist

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.blugon.tjfinder.R
import kr.blugon.tjfinder.module.*
import kr.blugon.tjfinder.module.State
import kr.blugon.tjfinder.ui.layout.*
import kr.blugon.tjfinder.ui.layout.card.playlist.PlaylistThumbnail
import kr.blugon.tjfinder.ui.layout.card.playlist.PlaylistTitle
import kr.blugon.tjfinder.ui.layout.card.song.PlaylistSongCard
import kr.blugon.tjfinder.ui.screen.child.playlist.InPlaylist.playlist
import kr.blugon.tjfinder.ui.theme.Pretendard
import kr.blugon.tjfinder.ui.theme.ThemeColor
import kr.blugon.tjfinder.utils.api.TjFinderApi
import kr.blugon.tjfinder.utils.isInternetAvailable
import my.nanihadesuka.compose.LazyColumnScrollbar

object InPlaylist {
    lateinit var playlist: Playlist
}

val sortExpanded = mutableStateOf(false)
var sort by mutableStateOf(SongSortType.SINGER)
@Composable
fun InPlaylistScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var user by remember { mutableStateOf<User?>(null) }
    val songs = remember { mutableStateListOf<PlaylistSong>() }
    val visibleSongs = remember { mutableStateListOf<PlaylistSong>() }
    var state by remember { mutableStateOf(State.DEFAULT) }
    val playlist = remember { playlist }
    LaunchedEffect(Unit) {
        if(!isInternetAvailable(context)) {
            state = State.NOT_INTERNET_AVAILABLE
            return@LaunchedEffect
        }
        if(LoginManager.getSavedUid(context) != null) {
            user = TjFinderApi.login(LoginManager.getSavedUid(context)!!)
            state = State.LOADING
            playlist.loadSongList(context)
            coroutineScope.launch {
                repeat(500) {
                    delay(10)
                    if(playlist.songList == null) return@repeat
                    songs.addAll(playlist.songList!!)
                    visibleSongs.addAll(songs)
                    state = State.SUCCESS
                    return@launch
                }
                state = State.FAIL
            }
        }
    }


    //검색
    val focusRequester = FocusRequester()

    var isFocusedSearchBar by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    if(!isFocusedSearchBar) focusManager.clearFocus()
    var searchInput by remember { mutableStateOf("") }


    Column {
        SortableTopBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) { // 검색창
                    BasicTextField( //검색 Input
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .focusRequester(focusRequester)
                            .padding(10.dp)
                            .onFocusEvent {
                                isFocusedSearchBar = it.isFocused
                            },
                        singleLine = true,
                        cursorBrush = SolidColor(ThemeColor.LightGray),
                        textStyle = TextStyle(
                            color = Color.White,
                            fontFamily = Pretendard,
                            fontWeight = FontWeight.Medium,
                            fontSize = TextUnit(18f, TextUnitType.Sp)
                        ),
                        value = searchInput,
                        onValueChange = {
                            if(100 < it.length) {
                                Toast.makeText(context, "입력 가능한 최대 길이는 100자입니다", Toast.LENGTH_SHORT).show()
                                return@BasicTextField
                            }
                            searchInput = it
                            if(it.isBlank()) {
                                visibleSongs.clear()
                                visibleSongs.addAll(songs)
                                return@BasicTextField
                            }
                            visibleSongs.sortBy {
                                val number = if(
                                    it.title.trim().lowercase() == searchInput.lowercase().trim() ||
                                    it.singer.trim().lowercase() == searchInput.lowercase().trim() ||
                                    it.memo?.trim()?.lowercase() == searchInput.lowercase().trim()
                                ) 0
                                else if(
                                    it.title.trim().lowercase().startsWith(searchInput.lowercase().trim()) ||
                                    it.singer.trim().lowercase().startsWith(searchInput.lowercase().trim()) ||
                                    it.memo?.trim()?.lowercase()?.startsWith(searchInput.lowercase().trim()) == true
                                ) 1
                                else 2
                                "!${number}${it.title}${it.singer}${it.memo}"
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(onSearch = { //검색 버튼 눌렀을때
                            focusManager.clearFocus()
                            searchInput = searchInput.trim()
                        })
                    ) { //PlaceHolder
                        if (searchInput == "") { //아무것도 입력 안했을 때
                            Text(
                                text = "플레이리스트 곡 검색",
                                color = ThemeColor.LightGray,
                                fontFamily = Pretendard,
                                fontWeight = FontWeight.Medium,
                                fontSize = TextUnit(18f, TextUnitType.Sp),
                            )
                        }
                        it()
                    }
                    if(searchInput.isNotEmpty()) { //입력했을때
                        TextButton( //X버튼
                            modifier = Modifier
                                .animateContentSize()
                                .size(17.5.dp)
                                .padding(0.dp),
                            contentPadding = PaddingValues(0.dp),
                            shape = RoundedCornerShape(0.dp),
                            content = {
                                Icon(
                                    modifier = Modifier
                                        .animateContentSize()
                                        .fillMaxSize(),
                                    imageVector = ImageVector.vectorResource(R.drawable.cancel),
                                    contentDescription = "cancel",
                                    tint = ThemeColor.LightGray
                                )
                            },
                            onClick = {
                                searchInput = ""
                                visibleSongs.clear()
                                visibleSongs.addAll(songs)
//                                focusRequester.requestFocus()
                            },
                        )
                    }
                }
            },
            icon = {
                Icon( //검색 아이콘(돋보기)
                    modifier = Modifier
                        .size(34.dp)
                        .padding(10.dp, 0.dp, 0.dp, 0.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.search),
                    contentDescription = "search",
                    tint = Color.White,
                )
            },
            isExpanded = sortExpanded,
            items = ArrayList<SortType>().apply {
                SongSortType.entries.forEach { this.add(it) }
            }
        ) {
            sort = it as SongSortType
            sortExpanded.value = false
            when(sort) {
                SongSortType.ID -> visibleSongs.sortBy { it.id }
                SongSortType.TITLE -> visibleSongs.sortBy { it.title }
                SongSortType.SINGER -> visibleSongs.sortBy { it.singer }
            }
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PlaylistInfoCard(playlist)
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
                LoadingStateScreen(state, fail = {enableScroll = false}) {
                    if (songs.isEmpty()) {
                        enableScroll = false
                        items(1) {
                            Column (
                                modifier = Modifier
                                    .fillMaxSize()
                                    .fillParentMaxHeight(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 5.dp),
                                    text = "해당 플레이리스트는 비어있습니다.",
                                    fontFamily = Pretendard,
                                    fontSize = TextUnit(20f, TextUnitType.Sp),
                                    fontWeight = FontWeight.Medium,
                                    color = ThemeColor.Main
                                )
                            }
                        }
                        return@LoadingStateScreen
                    }
                    enableScroll = 5 <= visibleSongs.size
                    items(visibleSongs.size) { i ->
                        val song = visibleSongs[i]
                        PlaylistSongCard(song = song, i == 0, i == visibleSongs.size-1, navController)
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaylistInfoCard(playlist: Playlist) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.875f)
            .padding(0.dp, 8.75.dp, 0.dp, 0.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(ThemeColor.ItemBackground)
    ) { //위 왼쪽
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Absolute.Left
        ) {
            PlaylistThumbnail(playlist = playlist, size = 48.dp) //썸네일
            Column(
                modifier = Modifier.padding(start = 10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                PlaylistTitle( //플레이리스트 이름
                    playlist = playlist,
                    fontSize = 15f,
                    withId = true,
                    maxLines = 2
                )
                DoubleText(
                    first = { //플레이리스트 만든이
                        it.text = playlist.creator
                        it.style = PretendardSpanStyle(fontSize = 13f)
                    },
                    second = { //플레이리스트 만든이 tag
                        it.text = "#${playlist.creatorTag}"
                        it.style = PretendardSpanStyle(
                            color = Color.Gray,
                            fontSize = 8f
                        )
                    },
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}