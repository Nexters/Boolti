package com.nexters.boolti.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey15
import com.nexters.boolti.presentation.theme.Grey70

@Composable
fun <T> HorizontalStepper(
    currentItem: T,
    modifier: Modifier = Modifier,
    minusEnabled: Boolean = true,
    plusEnabled: Boolean = true,
    onClickMinus: (current: T) -> Unit,
    onClickPlus: (current: T) -> Unit,
) {
    Card(
        shape = RoundedCornerShape(4.dp),
    ) {
        Row(
            modifier = modifier
                .defaultMinSize(minWidth = 100.dp, minHeight = 32.dp)
                .background(color = MaterialTheme.colorScheme.secondaryContainer),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Image(
                modifier = Modifier.clickable(
                    enabled = minusEnabled,
                    role = Role.Button,
                    onClickLabel = stringResource(R.string.stepper_minus_description),
                    onClick = { onClickMinus(currentItem) },
                ),
                painter = painterResource(id = R.drawable.ic_stepper_minus),
                colorFilter = ColorFilter.tint(if (minusEnabled) Grey15 else Grey70),
                contentDescription = stringResource(R.string.stepper_minus_description),
            )
            Box(
                modifier = Modifier
                    .defaultMinSize(minWidth = 32.dp, minHeight = 32.dp)
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = currentItem.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            }
            Image(
                modifier = Modifier.clickable(
                    enabled = plusEnabled,
                    role = Role.Button,
                    onClickLabel = stringResource(R.string.stepper_plus_description),
                    onClick = { onClickPlus(currentItem) },
                ),
                painter = painterResource(id = R.drawable.ic_stepper_plus),
                colorFilter = ColorFilter.tint(if (plusEnabled) Grey15 else Grey70),
                contentDescription = stringResource(R.string.stepper_plus_description),
            )
        }
    }
}

@Composable
fun HorizontalCountStepper(
    modifier: Modifier = Modifier,
    minCount: Int = 1,
    maxCount: Int = Int.MAX_VALUE,
    currentCount: Int = 1,
    onClickMinus: (current: Int) -> Unit = {},
    onClickPlus: (current: Int) -> Unit = {},
) {
    HorizontalStepper(
        modifier = modifier,
        currentItem = currentCount,
        minusEnabled = currentCount != minCount,
        plusEnabled = currentCount != maxCount,
        onClickMinus = onClickMinus,
        onClickPlus = onClickPlus,
    )
}

@Preview
@Composable
private fun HorizontalCountStepperPreview() {
    var current by remember { mutableIntStateOf(1) }
    BooltiTheme {
        HorizontalCountStepper(
            modifier = Modifier.width(100.dp),
            currentCount = current,
            maxCount = 10,
            onClickPlus = { current++ },
            onClickMinus = { current-- }
        )
    }
}
