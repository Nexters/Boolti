package com.nexters.boolti.presentation.screen.profileedit.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTTextField
import com.nexters.boolti.presentation.component.BtAppBar
import com.nexters.boolti.presentation.component.BtAppBarDefaults
import com.nexters.boolti.presentation.theme.marginHorizontal
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun ProfileEditScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    navigateToLinkEdit: (Pair<String, String>?) -> Unit,
    viewModel: ProfileEditViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileEditScreen(
        modifier = modifier,
        thumbnail = uiState.thumbnail,
        nickname = uiState.nickname,
        introduction = uiState.introduction,
        links = uiState.links.toImmutableList(),
        onClickBack = navigateBack,
        onClickComplete = viewModel::completeEdits,
        onChangeNickname = viewModel::changeNickname,
        onChangeIntroduction = viewModel::changeIntroduction,
        onClickAddLink = { navigateToLinkEdit(null) },
        onClickEditLink = { navigateToLinkEdit(uiState.links[it]) },
    )
}

@Composable
fun ProfileEditScreen(
    modifier: Modifier = Modifier,
    thumbnail: String,
    nickname: String,
    introduction: String,
    links: ImmutableList<Pair<String, String>>,
    onClickBack: () -> Unit,
    onClickComplete: () -> Unit,
    onChangeNickname: (String) -> Unit,
    onChangeIntroduction: (String) -> Unit,
    onClickAddLink: () -> Unit,
    onClickEditLink: (index: Int) -> Unit,
) {
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
                    TextButton(
                        onClick = onClickComplete,
                    ) {
                        Text(
                            text = stringResource(R.string.complete),
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                },
                title = stringResource(R.string.profile_edit),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
        ) {
            Box {
                AsyncImage(
                    modifier = Modifier
                        .padding(vertical = 40.dp)
                        .align(Alignment.Center),
                    model = thumbnail,
                    contentDescription = stringResource(R.string.description_user_thumbnail),
                )
            }
            Section(title = stringResource(R.string.label_nickname)) {
                BTTextField(
                    modifier = Modifier
                        .padding(marginHorizontal)
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
            ) { }
            Section(
                modifier = Modifier.padding(top = 12.dp),
                title = stringResource(R.string.label_links),
            ) { }
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
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                title, modifier = Modifier
                    .padding(top = 20.dp)
                    .padding(horizontal = marginHorizontal)
            )
            Spacer(Modifier.size(16.dp))
            content()
        }
    }
}
