package com.nexters.boolti.presentation.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nexters.boolti.domain.model.Show
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.extension.showDateTimeString
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey05
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.theme.point1
import com.nexters.boolti.presentation.theme.point2
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * ## 공연 정보를 나타내는 컴포넌트
 *
 * _[Show] 객체를 받는 컴포넌트_
 *
 * @param show 공연 정보
 * @param modifier 컴포넌트 외부 패딩은 [modifier]를 사용
 * @param showNameStyle 공연명 텍스트 스타일. 보통 헤더로 사용되면 [point2], 아이템으로 사용되면 [point1]
 * @param showDateStyle 공연 일자 텍스트 스타일
 * @param backgroundColor 배경 색상
 * @param contentPadding 컴포넌트 내부 패딩
 * @param shape 코너 radius 변경 등에 사용
 * @param onClick null 이면 클릭 불가
 */
@Composable
fun ShowItem(
    show: Show,
    modifier: Modifier = Modifier,
    showNameStyle: TextStyle = point1,
    showDateStyle : TextStyle = MaterialTheme.typography.bodySmall,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentPadding: PaddingValues = PaddingValues(vertical = 16.dp, horizontal = marginHorizontal),
    shape: Shape = RoundedCornerShape(4.dp),
    onClick: (() -> Unit)? = null,
) {
    ShowItem(
        modifier = modifier,
        poster = show.thumbnailImage,
        showName = show.name,
        showDate = show.date,
        showNameStyle = showNameStyle,
        showDateStyle = showDateStyle,
        backgroundColor = backgroundColor,
        contentPadding = contentPadding,
        shape = shape,
        onClick = onClick,
    )
}

/**
 * ## 공연 정보를 나타내는 컴포넌트
 *
 * _[poster], [showName], [showDate] 를 각각 받는 컴포넌트_
 *
 * @param poster 공연 포스터
 * @param showName 공연 이름
 * @param showDate 공연 일자. [LocalDateTime.showDateTimeString] 를 사용하여 화면에 표시됨
 * @param modifier 컴포넌트 외부 패딩은 [modifier]를 사용
 * @param showNameStyle 공연명 텍스트 스타일. 보통 헤더로 사용되면 [point2], 아이템으로 사용되면 [point1]
 * @param showDateStyle 공연 일자 텍스트 스타일
 * @param backgroundColor 배경 색상
 * @param contentPadding 컴포넌트 내부 패딩
 * @param shape 코너 radius 변경 등에 사용
 * @param onClick null 이면 클릭 불가
 */
@Composable
fun ShowItem(
    poster: String,
    showName: String,
    showDate: LocalDateTime,
    modifier: Modifier = Modifier,
    showNameStyle: TextStyle = point1,
    showDateStyle : TextStyle = MaterialTheme.typography.bodySmall,
    contentPadding: PaddingValues = PaddingValues(vertical = 16.dp, horizontal = marginHorizontal),
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    shape: Shape = RoundedCornerShape(4.dp),
    onClick: (() -> Unit)? = null,
) {
    ShowItem(
        modifier = modifier,
        poster = poster,
        title = showName,
        description = showDate.showDateTimeString,
        titleStyle = showNameStyle,
        descriptionStyle = showDateStyle,
        contentPadding = contentPadding,
        backgroundColor = backgroundColor,
        shape = shape,
        onClick = onClick,
    )
}

/**
 * ## 공연 정보를 나타내는 컴포넌트
 *
 * _[poster], [title], [description] 를 직접 받는 컴포넌트_
 *
 * @param poster 공연 포스터
 * @param title 타이틀 영역 문구 (공연에서는 공연명)
 * @param description 설명 문구 (공연 일자나 티켓 관련 정보 등을 사용)
 * @param modifier 컴포넌트 외부 패딩은 [modifier]를 사용
 * @param titleStyle 타이틀 텍스트 스타일. 보통 헤더로 사용되면 [point2], 아이템으로 사용되면 [point1]
 * @param descriptionStyle 설명 문구 텍스트 스타일
 * @param backgroundColor 배경 색상
 * @param contentPadding 컴포넌트 내부 패딩
 * @param shape 코너 radius 변경 등에 사용
 * @param onClick null 이면 클릭 불가
 */
