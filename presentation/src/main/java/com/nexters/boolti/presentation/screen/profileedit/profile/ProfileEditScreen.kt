package com.nexters.boolti.presentation.screen.profileedit.profile

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.domain.model.Link
import com.nexters.boolti.domain.model.Sns
import com.nexters.boolti.domain.model.UserCode
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTDialog
import com.nexters.boolti.presentation.component.BtAppBar
import com.nexters.boolti.presentation.component.BtAppBarDefaults
import com.nexters.boolti.presentation.component.BtCircularProgressIndicator
import com.nexters.boolti.presentation.component.BtSwitch
import com.nexters.boolti.presentation.component.UserThumbnail
import com.nexters.boolti.presentation.extension.icon
import com.nexters.boolti.presentation.extension.label
import com.nexters.boolti.presentation.screen.LocalSnackbarController
import com.nexters.boolti.presentation.theme.Grey05
import com.nexters.boolti.presentation.theme.Grey15
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.util.ObserveAsEvents
import kotlinx.coroutines.flow.Flow
import org.burnoutcrew.reorderable.ReorderableState
import org.burnoutcrew.reorderable.detectReorder

@Composable
fun ProfileEditScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    navigateToNicknameEdit: (nickname: String) -> Unit,
    navigateToSnsEdit: (Sns?) -> Unit,
    navigateToLinkEdit: (Link?) -> Unit,
    newLinkCallback: Flow<Link>,
    editLinkCallback: Flow<Link>,
    removeLinkCallback: Flow<String>,
    newSnsCallback: Flow<Sns>,
    editSnsCallback: Flow<Sns>,
    removeSnsCallback: Flow<String>,
    viewModel: ProfileEditViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val event = viewModel.event
    val context = LocalContext.current

    /*LaunchedEffect(newLinkCallback) {
        newLinkCallback.collect(viewModel::onNewLinkAdded)
    }
    LaunchedEffect(editLinkCallback) {
        editLinkCallback.collect(viewModel::onLinkEdited)
    }
    LaunchedEffect(removeLinkCallback) {
        removeLinkCallback.collect(viewModel::onLinkRemoved)
    }
    LaunchedEffect(newSnsCallback) {
        newSnsCallback.collect(viewModel::onSnsAdded)
    }
    LaunchedEffect(editSnsCallback) {
        editSnsCallback.collect(viewModel::onSnsEdited)
    }
    LaunchedEffect(removeSnsCallback) {
        removeSnsCallback.collect(viewModel::onSnsRemoved)
    }*/

    ProfileEditScreen(
        modifier = modifier,
        thumbnail = uiState.thumbnail,
        nickname = uiState.nickname,
        userCode = uiState.nickname,
        introduction = uiState.introduction,
        id = uiState.id,
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
        /*        onClickComplete = {
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
                    viewModel.completeEdits(file)
                }*/
//        onChangeNickname = viewModel::changeNickname,
//        onChangeIntroduction = viewModel::changeIntroduction,
        onClickNickname = { navigateToNicknameEdit(uiState.nickname) },
        onClickId = {},
        onClickIntroduction = {},
        onClickAddSns = { navigateToSnsEdit(null) },
        onClickVideo = { },
        onClickLink = { navigateToLinkEdit(null) },
        onClickEditSns = { /* TODO: SNS 수정 기능 구현 */ },
        onClickEditLink = { /* TODO: 링크 수정 기능 구현 */ },
    )
}

