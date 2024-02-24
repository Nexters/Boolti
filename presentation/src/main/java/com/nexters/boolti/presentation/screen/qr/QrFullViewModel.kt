package com.nexters.boolti.presentation.screen.qr

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QrFullViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val ticketName: String = savedStateHandle["ticketName"] ?: ""
    val data: String = requireNotNull(savedStateHandle["data"]) {
        "QrFullViewModel 에 data 가 전달되지 않았습니다"
    }
}
