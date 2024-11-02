package kr.blugon.tjfinder.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import kr.blugon.tjfinder.ui.layout.state.Loading
import kr.blugon.tjfinder.ui.layout.state.NotConnectedNetwork
import kr.blugon.tjfinder.ui.layout.card.songcard.SongCard
import kr.blugon.tjfinder.ui.layout.state.LoadFail
import kr.blugon.tjfinder.ui.theme.Pretendard
import kr.blugon.tjfinder.ui.theme.ThemeColor
import my.nanihadesuka.compose.LazyColumnScrollbar
import kotlin.concurrent.thread


private val songs = mutableStateListOf<Song>()
private lateinit var listState: LazyListState
@Composable
fun NewSongs(navController: NavHostController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    if(!(::listState.isInitialized)) listState = rememberLazyListState()

    var state by remember { mutableStateOf(State.SUCCESS) }
    LaunchedEffect(Unit) {
        if(!isInternetAvailable(context)) {
            state = State.NOT_INTERNET_AVAILABLE
            return@LaunchedEffect
        }
        if(songs.isNotEmpty()) return@LaunchedEffect
        state = State.LOADING
        thread {
            val data = SongManager.monthNew(context)
            if(data.isEmpty()) {
                state = State.FAIL
                return@thread
            }
            songs.addAll(data)
            state = State.SUCCESS
        }
    }

    var sortExpanded by remember { mutableStateOf(false) }
    var sort by remember { mutableStateOf(SortType.SINGER) }

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
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.875f)
                        .padding(0.dp, 0.dp, 10.dp, 0.dp)
                        .height(45.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ThemeColor.ItemBackground),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon( //빤짝이
                        modifier = Modifier
                            .size(34.dp)
                            .padding(10.dp, 0.dp, 0.dp, 0.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.starlight),
                        contentDescription = "newSongs",
                        tint = Color.White,
                    )
                    Text(
                        modifier = Modifier.padding(10.dp),
                        text = "이달의 신곡",
                        color = Color.White,
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = TextUnit(18f, TextUnitType.Sp),
                    )
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
                            SortType.entries.forEach { sorTypeEach ->
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
                                        sort = sorTypeEach
                                        sortExpanded = false
                                        val new = when(sort) {
                                            SortType.ID -> songs.sortedBy { it.id }
                                            SortType.TITLE -> songs.sortedBy { it.title }
                                            SortType.SINGER -> songs.sortedBy { it.singer }
                                        }
                                        songs.clear()
                                        coroutineScope.launch {
                                            delay(10)
                                            songs.addAll(new)
                                        }
                                    }
                                )
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
                        enableScroll = 5 <= songs.size
                        items(songs.size) { i ->
                            val song = songs[i]
                            SongCard(song = song, i == 0, i == songs.size - 1, navController)
                        }
                        return@LazyColumn
                    }
                    State.FAIL -> items(1) {LoadFail()}
                    State.LOADING,State.DEFAULT -> items(1) {Loading()}
                    State.NOT_INTERNET_AVAILABLE -> items(1) {NotConnectedNetwork()}
                }
                enableScroll = false
            }
        }
    }
}