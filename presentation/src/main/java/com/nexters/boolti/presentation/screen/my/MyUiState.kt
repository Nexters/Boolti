package com.nexters.boolti.presentation.screen.my

import com.nexters.boolti.domain.model.User

sealed interface MyUiState {
    data object Loading: MyUiState
    data class Success(val user: User): MyUiState
    data object Failure: MyUiState
}