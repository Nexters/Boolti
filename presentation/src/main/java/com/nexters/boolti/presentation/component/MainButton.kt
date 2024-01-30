package com.nexters.boolti.presentation.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey80

@Composable
fun MainButton(
    modifier: Modifier = Modifier,
    label: String = stringResource(id = R.string.btn_ok),
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = White, // MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = Grey80,
            disabledContentColor = Grey50,
        ),
        shape = RoundedCornerShape(4.dp),
        contentPadding = PaddingValues(12.dp),
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
