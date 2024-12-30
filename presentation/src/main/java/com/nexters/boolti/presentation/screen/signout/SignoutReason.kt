package com.nexters.boolti.presentation.screen.signout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTTextField
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.theme.point4

@Composable
fun SignoutReason(
    modifier: Modifier,
    viewModel: SignoutViewModel = hiltViewModel(),
) {
    val reason by viewModel.reason.collectAsStateWithLifecycle()

    Column(
        modifier = modifier,
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = marginHorizontal),
            text = stringResource(R.string.signout_reason_title),
            style = point4,
            color = MaterialTheme.colorScheme.onPrimary,
        )
        BTTextField(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp)
                .padding(horizontal = marginHorizontal),
            text = reason,
            minHeight = 160.dp,
            placeholder = stringResource(R.string.signout_reason_placeholder),
            onValueChanged = viewModel::setReason,
        )
    }
}
