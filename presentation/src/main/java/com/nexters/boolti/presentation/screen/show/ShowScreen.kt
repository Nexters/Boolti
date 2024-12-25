package com.nexters.boolti.presentation.screen.show

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BusinessInformation
import com.nexters.boolti.presentation.component.ShowFeed
import com.nexters.boolti.presentation.component.StatusBarCover
import com.nexters.boolti.presentation.extension.toPx
import com.nexters.boolti.presentation.theme.Grey05
import com.nexters.boolti.presentation.theme.Grey15
import com.nexters.boolti.presentation.theme.Grey60
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.Grey85
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.theme.point1
import com.nexters.boolti.presentation.theme.point4

@OptIn(ExperimentalLayoutApi::class)
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
    val appbarHeight = 196.dp
    val searchBarHeight = 80.dp
    val changeableAppBarHeightPx = (appbarHeight - searchBarHeight).toPx()
    var appbarOffsetHeightPx by rememberSaveable { mutableFloatStateOf(0f) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                appbarOffsetHeightPx += available.y

                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                appbarOffsetHeightPx -= available.y
                return super.onPostScroll(consumed, available, source)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                ShowEvent.Search -> appbarOffsetHeightPx = 0f
            }
        }
    }

    Box(
        modifier = modifier.nestedScroll(nestedScrollConnection),
        contentAlignment = Alignment.TopCenter,
    ) {
        StatusBarCover()
        LazyVerticalGrid(
            modifier = Modifier
                .padding(horizontal = marginHorizontal),
            state = lazyGridState,
            columns = GridCells.Adaptive(minSize = 150.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp),
            contentPadding = PaddingValues(top = 12.dp + appbarHeight),
        ) {
            items(
                count = uiState.shows.size.coerceAtMost(4),
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
                    navigateToShowRegistration = navigateToShowRegistration,
                )
            }

            // 나머지 공연 목록
            items(
                count = (uiState.shows.size - 4).coerceAtLeast(0),
                key = { index -> uiState.shows[index + 4].id }) { index ->
                ShowFeed(
                    show = uiState.shows[index + 4],
                    modifier = Modifier
                        .clickable { onClickShowItem(uiState.shows[index + 4].id) },
                )
            }

            item(
                span = { GridItemSpan(2) },
            ) {
                BusinessInformation(
                    modifier = Modifier.padding(bottom = 12.dp),
                    onClick = navigateToBusiness
                )
            }
        }
        ShowAppBar(
            modifier = Modifier.offset {
                IntOffset(
                    x = 0,
                    y = appbarOffsetHeightPx.coerceAtLeast(-changeableAppBarHeightPx).toInt(),
                )
            },
            nickname = nickname.ifBlank { stringResource(id = R.string.nickname_default) },
            text = uiState.keyword,
            onKeywordChanged = viewModel::updateKeyword,
            search = viewModel::search,
        )
    }
}

@Composable
fun ShowAppBar(
    text: String,
    nickname: String,
    onKeywordChanged: (keyword: String) -> Unit,
    search: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = marginHorizontal)
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = stringResource(id = R.string.home_sub_title, nickname),
            style = point4,
        )
        SearchBar(
            modifier = Modifier
                .padding(top = 8.dp)
                .background(color = MaterialTheme.colorScheme.background)
                .padding(vertical = 16.dp),
            text = text,
            onKeywordChanged = onKeywordChanged,
            search = search,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    text: String,
    onKeywordChanged: (keyword: String) -> Unit,
    search: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val colors = TextFieldDefaults.colors(
        unfocusedIndicatorColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedContainerColor = Grey85,
        focusedContainerColor = Grey85,
    )

    BasicTextField(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        value = text,
        onValueChange = onKeywordChanged,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { search() }),
        decorationBox = { innerTextField ->
            OutlinedTextFieldDefaults.DecorationBox(
                value = text,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                placeholder = {
                    Text(
                        stringResource(id = R.string.search_bar_hint),
                        style = MaterialTheme.typography.bodyLarge.copy(color = Grey70),
                    )
                },
                trailingIcon = {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = null,
                        tint = Grey60,
                    )
                },
                colors = colors,
                interactionSource = interactionSource,
                contentPadding = PaddingValues(horizontal = 12.dp),
            )
        },
        cursorBrush = SolidColor(Color.White),
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = Grey15),
    )
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
