package com.nexters.boolti.presentation.screen.signout

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BtBackAppBar
import com.nexters.boolti.presentation.component.MainButton
import com.nexters.boolti.presentation.theme.marginHorizontal

@Composable
fun SignoutScreen(
    navigateToHome: () -> Unit,
    navigateBack: () -> Unit,
    viewModel: SignoutViewModel = hiltViewModel(),
) {
    var firstPage by remember { mutableStateOf(true) }
    val reason by viewModel.reason.collectAsStateWithLifecycle()

    BackHandler {
        if (firstPage) navigateBack() else firstPage = true
    }

    LaunchedEffect(viewModel.event) {
        viewModel.event.collect {
            when (it) {
                SignoutEvent.SignoutSuccess -> navigateToHome()
            }
        }
    }

    Scaffold(
        modifier = Modifier.navigationBarsPadding(),
        topBar = { BtBackAppBar(title = stringResource(R.string.signout), onClickBack = navigateBack) },
        bottomBar = {
            MainButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = marginHorizontal)
                    .padding(bottom = 42.dp),
                label = if (firstPage) stringResource(R.string.next) else stringResource(R.string.signout_button),
                enabled = firstPage || reason.isNotBlank(),
                onClick = {
                    if (firstPage) firstPage = false else viewModel.signout()
                },
            )
        }
    ) { innerPadding ->
        if (firstPage) {
            SignoutNotice(modifier = Modifier.padding(innerPadding))
        } else {
            SignoutReason(modifier = Modifier.padding(innerPadding))
        }
    }
}
