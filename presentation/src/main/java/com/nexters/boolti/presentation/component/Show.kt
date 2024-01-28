package com.nexters.boolti.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.nexters.boolti.presentation.theme.Grey05
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey80
import com.nexters.boolti.presentation.theme.aggroFamily
import java.time.LocalDate

@Composable
fun Show(
    modifier: Modifier = Modifier,
    openDate: LocalDate = LocalDate.now(), // fixme : 추후 적절한 data model 로 변경하기
    showDate: LocalDate = LocalDate.now(), // fixme : 추후 적절한 data model 로 변경하기
) {
    val now = LocalDate.now()
    val dDay = openDate.toEpochDay() - now.toEpochDay()
    val isPreview = now < openDate
    val outdated = now > showDate

    val borderRadius = 8.dp

    Column(
        modifier = modifier,
    ) {
        Box(
            contentAlignment = Alignment.BottomEnd,
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("https://picsum.photos/200/200")
                    .build(),
                contentDescription = "poster",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(210f / 297f)
                    .clip(RoundedCornerShape(borderRadius))
                    .border(
                        width = 1.dp,
                        color = Grey80,
                        shape = RoundedCornerShape(borderRadius)
                    ),
                contentScale = ContentScale.Crop,
            )
            if (outdated) Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(210f / 297f)
                    .background(
                        brush = SolidColor(Color.Black),
                        alpha = 0.5f,
                    )
            )
            if (outdated) Badge(label = "공연 종료", modifier = Modifier.padding(all = 10.dp))
            else if (isPreview) Badge(
                label = "예매 시작 D-$dDay",
                modifier = Modifier.padding(all = 10.dp),
                color = Grey05,
                containerColor = MaterialTheme.colorScheme.primary,
            )
        }
        Text(
            text = "2024.03.09 (토) 17:00",
            modifier = Modifier.padding(top = 12.dp),
            style = MaterialTheme.typography.bodySmall.copy(color = Grey30)
        )
        Text(
            text = "2024 TOGETHER LUCKY CLUB",
            modifier = Modifier.padding(top = 2.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontSize = 16.sp,
            lineHeight = 26.sp,
            fontFamily = aggroFamily,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}
