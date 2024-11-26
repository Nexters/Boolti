package com.nexters.boolti.presentation.component

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey30

/**
 * TextField 의 세로 중앙에 정렬하기 위한 컴포저블
 *
 * @param prevView TextField 왼쪽에 표시할 컴포저블. 선택 사항이며 정의하지 않을 경우 `null` 로 설정
 * @param textField TextField [BTTextField] 참고
 * @param postView TextField 오른쪽에 표시할 컴포저블. 선택 사항이며 정의하지 않을 경우 `null` 로 설정
 * @param modifier [Modifier]
 * @param alignmentOffset 정렬할 정렬 선의 오프셋. 기본적으로 1줄 높이의 `TextField` 높이 절반 값
 */
@Composable
fun TextFieldLayout(
    textField: @Composable () -> Unit,
    prevView: @Composable (() -> Unit)? = null,
    postView: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    alignmentOffset: Dp = 24.dp,
) {
    Layout(
        content = {
            prevView?.invoke()
            textField()
            postView?.invoke()
        },
        modifier = modifier,
    ) { measurables, constraints ->
        check(
            listOf(textField, prevView, postView).count { it != null } == measurables.size,
        ) {
            "정의되지 않은 슬롯이 있습니다. 정의하지 않을 슬롯은 null 로 설정하세요"
        }

        val (prevMeasurable, textFieldMeasurable, postMeasurable) = when {
            prevView != null && postView != null -> Triple(
                measurables[0],
                measurables[1],
                measurables[2]
            )

            prevView != null -> Triple(measurables[0], measurables[1], null)
            postView != null -> Triple(null, measurables[0], measurables[1])
            else -> Triple(null, measurables[0], null)
        }

        val prevPlaceable = prevMeasurable?.measure(constraints)
        val textFieldPlaceable = textFieldMeasurable.measure(constraints)
        val postPlaceable = postMeasurable?.measure(constraints)

        val offset = maxOf(
            alignmentOffset.roundToPx(),
            (prevPlaceable?.height ?: 0) / 2,
            (postPlaceable?.height ?: 0) / 2,
        )

        // 레이아웃 높이
        val layoutHeight = maxOf(
            prevPlaceable?.height ?: 0,
            textFieldPlaceable.height,
            postPlaceable?.height ?: 0,
            offset * 2,
        )

        // 레이아웃 너비
        val layoutWidth = (prevPlaceable?.width ?: 0) +
                textFieldPlaceable.width +
                (postPlaceable?.width ?: 0)

        layout(width = layoutWidth, height = layoutHeight) {
            val prevY = offset - ((prevPlaceable?.height ?: 0) / 2)
            prevPlaceable?.place(0, prevY)

            val textFieldX = (prevPlaceable?.width ?: 0)
            textFieldPlaceable.place(textFieldX, offset - alignmentOffset.roundToPx())

            val postX = textFieldX + textFieldPlaceable.width
            val postY = offset - ((postPlaceable?.height ?: 0) / 2)
            postPlaceable?.place(postX, postY)
        }
    }
}

@Preview
@Composable
private fun TextFieldLayoutPrev() {
    var username by remember { mutableStateOf("") }
    val usernameHasError: Boolean = username.contains(Regex("[^0-9a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣._]+"))

    BooltiTheme {
        TextFieldLayout(
            prevView = {
                Text(
                    modifier = Modifier.defaultMinSize(minWidth = 72.dp),
                    text = "Username",
                    color = Grey30,
                    style = MaterialTheme.typography.bodySmall,
                )
            },
            textField = {
                BTTextField(
                    modifier = Modifier,
                    text = username,
                    isError = usernameHasError,
                    placeholder = stringResource(R.string.sns_username_placeholder),
                    supportingText = when {
                        username.contains('@') -> stringResource(R.string.sns_username_contains_at_error)
                        usernameHasError -> stringResource(R.string.contains_unsupported_char_error)
                        else -> null
                    },
                    trailingIcon = if (username.isNotEmpty()) {
                        { BTTextFieldDefaults.ClearButton(onClick = { username = "" }) }
                    } else {
                        null
                    },
                    singleLine = true,
                    onValueChanged = { username = it },
                )
            },
        )
    }
}
