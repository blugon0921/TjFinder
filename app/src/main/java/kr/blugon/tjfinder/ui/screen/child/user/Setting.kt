package kr.blugon.tjfinder.ui.screen.child.user

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.navigation.NavController
import kr.blugon.tjfinder.module.SettingManager
import kr.blugon.tjfinder.ui.layout.PretendardText
import kr.blugon.tjfinder.ui.layout.card.SettingCard
import kr.blugon.tjfinder.ui.layout.card.SettingItem
import kr.blugon.tjfinder.ui.theme.Pretendard
import kr.blugon.tjfinder.ui.theme.ThemeColor

class SettingCategory(val name: String, val code: String) {
    companion object {
        val TestFunction = SettingCategory("실험적 기능", "TestFunction")
        val entries = listOf(
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
        val suggestPlaylist = SettingType(
            SettingCategory.TestFunction,
            "홈화면에 플레이리스트\n추천목록 표시",
            "suggestPlaylist",
            SettingValueType.Boolean,
            false
        )
        val entries = listOf(
            suggestPlaylist,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(navController: NavController) {
    val context = LocalContext.current

    var suggestPlaylistValue by remember { mutableStateOf(SettingManager.getSetting(context, SettingType.suggestPlaylist)) }


    val keyboardManager = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .clickable(interactionSource = interactionSource, indication = null) {
                focusManager.clearFocus()
            },
        topBar = { //Topbar
            TopAppBar(
                title = { PretendardText(text = "설정", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center, //asdf
        ) {
            items(SettingCategory.entries.size) { index ->
                val category = SettingCategory.entries[index]
                SettingCard(category.name) {
                    for(setting in SettingType.entries) {
                        if(setting.category != category) continue
                        SettingItem(settingText = setting.name) {
                            when (setting.valueType) {
                                SettingValueType.String -> {}//TODO
                                SettingValueType.Int -> {}
                                SettingValueType.Long -> {}//TODO
                                SettingValueType.Float -> {}//TODO
                                SettingValueType.Boolean -> { //Boolean
                                    Switch(
                                        checked = suggestPlaylistValue,
                                        onCheckedChange = {
                                            suggestPlaylistValue = it
                                            SettingManager.setSetting(context, setting, suggestPlaylistValue)
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