package kr.blugon.tjfinder.ui.layout

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import kr.blugon.tjfinder.R

@Composable
fun EllipsisOption(onClick: () -> Unit) {
    Icon( //땡땡떙옵션 (위 오른쪽)
        modifier = Modifier
            .animateContentSize()
            .size(24.dp)
            .clickable(onClick = onClick),
        imageVector = ImageVector.vectorResource(R.drawable.ellipsis),
        contentDescription = "ellipsisOption",
        tint = Color.White
    )
}