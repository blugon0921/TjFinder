package kr.blugon.tjfinder.ui.layout.state

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.CircularProgressIndicator
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
fun Loading(text: String? = null) {
    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 5.dp),
            text = text?: "곡 목록 로딩중",
            fontFamily = Pretendard,
            fontSize = TextUnit(30f, TextUnitType.Sp),
            fontWeight = FontWeight.Medium,
            color = ThemeColor.Main
        )
        CircularProgressIndicator(
            modifier = Modifier.width(48.dp),
            color = ThemeColor.Main,
            trackColor = ThemeColor.DarkMain,
        )
    }
}

@Composable
fun LazyItemScope.Loading(text: String? = null) {
    Column (
        modifier = Modifier.fillMaxSize().fillParentMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 5.dp),
            text = text?: "곡 목록 로딩중",
            fontFamily = Pretendard,
            fontSize = TextUnit(30f, TextUnitType.Sp),
            fontWeight = FontWeight.Medium,
            color = ThemeColor.Main
        )
        CircularProgressIndicator(
            modifier = Modifier.width(48.dp),
            color = ThemeColor.Main,
            trackColor = ThemeColor.DarkMain,
        )
    }
}