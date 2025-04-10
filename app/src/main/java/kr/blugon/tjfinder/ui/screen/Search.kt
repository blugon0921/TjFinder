@file:OptIn(DelicateCoroutinesApi::class)

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kr.blugon.tjfinder.R
import kr.blugon.tjfinder.module.*
import kr.blugon.tjfinder.module.database.SongCacheDB
import kr.blugon.tjfinder.module.database.SongManager
import kr.blugon.tjfinder.module.search.SearchCategory
import kr.blugon.tjfinder.module.search.SearchInfo
import kr.blugon.tjfinder.ui.layout.PretendardText
import kr.blugon.tjfinder.ui.layout.PretendardTextStyle
import kr.blugon.tjfinder.ui.layout.SortableTopBar
import kr.blugon.tjfinder.ui.layout.card.playlist.PlaylistCard
import kr.blugon.tjfinder.ui.layout.card.song.SearchSongCard
import kr.blugon.tjfinder.ui.layout.card.user.OtherUserCard
import kr.blugon.tjfinder.ui.layout.state.CenterText
import kr.blugon.tjfinder.ui.theme.Pretendard
import kr.blugon.tjfinder.ui.theme.ThemeColor
import kr.blugon.tjfinder.utils.api.TjFinderApi
import kr.blugon.tjfinder.utils.api.finder.getPlaylist
import kr.blugon.tjfinder.utils.api.finder.memoList
import kr.blugon.tjfinder.utils.api.finder.searchPlaylist
import kr.blugon.tjfinder.utils.isApiServerOpened
import kr.blugon.tjfinder.utils.isInternetAvailable
import my.nanihadesuka.compose.LazyColumnScrollbar
import okio.IOException


var searchFocus = false

var initValue = SearchInfo()

private var input by mutableStateOf(initValue.input)
private var category by mutableStateOf(initValue.category)
private val states by mutableStateOf(initValue.states.toStated())
private var results by mutableStateOf(initValue.results.toMutable())
private var sort by mutableStateOf(initValue.sort)
private var sortExpanded = mutableStateOf(false)

private val backStacks = mutableStateListOf<SearchInfo>()
fun saveBackStack() {
    backStacks.add(SearchInfo(
        input,
        category,
        states.toDefault(),
        results.toDefault(),
        sort
    ))
}
private lateinit var listState: LazyListState


fun reset() {
    input = initValue.input
    category = initValue.category
    states.setAll(initValue.states)
    results.clear()
    sort = initValue.sort
}
fun isChanged(): Boolean = (
    input != initValue.input ||
    category != initValue.category ||
    states.toDefault() != initValue.states ||
    results.isNotEmpty()
)

