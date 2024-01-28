package com.nexters.boolti.presentation.screen.show

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.TextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.Visibility
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.Show
import com.nexters.boolti.presentation.theme.getMarginHorizontal
import java.time.LocalDate

@Composable
fun ShowScreen(
    modifier: Modifier = Modifier,
    onClickTicketing: () -> Unit,
) {
    val shows = (5 downTo -5).toList()

    Scaffold(
        modifier = modifier,
        topBar = { ShowAppBar() },
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            LazyVerticalGrid(
                modifier = Modifier.padding(horizontal = getMarginHorizontal()),
                columns = GridCells.Adaptive(minSize = 150.dp),
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp),
            ) {
                val now = LocalDate.now()
                items(count = shows.size, key = { index -> shows[index] }) { index ->
                    val tempDay = now.plusDays(shows[index].toLong())
                    Show(openDate = tempDay, showDate = tempDay.plusDays(1))
                }
            }
        }
    }
}

@Composable
fun ShowAppBar(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = getMarginHorizontal()),
    ) {
        Image(
            modifier = Modifier
                .layoutId("logo")
                .width(115.dp)
                .height(44.dp),
            painter = painterResource(id = R.drawable.boolti_logo),
            contentDescription = "앱 로고",
        )
        Text(
            modifier = Modifier
                .layoutId("phrase")
                .fillMaxWidth(),
            text = "%닉네임%님\n오늘은 어떤 공연을\n즐겨볼까요?"
        )
        SearchBar(
            modifier = Modifier
                .layoutId("search bar")
                .fillMaxWidth()
        )
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
) {
    TextField(value = "", onValueChange = {})
}
