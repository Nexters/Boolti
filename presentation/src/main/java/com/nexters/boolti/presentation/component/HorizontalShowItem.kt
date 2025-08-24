package com.nexters.boolti.presentation.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nexters.boolti.domain.model.Show
import com.nexters.boolti.presentation.extension.showDateString
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey50
import java.time.LocalDateTime

@Composable
fun HorizontalShowItem(
    show: Show,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    size: DpSize = DpSize(128.dp, 256.dp),
) {
    val shape = RoundedCornerShape(8.dp)
    Column(
        modifier = modifier
            .size(size)
            .clip(shape)
            .clickable(onClick = onClick),
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(128 / 176f)
                .border(width = 1.dp, color = MaterialTheme.colorScheme.outline, shape = shape),
            model = show.thumbnailImage,
            contentDescription = show.name,
        )

        Text(
            modifier = Modifier.padding(top = 12.dp),
            text = show.name,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = show.date.showDateString,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Normal,
            color = Grey50,
            maxLines = 1,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun HorizontalShowItemPreview() {
    val show = Show(
        id = "",
        name = "2024 TOGETH ER LUCKY CLUB",
        date = LocalDateTime.of(2024, 3, 9, 0, 0),
        null,
        null,
        "",
    )
    BooltiTheme {
        HorizontalShowItem(
            modifier = Modifier.padding(20.dp),
            show = show,
            onClick = {},
        )
    }
}
