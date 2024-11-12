package kr.blugon.tjfinder.ui.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kr.blugon.tjfinder.R
import kr.blugon.tjfinder.module.SongSortType
import kr.blugon.tjfinder.module.SortType
import kr.blugon.tjfinder.ui.theme.ThemeColor
import java.util.HashMap

@Composable
fun TopBar(
    title: @Composable () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    rightButton: @Composable () -> Unit,
) {
    Row( //상단바 가운데로
        modifier = modifier
            .fillMaxWidth()
            .padding(0.dp, 14.5.dp, 0.dp, 0.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Row( //애들 양쪽으로 밀기
            modifier = Modifier
                .fillMaxWidth(0.875f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.875f)
                    .padding(0.dp, 0.dp, 10.dp, 0.dp)
                    .height(45.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ThemeColor.ItemBackground),
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon()
                title()
            }

            rightButton()
        }
    }
}
@Composable
fun TopBar(title: String, iconId: Int, modifier: Modifier, rightButton: @Composable () -> Unit) {
    TopBar(@Composable { TopBarTitle(title) }, @Composable { TopBarIcon(iconId) }, modifier, rightButton)
}

@Composable
fun <T: SortType> SortableTopBar(
    title: @Composable ()->Unit,
    icon: @Composable ()->Unit,
    isExpanded: MutableState<Boolean>,
    items: List<T>? = null,
    onClick: (T)->Unit,
) {
    TopBar(title, icon) {
        TextButton( //정렬 선택
            modifier = Modifier
                .size(45.dp)
                .padding(0.dp),
            contentPadding = PaddingValues(0.dp),
            shape = RoundedCornerShape(12.dp),
            onClick = {
                if((items?.size ?: 0) <= 0) return@TextButton
                isExpanded.value = true
            },
            colors = ButtonDefaults.buttonColors(
                containerColor =
                if(isExpanded.value) ThemeColor.Gray
                else ThemeColor.ItemBackground
            ),
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp),
                imageVector = ImageVector.vectorResource(R.drawable.sort),
                contentDescription = "sort",
                tint = Color.White
            )
            if(items == null) {
                Dropdown(
                    isExpanded = isExpanded,
                    items = ArrayList<Pair<String, SongSortType>>().apply {
                        SongSortType.entries.forEach { type ->
                            add(type.displayName to type)
                        }
                    }
                ) { onClick(it as T) }
            } else {
                Dropdown(
                    isExpanded = isExpanded,
                    items = ArrayList<Pair<String, T>>().apply {
                        items.forEach { type ->
                            add(type.displayName to type)
                        }
                    }
                ) { onClick(it) }
            }
        }
    }
}
@Composable
fun <T: SortType> SortableTopBar(
    title: String,
    iconId: Int,
    isExpanded: MutableState<Boolean>,
    items: List<T>?,
    onClick: (T)->Unit
) {
    SortableTopBar(
        title = { TopBarTitle(title) },
        icon = { TopBarIcon(iconId) },
        isExpanded = isExpanded,
        items = items,
        onClick = onClick
    )
}
@Composable
fun SortableTopBar(
    title: String,
    iconId: Int,
    isExpanded: MutableState<Boolean>,
    onClick: (SongSortType)->Unit
) {
    SortableTopBar(
        title = title,
        iconId = iconId,
        isExpanded = isExpanded,
        items = null,
        onClick = onClick
    )
}


@Composable
fun TopBarTitle(title: String) {
    PretendardText(
        modifier = Modifier.padding(10.dp),
        text = title,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18f
    )
}
@Composable
fun TopBarIcon(iconId: Int) {
    Icon(
        modifier = Modifier
            .size(34.dp)
            .padding(10.dp, 0.dp, 0.dp, 0.dp),
        imageVector = ImageVector.vectorResource(iconId),
        contentDescription = "$iconId",
        tint = Color.White,
    )
}