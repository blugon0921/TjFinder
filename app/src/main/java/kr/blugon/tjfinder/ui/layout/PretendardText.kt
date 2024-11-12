package kr.blugon.tjfinder.ui.layout

import android.annotation.SuppressLint
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import kr.blugon.tjfinder.ui.theme.Pretendard

@Composable
fun PretendardText(
    text: String,
    color: Color = Color.White,
    fontSize: Float = 20f,
    fontWeight: FontWeight = FontWeight.Medium,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    textAlign: TextAlign = TextAlign.Start,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        color = color,
        fontFamily = Pretendard,
        fontWeight = fontWeight,
        fontSize = TextUnit(fontSize, TextUnitType.Sp),
        overflow = overflow,
        maxLines = maxLines,
        modifier = modifier,
        textAlign = textAlign,
    )
}

@Composable
fun DoubleText(
    first: (TextObject) -> Unit,
    second: (TextObject) -> Unit,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
) {
    val firstObject = TextObject("")
    val secondObject = TextObject("")
    first(firstObject)
    second(secondObject)
    Text(buildAnnotatedString {
        withStyle(style = firstObject.style) { append(firstObject.text) }
        withStyle(style = secondObject.style) { append(secondObject.text) }
    },
        maxLines = maxLines,
        overflow = overflow,
        modifier = modifier
    )
}

fun PretendardSpanStyle(
    color: Color = Color.White,
    fontSize: Float = 20f,
    fontWeight: FontWeight = FontWeight.Medium,
): SpanStyle {
    return SpanStyle(
        color = color,
        fontFamily = Pretendard,
        fontWeight = fontWeight,
        fontSize = TextUnit(fontSize, TextUnitType.Sp),
    )
}
fun PretendardTextStyle(
    color: Color = Color.White,
    fontSize: Float = 20f,
    fontWeight: FontWeight = FontWeight.Medium,
    textAlign: TextAlign = TextAlign.Left
): TextStyle {
    return TextStyle(
        color = color,
        fontFamily = Pretendard,
        fontWeight = fontWeight,
        fontSize = TextUnit(fontSize, TextUnitType.Sp),
        textAlign = textAlign
    )
}

data class TextObject(
    var text: String,
    var style: SpanStyle = PretendardSpanStyle(),
)