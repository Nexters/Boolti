package com.nexters.boolti.presentation.screen.ticket

import android.widget.Toast
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey05
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.util.PhoneNumberVisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketingScreen(
    modifier: Modifier = Modifier,
    viewModel: TicketingViewModel = hiltViewModel(),
    onBackClicked: () -> Unit,
) {
    val scrollState = rememberScrollState()
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(start = 20.dp),
                title = { Text(text = "결제하기", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        contentDescription = "뒤로 가기",
                        modifier = Modifier.clickable(role = Role.Button) { onBackClicked() },
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
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
                        contentDescription = "포스터",
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
                            text = "2024.03.09 (토) 17:30",
                            style = MaterialTheme.typography.bodySmall,
                            color = Grey30,
                        )
                    }
                }

                // 예매자 정보
                Section(title = "예매자 정보") {
                    var name by remember { mutableStateOf("") } // TODO remove
                    var phoneNumber by remember { mutableStateOf("") } // TODO remove
                    InputRow("이름", name, placeholder = "예) 김불티") {
                        name = it
                    }
                    Spacer(modifier = Modifier.size(16.dp))
                    InputRow(
                        "연락처",
                        phoneNumber,
                        placeholder = "예) 010-1234-5678",
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

                // 입금자 정보
                Section(
                    title = "입금자 정보",
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
                                text = "예매자와 입금자가 같아요",
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
                        InputRow("이름", "", placeholder = "예) 김불티") {}
                        Spacer(modifier = Modifier.size(16.dp))
                        InputRow(
                            "연락처",
                            "",
                            placeholder = "예) 010-1234-5678",
                            isPhoneNumber = true,
                            imeAction = ImeAction.Default,
                        ) {}
                    }
                }

                // 티켓 정보
                Section(title = "티켓 정보") {
                    SectionTicketInfo("선택한 티켓 종류", "일반 티켓 B", marginTop = 0.dp)
                    SectionTicketInfo(label = "선택한 티켓 개수", value = "1개")
                    SectionTicketInfo(label = "총 결제 금액", value = "5,000원")
                    Spacer(modifier = Modifier.padding(bottom = 8.dp))
                }

                // 결제 수단
                Section(title = "결제 수단") {
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
                                Toast
                                    .makeText(context, "지금은 계좌 이체로만 결제할 수 있어요", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            .padding(12.dp),
                    ) {
                        Text(
                            text = "계좌이체",
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
                            text = "다음 페이지에서 계좌 번호를 안내해 드릴게요",
                            modifier = Modifier.padding(start = 4.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                }

                // 취소/환불 규정
                var expanded by remember { mutableStateOf(false) }
                val refundPolicy = stringArrayResource(R.array.refund_policy)
                val rotation by animateFloatAsState(
                    targetValue = if (expanded) 180F else 0F,
                    animationSpec = tween(),
                    label = "expandIconRotation"
                )
                Section(
                    title = "취소/환불 규정",
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
                                        text = "•",
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
                ) {}
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 24.dp),
                    shape = RoundedCornerShape(4.dp),
                    contentPadding = PaddingValues(12.dp),
                    onClick = { /*TODO*/ },
                ) {
                    Text(text = "5,000원 결제하기")
                }
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
            text = text.filter { it.isDigit() }.run {
                substring(0..minOf(10, lastIndex))
            },
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
