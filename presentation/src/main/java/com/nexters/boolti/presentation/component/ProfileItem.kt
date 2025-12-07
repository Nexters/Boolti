package com.nexters.boolti.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.model.UserCode
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey05
import com.nexters.boolti.presentation.theme.Grey50

data class ProfileItemColors(
    val nicknameColor: Color,
    val userCodeColor: Color,
    val backgroundColor: Color,
) {
    companion object {
        @Composable
        fun default(
            nicknameColor: Color = Grey05,
            userCodeColor: Color = Grey50,
            backgroundColor: Color = MaterialTheme.colorScheme.background,
        ) = ProfileItemColors(
            nicknameColor = nicknameColor,
            userCodeColor = userCodeColor,
            backgroundColor = backgroundColor,
        )
    }
}

@Composable
fun ProfileItem(
    profile: User.Others,
    onClick: (UserCode) -> Unit,
    modifier: Modifier = Modifier,
    thumbnailSize: Dp = 68.dp,
    shape: Shape = RoundedCornerShape(4.dp),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    spaceBetween: Dp = 16.dp,
    colors: ProfileItemColors = ProfileItemColors.default(),
    nicknameStyle: TextStyle = MaterialTheme.typography.titleMedium,
    userCodeStyle: TextStyle = MaterialTheme.typography.bodySmall,
) {
    Row(
        modifier = modifier
            .clip(shape)
            .background(colors.backgroundColor, shape)
            .clickable(
                onClick = { onClick(profile.userCode) },
            )
            .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spaceBetween),
    ) {
        UserThumbnail(
            size = thumbnailSize,
            model = profile.photo,
        )

        Column {
            Nickname(
                nickname = profile.nickname,
                style = nicknameStyle,
                color = colors.nicknameColor,
            )
            UserCode(
                userCode = profile.userCode,
                style = userCodeStyle,
                color = colors.userCodeColor,
            )
        }
    }
}

@Composable
private fun Nickname(
    nickname: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleMedium,
    color: Color = Grey05,
) {
    Text(
        modifier = modifier,
        text = nickname,
        style = style,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        color = color,
    )
}

@Composable
private fun UserCode(
    userCode: UserCode,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodySmall,
    color: Color = Grey50,
) {
    Text(
        modifier = modifier,
        text = userCode,
        style = style,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        color = color,
    )
}

@Preview
@Composable
fun ProfileItemPreview() {
    val profile = User.Others(
        nickname = "John Doe",
        photo = null,
        userCode = "B123",
    )
    BooltiTheme {
        ProfileItem(
            modifier = Modifier.fillMaxWidth(),
            profile = profile,
            onClick = {},
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        )
    }
}
