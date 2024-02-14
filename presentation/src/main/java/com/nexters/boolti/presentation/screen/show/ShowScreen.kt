package com.nexters.boolti.presentation.screen.show

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.ShowFeed
import com.nexters.boolti.presentation.theme.Grey15
import com.nexters.boolti.presentation.theme.Grey60
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.Grey85
import com.nexters.boolti.presentation.theme.aggroFamily
import com.nexters.boolti.presentation.theme.marginHorizontal

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
    val appbarHeight = if(uiState.hasPendingTicket) 196.dp + 52.dp else 196.dp
    val searchBarHeight = 80.dp
    val changeableAppBarHeightPx =
        with(LocalDensity.current) { (appbarHeight - searchBarHeight).roundToPx().toFloat() }
    var appbarOffsetHeightPx by rememberSaveable { mutableFloatStateOf(0f) }
    var changeableAppBarHeight by remember { mutableFloatStateOf(0f) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                appbarOffsetHeightPx += available.y

                return Offset.Zero
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.refresh()
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
            }
            ShowAppBar(
                modifier = Modifier.offset {
                    IntOffset(
                        x = 0,
                        y = appbarOffsetHeightPx.coerceIn(-changeableAppBarHeightPx, 0f).toInt()
                    )
                },
                navigateToReservations = navigateToReservations,
                hasPendingTicket = uiState.hasPendingTicket,
                nickname = nickname.ifBlank { stringResource(id = R.string.nickname_default) },
                text = uiState.keyword,
                onKeywordChanged = viewModel::updateKeyword,
                onChangeableSizeChanged = { size ->
                    changeableAppBarHeight = size.height.toFloat()
                },
                search = viewModel::search,
            )
        }
    }
}

/**
 * @param onChangeableSizeChanged 변할 수 있는 최대 사이즈를 전달 app bar height - search bar
 */
@Composable
fun ShowAppBar(
    text: String,
    hasPendingTicket: Boolean,
    navigateToReservations: () -> Unit,
    nickname: String,
    onKeywordChanged: (keyword: String) -> Unit,
    onChangeableSizeChanged: (size: IntSize) -> Unit,
    search: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var appBarHeight by remember { mutableFloatStateOf(0f) }
    val searchBarHeight = with(LocalDensity.current) { 80.dp.toPx() }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = marginHorizontal)
            .onSizeChanged(onSizeChanged = { size ->
                appBarHeight = size.height.toFloat()
                onChangeableSizeChanged(IntSize(0, size.height - searchBarHeight.toInt()))
            })
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
            style = TextStyle(
                lineHeight = 34.sp,
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp,
                fontFamily = aggroFamily,
            ),
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

@Composable
fun SearchBar(
    text: String,
    onKeywordChanged: (keyword: String) -> Unit,
    search: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TextField(
        modifier = modifier.fillMaxWidth(),
        value = text,
        onValueChange = onKeywordChanged,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { search() }),
        placeholder = {
            Text(
                stringResource(id = R.string.search_bar_hint),
                style = MaterialTheme.typography.bodyLarge.copy(color = Grey70),
            )
        },
        shape = RoundedCornerShape(4.dp),
        trailingIcon = {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "검색",
                tint = Grey60,
            )
        },
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = Grey15),
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            cursorColor = Color.White,
            unfocusedContainerColor = Grey85,
            focusedContainerColor = Grey85,
        ),
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