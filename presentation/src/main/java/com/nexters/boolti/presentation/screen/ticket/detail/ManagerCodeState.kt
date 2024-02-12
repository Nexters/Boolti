package com.nexters.boolti.presentation.screen.ticket.detail

import com.nexters.boolti.domain.exception.ManagerCodeErrorType

data class ManagerCodeState(
    val code: String = "",
    val error: ManagerCodeErrorType? = null,
)
