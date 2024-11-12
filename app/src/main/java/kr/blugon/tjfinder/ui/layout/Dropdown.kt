package kr.blugon.tjfinder.ui.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import kr.blugon.tjfinder.ui.theme.Pretendard
import kr.blugon.tjfinder.ui.theme.ThemeColor

@Composable
fun <T> Dropdown(
    isExpanded: MutableState<Boolean>,
    items: List<Pair<String, T>>,
    onClick: (T)->Unit,
) {
    MaterialTheme(
        shapes = MaterialTheme.shapes.copy(
            extraSmall = RoundedCornerShape(12.dp),
        )
    ) {
        DropdownMenu(
            modifier = Modifier
                .width(140.dp)
                .background(ThemeColor.Gray),
            expanded = isExpanded.value,
            onDismissRequest = {
                isExpanded.value = false
            },
            offset = DpOffset((-96).dp, 10.dp)
        ) {
            items.forEach { (name, type) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = name,
                            color = Color.White,
                            fontFamily = Pretendard,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = TextUnit(15f, TextUnitType.Sp),
                            textAlign = TextAlign.Center
                        )
                    },
                    onClick = { onClick(type) }
                )
            }
        }
    }
}