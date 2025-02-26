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
fun NotConnectedNetwork() = CenterText(
    text = "네트워크에 연결되지 않았어요:(",
    fontSize = 20f,
    fontWeight = FontWeight.Medium
)

@Composable
fun LazyItemScope.NotConnectedNetwork() = CenterText(
    text = "네트워크에 연결되지 않았어요:(",
    fontSize = 20f,
    fontWeight = FontWeight.Medium
)