package com.nexters.boolti.presentation.component

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.theme.Grey10

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InquiryBottomSheet(
    onDismissRequest: () -> Unit,
    isTelephone: Boolean,
    contact: String,
) {
    val textId = if (isTelephone) R.string.show_call_to_ask else R.string.show_text_to_ask
    val context = LocalContext.current

    BtBottomSheet(
        onDismissRequest = onDismissRequest,
    ) {
        Box(
            Modifier
                .height(58.dp)
                .fillMaxWidth()
                .clickable {
                    val uriKey = if (isTelephone) "tel" else "smsto"
                    val action = if (isTelephone) Intent.ACTION_DIAL else Intent.ACTION_SENDTO
                    val intent = Intent(action).setData("$uriKey:$contact".toUri())
                    context.startActivity(intent)
                    onDismissRequest()
                }
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = stringResource(id = textId),
                style = MaterialTheme.typography.bodyLarge.copy(color = Grey10),
            )
        }
        Spacer(
            modifier = Modifier.padding(
                bottom = 28.dp
            )
        )
    }
}
