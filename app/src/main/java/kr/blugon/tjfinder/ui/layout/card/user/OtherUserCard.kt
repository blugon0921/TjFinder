package kr.blugon.tjfinder.ui.layout.card.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kr.blugon.tjfinder.module.OtherUser
import kr.blugon.tjfinder.module.User
import kr.blugon.tjfinder.ui.layout.PretendardText
import kr.blugon.tjfinder.ui.layout.navigation.ChildScreen
import kr.blugon.tjfinder.ui.layout.navigation.navigateScreen
import kr.blugon.tjfinder.ui.screen.child.user.InOtherUser
import kr.blugon.tjfinder.ui.theme.ThemeColor
import kr.blugon.tjfinder.utils.api.finder.playlists

@Composable
fun OtherUserCard(
    otherUser: OtherUser,
    isFirst: Boolean,
    isLast: Boolean,
    isFromSearch: Boolean,
    navController: NavController,
) {
    val context = LocalContext.current

    var user by remember { mutableStateOf<User?>(null) }
    var playlistSize by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        playlistSize = otherUser.playlists(context)?.size?: 0
        user = User.login(context)?: return@LaunchedEffect
    }

//    var showBottomSheet by remember { mutableStateOf(false) }

    Row(
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
            .clickable {
                InOtherUser.otherUser = otherUser
                if (isFromSearch) navController.navigateScreen(ChildScreen.SearchOtherUser)
                else navController.navigateScreen(ChildScreen.OtherUserItem)
            }
    ) {
        Column(
            modifier = Modifier.padding(0.dp, 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
            ) {
                UserProfileImage(modifier = Modifier.padding(start = 10.dp), user = otherUser) //프로필 이미지
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp)
                ) {
                    UserName( //이름
                        modifier = Modifier.fillMaxWidth(),
                        user = otherUser,
                        fontSize = 18f
                    )
                    PretendardText( //플레이리스트 개수
                        text = "공개 플레이리스트 ${playlistSize}개",
                        fontSize = 12f,
                        maxLines = 1,
                    )
                }
            }
            if(otherUser.description.isBlank()) return@Column
            UserDescription(
                modifier = Modifier.padding(top = 10.dp, start = 10.dp),
                user = otherUser,
                fontSize = 17f
            )
        }
    }
}