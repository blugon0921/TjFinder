package kr.blugon.tjfinder.ui.layout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.blugon.tjfinder.R
import kr.blugon.tjfinder.module.Lyrics
import kr.blugon.tjfinder.module.Song
import kr.blugon.tjfinder.ui.theme.ThemeColor
import my.nanihadesuka.compose.LazyColumnScrollbar

val lyricsList = mutableStateMapOf<Int, Lyrics>()
@Composable
fun LyricsLayout(
    song: Song,
    isVisible: MutableState<Boolean>
) {
    var lyrics by remember { mutableStateOf(Lyrics("")) }
    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            lyrics = song.lyrics?: Lyrics("가사 불러오기 실패")
        }
    }

    val listState = rememberLazyListState()
    AlertDialog( //팝업
        modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f),
        containerColor = ThemeColor.ItemBackground,
        shape = RoundedCornerShape(12.dp),
        onDismissRequest = { isVisible.value = false },
        text = {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    PretendardText(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        text = song.title,
                        fontSize = 17.5f,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { isVisible.value = false },
                        imageVector = ImageVector.vectorResource(R.drawable.x),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                PretendardText(song.singer, fontSize = 15f)

                LazyColumnScrollbar(
                    modifier = Modifier.fillMaxSize(),
                    listState = listState,
                    thumbColor = ThemeColor.Gray,
                    thumbSelectedColor = ThemeColor.LittleLightGray,
                    alwaysShowScrollBar = false,
                    enabled = lyrics.text != "가사 불러오기 실패"
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 20.dp),
                        state = listState,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            lyrics.Compose()
                        }
                    }
                }
            }
        },
        dismissButton = {},
        confirmButton = {}
    )
}