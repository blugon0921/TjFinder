package kr.blugon.tjfinder.ui.layout.state

import androidx.compose.foundation.layout.*
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
fun NotConnectedNetwork(modifier: Modifier? = null) {
    Column (
        modifier = when(modifier) {
            null -> Modifier.fillMaxSize()
            else -> Modifier
        },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 5.dp),
            text = "네트워크에 연결되지 않았어요:(",
            fontFamily = Pretendard,
            fontSize = TextUnit(20f, TextUnitType.Sp),
            fontWeight = FontWeight.Medium,
            color = ThemeColor.Main
        )
    }
}

@Composable
fun LazyItemScope.NotConnectedNetwork() {
    Column (
        modifier = Modifier.fillMaxSize().fillParentMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 5.dp),
            text = "네트워크에 연결되지 않았어요:(",
            fontFamily = Pretendard,
            fontSize = TextUnit(20f, TextUnitType.Sp),
            fontWeight = FontWeight.Medium,
            color = ThemeColor.Main
        )
    }
}