package com.nexters.boolti.presentation.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nexters.boolti.domain.model.Venue
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey05
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey80

@Composable
fun VenueItem(
    venue: Venue,
    onClick: (venueId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.clickable(onClick = { onClick(venue.id) }),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AsyncImage(
            modifier = Modifier
                .size(68.dp)
                .clip(RoundedCornerShape(4.dp))
                .border(
                    width = 1.dp,
                    color = Grey80,
                    shape = RoundedCornerShape(4.dp),
                ),
            model = venue.thumbnailImage,
            contentDescription = venue.name,
            contentScale = ContentScale.Crop,
        )

        Column {
            Text(
                text = venue.name,
                style = MaterialTheme.typography.titleMedium,
                color = Grey05,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = venue.address,
                style = MaterialTheme.typography.bodySmall,
                color = Grey50,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview
@Composable
private fun VenueItemPreview() {
    BooltiTheme {
        VenueItem(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            venue = Venue(
                id = "1",
                name = "그레이존 라이브바",
                address = "서울 영등포구 도신로 38 지하1층",
                thumbnailImage = "",
            ),
            onClick = {},
        )
    }
}
