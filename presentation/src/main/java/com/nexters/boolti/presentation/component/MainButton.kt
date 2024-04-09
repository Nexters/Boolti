package com.nexters.boolti.presentation.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey80
import com.nexters.boolti.presentation.theme.marginHorizontal

@Composable
fun MainButton(
    modifier: Modifier = Modifier,
    label: String = stringResource(id = R.string.btn_ok),
    enabled: Boolean = true,
    disabledContentColor: Color = Grey50,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier.height(48.dp),
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = Grey80,
            disabledContentColor = disabledContentColor,
        ),
        shape = RoundedCornerShape(4.dp),
        contentPadding = PaddingValues(horizontal = marginHorizontal),
        interactionSource = remember { MutableInteractionSource() },
    ) {
        Text(text = label, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun SecondaryButton(
    modifier: Modifier = Modifier,
    label: String = stringResource(id = R.string.btn_ok),
    enabled: Boolean = true,
    disabledContentColor: Color = Grey50,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier.height(48.dp),
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Grey80,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = Grey80,
            disabledContentColor = disabledContentColor,
        ),
        shape = RoundedCornerShape(4.dp),
        contentPadding = PaddingValues(horizontal = marginHorizontal),
        interactionSource = remember { MutableInteractionSource() },
    ) {
        Text(text = label, style = MaterialTheme.typography.titleMedium)
    }
}

@Preview
@Composable
fun MainButtonPreview() {
    BooltiTheme {
        Surface {
            MainButton(label = "확인") {}
        }
    }
}
