package com.nexters.boolti.presentation.screen.my

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.nexters.boolti.domain.model.User
import com.nexters.boolti.presentation.BuildConfig
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.SmallButton
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey80
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.theme.point3

@Composable
fun MyScreen(
    modifier: Modifier = Modifier,
    viewModel: MyViewModel = hiltViewModel(),
    requireLogin: () -> Unit,
    navigateToReservations: () -> Unit,
    onClickQrScan: () -> Unit,
) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(Unit) {
        viewModel.fetchMyInfo()
    }

    MyScreen(
        modifier = modifier,
        user = user,
        onClickHeaderButton = if (user != null) requireLogin else requireLogin, // TODO 프로필 구현 후 프로필 화면 이동 연결
        onClickReservations = if (user != null) navigateToReservations else requireLogin,
        onClickRegisterShow = {
            val url = if (user != null) "https://boolti.in/home" else "https://boolti.in/login"
            uriHandler.openUri(url)
        },
        onClickQrScan = if (user != null) onClickQrScan else requireLogin,
    )
}

@Composable
fun MyScreen(
    modifier: Modifier = Modifier,
    user: User? = null,
    onClickHeaderButton: () -> Unit = {},
    onClickAccountSetting: () -> Unit = {},
    onClickReservations: () -> Unit = {},
    onClickRegisterShow: () -> Unit = { },
    onClickQrScan: () -> Unit = {},
) {
    Surface {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            Column(
                modifier = modifier,
            ) {
                MyHeader(
                    user = user,
                    onClickButton = onClickHeaderButton,
                )
                MyMenu(
                    modifier = Modifier.padding(top = 20.dp),
                    iconRes = R.drawable.ic_profile,
                    label = stringResource(R.string.account_setting),
                    onClick = onClickAccountSetting,
                )
                MyMenu(
                    iconRes = R.drawable.ic_list,
                    label = stringResource(R.string.my_ticketing_history),
                    onClick = onClickReservations
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 32.dp, horizontal = marginHorizontal),
                    color = MaterialTheme.colorScheme.surfaceTint,
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = marginHorizontal),
                    text = stringResource(R.string.my_show),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                MyMenu(
                    modifier = Modifier.padding(top = 8.dp),
                    iconRes = R.drawable.ic_plus_ticket,
                    label = stringResource(R.string.my_register_show),
                    onClick = onClickRegisterShow,
                )
                MyMenu(
                    iconRes = R.drawable.ic_qr_simple,
                    label = stringResource(R.string.my_scan_qr),
                    onClick = onClickQrScan,
                )
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    modifier = Modifier.size(width = 79.dp, height = 28.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.ic_logo_boolti),
                    tint = Grey80,
                    contentDescription = stringResource(R.string.description_app_logo),
                )
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = "Version ${BuildConfig.VERSION_NAME}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Grey80,
                )
            }
        }
    }
}

@Composable
private fun MyHeader(
    modifier: Modifier = Modifier,
    user: User? = null,
    onClickButton: () -> Unit,
) {
    val shape = RoundedCornerShape(
        bottomStart = 12.dp,
        bottomEnd = 12.dp,
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 28.dp, horizontal = marginHorizontal),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        user?.let {
            AsyncImage(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(36.dp)
                    .clip(shape = CircleShape)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = CircleShape,
                    ),
                model = user.photo,
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.ic_fallback_profile),
                fallback = painterResource(id = R.drawable.ic_fallback_profile),
            )
        }
        Text(
            modifier = Modifier.weight(1f),
            text = user?.nickname ?: stringResource(R.string.my_login),
            style = point3,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (user == null) { // TODO 프로필 기능 추가 후 조건문 제거
            SmallButton(
                modifier = Modifier.padding(start = 16.dp),
                label = if (user != null) {
                    stringResource(R.string.show_profile_button)
                } else {
                    stringResource(R.string.login)
                },
                backgroundColor = Grey80,
                onClick = onClickButton,
            )
        }
    }
}

@Composable
private fun MyMenu(
    @DrawableRes iconRes: Int,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .clickable(
                role = Role.Button,
                onClick = onClick,
            )
            .padding(vertical = 12.dp, horizontal = marginHorizontal),
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = ImageVector.vectorResource(iconRes),
            tint = Grey30,
            contentDescription = label,
        )
        Text(
            modifier = Modifier.padding(start = 12.dp),
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = Grey30,
        )
    }
}

@Preview("로그인 한 유저 헤더")
@Composable
private fun MyHeaderUserPreview() {
    val user = User(
        id = "",
        nickname = "일이삼사오육칠팔구십",
        email = "boolti@gmail.com",
        photo = "https://images.unsplash.com/photo-1721497684662-cf36f0ee232e?q=80&w=4965&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        userCode = "AB1800028",
    )
    BooltiTheme {
        MyHeader(user = user) {}
    }
}

@Preview("게스트 헤더")
@Composable
private fun MyHeaderGuestPreview() {
    BooltiTheme {
        MyHeader(user = null) {}
    }
}

@Preview
@Composable
private fun MyScreenPreview() {
    val user = User(
        id = "",
        nickname = "불티유저",
        email = "boolti@gmail.com",
        photo = "https://images.unsplash.com/photo-1721497684662-cf36f0ee232e?q=80&w=4965&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        userCode = "AB1800028",
    )
    BooltiTheme {
        MyScreen(
            user = user,
        )
    }
}
