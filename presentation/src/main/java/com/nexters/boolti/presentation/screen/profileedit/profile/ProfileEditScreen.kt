package com.nexters.boolti.presentation.screen.profileedit.profile

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.nexters.boolti.domain.model.UserCode
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTDialog
import com.nexters.boolti.presentation.component.BtAppBar
import com.nexters.boolti.presentation.component.BtAppBarDefaults
import com.nexters.boolti.presentation.component.BtCircularProgressIndicator
import com.nexters.boolti.presentation.component.BtSwitch
import com.nexters.boolti.presentation.screen.LocalSnackbarController
import com.nexters.boolti.presentation.theme.Grey05
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.Grey90
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.util.ObserveAsEvents
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@Composable
fun ProfileEditScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    navigateToNicknameEdit: () -> Unit,
    navigateToUserCodeEdit: () -> Unit,
    navigateToIntroductionEdit: () -> Unit,
    navigateToSnsEdit: () -> Unit,
    navigateToLinkEdit: (userCode: UserCode) -> Unit,
    navigateToVideoEdit: (userCode: UserCode) -> Unit,
    viewModel: ProfileEditViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val event = viewModel.event

    ProfileEditScreen(
        modifier = modifier,
        thumbnail = uiState.thumbnail,
        nickname = uiState.nickname,
        userCode = uiState.userCode,
        introduction = uiState.introduction,
        snsCount = uiState.snsCount,
        upcomingShowCount = uiState.upcomingShowCount,
        pastShowCount = uiState.pastShowCount,
        showUpcomingShows = uiState.showUpcomingShows,
        showPerformedShows = uiState.showPerformedShows,
        onClickUpcomingShows = viewModel::toggleShowUpcomingShows,
        onClickPastShows = viewModel::toggleShowPerformedShows,
        videoCount = uiState.videoCount,
        linkCount = uiState.linkCount,
        saving = uiState.saving,
        event = event,
        navigateBack = navigateBack,
        onChangeThumbnail = {
            val file = it?.let { uri ->
                val file = File(context.cacheDir, "temp_profile_image.jpg")
                try {
                    context.contentResolver.openInputStream(uri).use { inputStream ->
                        FileOutputStream(file).use { outputStream ->
                            inputStream?.copyTo(outputStream)
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                file
            }
            viewModel.changeThumbnail(file)
        },
        onClickNickname = navigateToNicknameEdit,
        onClickUserCode = navigateToUserCodeEdit,
        onClickIntroduction = navigateToIntroductionEdit,
        onClickSns = navigateToSnsEdit,
        onClickVideo = { navigateToVideoEdit(uiState.userCode) },
        onClickLink = { navigateToLinkEdit(uiState.userCode) },
    )
}

@Composable
private fun ProfileEditScreen(
    modifier: Modifier = Modifier,
    thumbnail: String,
    nickname: String,
    userCode: UserCode,
    introduction: String,
    snsCount: Int,
    upcomingShowCount: Int,
    pastShowCount: Int,
    showUpcomingShows: Boolean,
    showPerformedShows: Boolean,
    onClickUpcomingShows: () -> Unit,
    onClickPastShows: () -> Unit,
    videoCount: Int,
    linkCount: Int,
    saving: Boolean,
    event: Flow<ProfileEditEvent>,
    navigateBack: () -> Unit,
    onChangeThumbnail: (Uri?) -> Unit,
    onClickNickname: () -> Unit,
    onClickUserCode: () -> Unit,
    onClickIntroduction: () -> Unit,
    onClickSns: () -> Unit,
    onClickVideo: () -> Unit,
    onClickLink: () -> Unit,
) {
    val scrollState = rememberScrollState()
    val snackbarHostState = LocalSnackbarController.current

    val linkAddMsg = stringResource(R.string.link_add_msg)
    val linkEditMsg = stringResource(R.string.link_edit_msg)
    val linkRemoveMsg = stringResource(R.string.link_remove_msg)
    val snsAddMsg = stringResource(R.string.sns_add_msg)
    val snsEditMsg = stringResource(R.string.sns_edit_msg)
    val snsRemoveMsg = stringResource(R.string.sns_remove_msg)
    val profileEditSuccessMsg = stringResource(R.string.profile_edit_success_msg)
    val unknownErrorMsg = stringResource(R.string.message_unknown_error)

    val appBarBgColor by animateColorAsState(
        targetValue = if (scrollState.canScrollBackward) {
            MaterialTheme.colorScheme.surface
        } else {
            Color.Transparent
        },
        label = "appBarBgColor",
    )

    var selectedImage by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            selectedImage = uri
            onChangeThumbnail(uri)
        }
    )

    var showUnAuthorizedDialog by remember { mutableStateOf(false) }

    fun tryBack() {
        navigateBack()
    }

    BackHandler { tryBack() }

    ObserveAsEvents(event) {
        when (it) {
            ProfileEditEvent.OnLinkAdded -> snackbarHostState.showMessage(linkAddMsg)
            ProfileEditEvent.OnLinkEdited -> snackbarHostState.showMessage(linkEditMsg)
            ProfileEditEvent.OnLinkRemoved -> snackbarHostState.showMessage(linkRemoveMsg)
            ProfileEditEvent.OnSnsAdded -> snackbarHostState.showMessage(snsAddMsg)
            ProfileEditEvent.OnSnsEdited -> snackbarHostState.showMessage(snsEditMsg)
            ProfileEditEvent.OnSnsRemoved -> snackbarHostState.showMessage(snsRemoveMsg)
            ProfileEditEvent.OnSuccessEditProfile -> {
                snackbarHostState.showMessage(profileEditSuccessMsg)
                navigateBack()
            }

            ProfileEditEvent.UnAuthorized -> showUnAuthorizedDialog = true
            ProfileEditEvent.EditFailed -> snackbarHostState.showMessage(unknownErrorMsg)
        }
    }

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
                        onClick = ::tryBack,
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
                    thumbnail = selectedImage ?: thumbnail,
                    onClickPhotoButton = {
                        if (!saving) photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )
                Section(title = stringResource(R.string.label_information)) {
                    SectionItem(
                        label = stringResource(R.string.label_nickname),
                        value = nickname,
                        defaultValue = userCode,
                        onClick = onClickNickname,
                    )
                    SectionItem(
                        label = stringResource(R.string.label_id),
                        value = userCode,
                        defaultValue = userCode,
                        onClick = onClickUserCode,
                    )
                    SectionItem(
                        label = stringResource(R.string.label_introduction),
                        value = introduction,
                        defaultValue = stringResource(R.string.hint_add_introduction),
                        onClick = onClickIntroduction,
                    )
                    SectionItem(
                        label = stringResource(R.string.sns),
                        count = snsCount,
                        defaultValue = stringResource(R.string.hint_add_sns),
                        onClick = onClickSns,
                        right = if (snsCount > 0) {
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
                        count = upcomingShowCount,
                        defaultValue = "-",
                        onClick = if (upcomingShowCount > 0) {
                            onClickUpcomingShows
                        } else {
                            null
                        },
                        right = {
                            BtSwitch(
                                checked = upcomingShowCount > 0 && showUpcomingShows,
                                enabled = upcomingShowCount > 0,
                            )
                        },
                    )
                    SectionItem(
                        label = stringResource(R.string.label_past_shows),
                        count = pastShowCount,
                        defaultValue = "-",
                        onClick = if (pastShowCount > 0) {
                            onClickPastShows
                        } else {
                            null
                        },
                        right = {
                            BtSwitch(
                                checked = pastShowCount > 0 && showPerformedShows,
                                enabled = pastShowCount > 0,
                            )
                        },
                    )
                }
                Spacer(Modifier.height(12.dp))
                Section(title = stringResource(R.string.label_video_and_link)) {
                    SectionItem(
                        label = stringResource(R.string.video),
                        count = videoCount,
                        defaultValue = stringResource(R.string.video_add),
                        onClick = onClickVideo,
                        right = if (videoCount > 0) {
                            { ArrowRight() }
                        } else {
                            null
                        },
                    )
                    SectionItem(
                        label = stringResource(R.string.link),
                        count = linkCount,
                        defaultValue = stringResource(R.string.link_add),
                        onClick = onClickLink,
                        right = if (linkCount > 0) {
                            { ArrowRight() }
                        } else {
                            null
                        },
                    )
                }
            }

            if (saving) BtCircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        if (showUnAuthorizedDialog) {
            BTDialog(
                enableDismiss = false,
                showCloseButton = false,
                onDismiss = navigateBack,
                onClickPositiveButton = navigateBack,
                positiveButtonLabel = stringResource(R.string.btn_exit),
            ) {
                Text(
                    text = unknownErrorMsg,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
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
) {
    SectionItem(
        label = label,
        value = if (count != 0) stringResource(R.string.count, count) else "",
        defaultValue = defaultValue,
        modifier = modifier,
        onClick = onClick,
        right = right,
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
) {
    Row(
        modifier = modifier
            .clickable(
                onClick = onClick ?: {},
                role = Role.Button,
                enabled = onClick != null,
                onClickLabel = label,
            )
            .padding(vertical = 10.dp, horizontal = marginHorizontal),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.width(92.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = Grey30,
                maxLines = 1,
            )
        }
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
