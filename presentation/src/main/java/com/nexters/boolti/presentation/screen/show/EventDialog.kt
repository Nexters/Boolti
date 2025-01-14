package com.nexters.boolti.presentation.screen.show

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BtCheckBox
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey95

@Composable
fun BTDialog(
    imageUrl: String,
    actionUrl: String?,
    onDismiss: (hideToday: Boolean) -> Unit,
) {
    var hideToday by rememberSaveable { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current

    Dialog(
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
        ),
        onDismissRequest = { onDismiss(hideToday) }
    ) {
        Card(
            modifier = Modifier.width(311.dp),
            shape = RoundedCornerShape(8.dp),
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp)
                        .then(
                            if (actionUrl == null) {
                                Modifier
                            } else {
                                Modifier.clickable {
                                    uriHandler.openUri(actionUrl)
                                }
                            }
                        )
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = stringResource(id = R.string.description_event_poster),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(color = White),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .clickable(onClick = { hideToday = !hideToday })
                            .padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Box(
                            modifier = Modifier.size(24.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            BtCheckBox(
                                isSelected = hideToday
                            )
                        }
                        Text(
                            text = "오늘 하루 그만 보기",
                            style = MaterialTheme.typography.bodyLarge.copy(color = Grey50)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(68.dp)
                            .clickable(onClick = { onDismiss(hideToday) }),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.description_close_button),
                            style = MaterialTheme.typography.bodyLarge.copy(color = Grey95),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

@Preview(widthDp = 360, heightDp = 760)
@Composable
fun BTDialogPreview() {
    BooltiTheme {
        Surface {
            BTDialog(
                imageUrl = "",
                actionUrl = "",
                onDismiss = {}
            )
        }
    }
}
