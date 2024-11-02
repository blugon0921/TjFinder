package kr.blugon.tjfinder.ui.layout.card.songcard

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kr.blugon.tjfinder.R
import kr.blugon.tjfinder.module.*
import kr.blugon.tjfinder.module.BlugonTJApi.memoMap
import kr.blugon.tjfinder.ui.layout.*
import kr.blugon.tjfinder.ui.theme.ThemeColor


val highLightColor = Color(0xFFE9AC52)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongCard(
    song: Song,
    isFirst: Boolean = false,
    isLast: Boolean = false,
    navController: NavHostController,
    numberPrefix: String? = null,
    highlight: String? = null,
) {
    val context = LocalContext.current

    var user by remember { mutableStateOf<User?>(null) }
    var memo by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        user = User.login(context)?: return@LaunchedEffect
        user!!.memoMap()?.forEach { (songId, memoValue) ->
            if(songId == song.id) {
                memo = memoValue
                return@forEach
            }
        }
    }

    var showBottomSheet by remember { mutableStateOf(false) }
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
                    showBottomSheet = true
                })
            }
            SongCardTitle(song, highlight) //Title
            SongCardSinger(song, navController, highlight) //Singer

            var showMemoPopup by remember { mutableStateOf(false) }
            EditMemoPopup(
                song = song,
                user = user,
                isVisible = showMemoPopup,
                setVisible = {showMemoPopup = it},
                memo = memo,
                setMemo = {memo = it},
                highlight = highlight
            )
        }
    }


    val sheetState = rememberModalBottomSheetState()
    if(showBottomSheet) {
        BottomSheet(
            sheetState = sheetState,
            onDismiss = {
                showBottomSheet = false
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
                        if(song.isMR) {
                            PretendardText(
                                modifier = Modifier.width(45.dp).padding(start = 7.dp).clip(RoundedCornerShape(5.dp)).background(Color(0xFFff4a01)),
                                text = "MR",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18f,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            },
        ) {
            SongBottomItems(song = song, user = user!!, setShowBottomSheet = {showBottomSheet = it}, navController = navController)
        }
    }
}

@Composable
fun Top100SongCard(song: Top100Song, isFirst: Boolean = false, isLast: Boolean = false, navController: NavHostController) {
    SongCard(song, isFirst, isLast, navController, "#${song.top}")
}

@Composable
fun SearchSongCard(song: Song, isFirst: Boolean = false, isLast: Boolean = false, navController: NavHostController, searchValue: String) {
    SongCard(song, isFirst, isLast, navController, null, searchValue)
}

@Composable
fun PlaylistSongCard(song: PlaylistSong, isFirst: Boolean = false, isLast: Boolean = false, navController: NavHostController) {
    SongCard(song, isFirst, isLast, navController, null, null)
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