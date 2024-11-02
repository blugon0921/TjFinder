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
import kr.blugon.tjfinder.module.BlugonTJApi.playlists
import kr.blugon.tjfinder.module.State
import kr.blugon.tjfinder.ui.layout.ChildScreen
import kr.blugon.tjfinder.ui.layout.state.Loading
import kr.blugon.tjfinder.ui.layout.state.NotConnectedNetwork
import kr.blugon.tjfinder.ui.layout.card.playlist.PlaylistCard
import kr.blugon.tjfinder.ui.layout.state.LoadFail
import kr.blugon.tjfinder.ui.theme.Pretendard
import kr.blugon.tjfinder.ui.theme.ThemeColor
import my.nanihadesuka.compose.LazyColumnScrollbar


@Composable
fun PlaylistScene(navController: NavHostController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var user by remember { mutableStateOf<User?>(null) }
    var state by remember { mutableStateOf(State.DEFAULT) }
    val playlistCollection = remember { mutableStateListOf<Playlist>() }

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
                playlistCollection.sortBy { it.title }
                state = State.SUCCESS
            }
        }
    }
    LaunchedEffect(Unit) {
        if(!isInternetAvailable(context)) {
            state = State.NOT_INTERNET_AVAILABLE
            return@LaunchedEffect
        }
        user = BlugonTJApi.login(LoginManager.getSavedUid(context)!!)
        reload()
    }

    var sortExpanded by remember { mutableStateOf(false) }
    var sort by remember { mutableStateOf(PlaylistSortType.TITLE) }


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
                    Icon( //아이콘
                        modifier = Modifier.size(34.dp)
                            .padding(10.dp, 0.dp, 0.dp, 0.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.playlist),
                        contentDescription = "playlist",
                        tint = Color.White,
                    )
                    Text(
                        modifier = Modifier.padding(10.dp),
                        text = "플레이리스트",
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
                                            if(playlistCollection.isEmpty()) return@DropdownMenuItem
                                            sort = sorTypeEach
                                            sortExpanded = false
                                            val new = when(sort) {
                                                PlaylistSortType.SONG_COUNT -> playlistCollection.sortedBy { it.songIdList.size }
                                                PlaylistSortType.TITLE -> playlistCollection.sortedBy { it.title }
                                                PlaylistSortType.ID -> playlistCollection.sortedBy { it.id }
                                                PlaylistSortType.CREATOR -> playlistCollection.sortedBy { it.creator }
                                            }
                                            playlistCollection.clear()
                                            coroutineScope.launch {
                                                delay(10)
                                                playlistCollection.addAll(new)
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
                when(state) {
                    State.SUCCESS -> {
                        enableScroll = 6 <= playlistCollection.size
                        items(playlistCollection.size) { i ->
                            val playlist = playlistCollection[i]
                            PlaylistCard(playlist = playlist, reload = reload, i == 0, navController)
                        }
                        items(1) {
                            Box( //플레이 리스트 추가
                                modifier = Modifier
                                    .padding(vertical = if(!playlistCollection.isEmpty()) 17.5.dp else 0.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ThemeColor.AddPlaylist)
                                    .clickable { //플레이리스트 생성씬으로 이동
                                        navController.navigate(ChildScreen.CreatePlaylist.name)
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
                        return@LazyColumn
                    }
                    State.FAIL -> items(1) { LoadFail() }
                    State.LOADING,State.DEFAULT -> items(1) { Loading("플레이리스트 로딩중") }
                    State.NOT_INTERNET_AVAILABLE -> items(1) {NotConnectedNetwork()}
                }
                enableScroll = false
            }
        }
    }
}