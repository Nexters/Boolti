package com.nexters.boolti.presentation.screen.my

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.nexters.boolti.domain.model.User
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTDialog
import com.nexters.boolti.presentation.theme.Grey10
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.marginHorizontal

@Composable
fun MyScreen(
    requireLogin: () -> Unit,
    navigateToReservations: () -> Unit,
    onClickQrScan: () -> Unit,
    onClickSignout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyViewModel = hiltViewModel(),
) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    var openLogoutDialog by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(Unit) {
        viewModel.fetchMyInfo()
    }

    Column(
        modifier = modifier.verticalScroll(state = rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MyHeader(user = user, requireLogin = requireLogin)
        MyButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.my_ticketing_history),
            onClick = if (user == null) requireLogin else navigateToReservations,
        )
        MyButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            text = stringResource(R.string.my_register_show),
            onClick = {
                if (user != null) {
                    uriHandler.openUri("https://boolti.in/home") // 웹에서 로그인되지 않은 상태라면 login 페이지로 리다이렉션 시킴
                } else {
                    uriHandler.openUri("https://boolti.in/login")
                }
            }
        )
        MyButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            text = stringResource(id = R.string.my_scan_qr),
            onClick = onClickQrScan,
        )

        if (user != null) {
            MyButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                text = stringResource(id = R.string.my_logout),
                onClick = { openLogoutDialog = true },
            )
        }

        Spacer(modifier = Modifier.weight(1.0f))
        if (user != null) SignoutButton(onClick = onClickSignout)
    }

    if (openLogoutDialog) {
        BTDialog(
            positiveButtonLabel = stringResource(id = R.string.my_logout),
            onClickPositiveButton = {
                openLogoutDialog = false
                viewModel.logout()
            },
            onDismiss = { openLogoutDialog = false }
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.my_logout_popup),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun MyHeader(modifier: Modifier = Modifier, user: User?, requireLogin: () -> Unit) {
    val headerModifier = if (user == null) modifier.clickable(onClick = requireLogin) else modifier

    Row(
        modifier = headerModifier
            .fillMaxWidth()
            .padding(horizontal = marginHorizontal)
            .padding(top = 40.dp, bottom = 32.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            modifier = Modifier
                .size(70.dp)
                .clip(shape = RoundedCornerShape(100.dp)),
            model = user?.photo,
            contentDescription = null,
            fallback = painterResource(id = R.drawable.ic_fallback_profile)
        )
        Column(
            modifier = Modifier.padding(start = 12.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = user?.nickname ?: stringResource(id = R.string.my_login),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = user?.email ?: stringResource(id = R.string.my_login_sub),
                style = MaterialTheme.typography.bodyLarge.copy(color = Grey30),
            )
        }
        Spacer(modifier = modifier.weight(1.0f))
        if (user == null) {
            Icon(
                modifier = Modifier.padding(start = 12.dp),
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = null,
                tint = Grey50,
            )
        }
    }
}

@Composable
private fun MyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(horizontal = marginHorizontal, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge.copy(color = Grey10),
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = null,
            tint = Grey50,
        )
    }
}

@Composable
fun SignoutButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Text(
        modifier = modifier
            .padding(bottom = 40.dp)
            .clickable(onClick = onClick),
        text = stringResource(id = R.string.signout),
        style = MaterialTheme.typography.bodySmall.copy(color = Grey50),
        textDecoration = TextDecoration.Underline,
    )
}
