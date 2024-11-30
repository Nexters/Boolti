package com.nexters.boolti.presentation.extension

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.component.BTTextField

/**
 * 상위로부터 [top] 만큼 떨어진 정렬 선이 컴포저블의 중앙에 위치하도록 하는 수정자
 *
 * [top]이 컴포저블 높이의 절반보다 작은 경우 컴포저블이 정렬 선의 중앙에 위치하지 않을 수 있음을 주의해야 한다.
 *
 * 주로 [BTTextField] 와 같이 사용될 때 [BTTextField] 입력 창의 중앙(24.dp)에 컴포저블의 중앙 정렬하기 위해 사용함.
 *
 * @param top 컴포저블을 중앙 정렬 할 정렬 선의 위치
 */
fun Modifier.centerToTop(
    top: Dp = 0.dp,
) = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)

    val centerOffset = placeable.height / 2

    val placeableY = (top.roundToPx() - centerOffset).coerceAtLeast(0)
    val height = placeable.height + placeableY
    layout(placeable.width, height) {
        placeable.placeRelative(0, placeableY)
    }
}

@Preview
@Composable
private fun PaddingFromCenterPreview() {
    Box(
        modifier = Modifier.width(120.dp),
    ) {
        Row {
            Box(
                Modifier
                    .centerToTop(top = 20.dp)
                    .size(20.dp)
                    .background(Color.Red),
            )
            Box(
                Modifier
                    .centerToTop(top = 20.dp)
                    .size(40.dp)
                    .background(Color.Blue),
            )
            Box(
                Modifier
                    .centerToTop(top = 20.dp)
                    .size(60.dp)
                    .background(Color.Green),
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = 20.dp)
        )
    }
}
