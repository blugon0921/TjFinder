package kr.blugon.tjfinder.ui.layout.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import kr.blugon.tjfinder.module.SettingManager
import kr.blugon.tjfinder.ui.screen.child.user.SettingType
import kr.blugon.tjfinder.ui.screen.child.user.SettingValueType
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
//fun <T> SettingItem(settingText: String, settingTrigger: @Composable RowScope.()->Unit) {
fun <T> SettingItem(type: SettingType<T>, value: MutableState<T>) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier,
            text = type.name,
            color = Color.White,
            fontFamily = Pretendard,
            fontWeight = FontWeight.SemiBold,
            fontSize = TextUnit(16f, TextUnitType.Sp)
        )
        when (type.valueType) {
            SettingValueType.String -> {}//TODO
            SettingValueType.Int -> {}
            SettingValueType.Long -> {}//TODO
            SettingValueType.Float -> {}//TODO
            SettingValueType.Boolean -> { //Boolean
                Switch(
                    checked = value.value as Boolean,
                    onCheckedChange = {
                        value.value = it as T
                        SettingManager[context, type] = value.value
                    },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = ThemeColor.Main,
                        uncheckedTrackColor = ThemeColor.MainGray,
                        uncheckedBorderColor = ThemeColor.MainGray,
                        uncheckedThumbColor = ThemeColor.Main,
                        checkedThumbColor = Color.White
                    )
                )
            }
        }
    }
}