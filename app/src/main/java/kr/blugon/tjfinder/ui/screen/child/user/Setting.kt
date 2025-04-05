package kr.blugon.tjfinder.ui.screen.child.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import kr.blugon.tjfinder.module.SettingManager
import kr.blugon.tjfinder.ui.layout.PretendardText
import kr.blugon.tjfinder.ui.layout.card.SettingCard
import kr.blugon.tjfinder.ui.layout.card.SettingItem

class SettingCategory(val name: String, val code: String) {
    companion object {
        val Lyrics = SettingCategory("가사", "Lyrics")
        val TestFunction = SettingCategory("실험적 기능", "TestFunction")
        val Etc = SettingCategory("기타", "Etc")
        val entries = listOf(
            Lyrics,
            TestFunction,
            Etc
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
        val showFurigana = SettingType(
            SettingCategory.Lyrics,
            "일본어 곡에 후리가나 표시",
            "showFurigana",
            SettingValueType.Boolean,
            true
        )
        val showKoreanPronunciation = SettingType(
            SettingCategory.Lyrics,
            "일본어 곡에 한국어 발음 표시",
            "showKoreanPronunciation",
            SettingValueType.Boolean,
            true
        )
        val suggestPlaylist = SettingType(
            SettingCategory.TestFunction,
            "홈화면에 공개 플레이리스트 목록 표시",
            "suggestPlaylist",
            SettingValueType.Boolean,
            false
        )
        val entries = listOf(
            showFurigana,
            showKoreanPronunciation,
            suggestPlaylist
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

    val settingValues = remember { mutableMapOf(
        SettingType.showFurigana to mutableStateOf(SettingManager[context, SettingType.showFurigana]),
        SettingType.showKoreanPronunciation to mutableStateOf(SettingManager[context, SettingType.showKoreanPronunciation]),
        SettingType.suggestPlaylist to mutableStateOf(SettingManager[context, SettingType.suggestPlaylist]),
    ) }

//    val keyboardManager = LocalSoftwareKeyboardController.current
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
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
            verticalArrangement = Arrangement.Center, //asdf <- 과거의 나는 무슨 생각으로 이런 메모를 남겼을까
        ) {
            items(SettingCategory.entries.size) { index ->
                val category = SettingCategory.entries[index]
                SettingCard(category.name) {
                    for(setting in SettingType.entries) {
                        if(setting.category != category) continue
                        SettingItem(setting, settingValues[setting]!!)
                    }
                }
            }
        }
    }
}