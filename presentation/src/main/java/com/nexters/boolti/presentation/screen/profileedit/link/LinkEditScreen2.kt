package com.nexters.boolti.presentation.screen.profileedit.link

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BtAppBar
import com.nexters.boolti.presentation.component.BtAppBarDefaults

@Composable
fun LinkEditScreen2(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LinkEditScreen(
        title = "링크 추가",
        linkName = "",
        url = "",
        onChangeLinkName = {},
        onChangeUrl = {},
        onComplete = {},
        navigateUp = navigateUp,
        modifier = modifier,
    )
}

@Composable
private fun LinkEditScreen(
    title: String,
    linkName: String,
    url: String,
    onChangeLinkName: (String) -> Unit,
    onChangeUrl: (String) -> Unit,
    onComplete: () -> Unit,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            BtAppBar(
                title = title,
                navigateButtons = {
                    BtAppBarDefaults.AppBarIconButton(
                        iconRes = R.drawable.ic_arrow_back,
                        onClick = navigateUp,
                    )
                },
                actionButtons = {
                    BtAppBarDefaults.AppBarTextButton(
                        label = stringResource(R.string.complete),
                        onClick = onComplete,
                    )
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            Text("링크 추가 화면")
        }
    }
}
