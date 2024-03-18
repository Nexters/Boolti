package com.nexters.boolti.presentation.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.theme.Grey10
import com.nexters.boolti.presentation.theme.marginHorizontal

@Composable
fun BtAppBar(
    title: String,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes navIconRes: Int = R.drawable.ic_arrow_back,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(color = MaterialTheme.colorScheme.background),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            modifier = Modifier.size(width = 48.dp, height = 44.dp), onClick = onBackPressed
        ) {
            Icon(
                modifier = modifier
                    .padding(start = marginHorizontal)
                    .size(width = 24.dp, height = 24.dp),
                tint = Grey10,
                painter = painterResource(navIconRes),
                contentDescription = stringResource(id = R.string.description_navigate_back),
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(color = Grey10),
        )
    }
}