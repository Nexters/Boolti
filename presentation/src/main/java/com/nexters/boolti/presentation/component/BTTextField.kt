package com.nexters.boolti.presentation.component

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldDecorator
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults.FocusedBorderThickness
import androidx.compose.material3.OutlinedTextFieldDefaults.UnfocusedBorderThickness
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.extension.dpToSp
import com.nexters.boolti.presentation.extension.takeForUnicode
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Error
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.Grey80
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * 입력값이 있고, 포커즈를 받은 경우 Clear 버튼을 보여주는 텍스트 필드
 *
 * [trailingIcon] 이 null 이 아닌 경우 [trailingIcon] 을 보여주고, null 인 경우 조건에 충족하면 Clear 버튼을 보여준다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BTClearableTextField(
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
    height: Dp = Dp.Unspecified,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    outputTransformation: OutputTransformation? = null,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    enableEdgeFade: Boolean = !singleLine,
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
    val focused by interactionSource.collectIsFocusedAsState()

    BTTextField(
        text = text,
        onValueChanged = onValueChanged,
        modifier = modifier,
        placeholder = placeholder,
        supportingText = supportingText,
        trailingIcon = when {
            trailingIcon != null -> trailingIcon
            focused && text.isNotEmpty() -> {
                { BTTextFieldDefaults.ClearButton(onClick = { onValueChanged("") }) }
            }

            else -> null
        },
        bottomEndText = bottomEndText,
        enabled = enabled,
        isError = isError,
        readOnly = readOnly,
        height = height,
        textStyle = textStyle,
        keyboardOptions = keyboardOptions,
        onKeyboardAction = onKeyboardAction,
        outputTransformation = outputTransformation,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        enableEdgeFade = enableEdgeFade,
        colors = colors,
        interactionSource = interactionSource,
    )
}

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
    height: Dp = Dp.Unspecified,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    outputTransformation: OutputTransformation? = null,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    enableEdgeFade: Boolean = !singleLine,
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
    val resolvedHeight = when {
        height != Dp.Unspecified -> height
        singleLine -> BTTextFieldDefaults.Height.SingleLine
        else -> BTTextFieldDefaults.Height.MultiLine
    }

    val textFieldState =
        rememberTextFieldState(text) // TextFieldState 를 꼭 사용해야 하는 상황이 오면 TextFieldState 타입을 파라미터로 받도록 수정
    LaunchedEffect(text) {
        if (textFieldState.text.toString() != text) {
            textFieldState.setTextAndPlaceCursorAtEnd(text)
        }
    }
    val currentText by rememberUpdatedState(text)
    val currentOnValueChanged by rememberUpdatedState(onValueChanged)
    LaunchedEffect(Unit) {
        snapshotFlow { textFieldState.text.toString() }
            .distinctUntilChanged()
            .collect { newText ->
                if (newText != currentText) currentOnValueChanged(newText)
            }
    }

    val actualEnableEdgeFade = !singleLine && enableEdgeFade
    val enableBottomEndText = bottomEndText != null

    val topPad = 12.dp
    val bottomPad = 12.dp
    val bottomEndTextGapPad = 8.dp
    val verticalPad = topPad
    val horizontalPad = 12.dp
    val bottomEndTextHeight = 18.dp

    val lineLimits = if (singleLine) {
        TextFieldLineLimits.SingleLine
    } else {
        TextFieldLineLimits.MultiLine(minHeightInLines = minLines, maxHeightInLines = maxLines)
    }

    val scrollState = rememberScrollState()
    val internalScrollState = rememberScrollState()

    val textColor = textStyle.color.takeOrElse {
        colors.textColor(enabled, isError, interactionSource).value
    }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))
    CompositionLocalProvider(LocalTextSelectionColors provides colors.textSelectionColors) {
        ConstraintLayout(modifier = modifier.defaultMinSize(minWidth = OutlinedTextFieldDefaults.MinWidth)) {
            val (textFieldRef, bottomEndTextRef, supportingTextRef) = createRefs()
            BasicTextField(
                state = textFieldState,
                modifier = Modifier
                    .defaultMinSize(
                        minWidth = OutlinedTextFieldDefaults.MinWidth
                    )
                    .height(resolvedHeight)
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
                onKeyboardAction = onKeyboardAction,
                lineLimits = lineLimits,
                outputTransformation = outputTransformation,
                interactionSource = interactionSource,
                scrollState = if (actualEnableEdgeFade) internalScrollState else scrollState,
                decorator = TextFieldDecorator { innerTextField ->
                    OutlinedTextFieldDefaults.DecorationBox(
                        value = textFieldState.text.toString(),
                        visualTransformation = VisualTransformation.None,
                        innerTextField = {
                            if (actualEnableEdgeFade) {
                                Box {
                                    Column(
                                        modifier = Modifier.verticalScroll(scrollState), // topPad 만큼 통째로 스크롤하여 글자가 상단에 붙도록 함
                                    ) {
                                        Spacer(modifier = Modifier.height(topPad))
                                        innerTextField()
                                        if (!enableBottomEndText) {
                                            Spacer(modifier = Modifier.height(bottomPad))
                                        }
                                    }

                                    val containerColor = colors.containerColor(
                                        enabled, isError, interactionSource,
                                    ).value
                                    EdgeFadeEffect(scrollState, containerColor) // EdgeFade 그라데이션
                                }
                            } else {
                                innerTextField()
                            }
                        },
                        placeholder = placeholder?.let {
                            {
                                Text(
                                    text = placeholder,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = if (actualEnableEdgeFade) {
                                        Modifier.padding(top = topPad)
                                    } else {
                                        Modifier
                                    },
                                )
                            }
                        },
                        singleLine = singleLine,
                        enabled = enabled,
                        isError = isError,
                        interactionSource = interactionSource,
                        colors = colors,
                        contentPadding = when {
                            actualEnableEdgeFade && enableBottomEndText -> PaddingValues(
                                start = horizontalPad,
                                end = horizontalPad,
                                top = FocusedBorderThickness + (0.1).dp, // 렌더링 해보면 그라데이션이 아주 미세하게 border 를 덮게됨. 0.1 정도 보정하면 border 가리지 않고 노출되어 하드코딩으로 추가
                                bottom = bottomEndTextGapPad + bottomEndTextHeight + bottomPad,
                            )

                            actualEnableEdgeFade -> PaddingValues(
                                horizontal = horizontalPad,
                                vertical = FocusedBorderThickness + (0.1).dp
                            )

                            enableBottomEndText -> PaddingValues(
                                start = horizontalPad,
                                end = horizontalPad,
                                top = topPad,
                                bottom = bottomEndTextGapPad + bottomEndTextHeight + bottomPad,
                            )

                            else -> PaddingValues(horizontal = horizontalPad, vertical = verticalPad)
                        },
                        trailingIcon = trailingIcon,
                        container = {
                            OutlinedTextFieldDefaults.Container(
                                enabled = enabled,
                                isError = isError,
                                interactionSource = interactionSource,
                                colors = colors,
                                shape = shape,
                                focusedBorderThickness = FocusedBorderThickness,
                                unfocusedBorderThickness = UnfocusedBorderThickness,
                            )
                        },
                    )
                },
            )
            bottomEndText?.let {
                Text(
                    modifier = Modifier
                        .padding(
                            top = bottomEndTextGapPad,
                            start = horizontalPad,
                            end = horizontalPad,
                            bottom = bottomPad
                        )
                        .constrainAs(bottomEndTextRef) {
                            end.linkTo(textFieldRef.end)
                            bottom.linkTo(textFieldRef.bottom)
                        }
                        .height(bottomEndTextHeight),
                    text = it,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = MaterialTheme.typography.labelMedium.fontSize.dpToSp,
                    ),
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
                        top.linkTo(textFieldRef.bottom, bottomEndTextGapPad)
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
    object Height {
        val SingleLine = 48.dp
        val MultiLine = 160.dp
    }

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
private fun BoxScope.EdgeFadeEffect(scrollState: ScrollState, fadeColor: Color) {
    if (scrollState.canScrollBackward) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(32.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(fadeColor, fadeColor.copy(alpha = 0f))
                    )
                )
        )
    }
    if (scrollState.canScrollForward) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(32.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(fadeColor.copy(alpha = 0f), fadeColor)
                    )
                )
        )
    }
}

