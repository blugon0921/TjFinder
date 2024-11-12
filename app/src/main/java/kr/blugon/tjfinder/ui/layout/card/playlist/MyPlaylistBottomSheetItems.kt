package kr.blugon.tjfinder.ui.layout.card.playlist

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import kr.blugon.tjfinder.R
import kr.blugon.tjfinder.module.BlugonTJApi.deletePlaylist
import kr.blugon.tjfinder.module.BlugonTJApi.editIsPrivateOfPlaylist
import kr.blugon.tjfinder.module.MyPlaylist
import kr.blugon.tjfinder.module.User
import kr.blugon.tjfinder.ui.layout.BottomSheetItem
import kr.blugon.tjfinder.ui.layout.ChildScreen
import kr.blugon.tjfinder.ui.layout.ConfirmCancelModal
import kr.blugon.tjfinder.ui.layout.navigateScreen
import kr.blugon.tjfinder.ui.screen.child.playlist.EditPlaylist
import kr.blugon.tjfinder.ui.theme.Pretendard
import kr.blugon.tjfinder.ui.theme.ThemeColor

@Composable
fun MyPlaylistBottomSheetItems(
    playlist: MyPlaylist,
    user: User,
    isPrivate: () -> Boolean,
    setIsPrivate: (Boolean) -> Unit,
    reload: () -> Unit,
    setShowBottomSheet: (Boolean) -> Unit,
    navController: NavController
) {
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    BottomSheetItem(iconId = R.drawable.pen, iconDescription = "editPen", text = "편집") { //편집
        EditPlaylist.playlist = playlist
        navController.navigateScreen(ChildScreen.EditPlaylist)
    }
    var showDeleteModal by remember { mutableStateOf(false) }
    BottomSheetItem(iconId = R.drawable.x, iconDescription = "delete", text = "삭제") { //삭제
        showDeleteModal = true
    }
    if(showDeleteModal) { //삭제 확인 팝업
        ConfirmCancelModal(
            confirmButtonText = "삭제",
            setShowModal = {showDeleteModal = it},
            contents = {
                Column {
                    Text(buildAnnotatedString { //확인
                        withStyle(style = SpanStyle(
                            color = Color.Gray,
                            fontFamily = Pretendard,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = TextUnit(18f, TextUnitType.Sp),
                        )
                        ) { append("정말로 '") }
                        withStyle(style = SpanStyle( //플레이리스트 제목
                            color = Color.White,
                            fontFamily = Pretendard,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = TextUnit(18f, TextUnitType.Sp)
                        )
                        ) { append(playlist.title) }
                        withStyle(style = SpanStyle( //플레이리스트 ID
                            color = Color.LightGray,
                            fontFamily = Pretendard,
                            fontWeight = FontWeight.Medium,
                            fontSize = TextUnit(15f, TextUnitType.Sp)
                        )
                        ) { append("#${playlist.id}") }
                        withStyle(style = SpanStyle(
                            color = Color.Gray,
                            fontFamily = Pretendard,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = TextUnit(18f, TextUnitType.Sp),
                        )
                        ) { append("' 을(를) 삭제하시겠습니까?") }
                    })
                    Text( //경고
                        text = "삭제 시 복구할 수 없습니다",
                        color = Color(200, 64, 64),
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.Medium,
                        fontSize = TextUnit(15f, TextUnitType.Sp),
                    )
                }
            }
        ) {
            coroutineScope.launch {
                showDeleteModal = false
                val isDeleted = user.deletePlaylist(playlist)
                Toast.makeText(context, "플레이리스트를 ${
                    if(isDeleted) "삭제했습니다"
                    else "삭제하는데 실패했습니다"
                }", Toast.LENGTH_SHORT).show()
                if(isDeleted) reload()
                setShowBottomSheet(false)
            }
        }
    }
    BottomSheetItem(iconId = R.drawable.share, iconDescription = "share", text = "공유") { //공유
        clipboardManager.setText(AnnotatedString(playlist.id))
//        Toast.makeText(context, "클립보드에 플레이리스트 코드를 복사했습니다", Toast.LENGTH_SHORT).show()
        setShowBottomSheet(false)
    }

    fun switchPrivate() { //공개 비공개 전환 함수
        setIsPrivate(!isPrivate())
        val word = if(isPrivate()) "비공개" else "공개"
        coroutineScope.launch {
            val isChanged = user.editIsPrivateOfPlaylist(playlist, isPrivate())
            if(!isChanged) {
                setIsPrivate(!isPrivate())
                Toast.makeText(context, "플레이리스트를 ${word}상태로 전환하는데 실패했습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }
    Box( //공개 비공개 전환
        modifier = Modifier
            .fillMaxWidth()
            .clickable { switchPrivate() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9125f)
                .padding(0.dp, 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(0.925f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Absolute.Left
                ) {
                    Icon( //아이콘
                        modifier = Modifier.size(25.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.lock),
                        contentDescription = "private",
                        tint = Color.Gray,
                    )
                    Text( //텍스트
                        modifier = Modifier.padding(10.dp, 0.dp, 0.dp, 0.dp),
                        text = "비공개",
                        color = Color.White,
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.Medium,
                        fontSize = TextUnit(15f, TextUnitType.Sp),
                    )
                }
                Switch(
                    modifier = Modifier.fillMaxHeight(0.05f),
                    checked = isPrivate(),
                    onCheckedChange = { switchPrivate() },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = ThemeColor.Main,
                        uncheckedTrackColor = ThemeColor.MainGray,
                        uncheckedBorderColor = ThemeColor.MainGray,
                        uncheckedThumbColor = ThemeColor.Main,
                        checkedThumbColor = Color.White
                    )
                )
            }
        }
    }
}