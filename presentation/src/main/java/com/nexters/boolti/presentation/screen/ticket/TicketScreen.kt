package com.nexters.boolti.presentation.screen.ticket

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexters.boolti.presentation.R

@Composable
fun TicketScreen(
    modifier: Modifier = Modifier,
    viewModel: TicketViewModel = hiltViewModel(),
    requireLogin: (screenName: String) -> Unit,
) {
    val loggedIn by viewModel.loggedIn.collectAsState()

    if (loggedIn == true) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "티켓 목록 화면", style = MaterialTheme.typography.headlineMedium)
        }
    } else if(loggedIn == false) {
        val currentScreenName = stringResource(id = R.string.menu_tickets)
        TextButton(onClick = {
            requireLogin(currentScreenName)
        }) {
            Text("로그인 하러 가기")
        }
    }
}
