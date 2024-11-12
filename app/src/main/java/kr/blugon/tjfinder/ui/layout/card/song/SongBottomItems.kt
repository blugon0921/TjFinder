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
import kr.blugon.tjfinder.module.BlugonTJApi
import kr.blugon.tjfinder.module.BlugonTJApi.removeSongFromPlaylist
import kr.blugon.tjfinder.module.PlaylistSong
import kr.blugon.tjfinder.module.Song
import kr.blugon.tjfinder.module.User
import kr.blugon.tjfinder.module.search.SearchInfo
import kr.blugon.tjfinder.ui.layout.BottomScreen
import kr.blugon.tjfinder.ui.layout.BottomSheetItem
import kr.blugon.tjfinder.ui.layout.ChildScreen
import kr.blugon.tjfinder.ui.layout.navigateScreen
import kr.blugon.tjfinder.ui.screen.child.playlist.InPlaylist
import kr.blugon.tjfinder.ui.screen.initValue

@Composable
fun SongBottomItems(
    song: Song,
    user: User,
    setShowBottomSheet: (Boolean) -> Unit,
    navController: NavController,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isAddToPlaylistScene by remember { mutableStateOf(false) }

    AnimatedVisibility(visible = !isAddToPlaylistScene) {
        Column(modifier = Modifier.fillMaxWidth()) {
            BottomSheetItem(iconId = R.drawable.plus, iconDescription = "add", text = "플레이리스트에 추가/제거") { //추가
                isAddToPlaylistScene = true
            }
            if(song is PlaylistSong && song.playlist.isMine) {
                BottomSheetItem(iconId = R.drawable.minus, iconDescription = "remove", text = "이 플레이리스트에서 제거") { //제거
                    coroutineScope.launch {
                        val response = user.removeSongFromPlaylist(song.playlist.toMine(), song.id)
                        when(response) {
                            BlugonTJApi.RemoveFromPlaylistResponse.SUCCESS -> Toast.makeText(context, "플레이리스트에서 곡을 제거했습니다", Toast.LENGTH_SHORT).show()
                            BlugonTJApi.RemoveFromPlaylistResponse.FAIL -> Toast.makeText(context, "플레이리스트에서 곡을 제거하는데 실패했습니다", Toast.LENGTH_SHORT).show()
                        }
                    }
                    InPlaylist.playlist = song.playlist
                    navController.navigateScreen(ChildScreen.PlaylistItem.parent)
                }
            }
            BottomSheetItem(iconId = R.drawable.user, iconDescription = "search", text = "해당 가수 검색") { //가수 검색
                setShowBottomSheet(false)
                initValue = SearchInfo(song.singer)
                navController.navigateScreen(BottomScreen.Search)
            }
        }
    }

    AnimatedVisibility(visible = isAddToPlaylistScene) { //Adding playlist
        AddSongToPlaylistSheetItems(user = user, song = song, setVisible = {isAddToPlaylistScene = it})
    }
}