package com.nexters.boolti.presentation.screen.qr

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.theme.Grey90
import com.nexters.boolti.presentation.util.rememberQrBitmapPainter

@Composable
fun QrFullScreen(
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
) {
    val data = "BooltiTicket1"
    ConstraintLayout(
        modifier = modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        val (closeButton, logo, qr) = createRefs()

        IconButton(
            onClick = onClose,
            modifier = Modifier
                .constrainAs(closeButton) {
                    top.linkTo(parent.top, margin = 10.dp)
                    end.linkTo(parent.end, margin = 20.dp)
                }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_close),
                tint = Grey90,
                contentDescription = stringResource(R.string.description_close_button),
            )
        }

        Image(
            modifier = Modifier
                .constrainAs(logo) {
                    centerHorizontallyTo(parent)
                    bottom.linkTo(qr.top, margin = 8.dp)
                }
                .width(84.dp),
            painter = painterResource(R.drawable.boolti_logo),
            colorFilter = ColorFilter.tint(Color.Black),
            contentScale = ContentScale.FillWidth,
            contentDescription = null,
        )
        Image(
            modifier = Modifier
                .constrainAs(qr) {
                    centerVerticallyTo(parent)
                    centerHorizontallyTo(parent)
                }
                .background(Color.White)
                .padding(8.dp),
            painter = rememberQrBitmapPainter(
                data,
                size = 260.dp,
            ),
            contentScale = ContentScale.Inside,
            contentDescription = stringResource(R.string.description_qr),
        )
    }
}
