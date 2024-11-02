package kr.blugon.tjfinder.ui.layout.card.playlist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kr.blugon.tjfinder.module.BlugonTJApi.getPlaylist
import kr.blugon.tjfinder.module.Playlist
import kr.blugon.tjfinder.module.User
import kr.blugon.tjfinder.ui.layout.*
import kr.blugon.tjfinder.ui.screen.child.playlist.InPlaylist
import kr.blugon.tjfinder.ui.theme.ThemeColor

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PlaylistCard(playlist: Playlist, reload: () -> Unit, isFirst: Boolean = false, navController: NavHostController, isFromSearch: Boolean = false) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var user by remember { mutableStateOf<User?>(null) }
    var isPrivate by remember { mutableStateOf(playlist.isPrivate) }
    LaunchedEffect(Unit) {
        user = User.login(context)?: return@LaunchedEffect
        isPrivate = user!!.getPlaylist(playlist.id, context)?.isPrivate?: false
    }

    var showBottomSheet by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth(0.875f)
            .padding(
                0.dp, when (isFirst) {
                    true -> 0.dp
                    false -> 17.5.dp
                }, 0.dp, 0.dp
            )
            .clip(RoundedCornerShape(12.dp))
            .background(ThemeColor.ItemBackground)
            .clickable {
                InPlaylist.playlist = playlist
                if (isFromSearch) navController.navigate(ChildScreen.SearchPlaylist.name) {
                    launchSingleTop = true
                    restoreState = false
                }
                else navController.navigate(ChildScreen.PlaylistItem.name)
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        PlaylistThumbnail(modifier = Modifier.padding(start = 10.dp), playlist = playlist) //썸네일
        Column(modifier = Modifier.fillMaxSize()) {
            ScrollingPlaylistTitle( //제목
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp, 10.dp, 10.dp, 0.dp),
                playlist = playlist,
                width = 0.925f,
                rightEnd = {
                    EllipsisOption(onClick = { showBottomSheet = true })
                }
            )
            DoubleText( //만든이#태그
                modifier = Modifier.padding(start = 10.dp),
                first = {
                    it.text = playlist.creator
                    it.style = PretendardTextStyle(fontSize = 14f)
                },
                second = {
                    it.text = "#${playlist.creatorTag}"
                    it.style = PretendardTextStyle(
                        color = Color.Gray,
                        fontSize = 10f
                    )
                },
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            PretendardText( //곡 수
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(start = 10.dp, bottom = 10.dp),
                text = "${playlist.songIdList.size}곡",
                fontSize = 12f,
                maxLines = 1,
            )
        }
    }



    //하단 메뉴
    val sheetState = rememberModalBottomSheetState()
    if(showBottomSheet) {
        BottomSheet(
            sheetState = sheetState,
            onDismiss = { showBottomSheet = false },
            topBar = {
                Row( //이름
                    modifier = Modifier
                        .fillMaxWidth(0.925f)
                        .padding(0.dp, 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Absolute.Left
                ) { //위 왼쪽
                    PlaylistThumbnail(playlist = playlist, size = 64.dp) //썸네일
                    Column(
                        modifier = Modifier.padding(start = 10.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        PlaylistTitle( //플레이리스트 이름
                            playlist = playlist,
                            withId = false,
                            maxLines = 2
                        )
                        PretendardText( //플레이리스트 ID
                            text = "#${playlist.id}",
                            color = Color.Gray,
                            fontSize = 15f,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        DoubleText(
                            first = { //플레이리스트 만든이
                                it.text = playlist.creator
                                it.style = PretendardTextStyle(fontSize = 13f)
                            },
                            second = { //플레이리스트 만든이 tag
                                it.text = "#${playlist.creatorTag}"
                                it.style = PretendardTextStyle(
                                    color = Color.Gray,
                                    fontSize = 10f
                                )
                            },
                            maxLines = 1, overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        ) {
            if((playlist.creator == user!!.name) && (playlist.creatorTag == user!!.tag)) {
                MyPlaylistBottomSheetItems(
                    playlist = playlist.toMine(),
                    navController = navController,
                    user = user!!,
                    isPrivate = {isPrivate},
                    setIsPrivate = {
                        isPrivate = it
                    },
                    reload = reload,
                    setShowBottomSheet = {
                        showBottomSheet = it
                    },
                )
            } else {
                PlaylistBottomSheetItems(
                    playlist = playlist,
                    setShowBottomSheet = {
                        showBottomSheet = it
                    }
                )
            }
        }
    }
}