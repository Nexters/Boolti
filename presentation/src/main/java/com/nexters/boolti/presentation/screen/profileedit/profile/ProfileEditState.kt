package com.nexters.boolti.presentation.screen.profileedit.profile

import com.nexters.boolti.domain.model.Link
import com.nexters.boolti.domain.model.Sns

data class ProfileEditState(
    val loading: Boolean = false,
    val saving: Boolean = false,
    val thumbnail: String = "",
    val nickname: String = "",
    val introduction: String = "",
    val snsList: List<Sns> = emptyList(),
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
        val InvalidRegex = Regex("""^(?!\s)([0-9a-zA-Z\s가-힣]{1,20})(?<!\s)$""")
    }
}
