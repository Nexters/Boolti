package com.nexters.boolti.presentation.screen.accountsetting

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTDialog
import com.nexters.boolti.presentation.component.BtBackAppBar
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.KakaoYellow
import com.nexters.boolti.presentation.theme.marginHorizontal

@Composable
fun AccountSettingScreen(
    modifier: Modifier = Modifier,
    viewModel: AccountSettingViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    onClickResign: () -> Unit,
) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    val loggedIn by viewModel.loggedIn.collectAsStateWithLifecycle()

    LaunchedEffect(loggedIn) {
        if (loggedIn == false) navigateBack()
    }

    AccountSettingScreen(
        modifier = modifier,
        userCode = user?.userCode ?: "",
        onClickBack = navigateBack,
        requireLogout = viewModel::logout,
        onClickResign = onClickResign,
    )
}

@Composable
fun AccountSettingScreen(
    modifier: Modifier = Modifier,
    userCode: String,
    onClickBack: () -> Unit = {},
    requireLogout: () -> Unit = {},
    onClickResign: () -> Unit = {},
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            BtBackAppBar(
                title = stringResource(R.string.account_setting),
                onClickBack = onClickBack,
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {

                Section(
                    modifier = Modifier.padding(top = 20.dp),
                ) {
                    Title(stringResource(R.string.user_code))
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        text = "#$userCode",
                        color = Grey30,
                    )
                }

                Section {
                    Title(stringResource(R.string.sns_provider))
                    KakaoChip(modifier = Modifier.padding(top = 16.dp))
                }

                Section(
                    modifier = Modifier.clickable { showLogoutDialog = true },
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Title(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.my_logout),
                        )
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_right),
                            contentDescription = stringResource(R.string.my_logout),
                            tint = Grey50,
                        )
                    }
                }
            }

            TextButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 30.dp),
                onClick = onClickResign,
            ) {
                Text(
                    style = MaterialTheme.typography.bodyLarge,
                    text = stringResource(R.string.signout),
                    textDecoration = TextDecoration.Underline,
                    color = Grey50,
                )
            }

            if (showLogoutDialog) {
                BTDialog(
                    positiveButtonLabel = stringResource(id = R.string.my_logout),
                    onClickPositiveButton = {
                        showLogoutDialog = false
                        requireLogout()
                    },
                    onDismiss = { showLogoutDialog = false }
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = R.string.my_logout_popup),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
private fun Section(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 16.dp, horizontal = marginHorizontal),
        content = content,
    )
}

@Composable
private fun Title(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground,
    )
}

@Composable
private fun KakaoChip(modifier: Modifier = Modifier) {
    SnsProvider(
        modifier = modifier,
        iconRes = R.drawable.ic_kakaotalk,
        iconBackgroundColor = KakaoYellow,
        label = stringResource(R.string.kakao),
    )
}

@Composable
private fun SnsProvider(
    @DrawableRes iconRes: Int,
    iconBackgroundColor: Color,
    label: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .border(width = 1.dp, color = MaterialTheme.colorScheme.outline, shape = CircleShape)
            .padding(top = 6.dp, bottom = 6.dp, start = 6.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(iconBackgroundColor),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape),
                imageVector = ImageVector.vectorResource(iconRes),
                contentDescription = label,
            )
        }
        Text(
            modifier = Modifier.padding(start = 12.dp),
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview
@Composable
private fun KakaoChipPreview() {
    BooltiTheme {
        KakaoChip()
    }
}

@Preview
@Composable
private fun AccountSettingScreenPreview() {
    BooltiTheme {
        AccountSettingScreen(
            userCode = "AB1800028",
            onClickBack = {},
            requireLogout = {},
        ) { }
    }
}
