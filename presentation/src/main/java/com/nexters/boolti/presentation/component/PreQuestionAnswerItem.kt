package com.nexters.boolti.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nexters.boolti.domain.model.PreQuestionAnswer
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey60
import com.nexters.boolti.presentation.theme.Grey85

@Composable
fun PreQuestionAnswerItem(
    answer: PreQuestionAnswer,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(8.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = shape,
            )
            .background(Grey85)
            .padding(20.dp),
    ) {
        Row {
            Text(
                modifier = Modifier.weight(1f, fill = false),
                text = answer.question,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (answer.isRequired) {
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = "*",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        if (answer.description.isNotBlank()) {
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = answer.description,
                style = MaterialTheme.typography.bodySmall,
                color = Grey30,
            )
        }

        Row(
            modifier = Modifier
                .padding(top = 12.dp)
                .height(IntrinsicSize.Min),
        ) {
            Box(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .width(3.dp)
                    .fillMaxHeight()
                    .background(Grey60),
            )
            Text(
                text = answer.answer.ifBlank {
                    stringResource(R.string.pre_question_no_answer)
                },
                style = MaterialTheme.typography.bodyLarge,
                color = if (answer.answer.isBlank()) Grey50 else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
