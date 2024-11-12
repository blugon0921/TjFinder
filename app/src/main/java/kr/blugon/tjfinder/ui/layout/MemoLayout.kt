package kr.blugon.tjfinder.ui.layout

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kr.blugon.tjfinder.R
import kr.blugon.tjfinder.module.BlugonTJApi.memoList
import kr.blugon.tjfinder.module.BlugonTJApi.removeMemo
import kr.blugon.tjfinder.module.BlugonTJApi.setMemo
import kr.blugon.tjfinder.module.Song
import kr.blugon.tjfinder.module.User
import kr.blugon.tjfinder.module.memoList
import kr.blugon.tjfinder.ui.layout.card.song.highLightColor
import kr.blugon.tjfinder.ui.layout.card.song.splitHighlight
import kr.blugon.tjfinder.ui.theme.Pretendard

@Composable
fun MemoLayout(
    song: Song,
    user: User?,
    isVisible: MutableState<Boolean>,
//    memo: MutableState<String>,
    highlight: String? = null,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

//    val memo = remember { mutableStateOf("") }
//    val showMemoPopup = remember { mutableStateOf(false) }
//    LaunchedEffect(user) {
//        memo.value = user?.getMemo(song)?: ""
//    }

    Row(
        modifier = Modifier
            .padding(10.dp, 5.dp, 0.dp, 10.dp)
            .clickable {
                isVisible.value = true
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon( // 메모 아이콘
            modifier = Modifier
                .size(21.dp),
            imageVector = ImageVector.vectorResource(R.drawable.memo),
            contentDescription = "memo",
            tint = Color.White
        )
        Box( //스크롤 애니메이션 블러
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(1.dp, 0.dp)
                .graphicsLayer { alpha = 0.99f }
                .horizontalScrollAnimeBlur()
        ) {
            Row( //스크롤 애니메이션
                modifier = Modifier
                    .basicMarquee(iterations = Int.MAX_VALUE, velocity = 60.dp, repeatDelayMillis = 3000)
                    .padding(start = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Absolute.Left
            ) { //위 왼쪽
//                memo.value.splitHighlight(highlight).forEach {
                song.memo?.splitHighlight(highlight)?.forEach {
                    Text(
                        text = it,
                        color = if(it.lowercase() == highlight?.lowercase()) highLightColor else Color.White,
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.Medium,
                        fontSize = TextUnit(14f, TextUnitType.Sp),
                        maxLines = 1
                    )
                }
            }
        }
        if(isVisible.value) {
//            TextConfirmCancelModal(defaultValue = memo.value, placeHolder = "메모", setShowModal = {isVisible.value = it}) {
            TextConfirmCancelModal(defaultValue = song.memo?: "", placeHolder = "메모", setShowModal = {isVisible.value = it}) {
                coroutineScope.launch {
                    if(user!!.memoList() == null) return@launch
                    memoList[song.id] = it //클라이언트 먼저 반영
                    if(it.isBlank()) user.removeMemo(song.id) //서버 업로드
                    else user.setMemo(song.id, it.trim()) //서버 업로드2
                }
            }
        }
    }
}