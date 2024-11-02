package kr.blugon.tjfinder.ui.screen

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.*
import kr.blugon.tjfinder.R
import kr.blugon.tjfinder.module.*
import kr.blugon.tjfinder.module.BlugonTJApi.getPlaylist
import kr.blugon.tjfinder.module.BlugonTJApi.memoMap
import kr.blugon.tjfinder.module.BlugonTJApi.searchPlaylist
import kr.blugon.tjfinder.module.database.SongCacheDB
import kr.blugon.tjfinder.module.database.SongManager
import kr.blugon.tjfinder.ui.layout.card.songcard.SearchSongCard
import kr.blugon.tjfinder.ui.layout.card.playlist.PlaylistCard
import kr.blugon.tjfinder.ui.theme.Pretendard
import kr.blugon.tjfinder.ui.theme.ThemeColor
import my.nanihadesuka.compose.LazyColumnScrollbar
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set
import kotlin.concurrent.thread


var searchFocus = false

enum class SearchCategory(val koName: String) {
    SONG("곡"),
    PLAYLIST("플레이리스트"),
    USER("유저")
}

enum class SearchState {
    SUCCESS,
    NO_RESULT,
    SEARCHING,
    NOT_INTERNET_AVAILABLE
}
data class SearchInfo(
    val searchValue: String = "",
    val searchCategory: SearchCategory = SearchCategory.SONG,
    val searchState: SearchState = SearchState.NO_RESULT,
    val songs: List<Song> = listOf(),
    val playlists: List<Playlist> = listOf(),
    val sort: SortType = SortType.SINGER,
    val playlistSort: PlaylistSortType = PlaylistSortType.TITLE
)

var initValue = SearchInfo()

private var searchValue by mutableStateOf(initValue.searchValue)
private var searchCategory by mutableStateOf(initValue.searchCategory)
private var searchState by mutableStateOf(initValue.searchState)
private val searchValues = object {
    val songs = mutableStateListOf<Song>()
    val playlists = mutableStateListOf<Playlist>()
}
private var sort by mutableStateOf(SortType.SINGER)
private var playlistSort by mutableStateOf(PlaylistSortType.TITLE)

private val searchBackStack = mutableStateListOf<SearchInfo>()
fun saveBackStack() {
    searchBackStack.add(SearchInfo(
        searchValue,
        searchCategory,
        searchState,
        ArrayList<Song>().apply { addAll(searchValues.songs) },
        ArrayList<Playlist>().apply { addAll(searchValues.playlists) },
        sort,
        playlistSort
    ))
}
private lateinit var listState: LazyListState

