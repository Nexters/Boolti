package com.nexters.boolti.presentation.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey05
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey80
import com.nexters.boolti.presentation.theme.Grey85

@Composable
fun SmallButton(
    label: String,
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int? = null,
    backgroundColor: Color = Grey85,
    iconTint: Color = Grey50,
    labelStyle: TextStyle = MaterialTheme.typography.labelMedium.copy(color = Grey05),
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clip(shape = RoundedCornerShape(4.dp))
            .clickable(onClick = onClick)
            .height(30.dp)
            .background(color = backgroundColor)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        iconRes?.let { id ->
            Icon(
                modifier = Modifier.padding(end = 6.dp),
                imageVector = ImageVector.vectorResource(id = id),
                tint = iconTint,
                contentDescription = label,
            )
        }
        Text(
            text = label,
            style = labelStyle,
        )
    }
}

@Preview
@Composable
private fun CopyButtonPreview() {
    BooltiTheme {
        SmallButton(
            label = stringResource(R.string.ticketing_copy_address),
            iconRes = R.drawable.ic_copy,
        ) { }
    }
}

@Preview
@Composable
private fun LoginButtonPreview() {
    BooltiTheme {
        SmallButton(
            label = stringResource(R.string.login),
            backgroundColor = Grey80,
        ) { }
    }
}
