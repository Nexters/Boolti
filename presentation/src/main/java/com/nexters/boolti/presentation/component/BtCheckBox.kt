package com.nexters.boolti.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.theme.Grey05
import com.nexters.boolti.presentation.theme.Grey50

@Composable
fun BtCheckBox(
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    if (isSelected) {
        Icon(
            modifier = modifier
                .padding(3.dp)
                .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
            painter = painterResource(R.drawable.ic_checkbox_selected),
            tint = Grey05,
            contentDescription = null,
        )
    } else {
        Icon(
            modifier = modifier,
            painter = painterResource(R.drawable.ic_checkbox_18),
            tint = Grey50,
            contentDescription = null,
        )
    }
}
