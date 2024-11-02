package kr.blugon.tjfinder.ui.screen.child.user

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import kr.blugon.tjfinder.module.SettingManager
import kr.blugon.tjfinder.ui.layout.card.SettingCard
import kr.blugon.tjfinder.ui.layout.card.SettingItem
import kr.blugon.tjfinder.ui.theme.Pretendard
import kr.blugon.tjfinder.ui.theme.ThemeColor

class SettingCategory(val name: String, val code: String) {
    companion object {
        val Theme = SettingCategory("테마", "Theme")
        val TestFunction = SettingCategory("실험적 기능", "TestFunction")
        val entries = listOf(
            Theme,
            TestFunction
        )
    }
}

class SettingType <T> (
    val category: SettingCategory,
    val name: String,
    val code: String,
    val valueType: SettingValueType,
    val defaultValue: T
) {
    companion object {
        val isDarkMode = SettingType<Boolean>(SettingCategory.Theme, "다크모드", "isDarkMode", SettingValueType.Boolean, true)
        val testOption = SettingType<Int>(SettingCategory.TestFunction, "테스트 옵션", "testOption", SettingValueType.Int, 10)
        val entries = listOf(
            isDarkMode,
            testOption
        )
    }
}

enum class SettingValueType {
    String,
    Int,
    Long,
    Float,
    Boolean
}

@Composable
fun Setting() {
    val context = LocalContext.current

    var darkModeValue by remember { mutableStateOf(SettingManager.getSetting(context, SettingType.isDarkMode)) }
    var testOptionValue by remember { mutableIntStateOf(SettingManager.getSetting(context, SettingType.testOption)) }


    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center, //asdf
    ) {
        items(1) {
            Text(
                text = "업데이트 예정",
                fontFamily = Pretendard,
                fontSize = TextUnit(30f, TextUnitType.Sp),
                fontWeight = FontWeight.Medium,
                color = ThemeColor.Main
            )
        }
        return@LazyColumn
        items(SettingCategory.entries.size) { index ->
            val category = SettingCategory.entries[index]
            SettingCard(category.name) {
                for(setting in SettingType.entries) {
                    if(setting.category == category) {
                        SettingItem(settingText = setting.name) {
                            when (setting.valueType) {
                                SettingValueType.String -> {}//TODO
                                SettingValueType.Int -> { //Int
                                    TextField(
                                        modifier = Modifier.fillMaxWidth(0.3f),
                                        value = testOptionValue.toString(),
                                        onValueChange = {
                                            if(100 < it.length) {
                                                Toast.makeText(context, "입력 가능한 최대 길이는 100자입니다", Toast.LENGTH_SHORT).show()
                                                return@TextField
                                            }
                                            testOptionValue = try { it.replace(Regex("/[^0-9]/g"), "").toInt()
                                            } catch (e: NumberFormatException) { 0 }
                                            SettingManager.setSetting(context, setting, testOptionValue)
                                        },
                                        textStyle = TextStyle(
                                            color = Color.White,
                                            fontFamily = Pretendard,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = TextUnit(18f, TextUnitType.Sp)
                                        ),
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = ThemeColor.ItemBackground,
                                            unfocusedContainerColor = ThemeColor.ItemBackground,
                                            cursorColor = ThemeColor.LightGray,
                                            focusedIndicatorColor = ThemeColor.LightGray,
                                            unfocusedIndicatorColor = ThemeColor.LightGray
                                        ),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Number,
                                            imeAction = ImeAction.Done
                                        )
                                    )
                                }
                                SettingValueType.Long -> {}//TODO
                                SettingValueType.Float -> {}//TODO
                                SettingValueType.Boolean -> { //Boolean
                                    Switch(
                                        checked = darkModeValue,
                                        onCheckedChange = {
                                            darkModeValue = !darkModeValue
                                            SettingManager.setSetting(context, setting, darkModeValue)
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
                }
            }
        }
    }
}