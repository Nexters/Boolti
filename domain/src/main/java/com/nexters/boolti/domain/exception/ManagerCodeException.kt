package com.nexters.boolti.domain.exception

data class ManagerCodeException(
    val errorType: ManagerCodeErrorType?,
) : Exception(errorType?.name)

enum class ManagerCodeErrorType {
    Unknown, Mismatch, ShowNotToday;

    companion object {
        fun fromString(type: String?): ManagerCodeErrorType {
            return when (type?.trim()?.uppercase()) {
                "SHOW_MANAGER_CODE_MISMATCH" -> Mismatch
                "SHOW_NOT_TODAY" -> ShowNotToday
                else -> Unknown
            }
        }
    }
}
