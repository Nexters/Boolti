package com.nexters.boolti.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Error
import com.nexters.boolti.presentation.theme.Grey70

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BTTextField(
    text: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    supportingText: String? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    readOnly: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
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
        errorLabelColor = Error,
    ),
    supportingText: String? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(4.dp)

    val textColor = textStyle.color.takeOrElse {
        colors.textColor(enabled, isError, interactionSource).value
    }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))
    CompositionLocalProvider(LocalTextSelectionColors provides colors.textSelectionColors) {
        ConstraintLayout(modifier = modifier.defaultMinSize(minWidth = OutlinedTextFieldDefaults.MinWidth)) {
            val (textFieldRef, supportingTextRef) = createRefs()
            BasicTextField(
                value = text,
                onValueChange = onValueChanged,
                modifier = Modifier
                    .defaultMinSize(minHeight = 48.dp, minWidth = OutlinedTextFieldDefaults.MinWidth)
                    .constrainAs(textFieldRef) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        width = Dimension.fillToConstraints
                    },
                enabled = enabled,
                readOnly = readOnly,
                textStyle = mergedTextStyle,
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
                        placeholder = placeholder?.let {
                            { Text(text = placeholder, style = MaterialTheme.typography.bodyLarge) }
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
            supportingText?.let {
                val supportingTextColor =
                    colors.supportingTextColor(enabled, isError, interactionSource).value
                Text(
                    modifier = Modifier.constrainAs(supportingTextRef) {
                        start.linkTo(textFieldRef.start)
                        end.linkTo(textFieldRef.end)
                        top.linkTo(textFieldRef.bottom, 12.dp)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                    },
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Start,
                    color = supportingTextColor,
                )
            }
        }
    }
}

@Composable
private fun TextFieldColors.textColor(
    enabled: Boolean,
    isError: Boolean,
    interactionSource: InteractionSource
): State<Color> {
    val focused by interactionSource.collectIsFocusedAsState()

    val targetValue = when {
        !enabled -> disabledTextColor
        isError -> errorTextColor
        focused -> focusedTextColor
        else -> unfocusedTextColor
    }
    return rememberUpdatedState(targetValue)
}

@Composable
private fun TextFieldColors.supportingTextColor(
    enabled: Boolean,
    isError: Boolean,
    interactionSource: InteractionSource
): State<Color> {
    val focused by interactionSource.collectIsFocusedAsState()

    return rememberUpdatedState(
        when {
            !enabled -> disabledSupportingTextColor
            isError -> errorSupportingTextColor
            focused -> focusedSupportingTextColor
            else -> unfocusedSupportingTextColor
        }
    )
}

@Preview
@Composable
fun BTTextFieldPreview() {
    BooltiTheme {
        Surface {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                BTTextField(
                    text = "테스트",
                    placeholder = "예매) 불티",
                    onValueChanged = {}
                )
                BTTextField(
                    text = "테스트",
                    placeholder = "예매) 불티",
                    isError = true,
                    supportingText = "에러!!에러!!에러!!에러!!에러!!에러!!에러!!에러!!에러!!에러!!에러!!에러!!",
                    onValueChanged = {}
                )
            }
        }
    }
}
