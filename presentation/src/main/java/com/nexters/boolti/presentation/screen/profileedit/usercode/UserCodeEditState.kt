package com.nexters.boolti.presentation.screen.profileedit.usercode

import com.nexters.boolti.domain.model.UserCode

data class UserCodeEditState(
    val userCode: String = "",
    val checkingDuplicated: Boolean = false,
    val saving: Boolean = false,
    val showExitAlertDialog: Boolean = false,
    val duplicatedUserCodes: List<UserCode> = emptyList(),
) {
    val userCodeError: UserCodeError? = when {
        userCode.length < 4 -> UserCodeError.MinLength
        userCode.length > 20 -> UserCodeError.MaxLength
        userCode.any { it.isWhitespace() } -> UserCodeError.ContainsWhitespace
        !UserCodeError.InvalidRegex.matches(userCode) -> UserCodeError.Invalid
        duplicatedUserCodes.contains(userCode) -> UserCodeError.Duplicated
        else -> null
    }

    val saveEnabled: Boolean = userCodeError == null && !saving && !checkingDuplicated
}

enum class UserCodeError {
    MinLength, MaxLength, Invalid, ContainsWhitespace, Duplicated;

    companion object {
        val InvalidRegex = Regex("""^(?!\s)([0-9a-z_]{4,20})(?<!\s)$""")
    }
}
