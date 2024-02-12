package com.nexters.boolti.presentation.screen.report

data class ReportUiState(
    val reason: String = "",
) {
    val reportable: Boolean
        get() = reason.isNotBlank()
}
