package kr.blugon.tjfinder.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.blugon.tjfinder.R
import kr.blugon.tjfinder.module.*
import kr.blugon.tjfinder.module.State
import kr.blugon.tjfinder.module.database.SongCacheDB
import kr.blugon.tjfinder.module.database.SongManager
import kr.blugon.tjfinder.ui.layout.BottomScreen
import kr.blugon.tjfinder.ui.layout.DefaultScreen
import kr.blugon.tjfinder.ui.layout.state.Loading
import kr.blugon.tjfinder.ui.layout.state.NotConnectedNetwork
import kr.blugon.tjfinder.ui.layout.card.songcard.Top100SongCard
import kr.blugon.tjfinder.ui.layout.state.LoadFail
import kr.blugon.tjfinder.ui.theme.Pretendard
import kr.blugon.tjfinder.ui.theme.ThemeColor
import my.nanihadesuka.compose.LazyColumnScrollbar
import kotlin.concurrent.thread


private val songs = mutableMapOf<SongType, SnapshotStateList<Top100Song>>()
private var songType by mutableStateOf(SongType.K_POP)
private lateinit var listState: LazyListState

private val top100 = mutableStateListOf<Top100Song>()
@Composable
fun Home(navController: NavHostController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    if(!(::listState.isInitialized)) listState = rememberLazyListState()

    var songTypeExpanded by remember { mutableStateOf(false) }

    var user by remember { mutableStateOf<User?>(null) }
    var state by remember { mutableStateOf(State.SUCCESS) }
    LaunchedEffect(Unit) {
        if(!isInternetAvailable(context)) {
            state = State.NOT_INTERNET_AVAILABLE
            return@LaunchedEffect
        }
        user = User.login(context)
        if(user == null) {
            navController.navigate(DefaultScreen.Login.name) {
                launchSingleTop = true
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
            }
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
        Row( //상단바 가운데로
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 14.5.dp, 0.dp, 0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Row( //애들 양쪽으로 밀기
                modifier = Modifier
                    .fillMaxWidth(0.875f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row( //검색창
                    modifier = Modifier
                        .fillMaxWidth(0.875f)
                        .padding(0.dp, 0.dp, 10.dp, 0.dp)
                        .height(45.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ThemeColor.ItemBackground)
                        .clickable {
                            searchFocus = true
                            navController.navigate(BottomScreen.Search.name)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon( //검색 아이콘(돋보기)
                        modifier = Modifier
                            .size(34.dp)
                            .padding(10.dp, 0.dp, 0.dp, 0.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.search),
                        contentDescription = "search",
                        tint = Color.White,
                    )
                    Text( //PlaceHolder
                        modifier = Modifier.padding(10.dp),
                        text = "가수 또는 제목으로 검색",
                        color = ThemeColor.LightGray,
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.Medium,
                        fontSize = TextUnit(18f, TextUnitType.Sp),
                    )
                }

                TextButton( //TOP100 카테고리 선택
                    modifier = Modifier
                        .size(45.dp)
                        .padding(0.dp),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(12.dp),
                    onClick = {
                        songTypeExpanded = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                        if(songTypeExpanded) ThemeColor.Gray
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
                            imageVector = ImageVector.vectorResource(R.drawable.menu),
                            contentDescription = "arrow",
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
                                expanded = songTypeExpanded,
                                onDismissRequest = {
                                    songTypeExpanded = false
                                },
                                offset = DpOffset((-96).dp, 10.dp)
                            ) {
                                SongType.entries.forEach { songTypeEach ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                modifier = Modifier.fillMaxWidth(),
                                                text = songTypeEach.displayName,
                                                color = Color.White,
                                                fontFamily = Pretendard,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = TextUnit(15f, TextUnitType.Sp),
                                                textAlign = TextAlign.Center
                                            )
                                        },
                                        onClick = {
                                            songType = songTypeEach
                                            songTypeExpanded = false
                                            top100.clear()
                                            if(songs[songType] != null) {
                                                coroutineScope.launch {
                                                    delay(10)
                                                    top100.addAll(songs[songType]!!)
                                                    listState.scrollToItem(0, 0)
                                                }
                                                return@DropdownMenuItem
                                            }
                                            state = State.LOADING
                                            coroutineScope.launch {
                                                if(!isInternetAvailable(context)) {
                                                    state = State.NOT_INTERNET_AVAILABLE
                                                    return@launch
                                                }
                                                thread {
                                                    val data = SongManager.monthPopular(songType, context)
                                                    if(data.isEmpty()) {
                                                        state = State.FAIL
                                                        return@thread
                                                    }
                                                    Thread.sleep(1)
                                                    top100.addAll(data)
                                                    songs[songType] = mutableStateListOf<Top100Song>().apply { addAll(data) }
                                                    state = State.SUCCESS
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
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
                when(state) {
                    State.SUCCESS -> {
                        enableScroll = true
                        items(top100.size) { i ->
                            val song = top100[i]
                            Top100SongCard(song = song, i == 0, i == top100.size - 1, navController)
                        }
                        return@LazyColumn
                    }
                    State.LOADING -> items(1) {Loading()}
                    State.FAIL,State.DEFAULT -> items(1) {LoadFail()}
                    State.NOT_INTERNET_AVAILABLE -> items(1) {NotConnectedNetwork()}
                }
                enableScroll = false
            }
        }
    }
}