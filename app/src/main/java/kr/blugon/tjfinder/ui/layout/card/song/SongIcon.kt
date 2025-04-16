package kr.blugon.tjfinder.ui.layout.card.song

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kr.blugon.tjfinder.ui.layout.PretendardText


@Composable
fun MRIcon() {
    PretendardText(
        modifier = Modifier
            .width(60.dp)
            .padding(start = 7.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(
                Brush.horizontalGradient(
                listOf(Color(0xFFffc75f), Color(0xFFff6b00))
            )),
        text = "MR",
        fontWeight = FontWeight.SemiBold,
        fontSize = 18f,
        textAlign = TextAlign.Center
    )
}

@Composable
fun MVIcon() {
    PretendardText(
        modifier = Modifier
            .width(60.dp)
            .padding(start = 7.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF0695f0),
                        Color(0xFF0caedd),
                        Color(0xFF15c6c9),
                        Color(0xFF84dae8),
                    )
                )),
        text = "MV",
        fontWeight = FontWeight.SemiBold,
        fontSize = 18f,
        textAlign = TextAlign.Center
    )
}