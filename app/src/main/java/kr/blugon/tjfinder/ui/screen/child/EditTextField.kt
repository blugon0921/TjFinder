package kr.blugon.tjfinder.ui.screen.child

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kr.blugon.tjfinder.ui.layout.PretendardText
import kr.blugon.tjfinder.ui.layout.PretendardTextStyle
import kr.blugon.tjfinder.ui.theme.ThemeColor

@Composable
fun EditTextField(
    label: String,
    maxLength: Int,
    value: MutableState<String>,
    isEssential: Boolean = true,
    keyboardManager: SoftwareKeyboardController?
) {
    val context = LocalContext.current

    OutlinedTextField(
        modifier = Modifier
            .padding(bottom = 5.dp)
            .fillMaxWidth(0.95f),
        value = value.value,
        onValueChange = {
            if(maxLength < it.length) {
                Toast.makeText(context, "입력 가능한 최대 길이는 ${maxLength}자입니다", Toast.LENGTH_SHORT).show()
                return@OutlinedTextField
            }
            value.value = it
        },
        textStyle = PretendardTextStyle(),
        label = {
            PretendardText(
                text = label,
                color = if(isEssential && value.value.isBlank()) ThemeColor.RedGray else ThemeColor.LightGray,
                fontSize = 18f,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { keyboardManager?.hide() }),
        isError = isEssential && value.value.isBlank(),
    )
}