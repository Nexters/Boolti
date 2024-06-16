package com.nexters.boolti.presentation.screen.gift

import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.usecase.GetRefundPolicyUsecase
import com.nexters.boolti.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class GiftViewModel @Inject constructor(
    private val getRefundPolicyUseCase: GetRefundPolicyUsecase,
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(GiftUiState())
    val uiState: StateFlow<GiftUiState> = _uiState.asStateFlow()

    fun toggleAgreement() {
        _uiState.update { it.toggleAgreement() }
    }

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch(recordExceptionHandler) {
            getRefundPolicyUseCase()
                .onEach { refundPolicy ->
                    _uiState.update {
                        it.copy(refundPolicy = refundPolicy)
                    }
                }
                .launchIn(viewModelScope + recordExceptionHandler)
        }
    }
}
