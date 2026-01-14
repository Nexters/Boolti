package com.nexters.boolti.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.theme.Grey15
import com.nexters.boolti.presentation.theme.Grey60
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.Grey85

@Composable
fun BtSearchBar(
    keyword: String,
    onKeywordChanged: (keyword: String) -> Unit,
    hint: String,
    search: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showClearButtonOnFocus: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue(keyword)) }

    // keyword가 외부에서 변경되면 textFieldValue도 업데이트
    if (textFieldValue.text != keyword) {
        textFieldValue = TextFieldValue(keyword)
    }

    BtSearchBar(
        value = textFieldValue,
        onValueChange = { newValue ->
            textFieldValue = newValue
            onKeywordChanged(newValue.text)
        },
        hint = hint,
        search = search,
        modifier = modifier,
        enabled = enabled,
        showClearButtonOnFocus = showClearButtonOnFocus,
        interactionSource = interactionSource,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BtSearchBar(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    hint: String,
    search: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showClearButtonOnFocus: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val focused by interactionSource.collectIsFocusedAsState()

    val colors = TextFieldDefaults.colors(
        unfocusedIndicatorColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedContainerColor = Grey85,
        focusedContainerColor = Grey85,
    )

    BasicTextField(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        value = value,
        enabled = enabled,
        singleLine = true,
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { search() }),
        decorationBox = { innerTextField ->
            OutlinedTextFieldDefaults.DecorationBox(
                value = value.text,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                placeholder = {
                    Text(
                        text = hint,
                        style = MaterialTheme.typography.bodyLarge.copy(color = Grey70),
                    )
                },
                trailingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (showClearButtonOnFocus && value.text.isNotEmpty() && focused) {
                            BTTextFieldDefaults.ClearButton(
                                onClick = { onValueChange(TextFieldValue()) },
                            )

                            Spacer(Modifier.size(4.dp))
                        }

                        Icon(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .clickable(onClick = search, role = Role.Button, enabled = enabled)
                                .padding(12.dp),
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = null,
                            tint = Grey60,
                        )
                    }
                },
                colors = colors,
                interactionSource = remember { MutableInteractionSource() },
                contentPadding = PaddingValues(horizontal = 12.dp),
            )
        },
        interactionSource = interactionSource,
        cursorBrush = SolidColor(Color.White),
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = Grey15),
    )
}
