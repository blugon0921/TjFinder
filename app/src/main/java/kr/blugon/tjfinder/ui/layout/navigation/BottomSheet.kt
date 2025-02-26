package kr.blugon.tjfinder.ui.layout.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kr.blugon.tjfinder.ui.theme.ThemeColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    topBar: @Composable () -> Unit,
    mainContent: @Composable () -> Unit,
) {
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(12.dp, 12.dp, 0.dp, 0.dp),
        containerColor = ThemeColor.ItemBackground,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 0.dp, 0.dp, 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            topBar()
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.DarkGray)) //Bottom Border(_________________)
            mainContent()
        }
    }
}