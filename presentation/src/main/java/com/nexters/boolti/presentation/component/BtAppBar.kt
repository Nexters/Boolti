package com.nexters.boolti.presentation.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey10

@Composable
fun BtAppBar(
    modifier: Modifier = Modifier,
    title: String = "",
    colors: BtAppBarColors = BtAppBarDefaults.appBarColors(),
    navigateButtons: @Composable (RowScope.() -> Unit)? = null,
    actionButtons: @Composable (RowScope.() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .background(color = colors.containerColor)
            .fillMaxWidth()
            .height(44.dp)
            .padding(
                start = if (navigateButtons != null) 0.dp else 4.dp,
                end = if (actionButtons != null) 0.dp else 4.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        navigateButtons?.let {
            CompositionLocalProvider(
                LocalContentColor provides colors.navigationIconColor,
                content = { it() },
            )
        }
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(
                    start = if (navigateButtons != null) 0.dp else 16.dp,
                    end = if (actionButtons != null) 0.dp else 16.dp,
                ),
            text = title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium,
            color = colors.titleColor,
        )
        actionButtons?.let {
            CompositionLocalProvider(
                LocalContentColor provides colors.actionIconColor,
                content = { it() },
            )
        }
    }
}

@Composable
fun BtBackAppBar(
    modifier: Modifier = Modifier,
    title: String = "",
    colors: BtAppBarColors = BtAppBarDefaults.appBarColors(),
    onClickBack: () -> Unit,
) {
    BtAppBar(
        modifier = modifier,
        title = title,
        navigateButtons = {
            BtAppBarDefaults.AppBarIconButton(
                onClick = onClickBack,
                iconRes = R.drawable.ic_arrow_back,
            )
        },
        colors = colors,
    )
}

@Composable
fun BtCloseableAppBar(
    modifier: Modifier = Modifier,
    title: String = "",
    colors: BtAppBarColors = BtAppBarDefaults.appBarColors(),
    onClickClose: () -> Unit,
) {
    BtAppBar(
        modifier = modifier,
        title = title,
        actionButtons = {
            BtAppBarDefaults.AppBarIconButton(
                onClick = onClickClose,
                iconRes = R.drawable.ic_close,
                description = stringResource(R.string.description_close_button),
            )
        },
        colors = colors,
    )
}

object BtAppBarDefaults {
    @Composable
    fun AppBarIconButton(
        @DrawableRes iconRes: Int,
        modifier: Modifier = Modifier,
        description: String? = null,
        onClick: () -> Unit,
    ) {
        IconButton(
            modifier = modifier,
            onClick = onClick,
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(iconRes),
                contentDescription = description,
            )
        }
    }

    @Composable
    fun appBarColors(
        containerColor: Color = MaterialTheme.colorScheme.background,
        navigationIconColor: Color = Grey10,
        titleColor: Color = Grey10,
        actionIconColor: Color = Grey10,
    ): BtAppBarColors = BtAppBarColors(
        containerColor = containerColor,
        navigationIconColor = navigationIconColor,
        titleColor = titleColor,
        actionIconColor = actionIconColor,
    )
}

data class BtAppBarColors(
    val containerColor: Color,
    val navigationIconColor: Color,
    val titleColor: Color,
    val actionIconColor: Color,
)

@Preview
@Composable
private fun BackBtAppBarPreview() {
    BooltiTheme {
        Surface {
            BtBackAppBar(title = "title", onClickBack = {})
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun CloseableBtAppBarPreview() {
    BooltiTheme {
        Surface {
            BtCloseableAppBar(title = "", onClickClose = {})
        }
    }
}

@Preview
@Composable
private fun ShowDetailAppBarPreview() {
    BooltiTheme {
        Surface {
            BtAppBar(
                navigateButtons = {
                    BtAppBarDefaults.AppBarIconButton(
                        iconRes = R.drawable.ic_arrow_back,
                        description = stringResource(id = R.string.description_navigate_back),
                        onClick = {},
                    )
                    BtAppBarDefaults.AppBarIconButton(
                        iconRes = R.drawable.ic_home,
                        description = stringResource(id = R.string.description_toolbar_home),
                        onClick = {},
                    )
                },
                actionButtons = {
                    BtAppBarDefaults.AppBarIconButton(
                        iconRes = R.drawable.ic_share,
                        description = stringResource(id = R.string.ticketing_share),
                        onClick = {},
                    )
                    BtAppBarDefaults.AppBarIconButton(
                        iconRes = R.drawable.ic_verticle_more,
                        description = stringResource(id = R.string.description_more_menu),
                        onClick = {},
                    )
                }
            )
        }
    }
}
