package com.nexters.boolti.presentation.screen.show

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BusinessInformation
import com.nexters.boolti.presentation.component.ShowFeed
import com.nexters.boolti.presentation.extension.toPx
import com.nexters.boolti.presentation.theme.Grey15
import com.nexters.boolti.presentation.theme.Grey60
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.Grey85
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.theme.point4

@Composable
fun ShowScreen(
    navigateToReservations: () -> Unit,
    onClickShowItem: (showId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ShowViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    val nickname = user?.nickname ?: ""
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val lazyGridState = rememberLazyGridState()
    val appbarHeight = if (uiState.hasPendingTicket) 196.dp + 52.dp else 196.dp
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
        viewModel.fetchReservationInfo()
        viewModel.events.collect { event ->
            when (event) {
                ShowEvent.Search -> appbarOffsetHeightPx = 0f
            }
        }
    }

    Scaffold(
        modifier = modifier.nestedScroll(nestedScrollConnection),
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding),
            contentAlignment = Alignment.TopCenter,
        ) {
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
                    count = uiState.shows.size,
                    key = { index -> uiState.shows[index].id }) { index ->
                    ShowFeed(
                        show = uiState.shows[index],
                        modifier = Modifier
                            .clickable { onClickShowItem(uiState.shows[index].id) },
                    )
                }

                item(
                    span = { GridItemSpan(2) },
                ) {
                    BusinessInformation(
                        modifier = Modifier.padding(bottom = 12.dp)
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
                navigateToReservations = navigateToReservations,
                hasPendingTicket = uiState.hasPendingTicket,
                nickname = nickname.ifBlank { stringResource(id = R.string.nickname_default) },
                text = uiState.keyword,
                onKeywordChanged = viewModel::updateKeyword,
                search = viewModel::search,
            )
        }
    }
}

@Composable
fun ShowAppBar(
    text: String,
    hasPendingTicket: Boolean,
    navigateToReservations: () -> Unit,
    nickname: String,
    onKeywordChanged: (keyword: String) -> Unit,
    search: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = marginHorizontal)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        if (hasPendingTicket) Banner(
            modifier = Modifier.fillMaxWidth(),
            navigateToReservations = navigateToReservations,
        )
        Spacer(modifier = Modifier.height(20.dp))
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
    navigateToReservations: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(52.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(MaterialTheme.colorScheme.primary, Color(0xFFEB955B))
                ),
                shape = RoundedCornerShape(4.dp),
            )
            .clickable(onClick = navigateToReservations)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(R.string.has_pending_ticket),
            style = MaterialTheme.typography.bodyLarge,
        )
        Icon(painter = painterResource(id = R.drawable.ic_arrow_right), contentDescription = null)
    }
}
