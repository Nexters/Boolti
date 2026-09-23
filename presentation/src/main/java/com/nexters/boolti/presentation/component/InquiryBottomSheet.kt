package com.nexters.boolti.presentation.component

import androidx.annotation.StringRes
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.theme.Grey10

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Deprecated(message = "하위 호환을 위한 API입니다. InquiryBottomSheetType을 사용하는 API를 사용하세요.")
fun InquiryBottomSheet(
    onDismissRequest: () -> Unit,
    isTelephone: Boolean,
    contact: String,
) {
    val type =
        if (isTelephone) {
            InquiryBottomSheetType.Tel(contact = contact)
        } else {
            InquiryBottomSheetType.Sms(phoneNumber = contact)
        }

    InquiryBottomSheet(
        onDismissRequest = onDismissRequest,
        type = type,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InquiryBottomSheet(
    onDismissRequest: () -> Unit,
    type: InquiryBottomSheetType,
) {
    val uriHandler = LocalUriHandler.current

    BtBottomSheet(
        onDismissRequest = onDismissRequest,
    ) {
        Box(
            Modifier
                .height(58.dp)
                .fillMaxWidth()
                .clickable {
                    uriHandler.openUri(type.uri)
                    onDismissRequest()
                }
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = stringResource(id = type.textId),
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

sealed class InquiryBottomSheetType(
    @param:StringRes val textId: Int
) {
    abstract val uri: String

    data class Tel(
        val contact: String
    ) : InquiryBottomSheetType(R.string.inquiry_call_to_ask) {
        override val uri: String = "tel:$contact"
    }

    data class Mail(
        val address: String
    ) : InquiryBottomSheetType(R.string.inquiry_mail_to_ask) {
        override val uri: String = "mailto:$address"
    }

    data class Sms(
        val phoneNumber: String
    ) : InquiryBottomSheetType(R.string.inquiry_text_to_ask) {
        override val uri: String = "smsto:$phoneNumber"
    }
}