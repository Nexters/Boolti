package com.nexters.boolti.tosspayments

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TossPaymentsWidgetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val orderId: String = requireNotNull(savedStateHandle["extraKeyOrderId"])

    fun validateOrderId(orderId: String): Boolean {
        return this.orderId == orderId
    }
}
