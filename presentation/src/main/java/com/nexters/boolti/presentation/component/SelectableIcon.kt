package com.nexters.boolti.presentation.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.Grey80
import com.nexters.boolti.presentation.theme.Grey85

enum class SelectableSnsStatus {
    ACTIVE, INACTIVE, DISABLE
}

@Composable
fun SelectableIcon(
    @DrawableRes iconRes: Int,
    modifier: Modifier = Modifier,
    status: SelectableSnsStatus = SelectableSnsStatus.INACTIVE,
    shape: Shape = CircleShape,
    iconSize: Dp? = 28.dp,
    contentPadding: Dp = 10.dp,
    contentDescription: String? = null,
    onClick: () -> Unit = {},
) {
    val backgroundColor = remember(status) {
        when (status) {
            SelectableSnsStatus.ACTIVE,
            SelectableSnsStatus.DISABLE -> Grey85

            SelectableSnsStatus.INACTIVE -> Grey80
        }
    }

    val iconTint = remember(status) {
        when (status) {
            SelectableSnsStatus.ACTIVE,
            SelectableSnsStatus.INACTIVE -> Grey30

            SelectableSnsStatus.DISABLE -> Grey70
        }
    }

    SelectableIcon(
        status = status,
        modifier = modifier,
        shape = shape,
        backgroundColor = backgroundColor,
        contentPadding = contentPadding,
        onClick = onClick,
    ) {
        Icon(
            modifier = if (iconSize == null) Modifier.matchParentSize() else Modifier.size(iconSize),
            imageVector = ImageVector.vectorResource(iconRes),
            tint = iconTint,
            contentDescription = contentDescription,
        )
    }
}

@Composable
fun SelectableIcon(
    status: SelectableSnsStatus,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    iconTint: Color = Grey30,
    backgroundColor: Color = Grey85,
    contentPadding: Dp = 10.dp,
    onClick: () -> Unit = {},
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    icon: @Composable BoxScope.() -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = shape,
        border = if (status == SelectableSnsStatus.ACTIVE) BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary,
        ) else null,
        color = backgroundColor,
        onClick = onClick,
        contentColor = iconTint,
        interactionSource = interactionSource,
        content = {
            Box(
                modifier = Modifier.padding(contentPadding),
                contentAlignment = Alignment.Center,
            ) { icon() }
        },
    )
}

@Preview
@Composable
private fun SelectableIconPreview() {
    BooltiTheme {
        var selectedSns = remember { mutableStateOf("youtube") }

        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Low Level Composable
            SelectableIcon(
                status = SelectableSnsStatus.ACTIVE,
                onClick = { selectedSns.value = "instagram" },
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_logo_instagram),
                    tint = Grey30,
                    contentDescription = null,
                )
            }

            // High Level Composable
            // iconSize 가 null 이 아니고 modifier 에 크기를 지정하지 않으면 iconSize + contentPadding 만큼의 크기를 가진다.
            SelectableIcon(
                status = SelectableSnsStatus.INACTIVE,
                iconRes = R.drawable.ic_logo_youtube,
                iconSize = 40.dp,
                onClick = { selectedSns.value = "youtube" },
            )

            // High Level Composable
            // iconSize 가 null 이면 전체 크기에서 contentPadding을 뺀 만큼 차지한다. 이때 modifier에 크기를 지정해야 함
            SelectableIcon(
                modifier = Modifier.size(80.dp),
                status = SelectableSnsStatus.DISABLE,
                iconRes = R.drawable.ic_logo_youtube,
                iconSize = null,
                onClick = { selectedSns.value = "youtube" },
            )
        }
    }
}
