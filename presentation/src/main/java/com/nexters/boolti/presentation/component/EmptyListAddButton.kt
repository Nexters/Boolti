package com.nexters.boolti.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.marginHorizontal

@Composable
fun EmptyListAddButton(
    onClickAdd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DashedBorderBox(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                vertical = 20.dp,
                horizontal = marginHorizontal
            )
            .clip(RoundedCornerShape(4.dp))
            .clickable(onClick = onClickAdd),
        cornerRadius = 4.dp,
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 34.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_stepper_plus),
                contentDescription = null,
            )
            Text(
                text = stringResource(R.string.do_add),
                style = MaterialTheme.typography.bodyLarge,
                color = Grey30,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
