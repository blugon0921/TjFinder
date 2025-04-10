package kr.blugon.tjfinder.ui.layout.card.playlist

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import kotlinx.coroutines.launch
import kr.blugon.tjfinder.R
import kr.blugon.tjfinder.module.Playlist
import kr.blugon.tjfinder.module.User
import kr.blugon.tjfinder.ui.layout.BottomSheetItem
import kr.blugon.tjfinder.utils.api.finder.addPlaylistToLibrary
import kr.blugon.tjfinder.utils.api.finder.isExistInLibrary
import kr.blugon.tjfinder.utils.api.finder.removePlaylistFromLibrary

@Composable
fun PlaylistBottomSheetItems(
    playlist: Playlist,
    setShowBottomSheet: (Boolean) -> Unit,
    reload: () -> Unit,
    user: User?
) {
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    var isExistInLibrary by remember { mutableStateOf(false) }
    LaunchedEffect(user) {
        if(user == null) return@LaunchedEffect
        isExistInLibrary = user.isExistInLibrary(playlist.id)
    }

    if(user != null) {
        if(!isExistInLibrary) {
            BottomSheetItem(iconId = R.drawable.plus, iconDescription = "share", text = "내 라이브러리에 추가") {
                coroutineScope.launch {
                    val success = user.addPlaylistToLibrary(playlist.id)
                    if(success) {
                        isExistInLibrary = true
                        Toast.makeText(context, "추가 완료", Toast.LENGTH_SHORT).show()
                        reload()
                    } else {
                        Toast.makeText(context, "추가 실패", Toast.LENGTH_SHORT).show()
                    }
                    reload()
                    setShowBottomSheet(false)
                }
            }
        } else {
            BottomSheetItem(iconId = R.drawable.minus, iconDescription = "share", text = "내 라이브러리에서 제거") {
                coroutineScope.launch {
                    val success = user.removePlaylistFromLibrary(playlist.id)
                    if(success) {
                        isExistInLibrary = false
                        Toast.makeText(context, "제거 완료", Toast.LENGTH_SHORT).show()
                        reload()
                    } else {
                        Toast.makeText(context, "제거 실패", Toast.LENGTH_SHORT).show()
                    }
                    setShowBottomSheet(false)
                }
            }
        }
    }
    BottomSheetItem(iconId = R.drawable.share, iconDescription = "share", text = "공유") { //공유
        clipboardManager.setText(AnnotatedString(playlist.id))
//        Toast.makeText(context, "클립보드에 플레이리스트 코드를 복사했습니다", Toast.LENGTH_SHORT).show()
        setShowBottomSheet(false)
    }
}