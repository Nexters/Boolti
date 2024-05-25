package com.nexters.boolti.presentation.component

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.theme.Grey10
import com.nexters.boolti.presentation.theme.Grey70

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InquiryBottomSheet(
    onDismissRequest: () -> Unit,
    isTelephone: Boolean,
    contact: String,
) {
    val textId = if (isTelephone) R.string.show_call_to_ask else R.string.show_text_to_ask
    val context = LocalContext.current

    ModalBottomSheet(
        containerColor = MaterialTheme.colorScheme.surfaceTint,
        onDismissRequest = onDismissRequest,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 24.dp)
                    .size(45.dp, 4.dp)
                    .clip(CircleShape)
                    .background(color = Grey70)
            )
        },
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    ) {
        Box(
            Modifier
                .height(58.dp)
                .fillMaxWidth()
                .clickable {
                    val uriKey = if (isTelephone) "tel" else "smsto"
                    val action = if (isTelephone) Intent.ACTION_DIAL else Intent.ACTION_SENDTO
                    val intent = Intent(action).setData(Uri.parse("$uriKey:$contact"))
                    context.startActivity(intent)
                }
                .padding(horizontal = 24.dp, vertical = 18.dp)) {
            Text(
                text = stringResource(id = textId),
                style = MaterialTheme.typography.bodyLarge.copy(color = Grey10),
            )
        }
        Spacer(
            modifier = Modifier.padding(
                bottom = 20.dp + WindowInsets.navigationBars.asPaddingValues()
                    .calculateBottomPadding()
            )
        )
    }
}
