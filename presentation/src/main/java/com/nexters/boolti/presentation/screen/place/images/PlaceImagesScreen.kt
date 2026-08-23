package com.nexters.boolti.presentation.screen.place.images

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BtBackAppBar
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.marginHorizontal

/**
 * 공연장 사진 목록 화면.
 *
 * TODO(Phase 3): 썸네일 그리드 UI 및 ViewModel 을 연결한다.
 * 현재는 브릿지로 전달받은 값을 확인하기 위한 임시 화면이다.
 */
@Composable
fun PlaceImagesScreen(
    placeId: String,
    imageIds: List<Long>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            BtBackAppBar(
                title = stringResource(R.string.place_photos),
                onClickBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = marginHorizontal),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "placeId: $placeId",
                style = MaterialTheme.typography.bodyMedium,
                color = Grey50,
            )
            Text(
                text = "imageIds: $imageIds",
                style = MaterialTheme.typography.bodyMedium,
                color = Grey50,
            )
        }
    }
}

@Preview
@Composable
private fun PlaceImagesScreenPreview() {
    BooltiTheme {
        PlaceImagesScreen(
            placeId = "2",
            imageIds = listOf(11, 12, 13),
            onBack = {},
        )
    }
}
