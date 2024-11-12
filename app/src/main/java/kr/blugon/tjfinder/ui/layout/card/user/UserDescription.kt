package kr.blugon.tjfinder.ui.layout.card.user

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import kr.blugon.tjfinder.module.OtherUser
import kr.blugon.tjfinder.module.User
import kr.blugon.tjfinder.ui.layout.PretendardText

@Composable
fun UserDescription(modifier: Modifier = Modifier, user: OtherUser, fontSize: Float = 15f) {
    PretendardText(
        modifier = modifier,
        text = user.description.trim(),
        fontSize = fontSize,
        overflow = TextOverflow.Ellipsis
    )
}
@Composable
fun UserDescription(modifier: Modifier = Modifier, user: User, fontSize: Float = 15f)
    = UserDescription(modifier, user.toOtherUser(), fontSize)