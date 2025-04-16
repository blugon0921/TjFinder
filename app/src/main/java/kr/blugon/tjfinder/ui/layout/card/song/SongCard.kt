package kr.blugon.tjfinder.ui.layout.card.song

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kr.blugon.tjfinder.module.*
import kr.blugon.tjfinder.ui.layout.*
import kr.blugon.tjfinder.ui.layout.BottomSheet
import kr.blugon.tjfinder.ui.theme.ThemeColor
import kr.blugon.tjfinder.utils.isApiServerOpened


val highLightColor = Color(0xFFE9AC52)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongCard(
    song: Song,
    isFirst: Boolean = false,
    isLast: Boolean = false,
    numberPrefix: String? = null,
    highlight: String? = null,
    navController: NavController
) {
    val context = LocalContext.current

    var user by remember { mutableStateOf<User?>(null) }
    val showMemoPopup = remember { mutableStateOf(false) }
    var isApiServerOpened by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        isApiServerOpened = isApiServerOpened()
        if(!isApiServerOpened) return@LaunchedEffect
        user = User.login(context)?: return@LaunchedEffect
    }

    val showBottomSheet = remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth(0.875f)
            .padding(
                0.dp, when (isFirst) {
                    true -> 0.dp
                    false -> 17.5.dp
                }, 0.dp, when (isLast && !isFirst) {
                    true -> 17.5.dp
                    false -> 0.dp
                }
            )
            .clip(RoundedCornerShape(12.dp))
            .background(ThemeColor.ItemBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp, 10.dp, 10.dp, 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                SongCardId(song, numberPrefix, highlight)
                EllipsisOption(onClick = { //땡땡떙옵션
                    showBottomSheet.value = true
                })
            }
            SongCardTitle(song, highlight) //Title
            SongCardSinger(song, highlight, navController) //Singer

            MemoLayout(
                song = song,
                user = user,
                isVisible = showMemoPopup,
                highlight = highlight
            )
        }
    }

    val sheetState = rememberModalBottomSheetState()
    val showLyrics = remember { mutableStateOf(false) }
    if(showBottomSheet.value) {
        BottomSheet(
            sheetState = sheetState,
            onDismiss = {
                showBottomSheet.value = false
            },
            topBar = {
                Column( //이름
                    modifier = Modifier
                        .fillMaxWidth(0.925f)
                        .padding(0.dp, 10.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) { //위 왼쪽
                    PretendardText(text = song.singer, fontSize = 13f) //가수
                    Row {
                        PretendardText(text = song.title, fontSize = 20f) //제목
                        if(song.isMR) MRIcon()
                        if(song.isMV) MVIcon()
                    }
                }
            },
        ) {
            SongBottomItems(song = song, user = user, showBottomSheet = showBottomSheet, showLyrics, navController = navController)
        }
    }
    if(showLyrics.value) {
        LyricsLayout(song, showLyrics)
    }
}

@Composable
fun Top100SongCard(song: Top100Song, isFirst: Boolean = false, isLast: Boolean = false, navController: NavController) {
    SongCard(song, isFirst, isLast, "#${song.rank}", navController = navController)
}

@Composable
fun SearchSongCard(song: Song, isFirst: Boolean = false, isLast: Boolean = false, searchValue: String, navController: NavController) {
    SongCard(song, isFirst, isLast, null, searchValue, navController = navController)
}

@Composable
fun PlaylistSongCard(song: PlaylistSong, isFirst: Boolean = false, isLast: Boolean = false, navController: NavController) {
    SongCard(song, isFirst, isLast, null, null, navController = navController)
}

fun String.splitHighlight(highlight: String?): List<String> {
    if(highlight == null) return listOf(this)
    if(!this.lowercase().contains(highlight.lowercase())) return listOf(this)
    return ArrayList<String>().apply {
        this@splitHighlight.lowercase().split(highlight.lowercase()).forEach {
            val itIndex = this@splitHighlight.lowercase().indexOf(it)
            add(this@splitHighlight.substring(itIndex..<itIndex+it.length))

            val highlightIndex = this@splitHighlight.lowercase().indexOf(highlight.lowercase())
            add(this@splitHighlight.substring(highlightIndex..<highlightIndex+highlight.length))
        }
        removeLast()
        remove("")
    }
}