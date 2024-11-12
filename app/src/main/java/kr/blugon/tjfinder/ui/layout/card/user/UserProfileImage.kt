package kr.blugon.tjfinder.ui.layout.card.user

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kr.blugon.tjfinder.module.OtherUser
import kr.blugon.tjfinder.module.User

@Composable
fun UserProfileImage(modifier: Modifier = Modifier, user: OtherUser, size: Dp = 60.dp) {
    val context = LocalContext.current

    Box(modifier = modifier) { //박스
        AsyncImage( //프로필사진
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .border(2.dp, Color(255, 255, 255, 64), CircleShape),
            model = ImageRequest.Builder(context)
                .data(Uri.parse(user.photoUrl))
                .build(),
            contentDescription = "profile",
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center
        )
    }
}
@Composable
fun UserProfileImage(modifier: Modifier = Modifier, user: User, size: Dp = 60.dp)
    = UserProfileImage(modifier, user.toOtherUser(), size)