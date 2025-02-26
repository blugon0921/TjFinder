package kr.blugon.tjfinder.ui.layout.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import kr.blugon.tjfinder.ui.theme.Pretendard

@Composable
fun BottomSheetItem(iconId: Int, iconDescription: String, text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Box(modifier = Modifier.fillMaxWidth(0.925f).padding(0.dp, 10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Absolute.Left
            ) {
                Icon( //아이콘
                    modifier = Modifier.size(25.dp),
                    imageVector = ImageVector.vectorResource(iconId),
                    contentDescription = iconDescription,
                    tint = Color.Gray,
                )
                Text( //텍스트
                    modifier = Modifier.padding(10.dp, 0.dp, 0.dp, 0.dp),
                    text = text,
                    color = Color.White,
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Medium,
                    fontSize = TextUnit(15f, TextUnitType.Sp),
                )
            }
        }
    }
}