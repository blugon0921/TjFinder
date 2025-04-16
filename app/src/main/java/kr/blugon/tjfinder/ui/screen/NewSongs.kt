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
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import kr.blugon.tjfinder.R
import kr.blugon.tjfinder.module.*
import kr.blugon.tjfinder.module.State
import kr.blugon.tjfinder.module.database.SongManager
import kr.blugon.tjfinder.ui.layout.LoadingStateScreen
import kr.blugon.tjfinder.ui.layout.SortableTopBar
import kr.blugon.tjfinder.ui.layout.card.song.SongCard
import kr.blugon.tjfinder.ui.theme.ThemeColor
import kr.blugon.tjfinder.utils.isInternetAvailable
import my.nanihadesuka.compose.LazyColumnScrollbar
import kotlin.concurrent.thread



class NewSongsViewModel: ViewModel() {
    val songs = mutableStateListOf<Song>()
    var listState: LazyListState? = null
    var sortExpanded = mutableStateOf(false)
    var sort by mutableStateOf(SongSortType.SINGER)

    @Composable
    fun init() {
        if(listState != null) return
        listState = rememberLazyListState()
    }
}
private val states = NewSongsViewModel()
@Composable
fun NewSongs(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    states.init()

    var state by remember { mutableStateOf(State.SUCCESS) }
    LaunchedEffect(Unit) {
        if(!isInternetAvailable(context)) {
            state = State.NOT_INTERNET_AVAILABLE
            return@LaunchedEffect
        }
        if(states.songs.isNotEmpty()) return@LaunchedEffect
        state = State.LOADING
        coroutineScope.launch {
            val data = SongManager.monthNew(context)
            if(data.isEmpty()) {
                state = State.FAIL
                return@launch
            }
            states.songs.addAll(data)
            state = State.SUCCESS
        }
    }

    Column {
        SortableTopBar( //TopBar
            title = "이달의 신곡",
            iconId = R.drawable.starlight,
            isExpanded = states.sortExpanded,
            items = SongSortType.entries
        ) {
            states.sort = it
            states.sortExpanded.value = false
            when(states.sort) {
                SongSortType.ID -> states.songs.sortBy { it.id }
                SongSortType.TITLE -> states.songs.sortBy { it.title }
                SongSortType.SINGER -> states.songs.sortBy { it.singer }
            }
        }
        ArrayList<SongSortType>(SongSortType.entries)

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
                state = states.listState!!,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoadingStateScreen(state, fail = { enableScroll = false }) {
                    enableScroll = 5 <= states.songs.size
                    items(states.songs.size) { i ->
                        val song = states.songs[i]
                        SongCard(song = song, i == 0, i == states.songs.size - 1, navController = navController)
                    }
                }
            }
        }
    }
}