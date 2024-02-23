package com.nexters.boolti.presentation.screen.signout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.theme.point4

@Composable
fun SignoutNotice(
    modifier: Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = marginHorizontal),
            text = stringResource(R.string.signout_notice_title),
            style = point4,
            color = MaterialTheme.colorScheme.onPrimary,
        )
        stringArrayResource(R.array.signout_notice).forEach { notice ->
            Row(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(horizontal = marginHorizontal),
            ) {
                Text(
                    text = stringResource(R.string.bullet),
                    style = MaterialTheme.typography.bodySmall,
                    color = Grey50,
                )
                Text(
                    modifier = Modifier.padding(start = 2.dp),
                    text = notice,
                    style = MaterialTheme.typography.bodySmall,
                    color = Grey50,
                )
            }
        }
    }
}
