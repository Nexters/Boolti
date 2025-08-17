package com.nexters.boolti.presentation.screen.profileedit.nickname

data class NicknameEditState(
    val nickname: String = "",
    val saving: Boolean = false,
    val showExitAlertDialog: Boolean = false,
) {
    val nicknameError: NicknameError? = when {
        nickname.isEmpty() -> NicknameError.MinLength
        nickname.first().isWhitespace() || nickname.last().isWhitespace() -> NicknameError.ContainsWhitespace
        !NicknameError.InvalidRegex.matches(nickname) -> NicknameError.Invalid
        else -> null
    }

    val saveEnabled: Boolean = nicknameError == null && !saving
}

enum class NicknameError {
    MinLength, Invalid, ContainsWhitespace;

    companion object {
        val InvalidRegex = Regex("""^(?!\s)([0-9a-zA-Z\sㄱ-ㅎㅏ-ㅣ가-힣]{1,120})(?<!\s)$""")
    }
}
