package com.nexters.boolti.presentation.screen.profileedit.sns

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nexters.boolti.presentation.R

data class SnsEditState(
    val instagramUsername: String = "",
    val youtubeUsername: String = "",
    val originalInstagramUsername: String = "",
    val originalYoutubeUsername: String = "",
    val saving: Boolean = false,
    val showExitAlertDialog: Boolean = false,
) {
    val instagramUsernameError: SnsError? = when {
        instagramUsername.contains('@') -> SnsError.ContainsAtSign
        instagramUsername.contains(Regex("[^0-9a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣._]+")) -> SnsError.ContainsUnsupportedCharacter
        else -> null
    }
    val youtubeUsernameError: SnsError? = when {
        youtubeUsername.contains(Regex("[^0-9a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣._-]+")) -> SnsError.ContainsUnsupportedCharacter
        else -> null
    }
    val saveEnabled: Boolean =
        !(originalInstagramUsername == instagramUsername && originalYoutubeUsername == youtubeUsername) &&
                instagramUsernameError == null &&
                youtubeUsernameError == null &&
                !saving
}

enum class SnsError {
    ContainsAtSign, ContainsUnsupportedCharacter
}

internal val SnsError?.message: String?
    @Composable
    get() = when (this) {
        SnsError.ContainsAtSign -> stringResource(R.string.sns_edit_error_contains_at_sign)
        SnsError.ContainsUnsupportedCharacter -> stringResource(R.string.sns_edit_error_contains_unsupported_character)
        null -> null
    }
