package com.nexters.boolti.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.theme.Grey30

@Composable
fun ShowInquiry(
    hostName: String,
    hostNumber: String,
    modifier: Modifier = Modifier,
) {
    var telephoneBottomSheet: Boolean? by remember { mutableStateOf(null) }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = hostName,
            style = MaterialTheme.typography.bodyLarge.copy(color = Grey30),
        )
        IconButton(
            modifier = Modifier.size(24.dp),
            onClick = { telephoneBottomSheet = true }
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_telephone),
                contentDescription = stringResource(id = R.string.show_call_to_ask),
                tint = Grey30
            )
        }
        IconButton(
            modifier = Modifier.size(24.dp),
            onClick = { telephoneBottomSheet = false }
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_message),
                contentDescription = stringResource(id = R.string.show_text_to_ask),
                tint = Grey30
            )
        }
    }

    telephoneBottomSheet?.let { isTelephone ->
        InquiryBottomSheet(
            isTelephone = isTelephone,
            onDismissRequest = { telephoneBottomSheet = null },
            contact = hostNumber
        )
    }
}