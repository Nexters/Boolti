package com.nexters.boolti.presentation.screen.profile

import android.content.ActivityNotFoundException
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.domain.model.Link
import com.nexters.boolti.domain.model.User
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BtAppBarDefaults
import com.nexters.boolti.presentation.component.BtBackAppBar
import com.nexters.boolti.presentation.component.SmallButton
import com.nexters.boolti.presentation.component.UserThumbnail
import com.nexters.boolti.presentation.extension.toValidUrlString
import com.nexters.boolti.presentation.screen.LocalSnackbarController
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.theme.point3

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    navigateToProfileEdit: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileScreen(
        modifier = modifier,
        user = uiState.user,
        isMine = uiState.isMine,
        onClickBack = onClickBack,
        navigateToProfileEdit = navigateToProfileEdit,
    )
}

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    user: User,
    isMine: Boolean,
    onClickBack: () -> Unit,
    navigateToProfileEdit: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val snackbarHostState = LocalSnackbarController.current
    val invalidUrlMsg = stringResource(R.string.invalid_link)

    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier,
        topBar = {
            BtBackAppBar(
                title = stringResource(R.string.profile_title),
                colors = BtAppBarDefaults.appBarColors(containerColor = MaterialTheme.colorScheme.surface),
                onClickBack = onClickBack,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .verticalScroll(scrollState)
                .padding(innerPadding),
        ) {
            ProfileHeader(
                user = user,
                isMine = isMine,
                onClickEdit = navigateToProfileEdit,
            )

            if (user.link.isNotEmpty()) { // SNS 링크가 있으면
                Column(
                    modifier = Modifier.padding(vertical = 32.dp, horizontal = marginHorizontal),
                ) {
                    Text(
                        text = stringResource(R.string.profile_links_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    user.link.forEach { link ->
                        SnsLink(
                            modifier = Modifier.padding(top = 16.dp),
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
        }
    }
}

@Composable
private fun ProfileHeader(
    user: User,
    isMine: Boolean,
    onClickEdit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(
        bottomStart = 12.dp,
        bottomEnd = 12.dp,
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = marginHorizontal)
            .padding(bottom = 32.dp),
    ) {
        UserThumbnail(
            modifier = Modifier.padding(top = 40.dp),
            size = 70.dp,
            model = user.photo,
        )
        Text(
            modifier = Modifier.padding(top = 20.dp),
            text = user.nickname,
            style = point3,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )

        if (user.introduction.isNotBlank()) {
            Text(
                modifier = Modifier.padding(top = 2.dp),
                text = user.introduction,
                color = Grey30,
                style = MaterialTheme.typography.bodyLarge,
            )
        }

        if (isMine) {
            SmallButton(
                modifier = Modifier.padding(top = 28.dp),
                label = stringResource(R.string.profile_edit_button_label),
                iconRes = R.drawable.ic_edit_pen,
                iconTint = Grey30,
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = onClickEdit,
            )
        }
    }
}

@Composable
private fun SnsLink(
    modifier: Modifier = Modifier,
    link: Link,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .clip(RoundedCornerShape(4.dp))
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
