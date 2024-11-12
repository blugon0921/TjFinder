package kr.blugon.tjfinder.ui.layout.card.song

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import kr.blugon.tjfinder.module.Song
import kr.blugon.tjfinder.ui.theme.Pretendard

@Composable
fun SongCardTitle(
    song: Song,
    highlight: String? = null,
) {
    Row( //제목
        modifier = Modifier
            .fillMaxWidth(0.925f)
            .padding(10.dp, 12.dp, 0.dp, 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        song.title.splitHighlight(highlight).forEach {
            Text(
                text = it,
                color = if(it.lowercase() == highlight?.lowercase()) highLightColor else Color.White,
                fontFamily = Pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = TextUnit(20f, TextUnitType.Sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}