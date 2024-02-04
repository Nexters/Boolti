package com.nexters.boolti.presentation.screen.qr

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.screen.ticket.TicketState
import com.nexters.boolti.presentation.theme.Grey90
import com.nexters.boolti.presentation.util.rememberQrBitmapPainter

@Composable
fun QrFullScreen(
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
) {
    val data = "BooltiTicket1"
    Box(
        modifier = modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(vertical = 10.dp, horizontal = 20.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_close),
                tint = Grey90,
                contentDescription = stringResource(R.string.description_close_button),
            )
        }

        Image(
            modifier = Modifier
                .align(Alignment.Center)
                .background(Color.White)
                .padding(8.dp),
            painter = rememberQrBitmapPainter(
                data,
                size = 260.dp,
            ),
            contentScale = ContentScale.Inside,
            contentDescription = "입장 QR 코드",
        )
    }
}
