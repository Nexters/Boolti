package com.nexters.boolti.presentation.reservationdetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.theme.Grey95
import com.nexters.boolti.presentation.theme.KakaoYellow

@Composable
fun ResendGiftButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = KakaoYellow),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_kakaotalk), contentDescription = null,
                modifier = Modifier.size(width = 20.dp, height = 20.dp),
                tint = Color.Black,
            )
            Text(
                stringResource(id = R.string.gift_resend_message),
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium,
                color = Grey95,
                textAlign = TextAlign.Center,
            )
        }
    }
}