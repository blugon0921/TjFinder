package kr.blugon.tjfinder.ui.layout.card.song

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kr.blugon.tjfinder.R
import kr.blugon.tjfinder.module.*
import kr.blugon.tjfinder.ui.layout.DoubleText
import kr.blugon.tjfinder.ui.layout.PretendardText
import kr.blugon.tjfinder.ui.layout.PretendardSpanStyle
import kr.blugon.tjfinder.ui.layout.card.playlist.PlaylistThumbnail
import kr.blugon.tjfinder.ui.layout.card.playlist.ScrollingPlaylistTitle
import kr.blugon.tjfinder.ui.theme.Pretendard
import kr.blugon.tjfinder.ui.theme.ThemeColor
import kr.blugon.tjfinder.utils.api.TjFinderApi
import kr.blugon.tjfinder.utils.api.finder.PlaylistApi
import kr.blugon.tjfinder.utils.api.finder.addSongToPlaylist
import kr.blugon.tjfinder.utils.api.finder.playlists
import kr.blugon.tjfinder.utils.api.finder.removeSongFromPlaylist

@Composable
fun AddSongToPlaylistSheetItems(
    user: User,
    song: Song,
    setVisible: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val playlistCollection = remember { mutableStateListOf<MyPlaylist>() }
    val beforeAddedState = remember { mutableStateMapOf<MyPlaylist, Boolean>() }
    val isAdded = remember { mutableStateMapOf<MyPlaylist, Boolean>() }
    LaunchedEffect(Unit) {
        val playlists = user.playlists(context) ?: return@LaunchedEffect
        playlistCollection.clear()
        playlistCollection.addAll(playlists)
        playlistCollection.sortBy { it.title }
        for(playlist in playlistCollection) {
            beforeAddedState[playlist] = playlist.songIdList.contains(song.id)
            isAdded[playlist] = playlist.songIdList.contains(song.id)
        }
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp, 10.dp, 10.dp, 0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier) { //뒤로
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .clickable { setVisible(false) },
                    imageVector = ImageVector.vectorResource(R.drawable.arrow_left),
                    contentDescription = "back",
                    tint = Color.White,
                )
                Text( //추가 타이틀
                    modifier = Modifier.padding(5.dp, 0.dp),
                    text = "플레이리스트에 추가",
                    color = Color.White,
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = TextUnit(17f, TextUnitType.Sp)
                )
            }
            Button( //저장
                modifier = Modifier,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ThemeColor.Main
                ),
                contentPadding = PaddingValues(15.dp, 10.dp),
                shape = RoundedCornerShape(100.dp),
                onClick = {
                    coroutineScope.launch { //플레이리스트에 추가/삭제
                        var allCount = isAdded.size
                        var successCount = 0
                        for(added in isAdded) {
                            val playlist = added.key
                            val isAdd = added.value
                            if(isAdd == beforeAddedState[playlist]) {
                                allCount--
                                continue
                            }
                            if(isAdd) {
                                val response = user.addSongToPlaylist(playlist, song.id)
                                if(response != PlaylistApi.AddToPlaylistResponse.FAIL) successCount++
                            } else {
                                val response = user.removeSongFromPlaylist(playlist, song.id)
                                if(response != PlaylistApi.RemoveFromPlaylistResponse.FAIL) successCount++
                            }
                        }
                        if(allCount == 0) return@launch
                        if(allCount == successCount) Toast.makeText(context, "변경사항을 저장했습니다", Toast.LENGTH_SHORT).show()
                        else if(successCount in 1..<allCount) Toast.makeText(context, "변경사항을 일부 저장했습니다", Toast.LENGTH_SHORT).show()
                        else if(successCount == 0) Toast.makeText(context, "변경사항 저장에 실패했습니다", Toast.LENGTH_SHORT).show()
                    }
                    setVisible(false)
                },
            ) {
                Text(
                    modifier = Modifier,
                    text = "저장하기",
                    color = Color.White,
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Bold,
                    fontSize = TextUnit(15f, TextUnitType.Sp),
                )
            }
        }
        fun switchAddedPlaylist(playlist: MyPlaylist) { //플레이리스트에 추가/제거
            if(isAdded[playlist] == null) isAdded[playlist] = false
            isAdded[playlist] = !isAdded[playlist]!!
        }
        for (playlist in playlistCollection) {
            if(song is PlaylistSong &&
                song.playlist.isMine &&
                playlist.id == song.playlist.id
            ) continue
            Row(
                //Playlist
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { switchAddedPlaylist(playlist) }, //플레이리스트에 추가
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row( //플레이리스트
                    modifier = Modifier.fillMaxWidth(0.9f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                ) {
                    PlaylistThumbnail(playlist = playlist, modifier = Modifier.padding(start = 10.dp)) //썸네일
                    Column(modifier = Modifier) { //플레이리스트 정보
                        ScrollingPlaylistTitle(
                            playlist = playlist,
                            fontSize = 15f
                        ) //Title
                        DoubleText( //만든이#태그
                            modifier = Modifier.padding(start = 10.dp),
                            first = {
                                it.text = playlist.owner.name
                                it.style = PretendardSpanStyle(fontSize = 12f)
                            },
                            second = {
                                it.text = "#${playlist.owner.tag}"
                                it.style = PretendardSpanStyle(
                                    color = Color.Gray,
                                    fontSize = 8f
                                )
                            },
                            maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                        PretendardText( //곡 수
                            modifier = Modifier.padding(start = 10.dp, bottom = 10.dp),
                            text = "${playlist.songIdList.size}곡",
                            fontSize = 10f,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                RadioButton( //isAdded
                    selected = if(isAdded[playlist] != null) isAdded[playlist]!!
                    else {
                        isAdded[playlist] = false
                        false
                    },
                    onClick = { switchAddedPlaylist(playlist) }, //플레이리스트에 추가
                    colors = RadioButtonDefaults.colors(
                        selectedColor = ThemeColor.Main,
                    )
                )
            }
        }
    }
}