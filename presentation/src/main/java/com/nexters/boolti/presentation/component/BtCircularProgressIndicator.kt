package com.nexters.boolti.presentation.component

import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.theme.Grey60
import com.nexters.boolti.presentation.theme.Grey85
import kotlinx.coroutines.delay
import kotlin.time.Duration

/**
 * @param showDelay 이 시간이 지나도 화면에 남아 있을 때만 보여준다. 로딩이 금방 끝날 때 깜빡이지 않게 한다
 */
@Composable
fun BtCircularProgressIndicator(
    modifier: Modifier = Modifier,
    showDelay: Duration = Duration.ZERO,
) {
    var visible by remember { mutableStateOf(showDelay == Duration.ZERO) }
    LaunchedEffect(showDelay) {
        delay(showDelay)
        visible = true
    }

    if (visible) {
        CircularProgressIndicator(
            modifier = modifier.width(48.dp),
            color = Grey60,
            trackColor = Grey85,
        )
    }
}