@Composable
fun ShowItem(
    poster: String,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    titleStyle: TextStyle = point1,
    descriptionStyle : TextStyle = MaterialTheme.typography.bodySmall,
    contentPadding: PaddingValues = PaddingValues(vertical = 16.dp, horizontal = marginHorizontal),
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    shape: Shape = RoundedCornerShape(4.dp),
    onClick: (() -> Unit)? = null,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            disabledContainerColor = backgroundColor,
        ),
        shape = shape,
        enabled = onClick != null,
        onClick = onClick ?: {},
    ) {
        Row(
            modifier = Modifier.padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = poster,
                contentDescription = stringResource(R.string.description_poster),
                modifier = Modifier
                    .size(width = 70.dp, height = 98.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(4.dp),
                    ),
                contentScale = ContentScale.Crop,
            )
            Column(
                modifier = Modifier.padding(start = 16.dp),
            ) {
                Text(
                    text = title,
                    style = titleStyle,
                    color = Grey05,
                )
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = description,
                    style = descriptionStyle,
                    color = Grey30,
                )
            }
        }
    }
}

@Preview
@Composable
private fun ShowItemPreview() {
    BooltiTheme {
        Scaffold { innerPadding ->
            val scrollState = rememberScrollState()
            Column(
                Modifier
                    .padding(innerPadding)
                    .verticalScroll(scrollState),
            ) {
                Text("ShowItem 을 받는 컴포넌트")
                ShowItem(
                    modifier = Modifier.fillMaxWidth(),
                    show = Show(
                        id = "1",
                        name = "2024 TOGETHER LUCKY CLUB",
                        thumbnailImage = "",
                        date = LocalDateTime.now(),
                        salesStartDate = LocalDate.now(),
                        salesEndDate = LocalDate.now().plusDays(10),
                    ),
                )

                Text("문구를 수정할 수 있는 컴포넌트")
                ShowItem(
                    modifier = Modifier.fillMaxWidth(),
                    poster = "",
                    title = "맘대로 바꿀 수 있는 제목",
                    description = "여기도 바꿀 수 있지롱",
                )

                Text("클릭 가능한 컴포넌트")
                ShowItem(
                    modifier = Modifier.fillMaxWidth(),
                    show = Show(
                        id = "1",
                        name = "2024 TOGETHER LUCKY CLUB",
                        thumbnailImage = "",
                        date = LocalDateTime.now(),
                        salesStartDate = LocalDate.now(),
                        salesEndDate = LocalDate.now().plusDays(10),
                    ),
                ) {}

                Text("바깥쪽 패딩 조정 + 배경 색 변경")
                ShowItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = marginHorizontal),
                    show = Show(
                        id = "1",
                        name = "2024 TOGETHER LUCKY CLUB",
                        thumbnailImage = "",
                        date = LocalDateTime.now(),
                        salesStartDate = LocalDate.now(),
                        salesEndDate = LocalDate.now().plusDays(10),
                    ),
                    backgroundColor = Color.Blue,
                )

                Text("안쪽 패딩 조정 + shape 변경")
                ShowItem(
                    modifier = Modifier.fillMaxWidth(),
                    show = Show(
                        id = "1",
                        name = "2024 TOGETHER LUCKY CLUB",
                        thumbnailImage = "",
                        date = LocalDateTime.now(),
                        salesStartDate = LocalDate.now(),
                        salesEndDate = LocalDate.now().plusDays(10),
                    ),
                    contentPadding = PaddingValues(vertical = 20.dp, horizontal = 30.dp),
                    shape = RoundedCornerShape(30.dp),
                )

                Text("헤더로 사용되는 경우 글자 크기 조정")
                ShowItem(
                    modifier = Modifier.fillMaxWidth(),
                    show = Show(
                        id = "1",
                        name = "2024 TOGETHER LUCKY CLUB",
                        thumbnailImage = "",
                        date = LocalDateTime.now(),
                        salesStartDate = LocalDate.now(),
                        salesEndDate = LocalDate.now().plusDays(10),
                    ),
                    showNameStyle = point2,
                )
            }
        }
    }
}
