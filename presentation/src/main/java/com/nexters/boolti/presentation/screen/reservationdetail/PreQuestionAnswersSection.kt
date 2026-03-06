package com.nexters.boolti.presentation.screen.reservationdetail

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nexters.boolti.domain.model.PreQuestionAnswer
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.MainButton
import com.nexters.boolti.presentation.component.MainButtonDefaults
import com.nexters.boolti.presentation.component.PreQuestionAnswerItem
import com.nexters.boolti.presentation.component.PreQuestionAnswerItem
import com.nexters.boolti.presentation.theme.Grey70
import kotlinx.collections.immutable.ImmutableList
import java.time.LocalDateTime

@Composable
internal fun PreQuestionAnswersSection(
    answers: ImmutableList<PreQuestionAnswer>,
    salesEndDateTime: LocalDateTime,
    onNavigateToEdit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (answers.isEmpty()) return

    val hasAnswers = answers.any { it.answer.isNotBlank() }
    val canEdit = salesEndDateTime >= LocalDateTime.now()

    Section(
        modifier = modifier,
        title = stringResource(R.string.pre_questions_label),
        defaultExpanded = true,
    ) {
        answers.forEachIndexed { index, answer ->
            if (index > 0) {
                Spacer(modifier = Modifier.height(16.dp))
            }
            PreQuestionAnswerItem(answer = answer)
        }

        if (canEdit) {
            MainButton(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth(),
                label = stringResource(
                    if (hasAnswers) R.string.pre_question_edit_button
                    else R.string.pre_question_create_button,
                ),
                colors = MainButtonDefaults.buttonColors(
                    containerColor = if (hasAnswers) Grey70 else MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                onClick = onNavigateToEdit,
            )
        }
    }
}

