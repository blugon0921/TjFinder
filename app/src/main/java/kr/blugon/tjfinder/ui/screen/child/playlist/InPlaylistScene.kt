package kr.blugon.tjfinder.ui.screen.child.playlist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.blugon.tjfinder.R
import kr.blugon.tjfinder.module.*
import kr.blugon.tjfinder.module.State
import kr.blugon.tjfinder.module.database.SongCacheDB
import kr.blugon.tjfinder.ui.layout.state.Loading
import kr.blugon.tjfinder.ui.layout.state.NotConnectedNetwork
import kr.blugon.tjfinder.ui.layout.card.songcard.PlaylistSongCard
import kr.blugon.tjfinder.ui.layout.horizontalScrollAnimeBlur
import kr.blugon.tjfinder.ui.layout.state.LoadFail
import kr.blugon.tjfinder.ui.screen.child.playlist.InPlaylist.playlist
import kr.blugon.tjfinder.ui.theme.Pretendard
import kr.blugon.tjfinder.ui.theme.ThemeColor
import my.nanihadesuka.compose.LazyColumnScrollbar

object InPlaylist {
    lateinit var playlist: Playlist
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InPlaylistScene(navController: NavHostController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var user by remember { mutableStateOf<User?>(null) }
    val songList = remember { mutableStateListOf<PlaylistSong>() }
    var state by remember { mutableStateOf(State.DEFAULT) }
    val playlist = remember { playlist }
    LaunchedEffect(Unit) {
        if(!isInternetAvailable(context)) {
            state = State.NOT_INTERNET_AVAILABLE
            return@LaunchedEffect
        }
        if(LoginManager.getSavedUid(context) != null) {
            user = BlugonTJApi.login(LoginManager.getSavedUid(context)!!)
            state = State.LOADING
            playlist.loadSongList(context)
            GlobalScope.launch {
                repeat(500) {
                    delay(10)
                    if(playlist.songList == null) return@repeat
                    songList.addAll(playlist.songList!!)
                    state = State.SUCCESS
                    return@launch
                }
                state = State.FAIL
            }
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
                    Box( //썸네일 박스
                        modifier = Modifier.padding(start = 10.dp)
                    ) {
                        AsyncImage( //썸네일
                            modifier = Modifier
                                .size(34.dp)
                                .border(2.dp, Color(255, 255, 255, 64)),
                            model = ImageRequest.Builder(context)
                                .data(playlist.thumbnail)
                                .build(),
                            contentDescription = "thumbnail",
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        )
                    }
                    Box( //스크롤 애니메이션 블러
                        modifier = Modifier.fillMaxWidth(0.875f)
                            .graphicsLayer { alpha = 0.99f }
                            .horizontalScrollAnimeBlur()
                    ) {
                        Row( //스크롤 애니메이션
                            modifier = Modifier.basicMarquee(
                                iterations = Int.MAX_VALUE,
                                velocity = 60.dp
                            ).padding(start = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text( //플레이리스트 이름
                                text = playlist.title,
                                color = Color.White,
                                fontFamily = Pretendard,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = TextUnit(18f, TextUnitType.Sp),
                                maxLines = 1
                            )
                            Text( //플레이리스트 ID
                                text = "#${playlist.id}",
                                color = Color.Gray,
                                fontFamily = Pretendard,
                                fontWeight = FontWeight.Medium,
                                fontSize = TextUnit(13f, TextUnitType.Sp),
                                maxLines = 1
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
                                                SortType.ID -> songList.sortedBy { it.id }
                                                SortType.TITLE -> songList.sortedBy { it.title }
                                                SortType.SINGER -> songList.sortedBy { it.singer }
                                            }
                                            songList.clear()
                                            coroutineScope.launch {
                                                delay(10)
                                                songList.addAll(new)
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
                        if (songList.isEmpty()) {
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
                            return@LazyColumn
                        }
                        enableScroll = 5 <= songList.size
                        items(songList.size) { i ->
                            val song = songList[i]
                            PlaylistSongCard(song = song, i == 0, i == songList.size-1, navController)
                        }
                        return@LazyColumn
                    }
                    State.FAIL -> {items(1) {LoadFail()}}
                    State.LOADING,State.DEFAULT -> items(1) {Loading()}
                    State.NOT_INTERNET_AVAILABLE -> items(1) {NotConnectedNetwork()}
                }
                enableScroll = false
            }
        }
    }
}