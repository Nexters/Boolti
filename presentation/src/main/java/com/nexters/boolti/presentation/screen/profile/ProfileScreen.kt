package com.nexters.boolti.presentation.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nexters.boolti.domain.model.User
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BtAppBarDefaults
import com.nexters.boolti.presentation.component.BtBackAppBar
import com.nexters.boolti.presentation.component.SmallButton
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.theme.point3

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    navigateToProfileEdit: () -> Unit,
) {
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
            modifier = modifier.padding(innerPadding),
        ) {
            ProfileHeader(
                user = User("", "암표상인", "", "", ""),
                onClickEdit = navigateToProfileEdit,
            )

            if (true) { // SNS 링크가 있으면
                Column(
                    modifier = Modifier.padding(vertical = 32.dp, horizontal = marginHorizontal),
                ) {
                    Text("SNS 링크")
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    user: User,
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
            .padding(horizontal = marginHorizontal),
    ) {
        AsyncImage(
            modifier = Modifier
                .padding(top = 40.dp)
                .size(70.dp)
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
        Text(
            modifier = Modifier.padding(top = 20.dp),
            text = user.nickname,
            style = point3,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        SmallButton(
            modifier = Modifier.padding(top = 28.dp, bottom = 32.dp),
            label = stringResource(R.string.profile_edit_button_label),
            iconRes = R.drawable.ic_edit_pen,
            iconTint = Grey30,
            backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
            onClick = onClickEdit,
        )
    }
}

@Composable
private fun SnsLink(
    modifier: Modifier = Modifier,
    label: String,
    link: String,
    onClick: (link: String) -> Unit,
) {
    Row {
        Text(label)
    }
}
