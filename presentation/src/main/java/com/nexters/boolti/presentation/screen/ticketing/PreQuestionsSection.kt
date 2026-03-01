package com.nexters.boolti.presentation.screen.ticketing

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.nexters.boolti.domain.model.PreQuestion
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.extension.unicodeLength
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import com.nexters.boolti.presentation.component.BTTextField
import com.nexters.boolti.presentation.theme.Grey50

@Composable
internal fun PreQuestionsSection(
    preQuestions: ImmutableList<PreQuestion>,
    answers: ImmutableMap<Long, String>,
    onAnswerChanged: (Long, String) -> Unit,
    getAnswerError: (Long) -> Boolean,
    modifier: Modifier = Modifier,
) {
    if (preQuestions.isEmpty()) return

    Section(
        modifier = modifier,
        title = stringResource(R.string.pre_questions_label),
    ) {
        preQuestions.forEachIndexed { index, question ->
            PreQuestionItem(
                question = question,
                answer = answers[question.id] ?: "",
                isError = getAnswerError(question.id),
                onAnswerChanged = { onAnswerChanged(question.id, it) },
            )
            if (index < preQuestions.lastIndex) {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun PreQuestionItem(
    question: PreQuestion,
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
            singleLine = false,
            isError = isError,
            bottomEndText = stringResource(R.string.input_limit, answer.unicodeLength(), TicketingState.MAX_ANSWER_LENGTH),
            supportingText = if (isError) {
                stringResource(R.string.input_upper_limit_text, TicketingState.MAX_ANSWER_LENGTH)
            } else null,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Default,
            ),
        )
    }
}
