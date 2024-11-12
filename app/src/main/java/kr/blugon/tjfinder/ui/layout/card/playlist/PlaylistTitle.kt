package kr.blugon.tjfinder.ui.layout.card.playlist

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kr.blugon.tjfinder.module.Playlist
import kr.blugon.tjfinder.ui.layout.DoubleText
import kr.blugon.tjfinder.ui.layout.PretendardText
import kr.blugon.tjfinder.ui.layout.PretendardSpanStyle
import kr.blugon.tjfinder.ui.layout.horizontalScrollAnimeBlur

@Composable
fun PlaylistTitle(
    playlist: Playlist,
    fontSize: Float = 20f,
    withId: Boolean = true,
    maxLines: Int = 1,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
) {
    if(withId) DoubleText(
        first = {
            it.text = playlist.title
            it.style = PretendardSpanStyle(fontSize = fontSize, fontWeight = FontWeight.Bold)
        },
        second = {
            it.text = "#${playlist.id}"
            it.style = PretendardSpanStyle(color = Color.Gray, fontSize = fontSize-5)
        },
        maxLines = maxLines, modifier = modifier
    )
    else PretendardText(
        text = playlist.title,
        fontSize = fontSize,
        fontWeight = FontWeight.Bold,
        overflow = TextOverflow.Ellipsis,
        maxLines = maxLines,
        modifier = modifier
    )
}

@Composable
fun ScrollingPlaylistTitle(
    modifier: Modifier = Modifier,
    playlist: Playlist,
    fontSize: Float = 20f,
    width: Float = 1f,
    rightEnd: @Composable () -> Unit = {}
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Row( //스크롤 애니메이션 블러
            modifier = Modifier
                .fillMaxWidth(width)
                .graphicsLayer { alpha = 0.99f }
                .horizontalScrollAnimeBlur(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row( //스크롤 애니메이션
                modifier = Modifier
                    .fillMaxWidth()
                    .basicMarquee(
                        iterations = Int.MAX_VALUE,
                        velocity = 60.dp
                    )
                    .padding(start = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Absolute.Left
            ) { //위 왼쪽
                Text(buildAnnotatedString {
                    withStyle(style = PretendardSpanStyle(fontSize = fontSize)) { append(playlist.title) } //플레이리스트 Title
                    withStyle(style = PretendardSpanStyle(
                        color = Color.Gray,
                        fontSize = fontSize-5,
                    )) { append("#${playlist.id}") } //ID
                }, maxLines = 1)
            }
        }
        rightEnd()
    }
}