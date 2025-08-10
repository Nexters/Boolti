package com.nexters.boolti.presentation.screen.profile

import android.content.ActivityNotFoundException
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.nexters.boolti.domain.model.Link
import com.nexters.boolti.domain.model.Sns
import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.model.url
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTDialog
import com.nexters.boolti.presentation.component.BtAppBar
import com.nexters.boolti.presentation.component.BtAppBarDefaults
import com.nexters.boolti.presentation.component.ShowItem
import com.nexters.boolti.presentation.extension.toValidUrlString
import com.nexters.boolti.presentation.screen.LocalSnackbarController
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey15
import com.nexters.boolti.presentation.theme.Grey20
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey85
import com.nexters.boolti.presentation.theme.Grey90
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.theme.point3
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    navigateToProfileEdit: () -> Unit,
    navigateToLinks: (userCode: String) -> Unit,
    navigateToPerformedShows: (userCode: String?) -> Unit,
    navigateToShow: (showId: String) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val event = viewModel.event

    ProfileScreen(
        modifier = modifier,
        user = uiState.user,
        isMine = uiState.isMine,
        event = event,
        onClickBack = onClickBack,
        navigateToProfileEdit = navigateToProfileEdit,
        navigateToLinks = {
            navigateToLinks(uiState.user.userCode)
        },
        navigateToPerformedShows = {
            when (uiState.user) {
                is User.My -> navigateToPerformedShows(null)
                is User.Others -> navigateToPerformedShows(uiState.user.userCode)
            }
        },
        navigateToShow = navigateToShow,
    )
}

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    user: User,
    isMine: Boolean,
    event: Flow<ProfileEvent>,
    onClickBack: () -> Unit,
    navigateToProfileEdit: () -> Unit,
    navigateToLinks: () -> Unit,
    navigateToPerformedShows: () -> Unit,
    navigateToShow: (showId: String) -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val snackbarHostState = LocalSnackbarController.current
    val invalidUrlMsg = stringResource(R.string.invalid_link)

    val scrollState = rememberScrollState()
    val appBarTitleAlpha by animateFloatAsState(
        targetValue = if (scrollState.canScrollBackward) {
            1f
        } else {
            0f
        },
        label = "appBarTitleAlpha",
    )

    val appBarBgColor by animateColorAsState(
        targetValue = if (scrollState.canScrollBackward) {
            MaterialTheme.colorScheme.surface
        } else {
            Color.Transparent
        },
        label = "appBarBgColor",
    )

    var backDialogMessage by rememberSaveable { mutableStateOf<String?>(null) }
    val invalidUserMessage = stringResource(R.string.profile_invalid_user_message)
    val withdrawUserMessage = stringResource(R.string.profile_withdraw_user_message)
    val reportFinishedMessage = stringResource(R.string.report_finished)

    LaunchedEffect(event) {
        event.collectLatest {
            when (it) {
                ProfileEvent.Invalid -> backDialogMessage = invalidUserMessage
                ProfileEvent.WithdrawUser -> backDialogMessage = withdrawUserMessage
            }
        }
    }

    Scaffold(
        modifier = modifier,
    ) { innerPadding ->
        ProfileAppBar(
            title = user.nickname,
            titleAlpha = appBarTitleAlpha,
            onClickBack = onClickBack,
            isMine = isMine,
            bgColor = appBarBgColor,
            navigateToProfileEdit = navigateToProfileEdit,
            onReportFinished = { snackbarHostState.showMessage(reportFinishedMessage) },
        )
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(bottom = innerPadding.calculateBottomPadding()),
        ) {
            ProfileHeader(
                modifier = Modifier.fillMaxWidth(),
                user = user,
                onClickSns = { sns ->
                    try {
                        uriHandler.openUri(sns.url.toValidUrlString())
                    } catch (e: ActivityNotFoundException) {
                        snackbarHostState.showMessage(invalidUrlMsg)
                    }
                },
            )

            if (user.link.isNotEmpty() || user.performedShow.isNotEmpty()) {
                Spacer(Modifier.size(8.dp))
            }

            if (user.link.isNotEmpty()) { // 링크가 있으면
                Section(
                    title = stringResource(R.string.profile_links_title),
                    onClickShowAll = if (user.link.size >= 4) {
                        { navigateToLinks() }
                    } else {
                        null
                    },
                ) {
                    user.link.take(3).forEachIndexed { i, link ->
                        LinkItem(
                            modifier = Modifier
                                .padding(top = if (i == 0) 0.dp else 16.dp)
                                .padding(horizontal = marginHorizontal),
                            link = link,
                            onClick = {
                                try {
                                    uriHandler.openUri(link.url.toValidUrlString())
                                } catch (e: ActivityNotFoundException) {
                                    snackbarHostState.showMessage(invalidUrlMsg)
                                }
                            },
                        )
                    }
                }
            }

            if (user.link.isNotEmpty() && user.performedShow.isNotEmpty()) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = marginHorizontal),
                    color = Grey85,
                )
            }

            if (user.performedShow.isNotEmpty()) { // 출연한 공연이 있으면
                Section(
                    title = stringResource(R.string.performed_shows),
                    onClickShowAll = if (user.performedShow.size >= 3) {
                        { navigateToPerformedShows() }
                    } else {
                        null
                    },
                ) {
                    user.performedShow.take(2).forEachIndexed { i, show ->
                        ShowItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = if (i == 0) 0.dp else 20.dp)
                                .padding(horizontal = marginHorizontal),
                            show = show,
                            backgroundColor = MaterialTheme.colorScheme.background,
                            onClick = { navigateToShow(show.id) },
                            contentPadding = PaddingValues(),
                        )
                    }
                }
            }

            Spacer(Modifier.size(32.dp))
        }

        backDialogMessage?.let { msg ->
            BTDialog(
                enableDismiss = false,
                showCloseButton = false,
                positiveButtonLabel = stringResource(R.string.btn_exit),
                onClickPositiveButton = {
                    onClickBack()
                    backDialogMessage = null
                },
                onDismiss = {
                    onClickBack()
                    backDialogMessage = null
                },
            ) {
                Text(
                    text = msg,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ProfileAppBar(
    title: String,
    titleAlpha: Float,
    onClickBack: () -> Unit,
    isMine: Boolean,
    bgColor: Color,
    navigateToProfileEdit: () -> Unit,
    onReportFinished: () -> Unit,
) {
    var showContextMenu by rememberSaveable { mutableStateOf(false) }

    BtAppBar(
        modifier = Modifier.zIndex(1f),
        title = title,
        colors = BtAppBarDefaults.appBarColors(
            titleColor = BtAppBarDefaults.appBarColors().titleColor.copy(alpha = titleAlpha),
            containerColor = bgColor,
        ),
        navigateButtons = {
            BtAppBarDefaults.AppBarIconButton(
                iconRes = R.drawable.ic_arrow_back,
                onClick = onClickBack,
            )
        },
        actionButtons = {
            if (isMine) {
                BtAppBarDefaults.AppBarIconButton(
                    iconRes = R.drawable.ic_edit_pen,
                    description = stringResource(R.string.edit),
                    onClick = navigateToProfileEdit,
                )
            } else {
                BtAppBarDefaults.AppBarIconButton(
                    iconRes = R.drawable.ic_verticle_more,
                    description = stringResource(R.string.description_more_menu),
                    onClick = { showContextMenu = true },
                )
            }
        },
    )
    if (showContextMenu) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.TopEnd),
        ) {
            DropdownMenu(
                modifier = Modifier.background(Grey20),
                expanded = showContextMenu,
                onDismissRequest = { showContextMenu = false },
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(id = R.string.report),
                            color = Color.Black,
                        )
                    },
                    onClick = {
                        showContextMenu = false
                        onReportFinished()
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProfileHeader(
    user: User,
    modifier: Modifier = Modifier,
    onClickSns: (Sns) -> Unit,
) {
    val shape = RoundedCornerShape(
        bottomStart = 20.dp,
        bottomEnd = 20.dp,
    )
    val defaultProfile = painterResource(R.drawable.ic_profile_placeholder)

    Box(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .padding(bottom = 32.dp),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
            ) {
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = user.photo,
                    contentScale = ContentScale.Crop,
                    placeholder = defaultProfile,
                    fallback = defaultProfile,
                    contentDescription = stringResource(R.string.description_user_thumbnail),
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(Grey90.copy(0.2f), Grey90.copy(1f))
                            ),
                        ),
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = marginHorizontal),
                ) {
                    Text(
                        modifier = Modifier.padding(top = 20.dp),
                        text = user.nickname,
                        style = point3,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "@${user.userCode}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Normal,
                        color = Grey50,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = marginHorizontal),
            ) {
                if (user.introduction.isNotBlank()) {
                    Text(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth(),
                        text = user.introduction,
                        color = Grey15,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                if (user.sns.isNotEmpty()) {
                    FlowRow(
                        modifier = Modifier.padding(top = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        user.sns.forEach { sns -> SnsChip(sns) { onClickSns(sns) } }
                    }
                }
            }
        }
    }
}

@Composable
private fun SnsChip(
    sns: Sns,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val (iconRes, desc) = when (sns.type) {
        Sns.SnsType.INSTAGRAM -> R.drawable.ic_logo_instagram to "instagram"
        Sns.SnsType.YOUTUBE -> R.drawable.ic_logo_youtube to "youtube"
    }

    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable(onClick = onClick)
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            imageVector = ImageVector.vectorResource(iconRes),
            tint = Grey15,
            contentDescription = desc,
        )
    }
}