@Composable
fun ProfileEditScreen(
    modifier: Modifier = Modifier,
    thumbnail: String,
    nickname: String,
    userCode: UserCode,
    introduction: String,
//    snsList: ImmutableList<Sns>,
//    links: ImmutableList<Link>,
    id: String,
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
//    checkDataChanged: () -> Boolean,
    navigateBack: () -> Unit,
//    onClickComplete: (uri: Uri?) -> Unit,
//    onChangeNickname: (String) -> Unit,
//    onChangeIntroduction: (String) -> Unit,
    onClickNickname: () -> Unit,
    onClickId: () -> Unit,
    onClickIntroduction: () -> Unit,
    onClickVideo: () -> Unit,
    onClickAddSns: () -> Unit,
    onClickLink: () -> Unit,
    onClickEditSns: () -> Unit,
    onClickEditLink: () -> Unit,
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

    var selectedImage by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImage = uri }
    )

    var showExitAlertDialog by remember { mutableStateOf(false) }
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
        modifier = modifier.fillMaxSize(),
        topBar = {
            BtAppBar(
                navigateButtons = {
                    BtAppBarDefaults.AppBarIconButton(
                        onClick = ::tryBack,
                        iconRes = R.drawable.ic_arrow_back,
                    )
                },
                title = stringResource(R.string.profile_edit),
                colors = BtAppBarDefaults.appBarColors(),
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
            ) {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                ) {
                    val (thumbnailImg, cameraIcon) = createRefs()
                    UserThumbnail(
                        modifier = Modifier.constrainAs(thumbnailImg) { centerTo(parent) },
                        size = 100.dp,
                        model = selectedImage ?: thumbnail,
                        contentDescription = stringResource(R.string.description_user_thumbnail),
                    )
                    Surface(
                        modifier = Modifier
                            .constrainAs(cameraIcon) {
                                end.linkTo(thumbnailImg.end, margin = (-7.5).dp)
                                bottom.linkTo(thumbnailImg.bottom)
                            }
                            .size(40.dp),
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 6.dp,
                        onClick = {
                            if (!saving) photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    ) {
                        Image(
                            modifier = Modifier.padding(8.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.ic_camera),
                            contentDescription = stringResource(R.string.change_thumbnail_description),
                        )
                    }
                }
                Section(title = stringResource(R.string.label_information)) {
                    SectionItem(
                        label = stringResource(R.string.label_nickname),
                        value = nickname,
                        defaultValue = userCode,
                        onClick = onClickNickname,
                    )
                    SectionItem(
                        label = stringResource(R.string.label_id),
                        value = id,
                        defaultValue = id,
                        onClick = onClickId,
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
                        onClick = onClickIntroduction,
                        right = { ArrowRight() },
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
                                checked = showUpcomingShows,
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
                                checked = showPerformedShows,
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
                    )
                    SectionItem(
                        label = stringResource(R.string.link),
                        count = videoCount,
                        defaultValue = stringResource(R.string.link_add),
                        onClick = onClickLink,
                    )
                }

                /*val snsReorderState = rememberReorderableLazyListState(
                    onMove = { from, to ->
                        onReorderSns(from.index - 1, to.index - 1)
                    },
                )
                Section(
                    modifier = Modifier.padding(top = 12.dp),
                    title = stringResource(R.string.profile_edit_sns_title),
                ) {
                    LazyColumn(
                        state = snsReorderState.listState,
                        modifier = Modifier
                            .heightIn(max = 100.dp * (snsList.size + 1)) // 대충 넉넉하게 잡은 높이
                            .reorderable(snsReorderState),
                    ) {
                        val snsAddable = !snsList
                            .asSequence()
                            .map { it.type }
                            .toSet()
                            .containsAll(Sns.SnsType.entries)

                        if (snsAddable) {
                            item(
                                contentType = "SnsAddButton",
                            ) {
                                LinkAddButton(
                                    modifier = Modifier.padding(top = 4.dp),
                                    label = stringResource(R.string.sns_add),
                                    onClick = onClickAddSns,
                                    enabled = !saving,
                                )
                            }
                        }
                        items(
                            items = snsList,
                            key = { it.id },
                            contentType = { "SnsItem" },
                        ) { sns ->
                            ReorderableItem(
                                state = snsReorderState,
                                key = sns.id,
                            ) {
                                SnsItem(
                                    modifier = Modifier.padding(top = 12.dp),
                                    sns = sns,
                                    reorderableState = snsReorderState,
                                ) { if (!saving) onClickEditSns(sns) }
                            }
                        }
                    }
                }

                val linkReorderState = rememberReorderableLazyListState(
                    onMove = { from, to ->
                        onReorderLink(from.index - 1, to.index - 1)
                    },
                )
                Section(
                    modifier = Modifier.padding(top = 12.dp),
                    title = stringResource(R.string.label_links),
                ) {
                    LazyColumn(
                        state = linkReorderState.listState,
                        modifier = Modifier
                            .heightIn(max = 100.dp * (links.size + 1)) // 대충 넉넉하게 잡은 높이
                            .reorderable(linkReorderState),
                    ) {
                        item(
                            contentType = "LinkAddButton",
                        ) {
                            LinkAddButton(
                                modifier = Modifier.padding(top = 4.dp),
                                label = stringResource(R.string.link_add_btn),
                                onClick = onClickAddLink,
                                enabled = !saving,
                            )
                        }
                        items(
                            items = links,
                            key = { it.id },
                            contentType = { "LinkItem" },
                        ) { link ->
                            ReorderableItem(
                                state = linkReorderState,
                                key = link.id,
                            ) {
                                LinkItem(
                                    modifier = Modifier.padding(top = 12.dp),
                                    title = link.name,
                                    url = link.url,
                                    reorderableState = linkReorderState,
                                ) { if (!saving) onClickEditLink(link) }
                            }
                        }
                    }
                }

                Spacer(Modifier.size(32.dp))*/
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

@Composable
private fun LinkAddButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 10.dp, horizontal = marginHorizontal),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .border(1.dp, Grey50, CircleShape)
                .padding(8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = Icons.Rounded.Add,
                tint = Grey30,
                contentDescription = label,
            )
        }
        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = label,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
private fun SnsItem(
    sns: Sns,
    modifier: Modifier = Modifier,
    reorderableState: ReorderableState<*>,
    onClickEdit: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClickEdit)
            .padding(horizontal = marginHorizontal, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = ImageVector.vectorResource(sns.type.icon),
            tint = Grey30,
            contentDescription = sns.type.label,
        )
        Text(
            modifier = Modifier
                .padding(start = 8.dp)
                .defaultMinSize(minWidth = 72.dp),
            text = sns.type.label,
            style = MaterialTheme.typography.bodyLarge,
            color = Grey30,
        )
        Text(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .weight(1f),
            text = sns.username,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium,
            color = Grey15,
        )
        Icon(
            modifier = Modifier
                .padding(start = 20.dp)
                .size(20.dp)
                .detectReorder(reorderableState),
            imageVector = ImageVector.vectorResource(R.drawable.ic_reordable_handle),
            tint = Grey50,
            contentDescription = stringResource(R.string.sns_reorder_description),
        )
    }
}

@Composable
private fun LinkItem(
    title: String,
    url: String,
    reorderableState: ReorderableState<*>,
    modifier: Modifier = Modifier,
    onClickEdit: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClickEdit)
            .padding(horizontal = marginHorizontal),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 5.dp)
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = title,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                color = Grey15,
            )
            Text(
                text = url,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall,
                color = Grey30,
                maxLines = 1,
            )
        }
        Icon(
            modifier = Modifier
                .padding(start = 20.dp)
                .size(20.dp)
                .detectReorder(reorderableState),
            imageVector = ImageVector.vectorResource(R.drawable.ic_reordable_handle),
            tint = Grey50,
            contentDescription = stringResource(R.string.link_reorder_description),
        )
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
