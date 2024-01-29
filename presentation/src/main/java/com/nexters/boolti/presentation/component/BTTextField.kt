package com.nexters.boolti.presentation.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey70

@Composable
fun BTTextField(
    text: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true,
    isError: Boolean = false,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
) {
    val interactionSource = remember { MutableInteractionSource() }
    OutlinedTextField(
        value = text,
        onValueChange = onValueChanged,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
        placeholder = {
            Text(placeholder, style = MaterialTheme.typography.bodyLarge.copy(color = Grey70))
        },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        isError = isError,
        visualTransformation = visualTransformation,
        shape = RoundedCornerShape(4.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            errorTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceTint,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceTint,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceTint,
            focusedPlaceholderColor = Grey70,
            unfocusedPlaceholderColor = Grey70,
            disabledPlaceholderColor = Grey70,
            errorPlaceholderColor = Grey70,
            focusedBorderColor = MaterialTheme.colorScheme.surfaceTint,
            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceTint,
            disabledBorderColor = MaterialTheme.colorScheme.surfaceTint,
            errorBorderColor = MaterialTheme.colorScheme.surfaceTint,
        ),
    )
}

@Preview
@Composable
fun BTTextFieldPreview() {
    BooltiTheme {
        Surface {
            BTTextField(text = "테스트", placeholder = "예매) 불티", onValueChanged = {})
        }
    }
}