@Composable
private fun Section(
    title: String,
    modifier: Modifier = Modifier,
    onClickShowAll: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 24.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = if (onClickShowAll != null) 0.dp else 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier
                    .padding(start = marginHorizontal)
                    .weight(1f),
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            if (onClickShowAll != null) {
                TextButton(
                    modifier = Modifier.padding(end = 8.dp),
                    onClick = onClickShowAll,
                ) {
                    Text(
                        text = stringResource(R.string.show_all),
                        color = Grey50,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
        content()
    }
}

@Composable
fun LinkItem(
    modifier: Modifier = Modifier,
    link: Link,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = ImageVector.vectorResource(R.drawable.ic_link),
            tint = Grey30,
            contentDescription = stringResource(R.string.label_links),
        )
        Text(
            modifier = Modifier.padding(start = 12.dp),
            text = link.name,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview
@Composable
private fun SectionPreview() {
    BooltiTheme {
        Section(
            title = "링크",
            onClickShowAll = {},
        ) {}
    }
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    val user = User.My(
        id = "",
        nickname = "mangbaam",
        email = "mangbaam@boolti.com",
        photo = null,
        userCode = "oratio",
        introduction = "안녕하세요\n안녕하세요\n안녕하세요\n안녕하세요\n안녕하세요\n안녕하세요\n안녕하세요\n안녕하세요\n",
        sns = listOf(
            Sns("1", Sns.SnsType.INSTAGRAM, "hey__suun"),
            Sns("1", Sns.SnsType.YOUTUBE, "tune_official"),
        ),
        link = listOf(),
        performedShow = listOf(),
    )
    BooltiTheme {
        ProfileScreen(
            user = user,
            isMine = false,
            event = emptyFlow(),
            onClickBack = {},
            navigateToProfileEdit = {},
            navigateToLinks = {},
            navigateToShow = {},
            navigateToPerformedShows = {},
        )
    }
}
