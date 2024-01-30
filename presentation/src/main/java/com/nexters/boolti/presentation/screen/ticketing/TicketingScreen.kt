package com.nexters.boolti.presentation.screen.ticketing

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTTextField
import com.nexters.boolti.presentation.component.MainButton
import com.nexters.boolti.presentation.extension.filterToPhoneNumber
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey05
import com.nexters.boolti.presentation.theme.Grey20
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey80
import com.nexters.boolti.presentation.theme.Grey90
import com.nexters.boolti.presentation.theme.Success
import com.nexters.boolti.presentation.util.PhoneNumberVisualTransformation
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketingScreen(
    modifier: Modifier = Modifier,
    viewModel: TicketingViewModel = hiltViewModel(),
    onBackClicked: () -> Unit,
) {
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val isInviteTicket by remember { mutableStateOf(true) } // TODO 실제 데이터로 교체 필요

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(start = 20.dp),
                title = {
                    Text(
                        text = stringResource(R.string.ticketing_toolbar_title),
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                navigationIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        contentDescription = stringResource(R.string.description_navigate_back),
                        modifier = Modifier.clickable(role = Role.Button) { onBackClicked() },
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = 100.dp),
            ) { data ->
                Card(
                    shape = RoundedCornerShape(4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                        text = data.visuals.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = modifier.padding(innerPadding)) {
            Column(
                modifier = modifier
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(scrollState),
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AsyncImage(
                        model = state.poster,
                        contentDescription = stringResource(R.string.description_poster),
                        modifier = Modifier
                            .size(width = 70.dp, height = 98.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(4.dp),
                            ),
                        contentScale = ContentScale.Crop,
                    )

                    Column(verticalArrangement = Arrangement.Center, modifier = Modifier.padding(start = 16.dp)) {
                        Text(
                            text = state.ticket?.title ?: "",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "2024.03.09 (토) 17:30", // TODO API 나오면 대체하기
                            style = MaterialTheme.typography.bodySmall,
                            color = Grey30,
                        )
                    }
                }

                // 예매자 정보
                Section(title = stringResource(R.string.ticketing_ticket_holder_label)) {
                    var name by remember { mutableStateOf("") } // TODO remove
                    var phoneNumber by remember { mutableStateOf("") } // TODO remove
                    InputRow(
                        stringResource(R.string.ticketing_name_label),
                        name,
                        placeholder = stringResource(R.string.ticketing_name_placeholder),
                    ) {
                        name = it
                    }
                    Spacer(modifier = Modifier.size(16.dp))
                    InputRow(
                        stringResource(R.string.ticketing_contact_label),
                        phoneNumber,
                        placeholder = stringResource(R.string.ticketing_contact_placeholder),
                        isPhoneNumber = true,
                        imeAction = if (state.isSameContactInfo) {
                            ImeAction.Default
                        } else {
                            ImeAction.Next
                        },
                    ) {
                        phoneNumber = it
                    }
                }

                if (!isInviteTicket) {
                    // 입금자 정보
                    Section(
                        title = stringResource(R.string.ticketing_depositor_label),
                        titleRowOption = {
                            Row(
                                modifier = Modifier
                                    .padding(start = 20.dp)
                                    .clickable(role = Role.Checkbox) { viewModel.toggleIsSameContactInfo() }
                            ) {
                                if (state.isSameContactInfo) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_checkbox_selected),
                                        tint = Grey05,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(24.dp)
                                            .padding(3.dp)
                                            .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
                                    )
                                } else {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_checkbox_18),
                                        tint = Grey50,
                                        contentDescription = null,
                                    )
                                }
                                Text(
                                    text = stringResource(R.string.ticketing_same_contact_info),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        },
                        modifier = Modifier.animateContentSize(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMedium,
                            )
                        ),
                        contentVisible = !state.isSameContactInfo,
                    ) {
                        if (!state.isSameContactInfo) {
                            InputRow(
                                stringResource(R.string.ticketing_name_label),
                                "",
                                placeholder = stringResource(R.string.ticketing_name_placeholder),
                            ) {}
                            Spacer(modifier = Modifier.size(16.dp))
                            InputRow(
                                stringResource(R.string.ticketing_contact_label),
                                "",
                                placeholder = stringResource(R.string.ticketing_contact_placeholder),
                                isPhoneNumber = true,
                                imeAction = ImeAction.Default,
                            ) {}
                        }
                    }
                }

                // 티켓 정보
                Section(title = stringResource(R.string.ticketing_ticket_info_label)) {
                    SectionTicketInfo(
                        stringResource(R.string.ticketing_selected_ticket),
                        "일반 티켓 B",
                        marginTop = 0.dp
                    ) // TODO API 나오면 대체하기
                    SectionTicketInfo(
                        label = stringResource(R.string.ticketing_selected_ticket_count),
                        value = "1개"
                    ) // TODO 데이터 붙일 때 연결
                    SectionTicketInfo(
                        label = stringResource(R.string.ticketing_total_payment_amount),
                        value = "5,000원"
                    ) // TODO 데이터 붙일 때 연결
                    Spacer(modifier = Modifier.padding(bottom = 8.dp))
                }

                if (isInviteTicket) {
                    // 초청 코드
                    Section(title = stringResource(R.string.ticketing_invite_code_label)) {
                        var inviteCode by remember { mutableStateOf("") }
                        var inviteCodeUsed by remember { mutableStateOf(false) }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            BTTextField(
                                modifier = Modifier
                                    .weight(1F)
                                    .padding(end = 6.dp),
                                text = inviteCode,
                                singleLine = true,
                                placeholder = stringResource(R.string.ticketing_invite_code_placeholder),
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done,
                                ),
                                onValueChanged = { inviteCode = it },
                            )
                            Button(
                                onClick = { inviteCodeUsed = true },
                                enabled = !inviteCodeUsed,
                                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 13.dp),
                                shape = RoundedCornerShape(4.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Grey20,
                                    disabledContainerColor = Grey80,
                                    contentColor = Grey90,
                                    disabledContentColor = Grey50,
                                ),
                            ) {
                                Text(
                                    text = stringResource(R.string.ticketing_invite_code_use_button),
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            }
                        }
                        if (inviteCodeUsed) {
                            Text(
                                modifier = Modifier.padding(top = 12.dp),
                                text = stringResource(R.string.ticketing_invite_code_success),
                                style = MaterialTheme.typography.bodySmall,
                                color = Success,
                            )
                        }
                    }
                }

                if (!isInviteTicket) {
                    // 결제 수단
                    Section(title = stringResource(R.string.ticketing_payment_label)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(4.dp),
                                )
                                .background(MaterialTheme.colorScheme.surfaceTint)
                                .clickable(role = Role.DropdownList) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = context.getString(R.string.ticketing_payment_message),
                                            duration = SnackbarDuration.Short,
                                        )
                                    }
                                }
                                .padding(12.dp),
                        ) {
                            Text(
                                text = stringResource(R.string.ticketing_payment_account_transfer),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                        Row(Modifier.padding(top = 12.dp)) {
                            Icon(
                                painter = painterResource(R.drawable.ic_info_20),
                                tint = Grey50,
                                contentDescription = null,
                            )
                            Text(
                                text = stringResource(R.string.ticketing_payment_information),
                                modifier = Modifier.padding(start = 4.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                        }
                    }
                }

                // 취소/환불 규정
                var expanded by remember { mutableStateOf(false) }
                val refundPolicy = stringArrayResource(R.array.refund_policy)
                val rotation by animateFloatAsState(
                    targetValue = if (expanded) 0F else 180F,
                    animationSpec = tween(),
                    label = "expandIconRotation"
                )
                Section(
                    title = stringResource(R.string.ticketing_refund_policy_label),
                    titleRowOption = {
                        Icon(
                            modifier = Modifier
                                .clip(CircleShape)
                                .rotate(rotation)
                                .clickable(role = Role.Image) { expanded = !expanded },
                            painter = painterResource(R.drawable.ic_expand_24),
                            tint = Grey50,
                            contentDescription = null,
                        )
                    },
                    contentVisible = expanded,
                ) {
                    Column(
                        Modifier
                            .animateContentSize(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioNoBouncy,
                                    stiffness = Spring.StiffnessMedium,
                                )
                            )
                    ) {
                        if (expanded) {
                            refundPolicy.forEach {
                                Row {
                                    Text(
                                        text = stringResource(R.string.bullet),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Grey50,
                                    )
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Grey50,
                                    )
                                }
                            }
                        }
                    }
                }
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
                    label = stringResource(R.string.ticketing_payment_button_label, 5000),
                    onClick = { /* TODO */ },
                ) // TODO 데이터 붙일 때 연결
            }
        }
    }
}

