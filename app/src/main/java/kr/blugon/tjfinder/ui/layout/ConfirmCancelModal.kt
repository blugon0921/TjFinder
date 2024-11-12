package kr.blugon.tjfinder.ui.layout

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import kr.blugon.tjfinder.ui.theme.Pretendard
import kr.blugon.tjfinder.ui.theme.ThemeColor


@Composable
fun ConfirmCancelModal(confirmButtonText: String = "확인", setShowModal: (Boolean) -> Unit, contents: @Composable () -> Unit = {}, confirm: () -> Unit) {
    AlertDialog( //팝업
        modifier = Modifier,
        containerColor = ThemeColor.ItemBackground,
        shape = RoundedCornerShape(12.dp),
        onDismissRequest = { setShowModal(false) },
        text = { contents() },
        dismissButton = { //취소버튼
            Button( //취소
                modifier = Modifier,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(15.dp, 10.dp),
                shape = RoundedCornerShape(100.dp),
                onClick = { setShowModal(false) },
                border = BorderStroke(1.dp, Color.Gray)
            ) {
                Text(
                    modifier = Modifier,
                    text = "취소",
                    color = Color.White,
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Bold,
                    fontSize = TextUnit(15f, TextUnitType.Sp),
                )
            }
        },
        confirmButton = { //확인버튼
            Button(
                modifier = Modifier,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ThemeColor.Main
                ),
                contentPadding = PaddingValues(15.dp, 10.dp),
                shape = RoundedCornerShape(100.dp),
                onClick = {
                    confirm()
                    setShowModal(false)
                },
            ) {
                Text(
                    modifier = Modifier,
                    text = confirmButtonText,
                    color = Color.White,
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Bold,
                    fontSize = TextUnit(15f, TextUnitType.Sp),
                )
            }
        }
    )
}

@Composable
fun TextConfirmCancelModal(defaultValue: String = "", placeHolder: String, setShowModal: (Boolean) -> Unit, confirm: (String) -> Unit) {
    val context = LocalContext.current
    var text by remember { mutableStateOf(defaultValue) }

    val keyboardManager = LocalSoftwareKeyboardController.current
    ConfirmCancelModal(
        setShowModal = setShowModal,
        contents = {
            TextField( //인풋
                value = text,
                onValueChange = {
                    if(100 < it.length) {
                        Toast.makeText(context, "입력 가능한 최대 길이는 100자입니다", Toast.LENGTH_SHORT).show()
                        return@TextField
                    }
                    text = it
                },
                textStyle = TextStyle(
                    color = Color.White,
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Medium,
                    fontSize = TextUnit(20f, TextUnitType.Sp)
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = ThemeColor.ItemBackground,
                    unfocusedContainerColor = ThemeColor.ItemBackground,
                    cursorColor = ThemeColor.LightGray,
                    focusedIndicatorColor = ThemeColor.LightGray,
                    unfocusedIndicatorColor = ThemeColor.LightGray
                ),
                singleLine = true,
                placeholder = { //Place Holder
                    Text(
                        text = placeHolder,
                        color = ThemeColor.LightGray,
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.Medium,
                        fontSize = TextUnit(18f, TextUnitType.Sp),
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { keyboardManager?.hide() }) //완료 버튼 눌렀을때
            )
        }
    ) {
        confirm(text)
    }
}