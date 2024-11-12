package kr.blugon.tjfinder.ui.layout.card.user

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import kr.blugon.tjfinder.module.OtherUser
import kr.blugon.tjfinder.module.User
import kr.blugon.tjfinder.ui.layout.PretendardSpanStyle

@Composable
fun UserName(modifier: Modifier = Modifier, user: OtherUser, fontSize: Float = 23f) {
    Text(buildAnnotatedString { //닉네임
        withStyle(style = PretendardSpanStyle(fontSize = fontSize)) { append(user.name) }
        withStyle(style = PretendardSpanStyle( //태그
            color = Color.Gray,
            fontSize = fontSize*0.74f
        )) { append("#${user.tag}") }
    }, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = modifier)
}
@Composable
fun UserName(modifier: Modifier = Modifier, user: User, fontSize: Float = 23f)
    = UserName(modifier, user.toOtherUser(), fontSize)