package com.nexters.boolti.presentation.screen.show

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexters.boolti.domain.model.ShowState
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.ShowFeed
import com.nexters.boolti.presentation.theme.Grey15
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey60
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.Grey85
import com.nexters.boolti.presentation.theme.aggroFamily
import com.nexters.boolti.presentation.theme.marginHorizontal

@Composable
fun ShowScreen(
    modifier: Modifier = Modifier,
    onClickShowItem: (showId: Long) -> Unit,
    viewModel: ShowViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = { ShowAppBar() },
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            LazyVerticalGrid(
                modifier = Modifier.padding(horizontal = marginHorizontal),
                columns = GridCells.Adaptive(minSize = 150.dp),
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp),
                contentPadding = PaddingValues(top = 12.dp),
            ) {
                items(count = uiState.shows.size, key = { index -> uiState.shows[index].id }) { index ->
                    ShowFeed(
                        show = uiState.shows[index],
                        modifier = Modifier
                            .clickable { onClickShowItem(uiState.shows[index].id) },
                    )
                }
            }
        }
    }
}

@Composable
fun ShowAppBar(
    modifier: Modifier = Modifier,
    viewModel: ShowViewModel = hiltViewModel(),
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = marginHorizontal),
    ) {
        Box(
            modifier = Modifier.height(44.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Icon(
                modifier = Modifier
                    .width(61.dp)
                    .height(16.dp),
                painter = painterResource(id = R.drawable.boolti_logo),
                contentDescription = "앱 로고",
                tint = Grey50,
            )
        }
        Text(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth(),
            text = "\${닉네임}님\n오늘은 어떤 공연을\n즐겨볼까요?",
            style = TextStyle(
                lineHeight = 34.sp,
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp,
                fontFamily = aggroFamily,
            ),
        )
        SearchBar(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .padding(top = 20.dp)
        )
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
) {
    TextField(
        modifier = modifier
            .fillMaxWidth(),
        value = "",
        onValueChange = {},
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
