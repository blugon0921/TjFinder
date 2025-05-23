package kr.blugon.tjfinder.module

import androidx.compose.runtime.Composable
import kr.blugon.tjfinder.utils.convertToKoreanPronunciation
import kr.blugon.tjfinder.utils.isJapanese

class Lyrics(
    val text: String,
) {
    val textDataList = text.extractTextData()

    @Composable
    fun Compose(fontSize: Float = 15f) {
//        val context = LocalContext.current
//        val showFurigana = SettingManager[context, SettingType.showFurigana]
//        val showKoreanPronunciation = SettingManager[context, SettingType.showKoreanPronunciation]
//
//        Column(modifier = Modifier.fillMaxSize()) {
//            textDataList.first.forEachIndexed { index, line ->
//                Row(modifier = Modifier.fillMaxWidth()) {
//                    if(line.first && showFurigana) {
//                        line.second.forEach { data->
//                            Column(
//                                verticalArrangement = Arrangement.Center,
//                                horizontalAlignment = Alignment.CenterHorizontally,
//                            ) {
//                                PretendardText(data.reading?:"", fontSize = fontSize/2, textAlign = TextAlign.End)
//                                PretendardText(data.text, fontSize = fontSize, textAlign = TextAlign.Start, modifier = Modifier.offset(y= (-5).dp))
//                            }
//                        }
//                    } else {
//                        if(!line.second.isEmpty()) {
////                            PretendardText(" ", fontSize = fontSize)
//                            line.second.forEach { data->
//                                PretendardText(data.text, fontSize = fontSize)
//                            }
//                        }
//                    }
//                }
//                if(!showKoreanPronunciation) return@forEachIndexed
//                val pronunciation = textDataList.second.getOrNull(index)
//                if(pronunciation != null) {
//                    PretendardText(pronunciation, fontSize = fontSize-3, modifier = Modifier.offset(y= (-5).dp))
//                }
//            }
//        }
    }
}


private fun String.extractTextData(): Pair<List<Pair<Boolean, List<TextData>>>, List<String?>> {
    val result = mutableListOf<Pair<Boolean, List<TextData>>>()
    val regex = Regex("([\\u4E00-\\u9FFF\\u3005]+)「([\\u3040-\\u309F]+)」")
    val regex2 = Regex("＜(.*?)＞「(.*?)」")

    val str = regex.replace(this) { match ->
        val kanji = match.groupValues[1] // 한자
        val hiragana = match.groupValues[2] // 히라가나
        "＜$kanji＞「$hiragana」" // 치환 형식
    }

    // 텍스트를 줄 단위로 분리하여 처리
    for (line in str.split("\n")) {
        // 줄바꿈 처리
        if (line.replace("\t", "").isBlank()) {
            result.add(false to listOf())  // 줄바꿈 추가
            continue
        }

        val lineResult = mutableListOf<TextData>()
        var lastIndex = 0


        regex2.findAll(line).forEach { matchResult ->
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


    val pronunciations = ArrayList<String?>()
    var pronunciation = ""
    val includeJapanese = ArrayList<Boolean>()
    result.forEach { line ->
        var isIncludeJapanese = false
        var lineText = ""
        line.second.forEach { data->
            pronunciation += data.reading ?: data.text
            lineText+= data.reading?:data.text
            if(!isIncludeJapanese && (data.reading?:data.text).firstOrNull()?.isJapanese == true) {
                isIncludeJapanese = true
            }
        }
        pronunciation+="\n"
        includeJapanese.add(isIncludeJapanese)
    }
    pronunciation.convertToKoreanPronunciation().split("\n").forEachIndexed { index, it ->
        if(includeJapanese.getOrNull(index) == true) {
            pronunciations.add(it)
        } else pronunciations.add(null)
    }

    return result to pronunciations
}

data class TextData(
    val text: String,
    val reading: String? = null,
)