@Composable
private fun TextFieldColors.containerColor(
    enabled: Boolean,
    isError: Boolean,
    interactionSource: InteractionSource,
): State<Color> {
    val focused by interactionSource.collectIsFocusedAsState()

    val targetValue = when {
        !enabled -> disabledContainerColor
        isError -> errorContainerColor
        focused -> focusedContainerColor
        else -> unfocusedContainerColor
    }
    return rememberUpdatedState(targetValue)
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

@Preview(name = "SingleLine")
@Composable
private fun BTTextFieldSingleLinePreview() {
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
            }
        }
    }
}

@Preview(name = "MultiLine")
@Composable
private fun BTTextFieldMultiLinePreview() {
    @Composable
    fun ContentWithTitle(
        title: String,
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit,
    ) {
        Column(modifier = modifier) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = Color.Black)
            content()
        }
    }

    BooltiTheme {
        Surface {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                var text by remember {
                    mutableStateOf("높이가 160dp로 고정된 텍스트 필드입니다.\n줄이 늘어나도 높이가 변하지 않고 스크롤됩니다.\n세 번째 줄\n네 번째 줄\n다섯 번째 줄\n여섯 번째 줄\n일곱 번째 줄\n여덟 번째 줄")
                }

                ContentWithTitle(
                    title = "그라데이션 X + 하단 텍스트 X"
                ) {
                    BTTextField(
                        text = text,
                        placeholder = "답변을 입력해 주세요",
                        singleLine = false,
                        enableEdgeFade = false,
                        onValueChanged = { text = it },
                    )
                }

                ContentWithTitle(
                    title = "그라데이션 X + 하단 텍스트 O"
                ) {
                    BTTextField(
                        text = text,
                        placeholder = "답변을 입력해 주세요",
                        singleLine = false,
                        enableEdgeFade = false,
                        bottomEndText = "${text.length}/300자",
                        onValueChanged = { text = it },
                    )
                }

                ContentWithTitle(
                    title = "그라데이션 O + 하단 텍스트 O"
                ) {
                    BTTextField(
                        text = text,
                        placeholder = "답변을 입력해 주세요",
                        singleLine = false,
                        bottomEndText = "${text.length}/300자",
                        onValueChanged = { text = it },
                    )
                }

                ContentWithTitle(
                    title = "그라데이션 O + 하단 텍스트 X"
                ) {
                    BTTextField(
                        text = text,
                        placeholder = "답변을 입력해 주세요",
                        singleLine = false,
                        onValueChanged = { text = it },
                    )
                }
            }
        }
    }
}

@Preview(name = "Clearable")
@Composable
private fun BTClearableTextFieldPreview() {
    BooltiTheme {
        Surface {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                var text by remember { mutableStateOf("") }
                val maxLength = 3

                BTTextField(
                    text = text.takeForUnicode(maxLength),
                    placeholder = "예) 재즈와 펑크락을 좋아해요",
                    onValueChanged = { text = it.takeForUnicode(maxLength) },
                    trailingIcon = {
                        BTTextFieldDefaults.ClearButton(onClick = { /*TODO*/ })
                    },
                )
                BTClearableTextField(
                    text = text.takeForUnicode(maxLength),
                    placeholder = "예) 재즈와 펑크락을 좋아해요",
                    onValueChanged = { text = it.takeForUnicode(maxLength) },
                )
            }
        }
    }
}