@Composable
fun Search(navController: NavHostController) {
    val focusRequester = FocusRequester()
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    fun clearSearchValues() {
        searchValues.songs.clear()
        searchValues.playlists.clear()
    }
    fun reset() {
        searchValue = initValue.searchValue
        searchCategory = initValue.searchCategory
        searchState = initValue.searchState
        clearSearchValues()
    }
    fun isChanged(): Boolean = (
        searchValue != initValue.searchValue ||
        searchCategory != initValue.searchCategory ||
        searchState != initValue.searchState ||
        !searchValues.songs.isEmpty() ||
        !searchValues.songs.isEmpty()
    )

    val context = LocalContext.current
    if(!(::listState.isInitialized)) listState = rememberLazyListState()

    BackHandler(enabled = isChanged()) {
        if(!isChanged()) return@BackHandler
        searchBackStack.removeLastOrNull()
        val history = searchBackStack.lastOrNull()?: return@BackHandler reset()
        searchValue = history.searchValue
        searchCategory = history.searchCategory
        searchState = history.searchState
        searchValues.songs.clear()
        searchValues.playlists.clear()
        searchValues.songs.addAll(history.songs)
        searchValues.playlists.addAll(history.playlists)
    }


    var user by remember { mutableStateOf<User?>(null) }
    LaunchedEffect(Unit) {
        if(searchFocus) {
            focusRequester.requestFocus()
            searchFocus = false
        }
        if(!isInternetAvailable(context)) {
            searchState = SearchState.NOT_INTERNET_AVAILABLE
            return@LaunchedEffect
        }
        user = User.login(context)?: return@LaunchedEffect
        if(initValue.searchValue != "") {
            searchState = SearchState.SEARCHING
            searchValue = initValue.searchValue
            searchCategory = initValue.searchCategory
            if(searchValue.isBlank()) return@LaunchedEffect
            thread {
                GlobalScope.launch {
                    val searchResponse = search(initValue.searchValue, user, context, true)
                    searchValues.songs.addAll(searchResponse)
                    searchState = SearchState.SUCCESS
                    saveBackStack()
                    initValue = SearchInfo()
                }
            }
        }
    }

    var sortExpanded by remember { mutableStateOf(false) }

    var isFocusedSearchBar by remember { mutableStateOf(false) }
    if(!isFocusedSearchBar) {
        focusManager.clearFocus()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row( //애들 양쪽으로 밀기
            modifier = Modifier
                .fillMaxWidth(0.875f)
                .padding(0.dp, 14.5.dp, 0.dp, 0.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) { //Row
            Column(
                modifier = (
                        if (searchValue.isEmpty()) Modifier.defaultMinSize(0.dp, 45.dp)
                        else Modifier.height(45.dp)
                    )
                    .fillMaxWidth(0.875f)
                    .padding(0.dp, 0.dp, 10.dp, 0.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ThemeColor.ItemBackground),
                verticalArrangement = Arrangement.Center,
            ) { //Column
                Row(verticalAlignment = Alignment.CenterVertically) { // 검색창
                    Icon( //검색 아이콘(돋보기)
                        modifier = Modifier
                            .size(34.dp)
                            .padding(10.dp, 0.dp, 0.dp, 0.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.search),
                        contentDescription = "search",
                        tint = Color.White,
                    )
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
                        value = searchValue,
                        onValueChange = {
                            if(100 < it.length) {
                                Toast.makeText(context, "입력 가능한 최대 길이는 100자입니다", Toast.LENGTH_SHORT).show()
                                return@BasicTextField
                            }
                            searchValue = it
                            clearSearchValues()
                            if(it.isBlank()) return@BasicTextField
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(onSearch = { //검색 버튼 눌렀을때
                            clearSearchValues()
                            if(searchValue.isBlank()) return@KeyboardActions
                            focusManager.clearFocus()
                            if (!isInternetAvailable(context)) return@KeyboardActions
                            searchState = SearchState.SEARCHING
                            thread {
                                GlobalScope.launch {
                                    val searchSongResponse = search(searchValue, user, context)
                                    val searchPlaylistResponse = user!!.searchPlaylist(searchValue, context)
                                    val splitSharp = searchValue.split("#")
                                    val searchWithPlaylistId =
                                        if (!searchValue.startsWith("#")) user!!.getPlaylist(searchValue, context)
                                        else user!!.getPlaylist(searchValue.substring(1), context)
                                    val searchWithSplitPlaylistId = user!!.getPlaylist(splitSharp.last(), context)
                                    clearSearchValues()
                                    searchValues.songs.addAll(searchSongResponse)
                                    if (searchWithSplitPlaylistId != null) {
                                        if (searchWithSplitPlaylistId.title == splitSharp.subList(0, splitSharp.size - 1)
                                                .joinToString("")
                                        ) {
                                            searchValues.playlists.add(searchWithSplitPlaylistId)
                                        }
                                    }
                                    if (searchWithPlaylistId != null) searchValues.playlists.add(searchWithPlaylistId)
                                    searchValues.playlists.addAll(searchPlaylistResponse)
                                    searchState = when(searchCategory) {
                                        SearchCategory.SONG -> {
                                            if(searchValues.songs.isEmpty()) SearchState.NO_RESULT
                                            else SearchState.SUCCESS
                                        }
                                        SearchCategory.PLAYLIST -> {
                                            if(searchValues.playlists.isEmpty()) SearchState.NO_RESULT
                                            else SearchState.SUCCESS
                                        }
                                        SearchCategory.USER -> SearchState.NO_RESULT
                                    }
                                    saveBackStack()
                                }
                            }
                        })
                    ) { //PlaceHolder
                        if (searchValue.isBlank()) { //아무것도 입력 안했을 때
                            Text(
                                text = "가수 또는 제목으로 검색",
                                color = ThemeColor.LightGray,
                                fontFamily = Pretendard,
                                fontWeight = FontWeight.Medium,
                                fontSize = TextUnit(18f, TextUnitType.Sp),
                            )
                        }
                        it()
                    }
                    if(searchValue.isNotEmpty()) { //입력했을때
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
                                searchValue = ""
                                clearSearchValues()
                                searchState = SearchState.NO_RESULT
                                focusRequester.requestFocus()
                            },
                        )
                    }
                }
            }

            TextButton( //정렬 선택
                modifier = Modifier
                    .size(45.dp)
                    .padding(0.dp),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(12.dp),
                onClick = {
                    sortExpanded = true
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor =
                    if(sortExpanded) ThemeColor.Gray
                    else ThemeColor.ItemBackground
                ),
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        modifier = Modifier
                            .size(24.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.sort),
                        contentDescription = "sort",
                        tint = Color.White
                    )
                    MaterialTheme(
                        shapes = MaterialTheme.shapes.copy(
                            extraSmall = RoundedCornerShape(12.dp),
                        )
                    ) {
                        DropdownMenu(
                            modifier = Modifier
                                .width(140.dp)
                                .background(ThemeColor.Gray),
                            expanded = sortExpanded,
                            onDismissRequest = {
                                sortExpanded = false
                            },
                            offset = DpOffset((-96).dp, 10.dp)
                        ) {
                            when(searchCategory) {
                                SearchCategory.SONG -> {
                                    SortType.entries.forEach { sortTypeEach ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    text = sortTypeEach.visibleName,
                                                    color = Color.White,
                                                    fontFamily = Pretendard,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = TextUnit(15f, TextUnitType.Sp),
                                                    textAlign = TextAlign.Center
                                                )
                                            },
                                            onClick = {
                                                sort = sortTypeEach
                                                sortExpanded = false
                                                when(sort) {
                                                    SortType.ID -> searchValues.songs.sortBy { it.id }
                                                    SortType.TITLE -> searchValues.songs.sortBy {
                                                        if(it.title.lowercase() == searchValue.lowercase()) "!1"
                                                        else if(it.title.lowercase().startsWith(searchValue.lowercase())) "!2"
                                                        else if(it.title.lowercase().contains(searchValue.lowercase())) "!3"
                                                        else it.title.lowercase()
                                                    }
                                                    SortType.SINGER -> searchValues.songs.sortBy {
                                                        if(it.singer.lowercase() == searchValue.lowercase()) "!1"
                                                        else if(it.singer.lowercase().startsWith(searchValue.lowercase())) "!2"
                                                        else if(it.singer.lowercase().contains(searchValue.lowercase())) "!3"
                                                        else it.singer.lowercase()
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }
                                SearchCategory.PLAYLIST -> {
                                    PlaylistSortType.entries.forEach { sorTypeEach -> //아이템
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    text = sorTypeEach.visibleName,
                                                    color = Color.White,
                                                    fontFamily = Pretendard,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = TextUnit(15f, TextUnitType.Sp),
                                                    textAlign = TextAlign.Center
                                                )
                                            },
                                            onClick = {
                                                if(searchValues.playlists.isEmpty()) return@DropdownMenuItem
                                                playlistSort = sorTypeEach
                                                sortExpanded = false
                                                val new = when(playlistSort) {
                                                    PlaylistSortType.SONG_COUNT -> searchValues.playlists.sortedBy { it.songIdList.size }
                                                    PlaylistSortType.TITLE -> searchValues.playlists.sortedBy { it.title }
                                                    PlaylistSortType.ID -> searchValues.playlists.sortedBy { it.id }
                                                    PlaylistSortType.CREATOR -> searchValues.playlists.sortedBy { it.creator }
                                                }
                                                searchValues.playlists.clear()
                                                coroutineScope.launch {
                                                    delay(10)
                                                    searchValues.playlists.addAll(new)
                                                }
                                            }
                                        )
                                    }
                                }
                                SearchCategory.USER -> {}
                            }
                        }
                    }
                }
            }
        }


        Row(
            modifier = Modifier
                .fillMaxWidth(0.875f)
                .padding(vertical = 17.5.dp)
        ) { //검색유형
            SearchCategory.entries.forEach { category->
                Box(
                    modifier = Modifier
                        .widthIn(min = 70.dp)
                        .padding(horizontal = 5.dp)
                        .clip(CircleShape)
                        .background(
                            if (category == searchCategory) ThemeColor.Main
                            else ThemeColor.ItemBackground
                        )
                        .clickable {
                            searchCategory = category
                            when (category) {
                                SearchCategory.SONG -> {
                                    searchState = if (searchValues.songs.isEmpty()) SearchState.NO_RESULT
                                    else SearchState.SUCCESS
                                }

                                SearchCategory.PLAYLIST -> {
                                    searchState = if (searchValues.playlists.isEmpty()) SearchState.NO_RESULT
                                    else SearchState.SUCCESS
                                }

                                SearchCategory.USER -> {
//                                    searchState = SearchState.NO_RESULT
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier.padding(7.dp),
                        text = category.koName,
                        fontFamily = Pretendard,
                        fontSize = TextUnit(17f, TextUnitType.Sp),
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }

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
                state = listState,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if(searchCategory == SearchCategory.USER) {
                    items(1) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillParentMaxHeight(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 5.dp),
                                text = "지원 예정",
                                fontFamily = Pretendard,
                                fontSize = TextUnit(if(searchState == SearchState.NOT_INTERNET_AVAILABLE) 20f else 30f, TextUnitType.Sp),
                                fontWeight = FontWeight.Normal,
                                color = ThemeColor.Main
                            )
//                            if(searchState == SearchState.SEARCHING) {
//                                CircularProgressIndicator(
//                                    modifier = Modifier.width(48.dp),
//                                    color = ThemeColor.Main,
//                                    trackColor = ThemeColor.MainGray,
//                                )
//                            }
                        }
                    }
                    return@LazyColumn
                }
                if(searchState != SearchState.SUCCESS) {
                    enableScroll = false
                    items(1) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillParentMaxHeight(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 5.dp),
                                text = when(searchState) {
                                    SearchState.NO_RESULT -> "검색 결과 없음"
                                    SearchState.SUCCESS -> "성공" //나올 일 없음
                                    SearchState.SEARCHING -> "검색중..."
                                    SearchState.NOT_INTERNET_AVAILABLE -> "네트워크에 연결되지 않았어요:("
                                },
                                fontFamily = Pretendard,
                                fontSize = TextUnit(if(searchState == SearchState.NOT_INTERNET_AVAILABLE) 20f else 30f, TextUnitType.Sp),
                                fontWeight = FontWeight.Normal,
                                color = ThemeColor.Main
                            )
                            if(searchState == SearchState.SEARCHING) {
                                CircularProgressIndicator(
                                    modifier = Modifier.width(48.dp),
                                    color = ThemeColor.Main,
                                    trackColor = ThemeColor.MainGray,
                                )
                            }
                        }
                    }
                    return@LazyColumn
                }
                when(searchCategory) {
                    SearchCategory.SONG -> {
                        enableScroll = 5 <= searchValues.songs.size
                        items(searchValues.songs.size) { songNum ->
                            val song = searchValues.songs[songNum]
                            SearchSongCard(song = song, songNum == 0, songNum == searchValues.songs.size-1, navController, searchValue)
                        }
                    }
                    SearchCategory.PLAYLIST -> {
                        enableScroll = 6 <= searchValues.playlists.size
                        items(searchValues.playlists.size) { i ->
                            val playlist = searchValues.playlists[i]
                            PlaylistCard(playlist = playlist, reload = {}, isFirst = i == 0, navController = navController, true)
                        }
                    }
                    SearchCategory.USER -> {}
                }
            }
        }
    }
}

