package com.nexters.boolti.presentation.screen.profileedit.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.nexters.boolti.domain.model.Link
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTTextField
import com.nexters.boolti.presentation.component.BtAppBar
import com.nexters.boolti.presentation.component.BtAppBarDefaults
import com.nexters.boolti.presentation.screen.LocalSnackbarController
import com.nexters.boolti.presentation.theme.Grey15
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.util.ObserveAsEvents
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow

@Composable
fun ProfileEditScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    navigateToLinkEdit: (Link?) -> Unit,
    newLinkCallback: Flow<Link>,
    editLinkCallback: Flow<Link>,
    viewModel: ProfileEditViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val event = viewModel.event

    LaunchedEffect(newLinkCallback) {
        newLinkCallback.collect(viewModel::onNewLinkAdded)
    }
    LaunchedEffect(editLinkCallback) {
        editLinkCallback.collect(viewModel::onLinkEditted)
    }

    ProfileEditScreen(
        modifier = modifier,
        thumbnail = uiState.thumbnail,
        nickname = uiState.nickname,
        introduction = uiState.introduction,
        links = uiState.links.toImmutableList(),
        event = event,
        onClickBack = navigateBack,
        onClickComplete = viewModel::completeEdits,
        onChangeNickname = viewModel::changeNickname,
        onChangeIntroduction = viewModel::changeIntroduction,
        onClickAddLink = { navigateToLinkEdit(null) },
        onClickEditLink = { link -> navigateToLinkEdit(link) },
    )
}

@Composable
fun ProfileEditScreen(
    modifier: Modifier = Modifier,
    thumbnail: String,
    nickname: String,
    introduction: String,
    links: ImmutableList<Link>,
    event: Flow<ProfileEditEvent>,
    onClickBack: () -> Unit,
    onClickComplete: () -> Unit,
    onChangeNickname: (String) -> Unit,
    onChangeIntroduction: (String) -> Unit,
    onClickAddLink: () -> Unit,
    onClickEditLink: (Link) -> Unit,
) {
    val scrollState = rememberScrollState()
    val snackbarHostState = LocalSnackbarController.current

    val linkAddMsg = stringResource(R.string.link_add_msg)
    val linkEditMsg = stringResource(R.string.link_edit_msg)
    val linkRemoveMsg = stringResource(R.string.link_remove_msg)

    LaunchedEffect(event) {
        event.collect {
            when (it) {
                ProfileEditEvent.OnLinkAdded -> snackbarHostState.showMessage(linkAddMsg)
                ProfileEditEvent.OnLinkEditted -> snackbarHostState.showMessage(linkEditMsg)
                ProfileEditEvent.OnLinkRemoved -> snackbarHostState.showMessage(linkRemoveMsg)
            }
        }
    }
    ObserveAsEvents(event) {
        when (it) {
            ProfileEditEvent.OnLinkAdded -> snackbarHostState.showMessage(linkAddMsg)
            ProfileEditEvent.OnLinkEditted -> snackbarHostState.showMessage(linkEditMsg)
            ProfileEditEvent.OnLinkRemoved -> snackbarHostState.showMessage(linkRemoveMsg)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            BtAppBar(
                navigateButtons = {
                    BtAppBarDefaults.AppBarIconButton(
                        onClick = onClickBack,
                        iconRes = R.drawable.ic_arrow_back,
                    )
                },
                actionButtons = {
                    BtAppBarDefaults.AppBarTextButton(
                        label = stringResource(R.string.complete),
                        onClick = onClickComplete,
                    )
                },
                title = stringResource(R.string.profile_edit),
                colors = BtAppBarDefaults.appBarColors(),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .verticalScroll(scrollState),
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
            ) {
                val (thumbnailImg, cameraIcon) = createRefs()
                AsyncImage(
                    modifier = Modifier
                        .constrainAs(thumbnailImg) {
                            centerTo(parent)
                        }
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
                    model = thumbnail,
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
                ) {
                    Image(
                        modifier = Modifier.padding(8.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.ic_camera),
                        contentDescription = stringResource(R.string.change_thumbnail_description),
                    )
                }
            }
            Section(title = stringResource(R.string.label_nickname)) {
                BTTextField(
                    modifier = Modifier
                        .padding(horizontal = marginHorizontal)
                        .fillMaxWidth(),
                    text = nickname,
                    placeholder = stringResource(R.string.profile_edit_nickname_placeholder),
                    onValueChanged = onChangeNickname,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                    ),
                    singleLine = true,
                )
            }
            Section(
                modifier = Modifier.padding(top = 12.dp),
                title = stringResource(R.string.label_introduction)
            ) {
                BTTextField(
                    modifier = Modifier
                        .padding(horizontal = marginHorizontal)
                        .fillMaxWidth()
                        .height(122.dp),
                    text = introduction,
                    placeholder = stringResource(R.string.profile_edit_introduction_placeholder),
                    onValueChanged = onChangeIntroduction,
                )
            }
            Section(
                modifier = Modifier.padding(top = 12.dp),
                title = stringResource(R.string.label_links),
            ) {
                Column {
                    LinkAddButton(
                        modifier = Modifier.padding(top = 4.dp),
                        onClick = onClickAddLink,
                    )
                    links.forEach { link ->
                        LinkItem(
                            modifier = Modifier.padding(top = 12.dp),
                            title = link.name,
                            url = link.url,
                        ) { onClickEditLink(link) }
                    }
                }
            }
        }
    }
}

@Composable
private fun LinkAddButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
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
                contentDescription = stringResource(R.string.link_add_btn),
            )
        }
        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = stringResource(R.string.link_add_btn),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
private fun LinkItem(
    title: String,
    url: String,
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
            )
        }
        Icon(
            modifier = Modifier.padding(start = 20.dp),
            imageVector = ImageVector.vectorResource(R.drawable.ic_edit_pen),
            tint = Grey50,
            contentDescription = stringResource(R.string.link_edit),
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
            )
            Spacer(Modifier.size(16.dp))
            content()
        }
    }
}
