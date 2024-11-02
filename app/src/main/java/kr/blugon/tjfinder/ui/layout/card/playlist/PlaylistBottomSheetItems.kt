package kr.blugon.tjfinder.ui.layout.card.playlist

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import kr.blugon.tjfinder.R
import kr.blugon.tjfinder.module.Playlist
import kr.blugon.tjfinder.ui.layout.BottomSheetItem

@Composable
fun PlaylistBottomSheetItems(
    playlist: Playlist,
    setShowBottomSheet: (Boolean) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    BottomSheetItem(iconId = R.drawable.share, iconDescription = "share", text = "공유") { //공유
        clipboardManager.setText(AnnotatedString(playlist.id))
        Toast.makeText(context, "클립보드에 플레이리스트 코드를 복사했습니다", Toast.LENGTH_SHORT).show()
        setShowBottomSheet(false)
    }
}