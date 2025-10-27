package kr.blugon.tjfinder.ui.layout.card.song

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kr.blugon.tjfinder.ui.layout.PretendardText


@Composable
fun MRIcon() {
    SongIcon(text = "MR", background = Brush.horizontalGradient(
        listOf(Color(0xFFffc75f), Color(0xFFff6b00))
    ))
}

@Composable
fun MVIcon() {
    SongIcon(text = "MV", background = Brush.linearGradient(
        listOf(
            Color(0xFF0695f0),
            Color(0xFF0caedd),
            Color(0xFF15c6c9),
            Color(0xFF84dae8),
        )
    ))
}

@Composable
fun ExclusiveIcon() {
    val context = LocalContext.current
    SongIcon(
        Modifier.clickable {
            Toast.makeText(context, "60이상 반주기 전용곡", Toast.LENGTH_SHORT).show()
        },
        "60+",
        Brush.horizontalGradient(
        listOf(Color(0xFF00AFEC), Color(0xFF7733FF))
    ), 70)
}


@Composable
private fun SongIcon(modifier: Modifier = Modifier, text: String, background: Brush, width: Int = 60) {
    PretendardText(
        modifier = modifier
            .width(width.dp)
            .padding(start = 7.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(background),
        text = text,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18f,
        textAlign = TextAlign.Center
    )
}