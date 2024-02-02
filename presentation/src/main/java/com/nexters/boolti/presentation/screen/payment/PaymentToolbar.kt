package com.nexters.boolti.presentation.screen.payment

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.theme.BooltiTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentToolbar(
    onClickHome: () -> Unit,
    onClickClose: () -> Unit,
) {
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = onClickHome) {
                Icon(
                    painter = painterResource(R.drawable.ic_home),
                    contentDescription = stringResource(R.string.description_toolbar_home),
                )
            }
        },
        actions = {
            IconButton(onClick = onClickClose) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = stringResource(R.string.description_close_button),
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
    )
}

@Preview
@Composable
fun PaymentToolbarPreview() {
    BooltiTheme {
        Surface {
            PaymentToolbar({}, {})
        }
    }
}
