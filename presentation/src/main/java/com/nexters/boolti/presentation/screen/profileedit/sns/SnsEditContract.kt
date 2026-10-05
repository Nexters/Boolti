package com.nexters.boolti.presentation.screen.profileedit.sns

data class SnsEditUiState(
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
        youtubeUsername.contains('@') -> SnsError.ContainsAtSign
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

sealed interface SnsEditAction {
    data class ChangeInstagramUsername(val username: String) : SnsEditAction
    data class ChangeYoutubeUsername(val username: String) : SnsEditAction
    data object Save : SnsEditAction
    data object ClickBack : SnsEditAction
    data object DismissExitAlertDialog : SnsEditAction
    data object ConfirmExit : SnsEditAction
}

sealed interface SnsEditEvent {
    data object NavigateUp : SnsEditEvent
}