suspend fun search(searchValue: String, user: User?, context: Context, match: Boolean = false, searchInTitle: Boolean = true, searchInSinger: Boolean = true): List<Song> {
    val idData = if(searchValue.toIntOrNull() != null) {
        SongManager.searchWithId(searchValue.toInt(), SongCacheDB(context), true)
    } else listOf()
    val titleData = if(searchInTitle) {
        SongManager.searchWithTitle(searchValue, SongCacheDB(context), match)
    } else listOf()
    val singerData = if(searchInSinger) {
        SongManager.searchWithSinger(searchValue, SongCacheDB(context), match)
    } else listOf()

    val data = ArrayList<Song>().apply {
        addAll(idData)
        addAll(titleData)
        addAll(singerData)
    }

    val a = ArrayList<Song>()
    a.addAll( //메모 검색
        arrayListOf<Song>().apply { //MemoList
            val memoMap = HashMap<Song, String>()
            val jobs = HashMap<Deferred<Song?>, String>() // Async<Song>: Memo
            user!!.memoMap()?.forEach { (songId, memo) ->
                jobs[CoroutineScope(Dispatchers.Default).async {
                    SongManager[songId, SongCacheDB(context)]
                }] = memo
            }
            for (it in jobs) memoMap[it.key.await() ?: continue] = it.value
            memoMap.forEach { (song, memo) ->
                if (memo.contains(searchValue) && !memo.startsWith(searchValue) && memo != searchValue) this.add(
                    song
                ) //검색어가 포함되어있을때 밑으로
            }
            memoMap.forEach { (song, memo) ->
                if (memo.startsWith(searchValue) && memo != searchValue) this.add(0, song) //검색어로 시작할때 중간으로
            }
            memoMap.forEach { (song, memo) ->
                if (memo == searchValue) this.add(0, song) //검색어와 같을때 제일 위로
            }
        }
    )
    a.addAll(data)

    return a.distinctBy { it.id }
}