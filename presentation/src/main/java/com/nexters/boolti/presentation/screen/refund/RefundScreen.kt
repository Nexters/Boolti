package com.nexters.boolti.presentation.screen.refund

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BtAppBar
import com.nexters.boolti.presentation.component.MainButton
import com.nexters.boolti.presentation.theme.Grey10
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.Grey85
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.theme.point4
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RefundScreen(
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState { 2 }

    Scaffold(
        topBar = {
            BtAppBar(
                title = stringResource(id = R.string.refund_button),
                onBackPressed = onBackPressed
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState,
            userScrollEnabled = false,
        ) { index ->
            if (index == 0) {
                ReasonPage(
                    onNextClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            } else {
                RefundInfoPage()
            }
        }
    }
}

@Composable
fun ReasonPage(
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier
                .padding(top = 20.dp)
                .padding(horizontal = marginHorizontal),
            text = stringResource(id = R.string.refund_reason_label),
            style = point4,
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = marginHorizontal)
                .height(160.dp)
                .padding(top = 20.dp)
                .clip(shape = RoundedCornerShape(4.dp)),
            value = "",
            onValueChange = {},
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Grey10),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Grey85,
                unfocusedContainerColor = Grey85,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            shape = RoundedCornerShape(4.dp),
            placeholder = {
                Text(
                    text = stringResource(id = R.string.refund_reason_hint),
                    style = MaterialTheme.typography.bodyLarge.copy(color = Grey70),
                )
            }
        )
        Spacer(modifier = Modifier.weight(1.0f))
        MainButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = marginHorizontal)
                .padding(bottom = 8.dp),
            onClick = onNextClick,
            enabled = true, // TODO 입력 여부
            label = stringResource(id = R.string.next)
        )
    }
}

@Composable
fun RefundInfoPage() {

}