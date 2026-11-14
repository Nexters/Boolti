package com.nexters.boolti.presentation.screen.profileedit.profile

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.nexters.boolti.common.tracker.AppTracker
import com.nexters.boolti.common.tracker.event.view
import com.nexters.boolti.common.tracker.field.ProfileEdit
import com.nexters.boolti.common.tracker.field.Screen
import com.nexters.boolti.domain.model.UserCode
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BtAppBar
import com.nexters.boolti.presentation.component.BtAppBarDefaults
import com.nexters.boolti.presentation.component.BtCircularProgressIndicator
import com.nexters.boolti.presentation.component.BtSwitch
import com.nexters.boolti.presentation.component.FixedWidthText
import com.nexters.boolti.presentation.screen.LocalSnackbarController
import com.nexters.boolti.presentation.theme.Grey05
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.Grey90
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.util.ObserveAsEvents

@Composable
fun ProfileEditScreen(
    navigateUp: () -> Unit,
    navigateToNicknameEdit: () -> Unit,
    navigateToUserCodeEdit: () -> Unit,
    navigateToIntroductionEdit: () -> Unit,
    navigateToSnsEdit: () -> Unit,
    navigateToLinkEdit: (userCode: UserCode) -> Unit,
    navigateToVideoEdit: (userCode: UserCode) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileEditViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarController = LocalSnackbarController.current
    val unknownErrorMsg = stringResource(R.string.message_unknown_error)

    LaunchedEffect(Unit) {
        AppTracker.view(Screen.ProfileEdit)
    }

    ObserveAsEvents(viewModel.event) { event ->
        when (event) {
            ProfileEditEvent.NavigateUp -> navigateUp()
            ProfileEditEvent.NavigateToNicknameEdit -> navigateToNicknameEdit()
            ProfileEditEvent.NavigateToUserCodeEdit -> navigateToUserCodeEdit()
            ProfileEditEvent.NavigateToIntroductionEdit -> navigateToIntroductionEdit()
            ProfileEditEvent.NavigateToSnsEdit -> navigateToSnsEdit()
            is ProfileEditEvent.NavigateToVideoEdit -> navigateToVideoEdit(event.userCode)
            is ProfileEditEvent.NavigateToLinkEdit -> navigateToLinkEdit(event.userCode)
            ProfileEditEvent.ShowUnknownError -> snackbarController.showMessage(unknownErrorMsg)
        }
    }

    BackHandler { viewModel.onAction(ProfileEditAction.ClickBack) }

    ProfileEditScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

@Composable
private fun ProfileEditScreen(
    uiState: ProfileEditUiState,
    onAction: (ProfileEditAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    val appBarBgColor by animateColorAsState(
        targetValue = if (scrollState.canScrollBackward) {
            MaterialTheme.colorScheme.surface
        } else {
            Color.Transparent
        },
        label = "appBarBgColor",
    )

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) onAction(ProfileEditAction.SelectThumbnail(uri.toString()))
        }
    )

    Scaffold(
        modifier = modifier,
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            BtAppBar(
                modifier = Modifier.zIndex(1f),
                navigateButtons = {
                    BtAppBarDefaults.AppBarIconButton(
                        onClick = { onAction(ProfileEditAction.ClickBack) },
                        iconRes = R.drawable.ic_arrow_back,
                    )
                },
                title = stringResource(R.string.profile_edit),
                colors = BtAppBarDefaults.appBarColors(
                    containerColor = appBarBgColor,
                ),
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(bottom = innerPadding.calculateBottomPadding()),
            ) {
                ProfileHeader(
                    modifier = Modifier.fillMaxWidth(),
                    thumbnail = uiState.displayedThumbnail,
                    onClickPhotoButton = {
                        if (!uiState.uploading) photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )
                Section(title = stringResource(R.string.label_information)) {
                    SectionItem(
                        label = stringResource(R.string.label_nickname),
                        value = uiState.nickname,
                        defaultValue = uiState.userCode,
                        onClick = { onAction(ProfileEditAction.ClickNickname) },
                    )
                    SectionItem(
                        label = stringResource(R.string.label_id),
                        value = uiState.userCode,
                        defaultValue = uiState.userCode,
                        onClick = { onAction(ProfileEditAction.ClickUserCode) },
                    )
                    SectionItem(
                        label = stringResource(R.string.label_introduction),
                        value = uiState.introduction,
                        defaultValue = stringResource(R.string.hint_add_introduction),
                        onClick = { onAction(ProfileEditAction.ClickIntroduction) },
                    )
                    SectionItem(
                        label = stringResource(R.string.sns),
                        count = uiState.snsCount,
                        defaultValue = stringResource(R.string.hint_add_sns),
                        onClick = { onAction(ProfileEditAction.ClickSns) },
                        right = if (uiState.snsCount > 0) {
                            { ArrowRight() }
                        } else {
                            null
                        },
                    )
                }
                Spacer(Modifier.height(12.dp))
                Section(title = stringResource(R.string.label_activity_visibility)) {
                    SectionItem(
                        label = stringResource(R.string.label_upcoming_shows),
                        count = uiState.upcomingShowCount,
                        defaultValue = "-",
                        onClick = if (uiState.upcomingShowCount > 0) {
                            { onAction(ProfileEditAction.ToggleUpcomingShows) }
                        } else {
                            null
                        },
                        right = {
                            BtSwitch(
                                checked = uiState.upcomingShowCount > 0 && uiState.showUpcomingShows,
                                enabled = uiState.upcomingShowCount > 0,
                            )
                        },
                    )
                    SectionItem(
                        label = stringResource(R.string.label_past_shows),
                        count = uiState.pastShowCount,
                        defaultValue = "-",
                        onClick = if (uiState.pastShowCount > 0) {
                            { onAction(ProfileEditAction.TogglePastShows) }
                        } else {
                            null
                        },
                        right = {
                            BtSwitch(
                                checked = uiState.pastShowCount > 0 && uiState.showPerformedShows,
                                enabled = uiState.pastShowCount > 0,
                            )
                        },
                    )
                }
                Spacer(Modifier.height(12.dp))
                Section(title = stringResource(R.string.label_video_and_link)) {
                    SectionItem(
                        label = stringResource(R.string.video),
                        count = uiState.videoCount,
                        defaultValue = stringResource(R.string.video_add),
                        onClick = { onAction(ProfileEditAction.ClickVideo) },
                        right = if (uiState.videoCount > 0) {
                            { ArrowRight() }
                        } else {
                            null
                        },
                    )
                    SectionItem(
                        label = stringResource(R.string.link),
                        count = uiState.linkCount,
                        defaultValue = stringResource(R.string.link_add),
                        onClick = { onAction(ProfileEditAction.ClickLink) },
                        right = if (uiState.linkCount > 0) {
                            { ArrowRight() }
                        } else {
                            null
                        },
                    )
                }
            }

            if (uiState.uploading) BtCircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProfileHeader(
    thumbnail: Any,
    onClickPhotoButton: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val defaultProfile = painterResource(R.drawable.ic_profile_placeholder)

    Box(
        modifier = modifier
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
                    model = thumbnail,
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

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(20.dp)
                        .size(40.dp),
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 6.dp,
                    onClick = onClickPhotoButton,
                ) {
                    Image(
                        modifier = Modifier.padding(8.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.ic_camera),
                        contentDescription = stringResource(R.string.change_thumbnail_description),
                    )
                }
            }
        }
    }
}

