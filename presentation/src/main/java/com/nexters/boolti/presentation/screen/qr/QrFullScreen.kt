package com.nexters.boolti.presentation.screen.qr

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.theme.Grey10
import com.nexters.boolti.presentation.theme.Grey85
import com.nexters.boolti.presentation.theme.Grey90
import com.nexters.boolti.presentation.util.rememberQrBitmapPainter

@Composable
fun QrFullScreen(
    modifier: Modifier = Modifier,
    viewModel: QrFullViewModel = hiltViewModel(),
    onClose: () -> Unit,
) {
    ConstraintLayout(
        modifier = modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        val (closeButton, qr) = createRefs()

        IconButton(
            onClick = onClose,
            modifier = Modifier
                .constrainAs(closeButton) {
                    top.linkTo(parent.top, margin = 10.dp)
                    end.linkTo(parent.end, margin = 20.dp)
                }
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_close),
                tint = Grey90,
                contentDescription = stringResource(R.string.description_close_button),
            )
        }

        Column(
            modifier = Modifier
                .constrainAs(qr) {
                    centerTo(parent)
                }
                .background(
                    color = Grey10,
                    shape = RoundedCornerShape(8.dp),
                )
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = viewModel.ticketName,
                style = MaterialTheme.typography.titleMedium,
                color = Grey85.copy(alpha = .85f),
            )
            Image(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White)
                    .padding(14.dp),
                painter = rememberQrBitmapPainter(
                    viewModel.data,
                    size = 260.dp,
                ),
                contentScale = ContentScale.Inside,
                contentDescription = stringResource(R.string.description_qr),
            )
            Image(
                modifier = Modifier
                    .width(84.dp)
                    .padding(bottom = 12.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_logo_boolti),
                contentScale = ContentScale.FillWidth,
                contentDescription = null,
            )
        }
    }
}
