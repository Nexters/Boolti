package com.nexters.boolti.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.extension.takeForUnicode
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Error
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.Grey80
import okio.utf8Size

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BTTextField(
    text: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    supportingText: String? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    bottomEndText: String? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    readOnly: Boolean = false,
    minHeight: Dp = 48.dp,
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
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val shape = RoundedCornerShape(4.dp)

    val textColor = textStyle.color.takeOrElse {
        colors.textColor(enabled, isError, interactionSource).value
    }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))
    CompositionLocalProvider(LocalTextSelectionColors provides colors.textSelectionColors) {
        ConstraintLayout(modifier = modifier.defaultMinSize(minWidth = OutlinedTextFieldDefaults.MinWidth)) {
            val (textFieldRef, bottomEndTextRef, supportingTextRef) = createRefs()
            BasicTextField(
                value = text,
                onValueChange = onValueChanged,
                modifier = Modifier
                    .defaultMinSize(
                        minHeight = minHeight.coerceAtLeast(48.dp),
                        minWidth = OutlinedTextFieldDefaults.MinWidth
                    )
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
                        contentPadding = if (bottomEndText != null) {
                            PaddingValues(
                                start = 12.dp,
                                end = 12.dp,
                                top = 12.dp,
                                bottom = (12 + 18 + 8).dp
                            )
                        } else {
                            PaddingValues(horizontal = 12.dp, vertical = 12.dp)
                        },
                        trailingIcon = trailingIcon,
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
            )
            bottomEndText?.let {
                Text(
                    modifier = Modifier
                        .padding(top = 8.dp, start = 12.dp, end = 12.dp, bottom = 12.dp)
                        .constrainAs(bottomEndTextRef) {
                            end.linkTo(textFieldRef.end)
                            bottom.linkTo(textFieldRef.bottom)
                        },
                    text = it,
                    style = MaterialTheme.typography.labelMedium,
                    color = Grey70,
                )
            }
            supportingText?.let {
                val supportingTextColor =
                    colors.supportingTextColor(enabled, isError, interactionSource).value
                Text(
                    modifier = Modifier.constrainAs(supportingTextRef) {
                        start.linkTo(textFieldRef.start)
                        end.linkTo(textFieldRef.end)
                        top.linkTo(textFieldRef.bottom, 8.dp)
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

object BTTextFieldDefaults {
    @Composable
    fun ClearButton(
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        iconColor: Color = Grey80,
        bgColor: Color = Grey30,
    ) {
        Icon(
            modifier = modifier
                .size(20.dp)
                .drawBehind {
                    drawCircle(color = bgColor)
                }
                .padding(4.dp)
                .clickable(onClick = onClick),
            imageVector = Icons.Rounded.Clear,
            tint = iconColor,
            contentDescription = stringResource(R.string.description_clear_button),
        )
    }
}

@Composable
private fun TextFieldColors.textColor(
    enabled: Boolean,
    isError: Boolean,
    interactionSource: InteractionSource,
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
    interactionSource: InteractionSource,
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
                var text by remember { mutableStateOf("") }

                BTTextField(
                    text = text,
                    placeholder = "예매) 불티",
                    onValueChanged = { text = it },
                )
                BTTextField(
                    text = text,
                    placeholder = "예매) 불티",
                    isError = true,
                    supportingText = "에러!!에러!!에러!!에러!!에러!!에러!!에러!!에러!!에러!!에러!!에러!!에러!!",
                    onValueChanged = { text = it },
                )

                val maxLength = 3
                BTTextField(
                    text = text.takeForUnicode(maxLength),
                    placeholder = "예) 재즈와 펑크락을 좋아해요",
                    minHeight = 72.dp,
                    bottomEndText = "${text.utf8Size()}/${maxLength}자",
                    onValueChanged = {
                        text = it.takeForUnicode(maxLength)
                    },
                )
                BTTextField(
                    text = text.takeForUnicode(maxLength),
                    placeholder = "예) 재즈와 펑크락을 좋아해요",
                    onValueChanged = { text = it.takeForUnicode(maxLength) },
                    trailingIcon = {
                        BTTextFieldDefaults.ClearButton(onClick = { /*TODO*/ },)
                    },
                )
            }
        }
    }
}
