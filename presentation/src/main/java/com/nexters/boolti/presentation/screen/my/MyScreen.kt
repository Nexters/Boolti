package com.nexters.boolti.presentation.screen.my

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.theme.Grey10
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.marginHorizontal

@Composable
fun MyScreen(
    requireLogin: () -> Unit,
    navigateToReservations: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchMyInfo()
    }

    LaunchedEffect(uiState) {
        if (uiState is MyUiState.Failure) requireLogin()
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = marginHorizontal)
                .padding(top = 40.dp, bottom = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(70.dp)
                    .clip(shape = RoundedCornerShape(100.dp)),
                model = "https://picsum.photos/200", contentDescription = null
            )
            Column(
                modifier = Modifier.padding(start = 12.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(text = "김불티 Kim Boolti", style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "boolti1234@gmail.com",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Grey30),
                )
            }
        }

        MyButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.my_ticketing_history),
            onClick = navigateToReservations,
        )
        Spacer(modifier = Modifier.height(12.dp))
        MyButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.my_scan_qr),
            onClick = {})

        Spacer(modifier = Modifier.weight(1.0f))
        Text(
            modifier = Modifier
                .padding(bottom = 40.dp)
                .clickable(onClick = viewModel::logout),
            text = stringResource(id = R.string.my_logout),
            style = MaterialTheme.typography.bodySmall.copy(color = Grey50),
            textDecoration = TextDecoration.Underline,
        )
    }
}

@Composable
private fun MyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(horizontal = marginHorizontal, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge.copy(color = Grey10),
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = null,
            tint = Grey50,
        )
    }
}