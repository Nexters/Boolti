package com.nexters.boolti.presentation.screen.prequestionedit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.domain.model.PreQuestionAnswer
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTTextField
import com.nexters.boolti.presentation.component.BtBackAppBar
import com.nexters.boolti.presentation.component.MainButton
import com.nexters.boolti.presentation.extension.unicodeLength
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.marginHorizontal
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PreQuestionEditScreen(
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PreQuestionEditViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is PreQuestionEditEvent.SaveSuccess -> onBackPressed()
                is PreQuestionEditEvent.SaveError -> Unit
            }
        }
    }

    Scaffold(
        modifier = modifier.navigationBarsPadding(),
        topBar = {
            BtBackAppBar(
                title = stringResource(R.string.pre_question_edit_title),
                onClickBack = onBackPressed,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surface),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = marginHorizontal)
                    .padding(bottom = 100.dp),
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                uiState.questions.forEachIndexed { index, question ->
                    if (index > 0) {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                    PreQuestionEditItem(
                        question = question,
                        answer = uiState.answers[question.preQuestionId] ?: "",
                        isError = uiState.isError(question.preQuestionId),
                        onAnswerChanged = { answer ->
                            viewModel.onIntent(PreQuestionEditIntent.SetAnswer(question.preQuestionId, answer))
                        },
                    )
                }
            }

            MainButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = marginHorizontal)
                    .padding(vertical = 20.dp),
                label = stringResource(R.string.complete),
                enabled = uiState.isValid && !uiState.loading,
                onClick = { viewModel.onIntent(PreQuestionEditIntent.Submit) },
            )
        }
    }
}

@Composable
private fun PreQuestionEditItem(
    question: PreQuestionAnswer,
    answer: String,
    isError: Boolean,
    onAnswerChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row {
            Text(
                text = question.question,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (question.isRequired) {
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = "*",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        if (question.description.isNotBlank()) {
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = question.description,
                style = MaterialTheme.typography.bodySmall,
                color = Grey50,
            )
        }

        BTTextField(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            text = answer,
            onValueChanged = onAnswerChanged,
            placeholder = stringResource(R.string.pre_question_placeholder),
            minHeight = 160.dp,
            isError = isError,
            bottomEndText = stringResource(
                R.string.input_limit,
                answer.unicodeLength(),
                PreQuestionEditUiState.MAX_ANSWER_LENGTH,
            ),
            supportingText = if (isError) {
                stringResource(R.string.input_upper_limit_text, PreQuestionEditUiState.MAX_ANSWER_LENGTH)
            } else null,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Default,
            ),
        )
    }
}
