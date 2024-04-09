package com.nexters.boolti.presentation.screen.ticket

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.MainButton

@Composable
fun TicketLoginScreen(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 28.dp)
                .fillMaxSize(),
            imageVector = ImageVector.vectorResource(R.drawable.bg_ticket_shimmer),
            contentDescription = null,
        )
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.ticket_require_login),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall,
            )
            MainButton(
                modifier = Modifier.padding(top = 28.dp),
                label = stringResource(R.string.btn_goto_login),
                onClick = onLoginClick,
            )
        }
    }
}
