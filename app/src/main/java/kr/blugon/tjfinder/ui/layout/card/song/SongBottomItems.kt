package kr.blugon.tjfinder.ui.layout.card.song

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import kr.blugon.tjfinder.R
import kr.blugon.tjfinder.module.PlaylistSong
import kr.blugon.tjfinder.module.Song
import kr.blugon.tjfinder.module.User
import kr.blugon.tjfinder.module.search.SearchInfo
import kr.blugon.tjfinder.ui.layout.BottomSheetItem
import kr.blugon.tjfinder.ui.layout.navigation.BottomScreen
import kr.blugon.tjfinder.ui.layout.navigation.ChildScreen
import kr.blugon.tjfinder.ui.layout.navigation.navigateMainScreen
import kr.blugon.tjfinder.ui.layout.navigation.navigateScreen
import kr.blugon.tjfinder.ui.screen.child.playlist.InPlaylist
import kr.blugon.tjfinder.ui.screen.initValue
import kr.blugon.tjfinder.utils.api.finder.PlaylistApi
import kr.blugon.tjfinder.utils.api.finder.removeSongFromPlaylist

@Composable
fun SongBottomItems(
    song: Song,
    user: User?,
    showBottomSheet: MutableState<Boolean>,
    showLyrics: MutableState<Boolean>,
    navController: NavController,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isAddToPlaylistScene by remember { mutableStateOf(false) }
//    var isApiServerOpened by remember { mutableStateOf(true) }
//    LaunchedEffect(Unit) {
//        isApiServerOpened = isApiServerOpened()
//    }

    AnimatedVisibility(visible = !isAddToPlaylistScene) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if(user != null) {
                BottomSheetItem(iconId = R.drawable.plus, iconDescription = "add", text = "플레이리스트에 추가/제거") { //추가
                    isAddToPlaylistScene = true
                }
                if(song is PlaylistSong && song.playlist.isMine) {
                    BottomSheetItem(iconId = R.drawable.minus, iconDescription = "remove", text = "이 플레이리스트에서 제거") { //제거
                        coroutineScope.launch {
                            val response = user.removeSongFromPlaylist(song.playlist.toMine(), song.id)
                            when(response) {
                                PlaylistApi.RemoveFromPlaylistResponse.SUCCESS -> Toast.makeText(context, "플레이리스트에서 곡을 제거했습니다", Toast.LENGTH_SHORT).show()
                                PlaylistApi.RemoveFromPlaylistResponse.FAIL -> Toast.makeText(context, "플레이리스트에서 곡을 제거하는데 실패했습니다", Toast.LENGTH_SHORT).show()
                            }
                        }
                        InPlaylist.playlist = song.playlist
                        navController.navigateScreen(ChildScreen.PlaylistItem.parent)
                    }
                }
            }
            BottomSheetItem(iconId = R.drawable.user, iconDescription = "search", text = "해당 가수 검색") { //가수 검색
                showBottomSheet.value = false
                initValue = SearchInfo(song.singer)
                navController.navigateMainScreen(BottomScreen.Search, coroutineScope)
            }
            BottomSheetItem(iconId = R.drawable.file, iconDescription = "lyrics", text = "가사") {
                showLyrics.value = true
                showBottomSheet.value = false
            }
        }
    }

    if(user == null) return
    AnimatedVisibility(visible = isAddToPlaylistScene) { //Adding playlist
        AddSongToPlaylistSheetItems(user = user, song = song, setVisible = {isAddToPlaylistScene = it})
    }
}