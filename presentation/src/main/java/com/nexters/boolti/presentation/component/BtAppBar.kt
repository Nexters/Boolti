package com.nexters.boolti.presentation.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

@Composable
fun BtAppBar(
    modifier: Modifier = Modifier,
    title: String = "",
    colors: BtAppBarColors = BtAppBarDefaults.appBarColors(),
    statusBarPadding: Boolean = true,
    navigateButtons: @Composable (RowScope.() -> Unit)? = null,
    actionButtons: @Composable (RowScope.() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .background(color = colors.containerColor)
            .let { if (statusBarPadding) it.statusBarsPadding() else it }
            .fillMaxWidth()
            .height(44.dp)
            .padding(horizontal = 8.dp),
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
            style = MaterialTheme.typography.titleLarge,
            color = colors.titleColor,
        )
        actionButtons?.let {
            CompositionLocalProvider(
                LocalContentColor provides colors.actionIconColor,
                LocalTextStyle provides MaterialTheme.typography.titleLarge.copy(color = colors.actionIconColor),
                content = { it() },
            )
        }
    }
}

@Composable
fun BtAppBar(
    modifier: Modifier = Modifier,
    title: String = "",
    colors: BtAppBarColors = BtAppBarDefaults.appBarColors(),
    statusBarPadding: Boolean = true,
    navigationButtons: List<Pair<Int, () -> Unit>> = emptyList(),
    actionButtons: List<Pair<Int, () -> Unit>> = emptyList(),
) {
    BtAppBar(
        modifier = modifier,
        title = title,
        colors = colors,
        statusBarPadding = statusBarPadding,
        navigateButtons = if (navigationButtons.isNotEmpty()) {
            {
                navigationButtons.forEach { (res, onClick) ->
                    BtAppBarDefaults.AppBarIconButton(iconRes = res, onClick = onClick)
                }
            }
        } else {
            null
        },
        actionButtons = if (actionButtons.isNotEmpty()) {
            {
                actionButtons.forEach { (res, onClick) ->
                    BtAppBarDefaults.AppBarIconButton(iconRes = res, onClick = onClick)
                }
            }
        } else {
            null
        },
    )
}

@Composable
fun BtBackAppBar(
    modifier: Modifier = Modifier,
    title: String = "",
    colors: BtAppBarColors = BtAppBarDefaults.appBarColors(),
    statusBarPadding: Boolean = true,
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
        statusBarPadding = statusBarPadding,
    )
}

@Composable
fun BtCloseableAppBar(
    modifier: Modifier = Modifier,
    title: String = "",
    colors: BtAppBarColors = BtAppBarDefaults.appBarColors(),
    statusBarPadding: Boolean = true,
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
        statusBarPadding = statusBarPadding,
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
    fun AppBarTextButton(
        label: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        color: Color = MaterialTheme.colorScheme.onBackground,
        enabled: Boolean = true,
    ) {
        TextButton(
            onClick = onClick,
            modifier = modifier,
            colors = ButtonDefaults.textButtonColors(
                contentColor = color,
            ),
            enabled = enabled,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }

    @Composable
    fun appBarColors(
        containerColor: Color = MaterialTheme.colorScheme.background,
        navigationIconColor: Color = MaterialTheme.colorScheme.onBackground,
        titleColor: Color = MaterialTheme.colorScheme.onBackground,
        actionIconColor: Color = MaterialTheme.colorScheme.onBackground,
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
private fun AppBarPreview() {
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

@Preview
@Composable
private fun AppBar2Preview() {
    BooltiTheme {
        Surface {
            BtAppBar(
                navigationButtons = listOf(
                    R.drawable.ic_arrow_back to { /* clicked */ },
                    R.drawable.ic_home to { /* clicked */ },
                ),
            )
        }
    }
}
