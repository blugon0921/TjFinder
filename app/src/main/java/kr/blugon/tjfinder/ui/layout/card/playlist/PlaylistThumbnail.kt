package kr.blugon.tjfinder.ui.layout.card.playlist

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kr.blugon.tjfinder.module.MyPlaylist
import kr.blugon.tjfinder.module.Playlist
import kr.blugon.tjfinder.ui.theme.ThemeColor

@Composable
fun PlaylistThumbnail(modifier: Modifier = Modifier, playlist: Playlist, size: Dp = 80.dp) {
    val context = LocalContext.current

    Box(modifier = modifier) { //썸네일 박스
        AsyncImage( //썸네일
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(8.dp))
                .border(2.dp, Color(255, 255, 255, 64), RoundedCornerShape(8.dp)),
            model = ImageRequest.Builder(context)
                .data(playlist.thumbnail)
                .build(),
            contentDescription = "thumbnail",
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center
        )
    }
}