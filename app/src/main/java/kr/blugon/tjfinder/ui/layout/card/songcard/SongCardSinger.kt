package kr.blugon.tjfinder.ui.layout.card.songcard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kr.blugon.tjfinder.module.Song
import kr.blugon.tjfinder.ui.layout.BottomScreen
import kr.blugon.tjfinder.ui.screen.SearchInfo
import kr.blugon.tjfinder.ui.screen.initValue
import kr.blugon.tjfinder.ui.theme.Pretendard

@Composable
fun SongCardSinger(
    song: Song,
    navController: NavController,
    highlight: String? = null,
) {
    Row( //가수
        modifier = Modifier
//                    .fillMaxWidth()
            .padding(10.dp, 0.dp)
            .clickable {
                initValue = SearchInfo(song.singer)
                navController.navigate(BottomScreen.Search.name)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        song.singer.splitHighlight(highlight).forEach {
            Text(
                text = it,
                color = if(it.lowercase() == highlight?.lowercase()) highLightColor else Color.White,
                fontFamily = Pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = TextUnit(14f, TextUnitType.Sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}