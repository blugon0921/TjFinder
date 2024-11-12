package kr.blugon.tjfinder.module

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kr.blugon.tjfinder.ui.layout.PretendardText
import kr.blugon.tjfinder.ui.screen.child.user.SettingType

class Lyrics(
    val text: String,
) {
    val textDataList = text.extractTextData()

    @Composable
    fun Compose(fontSize: Float = 15f) {
        val context = LocalContext.current
        val showFurigana = SettingManager.getSetting(context, SettingType.showFurigana)

        Column(modifier = Modifier.fillMaxSize()) {
            textDataList.forEach { line ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    if(line.first && showFurigana) {
                        line.second.forEach { data->
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                PretendardText(data.reading?:"", fontSize = fontSize/2, textAlign = TextAlign.End)
                                PretendardText(data.text, fontSize = fontSize, textAlign = TextAlign.Start, modifier = Modifier.offset(y= (-5).dp))
                            }
                        }
                    } else {
                        if(line.second.isEmpty()) {
                            PretendardText(" ", fontSize = fontSize)
                            return@Row
                        }
                        line.second.forEach { data->
                            PretendardText(data.text, fontSize = fontSize)
                        }
                    }
                }
            }
        }
    }
}

//GPT 채고
private fun String.extractTextData(): List<Pair<Boolean, List<TextData>>> {
    val result = mutableListOf<Pair<Boolean, List<TextData>>>()
    val regex = Regex("([\\u4E00-\\u9FFF]+)「([\\u3040-\\u309F]+)」")

    // 텍스트를 줄 단위로 분리하여 처리
    for (line in split("\n")) {
        // 줄바꿈 처리
        if (line.replace("\t", "").isBlank()) {
            result.add(false to listOf())  // 줄바꿈 추가
            continue
        }

        val lineResult = mutableListOf<TextData>()
        var lastIndex = 0

        regex.findAll(line).forEach { matchResult ->
            // 매칭되지 않는 텍스트를 TextData로 추가
            if (lastIndex < matchResult.range.first) {
                val unmatchedText = line.substring(lastIndex, matchResult.range.first).trim()
                if (unmatchedText.isNotEmpty()) {
                    lineResult.add(TextData(unmatchedText))
                }
            }

            // 매칭된 한자와 히라가나를 TextData로 추가
            val kanji = matchResult.groupValues[1]
            val hiragana = matchResult.groupValues[2]
            lineResult.add(TextData(kanji, hiragana))

            // 마지막 매칭 위치 업데이트
            lastIndex = matchResult.range.last + 1
        }

        // 마지막 매칭 후 남은 텍스트를 TextData로 추가
        if (lastIndex < line.length) {
            val unmatchedText = line.substring(lastIndex).trim()
            if (unmatchedText.isNotEmpty()) {
                lineResult.add(TextData(unmatchedText))
            }
        }

        // 해당 줄에서 `reading`이 존재하는지 확인
        val hasReading = lineResult.any { it.reading != null }
        result.add(hasReading to lineResult)
    }

    return result
}

data class TextData(
    val text: String,
    val reading: String? = null,
)