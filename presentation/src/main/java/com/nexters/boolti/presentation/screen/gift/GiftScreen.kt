package com.nexters.boolti.presentation.screen.gift

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BtAppBar
import com.nexters.boolti.presentation.component.BtAppBarDefaults
import com.nexters.boolti.presentation.component.BusinessInformation
import com.nexters.boolti.presentation.component.MainButton
import com.nexters.boolti.presentation.screen.ticketing.OrderAgreementSection
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.marginHorizontal

@Composable
fun GiftScreen(
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GiftViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    Scaffold(
        modifier = modifier,
        topBar = {
            BtAppBar(
                title = stringResource(id = R.string.ticketing_give_a_gift),
                navigateButtons = {
                    BtAppBarDefaults.AppBarIconButton(
                        iconRes = R.drawable.ic_arrow_back,
                        description = stringResource(id = R.string.description_navigate_back),
                        onClick = popBackStack,
                    )
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            Column {
                // TODO : 카드, 카드 선택
                // TODO : 받는 분 정보
                // TODO : 보내는 분 정보
                // TODO : 티켓 정보
                // TODO : 환불 규정

                // 주문내용 확인 및 결제 동의
                OrderAgreementSection(
                    totalAgreed = uiState.orderAgreed,
                    agreement = uiState.orderAgreement,
                    onClickTotalAgree = viewModel::toggleAgreement,
                    onClickShow = {
                        val url = when (it) {
                            0 -> "https://boolti.notion.site/00259d85983c4ba8a987a374e2615396?pvs=4"
                            1 -> "https://boolti.notion.site/3-354880c7d75e424486b7974e5cc8bcad?pvs=4"
                            else -> return@OrderAgreementSection
                        }
                        uriHandler.openUri(url)
                    },
                )

                Text(
                    modifier = Modifier
                        .padding(top = 24.dp, bottom = 20.dp)
                        .padding(horizontal = marginHorizontal),
                    text = stringResource(R.string.business_responsibility),
                    style = MaterialTheme.typography.labelMedium,
                    color = Grey70,
                )

                // 사업자 정보
                BusinessInformation(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { TODO("navigateToBusiness") }
                )
                Spacer(modifier = Modifier.height(120.dp))
            }

            Column(
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.background.copy(alpha = 0F),
                                    MaterialTheme.colorScheme.background,
                                )
                            ),
                            shape = RectangleShape,
                        )
                )
                MainButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 24.dp),
                    enabled = false,
                    label = stringResource(
                        R.string.ticketing_payment_button_label,
                        0 // TODO : 가격 수정
                    ),
                    onClick = {
                        TODO("팝업 구현")
                    },
                )
            }
        }
    }
}
