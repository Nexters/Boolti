package com.nexters.boolti.presentation.screen.report

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BtAppBar
import com.nexters.boolti.presentation.component.MainButton
import com.nexters.boolti.presentation.theme.Grey10
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.Grey85
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.theme.point2
import com.nexters.boolti.presentation.theme.point4

@Composable
fun ReportScreen(
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReportViewModel = hiltViewModel(),
) {
    val reason by viewModel.reason.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            BtAppBar(title = stringResource(id = R.string.report), onBackPressed = onBackPressed)
        }
    ) { innerPadding ->
        Column(
            modifier = modifier.padding(innerPadding)
        ) {
            Text(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .padding(horizontal = marginHorizontal),
                text = stringResource(id = R.string.report_sub_title),
                style = point4,
            )
            Text(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .padding(horizontal = marginHorizontal),
                text = stringResource(id = R.string.report_description),
                style = MaterialTheme.typography.bodyLarge.copy(color = Grey30),
            )
            TextField(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = marginHorizontal)
                .height(160.dp)
                .padding(top = 20.dp)
                .clip(shape = RoundedCornerShape(4.dp)),
                value = reason,
                onValueChange = viewModel::updateReason,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Grey10),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Grey85,
                    unfocusedContainerColor = Grey85,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(4.dp),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.report_reason_hint),
                        style = MaterialTheme.typography.bodyLarge.copy(color = Grey70),
                    )
                })

            Spacer(modifier = Modifier.weight(1.0f))
            MainButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = marginHorizontal)
                    .padding(bottom = 8.dp), onClick = { /* TODO */ }, enabled = true, // TODO 입력 여부
                label = stringResource(id = R.string.report)
            )
        }
    }
}