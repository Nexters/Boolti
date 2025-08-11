package com.nexters.boolti.presentation.screen.profileedit.profile

data class ProfileEditState(
    val loading: Boolean = false,
    val saving: Boolean = false,
    val thumbnail: String = "",
    val nickname: String = "",
    val id: String = "",
    val introduction: String = "",
    val snsCount: Int = 0,
    val upcomingShowCount: Int = 0,
    val pastShowCount: Int = 0,
    val videoCount: Int = 0,
    val linkCount: Int = 0,
    val showUpcomingShows: Boolean = false,
    val showPerformedShows: Boolean = false,
) {
    val nicknameError: NicknameError? // TODO 제거
        get() = when {
            nickname.isEmpty() -> NicknameError.MinLength
            !NicknameError.InvalidRegex.matches(nickname) -> NicknameError.Invalid
            else -> null
        }
}

enum class NicknameError {
    MinLength, Invalid;

    companion object {
        val InvalidRegex = Regex("""^(?!\s)([0-9a-zA-Z\s가-힣]{1,20})(?<!\s)$""")
    }
}