@Composable
private fun Section(
    modifier: Modifier = Modifier,
    title: String,
    titleRowOption: (@Composable () -> Unit)? = null,
    contentVisible: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier
            .padding(bottom = 12.dp)
            .fillMaxWidth()
            .background((MaterialTheme.colorScheme.surface))
            .padding(start = 20.dp, end = 20.dp, bottom = if (contentVisible) 20.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SectionTitle(title)
            titleRowOption?.let { it() }
        }
        content()
    }
}

@Composable
fun InputRow(
    label: String,
    text: String,
    placeholder: String = "",
    isPhoneNumber: Boolean = false,
    imeAction: ImeAction = ImeAction.Next,
    onValueChanged: (String) -> Unit,
) {
    Row {
        Text(
            text = label,
            Modifier
                .width(44.dp)
                .align(Alignment.CenterVertically),
            style = MaterialTheme.typography.bodySmall,
        )
        BTTextField(
            text = if (isPhoneNumber) text.filterToPhoneNumber() else text,
            placeholder = placeholder,
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1F),
            singleLine = true,
            keyboardOptions = if (isPhoneNumber) {
                KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = imeAction)
            } else {
                KeyboardOptions.Default.copy(imeAction = imeAction)
            },
            visualTransformation = if (isPhoneNumber) {
                PhoneNumberVisualTransformation('-')
            } else {
                VisualTransformation.None
            },
            onValueChanged = onValueChanged,
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(title, style = MaterialTheme.typography.titleLarge)
}

@Composable
private fun SectionTicketInfo(label: String, value: String, marginTop: Dp = 16.dp) {
    Row(
        modifier = Modifier
            .padding(top = marginTop)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge, color = Grey30)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview
@Composable
private fun TicketingDetailScreenPreview() {
    BooltiTheme {
        Surface {
            TicketingScreen {}
        }
    }
}
