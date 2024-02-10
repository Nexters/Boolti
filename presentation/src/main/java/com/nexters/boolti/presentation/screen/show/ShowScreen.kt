package com.nexters.boolti.presentation.screen.show

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
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
    modifier: Modifier = Modifier,
    onClickShowItem: (showId: String) -> Unit,
    viewModel: ShowViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var offsetY by remember { mutableFloatStateOf(0f) }
    val appbarHeight = 116 * 2.625f//by remember { mutableFloatStateOf(0f) }
    var progress by remember { mutableFloatStateOf(0f) }
    val lazyGridState = rememberLazyGridState()
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {

                // LazyGrid 스크롤을 내려야 할 때
                if (progress == 1.0f && available.y < 0) {
                    return Offset.Zero
                }

                // LazyGrid 스크롤을 올려야 할 때
                if (progress == 1.0f && available.y > 0 && lazyGridState.canScrollBackward) {
                    return Offset.Zero
                }

                // 앱바를 움직여야 할 때!
                offsetY += available.y
                offsetY = offsetY.coerceIn(-appbarHeight, 0f)
                progress = -offsetY / appbarHeight
                println("프로그레스 : $progress")

                return available
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
                    .padding(horizontal = marginHorizontal)
                    .padding(top = 208.dp - ((appbarHeight / 2.625f) * progress).dp),
                columns = GridCells.Adaptive(minSize = 150.dp),
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp),
                contentPadding = PaddingValues(top = 12.dp),
                state = lazyGridState,
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
                progress = progress,
                text = uiState.keyword,
                onKeywordChanged = viewModel::updateKeyword,
                onSizeChanged = { size ->
//                    appbarHeight = size.height.toFloat()
                },
                search = viewModel::search,
            )
        }
    }
}

@OptIn(ExperimentalMotionApi::class)
@Composable
fun ShowAppBar(
    progress: Float,
    text: String,
    onKeywordChanged: (keyword: String) -> Unit,
    onSizeChanged: (size: IntSize) -> Unit,
    search: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val motionScene = remember {
        context.resources
            .openRawResource(R.raw.home_motion_scene)
            .readBytes()
            .decodeToString()
    }

    MotionLayout(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = marginHorizontal)
            .onSizeChanged(onSizeChanged = onSizeChanged),
        motionScene = MotionScene(content = motionScene),
        progress = progress,
    ) {
        Text(
            modifier = Modifier
                .layoutId("sub_title")
                .fillMaxWidth(),
            text = stringResource(id = R.string.home_sub_title, "닉네임"), // todo : 실 유저 네임으로 변경
            style = TextStyle(
                lineHeight = 34.sp,
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp,
                fontFamily = aggroFamily,
            ),
        )
        SearchBar(
            modifier = Modifier
                .layoutId("search_bar")
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
