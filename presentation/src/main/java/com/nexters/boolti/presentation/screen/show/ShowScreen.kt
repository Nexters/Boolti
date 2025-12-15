package com.nexters.boolti.presentation.screen.show

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.common.tracker.AppTracker
import com.nexters.boolti.common.tracker.event.click
import com.nexters.boolti.common.tracker.field.Banner
import com.nexters.boolti.common.tracker.field.Home
import com.nexters.boolti.common.tracker.field.Role
import com.nexters.boolti.common.tracker.field.Screen
import com.nexters.boolti.domain.model.Popup
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BusinessInformation
import com.nexters.boolti.presentation.component.NoticeDialog
import com.nexters.boolti.presentation.component.ShowFeed
import com.nexters.boolti.presentation.extension.extractEmphasizedText
import com.nexters.boolti.presentation.theme.Grey05
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.theme.point1
import com.nexters.boolti.presentation.theme.point4

private val screenField = Screen.Home

@Composable
fun ShowScreen(
    navigateToBusiness: () -> Unit,
    onClickShowItem: (showId: String) -> Unit,
    navigateToShowRegistration: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ShowViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    val nickname = user?.nickname ?: ""
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val lazyGridState = rememberLazyGridState()
    var popupToShow: Popup? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ShowEvent.ShowPopup -> popupToShow = event.popup
            }
        }
    }

    Box(
        modifier = modifier.statusBarsPadding(),
        contentAlignment = Alignment.TopCenter,
    ) {
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = marginHorizontal),
            state = lazyGridState,
            columns = GridCells.Adaptive(minSize = 150.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp),
        ) {
            item(
                span = { GridItemSpan(2) },
                contentType = "AppBar",
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 28.dp),
                    text = stringResource(
                        id = R.string.home_sub_title,
                        nickname.ifBlank { stringResource(id = R.string.nickname_default) }),
                    style = point4,
                )
            }

            items(
                count = uiState.shows.size.coerceAtMost(4),
                contentType = { "Show" },
                key = { index -> uiState.shows[index].id }) { index ->
                ShowFeed(
                    show = uiState.shows[index],
                    modifier = Modifier
                        .clickable { onClickShowItem(uiState.shows[index].id) },
                )
            }

            // 4개의 공연 뒤 보이는 배너
            if (uiState.shows.isNotEmpty()) item(
                span = { GridItemSpan(2) },
            ) {
                Banner(
                    modifier = Modifier.fillMaxWidth(),
                    navigateToShowRegistration = {
                        AppTracker.click(
                            screen = screenField,
                            objectRole = Role.Banner,
                            objectValue = "RegisterShow",
                        )
                        navigateToShowRegistration()
                    },
                )
            }

            // 나머지 공연 목록
            items(
                count = (uiState.shows.size - 4).coerceAtLeast(0),
                contentType = { "Show" },
                key = { index -> uiState.shows[index + 4].id }) { index ->
                ShowFeed(
                    show = uiState.shows[index + 4],
                    modifier = Modifier
                        .clickable { onClickShowItem(uiState.shows[index + 4].id) },
                )
            }

            item(
                contentType = "BusinessInformation",
                span = { GridItemSpan(2) },
            ) {
                BusinessInformation(
                    modifier = Modifier.padding(bottom = 12.dp),
                    onClick = navigateToBusiness
                )
            }
        }

        popupToShow?.let { popup ->
            when (popup) {
                is Popup.Event -> {
                    EventDialog(
                        imageUrl = popup.imageUrl,
                        actionUrl = popup.eventUrl,
                        onDismiss = { hideToday ->
                            popupToShow = null
                            if (hideToday) viewModel.hideEventToday(popup.id)
                        },
                    )
                }

                is Popup.Notice -> {
                    val (emphasizedText, remainingText) = popup.description.extractEmphasizedText()

                    NoticeDialog(
                        title = popup.title,
                        emphasizedText = emphasizedText,
                        content = remainingText,
                        onDismiss = { popupToShow = null },
                    )
                }
            }
        }
    }
}

@Composable
private fun Banner(
    navigateToShowRegistration: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clickable {
                navigateToShowRegistration()
            },
    ) {
        Image(
            modifier = Modifier
                .height(80.dp)
                .clip(RoundedCornerShape(8.dp)),
            painter = painterResource(id = R.drawable.background_banner),
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.padding(start = 24.dp)
            ) {
                Text(
                    text = "지금 공연의 불을 지펴보세요!",
                    style = MaterialTheme.typography.labelMedium.copy(color = Grey05),
                )
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = "공연 등록하러 가기",
                    style = point1.copy(
                        color = Grey05,
                        lineHeight = 20.sp,
                    ),
                )
            }
            Image(
                modifier = Modifier
                    .padding(end = 24.dp)
                    .size(80.dp),
                painter = painterResource(id = R.drawable.fire),
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )
        }
    }
}
