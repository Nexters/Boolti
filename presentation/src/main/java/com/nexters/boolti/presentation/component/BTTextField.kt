package com.nexters.boolti.presentation.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Error
import com.nexters.boolti.presentation.theme.Grey70

@OptIn(ExperimentalMaterial3Api::class)
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
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        errorTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedContainerColor = MaterialTheme.colorScheme.surfaceTint,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceTint,
        disabledContainerColor = MaterialTheme.colorScheme.surfaceTint,
        errorContainerColor = MaterialTheme.colorScheme.surfaceTint,
        focusedPlaceholderColor = Grey70,
        unfocusedPlaceholderColor = Grey70,
        disabledPlaceholderColor = Grey70,
        errorPlaceholderColor = Grey70,
        focusedBorderColor = MaterialTheme.colorScheme.surfaceTint,
        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceTint,
        disabledBorderColor = MaterialTheme.colorScheme.surfaceTint,
        errorBorderColor = Error,
    ),
    supportingText: String? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(4.dp)

    BasicTextField(
        value = text,
        onValueChange = onValueChanged,
        modifier = modifier.defaultMinSize(minHeight = 48.dp, minWidth = OutlinedTextFieldDefaults.MinWidth),
        enabled = enabled,
        readOnly = readOnly,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
        cursorBrush = SolidColor(if (isError) colors.errorCursorColor else colors.cursorColor),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        visualTransformation = visualTransformation,
        interactionSource = interactionSource,
        decorationBox = { innerTextField ->
            OutlinedTextFieldDefaults.DecorationBox(
                value = text,
                visualTransformation = visualTransformation,
                innerTextField = innerTextField,
                placeholder = {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyLarge.copy(color = Grey70),
                    )
                },
                singleLine = singleLine,
                enabled = enabled,
                isError = isError,
                interactionSource = interactionSource,
                colors = colors,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                container = {
                    OutlinedTextFieldDefaults.ContainerBox(
                        shape = shape,
                        enabled = enabled,
                        isError = isError,
                        interactionSource = interactionSource,
                        colors = colors,
                    )
                },
                supportingText = supportingText?.let { text ->
                    {
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                },
            )
        },
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
