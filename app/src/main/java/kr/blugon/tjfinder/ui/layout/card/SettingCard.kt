package kr.blugon.tjfinder.ui.layout.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import kr.blugon.tjfinder.ui.theme.Pretendard
import kr.blugon.tjfinder.ui.theme.ThemeColor

@Composable
fun SettingCard(title: String, items: @Composable ColumnScope.() -> Unit) {
    Text(
        modifier = Modifier.fillMaxWidth().padding(0.dp, 25.dp, 0.dp, 0.dp),
        text = title,
        color = Color.White,
        fontFamily = Pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = TextUnit(25f, TextUnitType.Sp),
        textAlign = TextAlign.Center
    )
    Box(
        modifier = Modifier
            .fillMaxWidth(0.875f)
            .padding(0.dp, 10.dp, 0.dp, 0.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(ThemeColor.ItemBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            items()
        }
    }
}

@Composable
fun SettingItem(settingText: String, settingTrigger: @Composable RowScope.()->Unit) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier,
            text = settingText,
            color = Color.White,
            fontFamily = Pretendard,
            fontWeight = FontWeight.SemiBold,
            fontSize = TextUnit(20f, TextUnitType.Sp)
        )
        settingTrigger()
    }
}