@Composable
fun Search(navController: NavController) {
    val focusRequester = FocusRequester()
    val focusManager = LocalFocusManager.current

    val context = LocalContext.current
    if(!(::listState.isInitialized)) listState = rememberLazyListState()

    BackHandler(enabled = isChanged()) {
        if(!isChanged()) return@BackHandler
        backStacks.removeLastOrNull()
        val history = backStacks.lastOrNull()?: return@BackHandler reset()
        input = history.input
        category = history.category
        states.setAll(history.states)
        results.clear()
        results.addAll(history.results)
    }

    var user by remember { mutableStateOf<User?>(null) }
    var isApiServerOpened by remember { mutableStateOf(true) }

    suspend fun searchSong(title: Boolean = true, singer: Boolean = true, match: Boolean = false) {
        states[SearchCategory.Song] = SearchState.SEARCHING
        val searchResponse = search(input, user, context, match, title, singer)
        if(searchResponse.isEmpty()) {
            states[SearchCategory.Song] = SearchState.NO_RESULT
            return
        }
        results.addAll(song = searchResponse)
        states[SearchCategory.Song] = SearchState.SUCCESS
    }
    suspend fun searchPlaylist() {
        if(!isApiServerOpened || user == null) return
        states[SearchCategory.Playlist] = SearchState.SEARCHING
        val playlists = ArrayList<Playlist>()
        playlists.addAll(user!!.searchPlaylist(input, context))
        val split = input.split("#")
        when(input.startsWith("#")) {
            true -> user!!.getPlaylist(input.substring(1), context)
            false -> user!!.getPlaylist(input, context)
        }?.also { playlists.add(it) }
        user!!.getPlaylist(split.last(), context)?.also {
            if(split.subList(0, split.lastIndex).joinToString("#")
                !=
                it.title
            ) return@also
            playlists.add(it)
        }
        if(playlists.isEmpty()) {
            states[SearchCategory.Playlist] = SearchState.NO_RESULT
            return
        }
        results.addAll(playlist = playlists)
        states[SearchCategory.Playlist] = SearchState.SUCCESS
    }
    suspend fun searchUser() {
        if(!isApiServerOpened || user == null) return
        states[SearchCategory.User] = SearchState.SEARCHING
        val users = ArrayList<OtherUser>()
        users.addAll(TjFinderApi.User.search(input))
        val split = input.split("#")
        if(split.size == 2) {
            val response = TjFinderApi.User.get(split.first(), split.last())
            if(response != null) users.add(response)
        }
        if(users.isEmpty()) {
            states[SearchCategory.User] = SearchState.NO_RESULT
            return
        }
        results.addAll(user = users)
        states[SearchCategory.User] = SearchState.SUCCESS
    }
    LaunchedEffect(Unit) {
        if(searchFocus) {
            focusRequester.requestFocus()
            searchFocus = false
        }
        if(!isInternetAvailable(context)) {
            states.setAll(SearchState.NOT_INTERNET_AVAILABLE)
            return@LaunchedEffect
        }
        isApiServerOpened = isApiServerOpened()
        if(!isApiServerOpened) return@LaunchedEffect
        user = User.login(context)?: return@LaunchedEffect
        if(initValue.input != "") {
            results.clear()
            input = initValue.input.trim()
            category = initValue.category
            if(input.isBlank()) return@LaunchedEffect
            CoroutineScope(IO).launch {
                val song = GlobalScope.launch { searchSong(title=false, singer=true, match=true) }
                val playlist = GlobalScope.launch { searchPlaylist() }
                val users = GlobalScope.launch { searchUser() }
                users.join()
                playlist.join()
                song.join()
                saveBackStack()
                initValue = SearchInfo()
            }
        }
    }

    var isFocusedSearchBar by remember { mutableStateOf(false) }
    if(!isFocusedSearchBar) focusManager.clearFocus()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                        textStyle = PretendardTextStyle(fontSize = 18f),
                        value = input,
                        onValueChange = {
                            if(100 < it.length) {
                                Toast.makeText(context, "입력 가능한 최대 길이는 100자입니다", Toast.LENGTH_SHORT).show()
                                return@BasicTextField
                            }
                            input = it
                            results.clear()
                            if(it.isBlank()) return@BasicTextField
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(onSearch = { //검색 버튼 눌렀을때
                            results.clear()
                            focusManager.clearFocus()
                            if(input.isBlank()) return@KeyboardActions
                            if (!isInternetAvailable(context)) return@KeyboardActions
                            input = input.trim()
                            CoroutineScope(IO).launch {
                                val song = GlobalScope.launch { searchSong() }
                                val playlist = GlobalScope.launch { searchPlaylist() }
                                val users = GlobalScope.launch { searchUser() }
                                users.join()
                                playlist.join()
                                song.join()
                                saveBackStack()
                            }
                        })
                    ) { //PlaceHolder
                        if (input == "") { //아무것도 입력 안했을 때
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
                    if(input.isNotEmpty()) { //입력했을때
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
                                input = ""
                                results.clear()
                                states.setAll(SearchState.NO_RESULT)
                                focusRequester.requestFocus()
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
                when(category) {
                    SearchCategory.Song -> {
                        addAll(SongSortType.entries)
                    }
                    SearchCategory.Playlist -> {
                        addAll(PlaylistSortType.entries)
                    }
                    SearchCategory.User -> {}
                }
            }
        ) {
            when(category) {
                SearchCategory.Song -> {
                    if(results.song.isEmpty()) return@SortableTopBar
                    sort.song = it as SongSortType
                    sortExpanded.value = false
                    when(sort.song) {
                        SongSortType.ID -> results.song.sortBy { it.id }
                        SongSortType.TITLE -> results.song.sortBy {
                            if(it.title.lowercase() == input.lowercase()) "!1${it.title}"
                            else if(it.title.lowercase().startsWith(input.lowercase())) "!2${it.title}"
                            else if(it.title.lowercase().contains(input.lowercase())) "!3${it.title}"
                            else "!4${it.title}"
                        }
                        SongSortType.SINGER -> results.song.sortBy {
                            if(it.singer.lowercase() == input.lowercase()) "!1${it.singer}"
                            else if(it.singer.lowercase().startsWith(input.lowercase())) "!2${it.singer}"
                            else if(it.singer.lowercase().contains(input.lowercase())) "!3${it.singer}"
                            else "!4${it.singer}"
                        }
                    }
                }
                SearchCategory.Playlist -> {
                    if(results.playlist.isEmpty()) return@SortableTopBar
                    sort.playlist = it as PlaylistSortType
                    sortExpanded.value = false
                    when(sort.playlist) {
                        PlaylistSortType.SONG_COUNT -> results.playlist.sortBy { it.songIdList.size }
                        PlaylistSortType.TITLE -> results.playlist.sortBy { it.title }
                        PlaylistSortType.ID -> results.playlist.sortBy { it.id }
                        PlaylistSortType.CREATOR -> results.playlist.sortBy { it.owner.name }
                    }
                }
                SearchCategory.User -> {}
            }
        }


        Row(
            modifier = Modifier
                .fillMaxWidth(0.875f)
                .padding(vertical = 17.5.dp)
        ) { //검색유형
            SearchCategory.entries.forEach { searchCategory->
                Box(
                    modifier = Modifier
                        .widthIn(min = 70.dp)
                        .padding(horizontal = 5.dp)
                        .clip(CircleShape)
                        .background(
                            if (searchCategory == category) ThemeColor.Main
                            else ThemeColor.ItemBackground
                        ).clickable { category = searchCategory },
                    contentAlignment = Alignment.Center
                ) {
                    PretendardText(
                        modifier = Modifier.padding(7.dp),
                        text = searchCategory.displayName,
                        fontSize = 17f,
                        fontWeight = FontWeight.SemiBold
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
                if(states[category] != SearchState.SUCCESS) {
                    enableScroll = false
                    items(1) {
                        if(category != SearchCategory.Song) {
                            if(!isApiServerOpened) return@items CenterText(text = "서버 연결에 실패했습니다")
                            if(user == null) return@items CenterText(text = "로그인 후 이용 가능합니다")
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillParentMaxHeight(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            PretendardText(
                                modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 5.dp),
                                text = when(states[category]) {
                                    SearchState.NO_RESULT -> "검색 결과 없음"
                                    SearchState.SUCCESS -> "성공" //나올 일 없음
                                    SearchState.SEARCHING -> "검색중..."
                                    SearchState.NOT_INTERNET_AVAILABLE -> "네트워크에 연결되지 않았어요:("
                                },
                                fontSize = if(states[category] == SearchState.NOT_INTERNET_AVAILABLE) 20f else 30f,
                                fontWeight = FontWeight.Normal,
                                color = ThemeColor.Main
                            )
                            if(states[category] == SearchState.SEARCHING) {
                                CircularProgressIndicator(
                                    modifier = Modifier.width(48.dp),
                                    color = ThemeColor.Main,
                                    trackColor = ThemeColor.DarkMain,
                                )
                            }
                        }
                    }
                    return@LazyColumn
                }
                when(category) {
                    SearchCategory.Song -> {
                        enableScroll = 5 <= results.song.size
                        items(results.song.size) {
                            val song = results.song[it]
                            SearchSongCard(song = song, it == 0, it == results.song.lastIndex, input, navController)
                        }
                    }
                    SearchCategory.Playlist -> {
                        enableScroll = 6 <= results.playlist.size
                        items(results.playlist.size) {
                            val playlist = results.playlist[it]
                            PlaylistCard(playlist = playlist, reload = {}, isFirst = it == 0, isFromSearch = true, navController = navController)
                        }
                    }
                    SearchCategory.User -> {
                        enableScroll = 3 <= results.user.size
                        items(results.user.size) {
                            val otherUser = results.user[it]
                            OtherUserCard(otherUser = otherUser, it == 0, it == results.song.lastIndex, true, navController)
                        }
                    }
                }
            }
        }
    }
}

suspend fun search(
    input: String,
    user: User?,
    context: Context,
    match: Boolean = false,
    searchInTitle: Boolean = true,
    searchInSinger: Boolean = true
): List<Song> {
    val idData = GlobalScope.async {
        if(input.toIntOrNull() != null) try {
            SongManager.searchWithId(input.toInt(), SongCacheDB(context), true)
        } catch (_: IOException) { listOf() }
        else listOf()
    }
    val titleData = GlobalScope.async {
        if(searchInTitle) try {
            SongManager.searchWithTitle(input, SongCacheDB(context), match)
        } catch (_: IOException) { listOf() }
        else listOf()
    }
    val singerData = GlobalScope.async {
        if(searchInSinger) try {
            SongManager.searchWithSinger(input, SongCacheDB(context), match)
        } catch (_: IOException) { listOf() }
        else listOf()
    }

    val data = ArrayList<Song>().apply {
        addAll(idData.await())
        addAll(titleData.await())
        addAll(singerData.await())
    }

    if(user == null) return data.distinctBy { it.id }

    return ArrayList<Song>().apply {
        val jobs = ArrayList<Deferred<Song?>>()
        user.memoList()?.forEach { (songId, memo) ->
            if(!memo.contains(input)) return@forEach
            jobs.add(GlobalScope.async { SongManager[songId, SongCacheDB(context)] })
        }
        jobs.forEach { add(it.await()?: return@forEach) }

        sortBy {
            val memo = memoList[it.id]?.trim()?: return@sortBy -1
            if(input.trim() == memo) 0
            else if(input.trim().startsWith(memo)) 1
            else 2
        }
        addAll(data)
    }.distinctBy { it.id }
}