package com.nexters.boolti.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey15
import com.nexters.boolti.presentation.theme.Grey50

@Composable
fun BtChip(
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    Surface(
        modifier = modifier,
        border = null,
        onClick = onClick,
        shape = RoundedCornerShape(100.dp),
    ) {
        Row(
            modifier = Modifier
                .defaultMinSize(minHeight = 39.dp)
                .padding(
                    start = 16.dp,
                    end = if (trailingIcon != null) 12.dp else 16.dp,
                )
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CompositionLocalProvider(
                LocalContentColor provides Grey15,
            ) {
                label()
            }
            CompositionLocalProvider(
                LocalContentColor provides Grey50,
            ) {
                trailingIcon?.invoke()
            }
        }
    }
}

@Composable
fun BtChip(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onClickClose: (() -> Unit)? = null,
) {
    BtChip(
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        onClick = onClick,
        trailingIcon = onClickClose?.let { onClick ->
            {
                Icon(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(onClick = onClick)
                        .size(18.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.ic_close),
                    contentDescription = stringResource(R.string.description_close_button),
                )
            }
        },
        modifier = modifier,
    )
}

@Preview
@Composable
private fun BtChipPreview() {
    BooltiTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            BtChip(
                label = "윤하",
            )
            BtChip(
                label = "윤하",
                onClickClose = {

                },
            )
        }
    }
}
