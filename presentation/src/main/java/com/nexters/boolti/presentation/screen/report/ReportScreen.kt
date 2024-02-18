package com.nexters.boolti.presentation.screen.report

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTTextField
import com.nexters.boolti.presentation.component.BtAppBar
import com.nexters.boolti.presentation.component.MainButton
import com.nexters.boolti.presentation.theme.Grey10
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.Grey85
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.theme.point4

@Composable
fun ReportScreen(
    onBackPressed: () -> Unit,
    popupToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReportViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    val interactionSource = remember { MutableInteractionSource() }
    val context = LocalContext.current
    val finishedReportMessage = stringResource(id = R.string.report_finished)

    Scaffold(
        modifier = modifier.clickable(
            interactionSource = interactionSource,
            indication = null
        ) {
            keyboardController?.hide()
        },
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
            BTTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = marginHorizontal)
                    .height(160.dp)
                    .padding(top = 20.dp),
                text = uiState.reason,
                placeholder = stringResource(id = R.string.report_reason_hint),
                onValueChanged = viewModel::updateReason,
            )
            Spacer(modifier = Modifier.weight(1.0f))
            MainButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = marginHorizontal)
                    .padding(bottom = 8.dp),
                onClick = {
                    popupToHome()
                    Toast.makeText(context, finishedReportMessage, Toast.LENGTH_LONG).show()
                },
                enabled = uiState.reportable,
                label = stringResource(id = R.string.report)
            )
        }
    }
}