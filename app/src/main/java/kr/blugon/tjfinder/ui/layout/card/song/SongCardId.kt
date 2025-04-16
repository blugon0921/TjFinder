package kr.blugon.tjfinder.ui.layout.card.song

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import kr.blugon.tjfinder.module.Song
import kr.blugon.tjfinder.ui.layout.PretendardText
import kr.blugon.tjfinder.ui.theme.Pretendard

@Composable
fun SongCardId(
    song: Song,
    numberPrefix: String? = null,
    highlight: String? = null,
) {
    Row( //ID
        modifier = Modifier.fillMaxWidth(0.94f),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(0.7f),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "${numberPrefix?: ""}${if(numberPrefix != null) " | " else ""}",
                color = Color.White,
                fontFamily = Pretendard,
                fontWeight = FontWeight.SemiBold,
                fontSize = TextUnit(18f, TextUnitType.Sp)
            )
            song.id.toString().splitHighlight(highlight).forEach {
                PretendardText(
                    text = it,
                    color = if(it.lowercase() == highlight?.lowercase()) highLightColor else Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18f
                )
            }
        }
        if(song.isMR) MRIcon()
        if(song.isMV) MVIcon()
    }
}