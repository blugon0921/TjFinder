package kr.blugon.tjfinder.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kr.blugon.tjfinder.R
import kr.blugon.tjfinder.module.*
import kr.blugon.tjfinder.module.State
import kr.blugon.tjfinder.module.database.SongManager
import kr.blugon.tjfinder.ui.layout.*
import kr.blugon.tjfinder.ui.layout.card.song.Top100SongCard
import kr.blugon.tjfinder.ui.theme.ThemeColor
import my.nanihadesuka.compose.LazyColumnScrollbar
import kotlin.concurrent.thread


private val songs = mutableMapOf<SongType, SnapshotStateList<Top100Song>>()
private var songType by mutableStateOf(SongType.K_POP)
private lateinit var listState: LazyListState

private val top100 = mutableStateListOf<Top100Song>()
private var songTypeExpanded = mutableStateOf(false)
@Composable
fun Home(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    if(!(::listState.isInitialized)) listState = rememberLazyListState()


    var user by remember { mutableStateOf<User?>(null) }
    var state by remember { mutableStateOf(State.SUCCESS) }
    LaunchedEffect(Unit) {
        if(!isInternetAvailable(context)) {
            state = State.NOT_INTERNET_AVAILABLE
            return@LaunchedEffect
        }
        user = User.login(context)
        if(user == null) {
            navController.navigateScreen(DefaultScreen.Login)
            return@LaunchedEffect
        }
        if (top100.isEmpty()) {
            if(songs[songType] != null) {
                top100.addAll(songs[songType]!!)
                return@LaunchedEffect
            }
            state = State.LOADING
            thread {
                val data = SongManager.monthPopular(songType, context)
                if(data.isEmpty()) {
                    state = State.FAIL
                    return@thread
                }
                top100.addAll(data)
                songs[songType] = mutableStateListOf<Top100Song>().apply { addAll(data) }
                state = State.SUCCESS
            }
        }
    }



    Column {
        TopBar ( //TopBar
            modifier = Modifier
                .clickable {
                    searchFocus = true
                    navController.navigateScreen(BottomScreen.Search)
                },
            title = "가수 또는 제목으로 검색",
            iconId = R.drawable.search
        ) {
            TextButton( //곡 타입 설정
                modifier = Modifier
                    .size(45.dp)
                    .padding(0.dp),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(12.dp),
                onClick = { songTypeExpanded.value = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor =
                    if(songTypeExpanded.value) ThemeColor.Gray
                    else ThemeColor.ItemBackground
                ),
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.menu),
                    contentDescription = "type",
                    tint = Color.White
                )
                Dropdown(
                    isExpanded = songTypeExpanded,
                    items = ArrayList<Pair<String, SongType>>().apply {
                        SongType.entries.forEach { entry ->
                            add(entry.displayName to entry)
                        }
                    }
                ) { type ->
                    songType = type
                    songTypeExpanded.value = false
                    if(songs[songType] != null) {
                        coroutineScope.launch {
                            top100.clear()
                            top100.addAll(songs[songType]!!)
                            listState.scrollToItem(0, 0)
                        }
                        return@Dropdown
                    }
                    state = State.LOADING
                    CoroutineScope(IO).launch {
                        if(!isInternetAvailable(context)) {
                            state = State.NOT_INTERNET_AVAILABLE
                            return@launch
                        }
                        val data = SongManager.monthPopular(songType, context)
                        if(data.isEmpty()) {
                            state = State.FAIL
                            return@launch
                        }
                        top100.clear()
                        top100.addAll(data)
                        songs[songType] = mutableStateListOf<Top100Song>().apply { addAll(data) }
                        state = State.SUCCESS
                    }
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
                LoadingStateScreen(state, fail = { enableScroll = false }) {
                    enableScroll = true
                    items(top100.size) { i ->
                        val song = top100[i]
                        Top100SongCard(song = song, i == 0, i == top100.size - 1, navController)
                    }
                }
            }
        }
    }
}