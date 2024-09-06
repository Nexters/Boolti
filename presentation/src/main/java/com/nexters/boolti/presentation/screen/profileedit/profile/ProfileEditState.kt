package com.nexters.boolti.presentation.screen.profileedit.profile

import com.nexters.boolti.domain.model.Link

data class ProfileEditState(
    val loading: Boolean = false,
    val saving: Boolean = false,
    val thumbnail: String = "",
    val nickname: String = "",
    val introduction: String = "",
    val links: List<Link> = emptyList(),
) {
    val nicknameError: NicknameError?
        get() = when {
            nickname.isEmpty() -> NicknameError.MinLength
            !NicknameError.InvalidRegex.matches(nickname) -> NicknameError.Invalid
            else -> null
        }
}

enum class NicknameError {
    MinLength, Invalid;

    companion object {
        val InvalidRegex = Regex("^[0-9a-zA-Z가-힣ㄱ-ㅎ]{1,20}$")
    }
}