@Composable
private fun Section(
    modifier: Modifier = Modifier,
    title: String,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 20.dp)
                .fillMaxWidth(),
        ) {
            Text(
                modifier = Modifier.padding(horizontal = marginHorizontal),
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.size(16.dp))
            content()
        }
    }
}

@Composable
private fun SectionItem(
    label: String,
    count: Int,
    defaultValue: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    right: (@Composable RowScope.() -> Unit)? = null,
    labelScrollState: ScrollState = rememberScrollState(),
) {
    SectionItem(
        label = label,
        value = if (count != 0) stringResource(R.string.count, count) else "",
        defaultValue = defaultValue,
        modifier = modifier,
        onClick = onClick,
        right = right,
        labelScrollState = labelScrollState,
    )
}

@Composable
private fun SectionItem(
    label: String,
    value: String,
    defaultValue: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    right: (@Composable RowScope.() -> Unit)? = null,
    labelScrollState: ScrollState = rememberScrollState(),
) {
    Row(
        modifier = modifier.padding(start = marginHorizontal, end = marginHorizontal - 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FixedWidthText(
            text = label,
            width = 100.dp,
            style = MaterialTheme.typography.bodyLarge,
            color = Grey30,
        )
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable(
                    onClick = onClick ?: {},
                    role = Role.Button,
                    enabled = onClick != null,
                    onClickLabel = label,
                )
                .padding(vertical = 10.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = value.ifEmpty { defaultValue },
                style = MaterialTheme.typography.bodyLarge,
                color = if (value.isNotEmpty()) Grey05 else Grey70,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            right?.invoke(this)
        }
    }
}

@Composable
private fun ArrowRight(
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    Icon(
        modifier = modifier.size(20.dp),
        imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_right),
        contentDescription = contentDescription,
        tint = Grey50,
    )
}
