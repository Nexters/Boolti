package com.nexters.boolti.presentation.extension

import androidx.compose.ui.graphics.Color
import com.nexters.boolti.domain.model.ReservationState
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.theme.Error
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Success

fun ReservationState.toDescriptionAndColorPair(): Pair<Int, Color> {
    return when (this) {
        ReservationState.DEPOSITING -> Pair(R.string.reservations_depositing, Grey30)
        ReservationState.REFUNDING -> Pair(R.string.reservations_refunding, Success)
        ReservationState.CANCELED -> Pair(R.string.reservations_canceled, Error)
        ReservationState.RESERVED -> Pair(R.string.reservations_reserved, Success)
        ReservationState.REFUNDED -> Pair(R.string.reservations_refunded, Error)
        ReservationState.UNDEFINED -> Pair(R.string.reservations_unknown, Error)
    }
}
