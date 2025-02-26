package kr.blugon.tjfinder.ui.layout.state

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import kr.blugon.tjfinder.ui.theme.Pretendard
import kr.blugon.tjfinder.ui.theme.ThemeColor

@Composable
fun CenterText(
    modifier: Modifier = Modifier.fillMaxSize(),
    text: String,
    fontSize: Float = 30f,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 5.dp),
            text = text,
            fontFamily = Pretendard,
            fontSize = TextUnit(fontSize, TextUnitType.Sp),
            fontWeight = fontWeight,
            color = ThemeColor.Main
        )
    }
}


@Composable
fun LazyItemScope.CenterText(
    modifier: Modifier = Modifier.fillMaxSize().fillParentMaxSize(),
    text: String,
    fontSize: Float = 30f,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Column (
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 5.dp),
            text = text,
            fontFamily = Pretendard,
            fontSize = TextUnit(fontSize, TextUnitType.Sp),
            fontWeight = fontWeight,
            color = ThemeColor.Main
        )
    }
}