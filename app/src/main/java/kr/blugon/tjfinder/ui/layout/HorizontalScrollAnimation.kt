package kr.blugon.tjfinder.ui.layout

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

fun Modifier.horizontalScrollAnimeBlur(): Modifier = this.drawWithContent {
    drawContent()
    drawRect(
        brush = Brush.horizontalGradient(arrayListOf(Color.Transparent).apply {
            repeat(30) { add(0, Color.Black) }
        }),
        blendMode = BlendMode.DstIn
    )
    drawRect(
        brush = Brush.horizontalGradient(arrayListOf(Color.Transparent).apply {
            repeat(30) { add(Color.Black) }
        }),
        blendMode = BlendMode.DstIn
    )
}