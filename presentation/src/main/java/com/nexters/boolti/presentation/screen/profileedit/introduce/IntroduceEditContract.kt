package com.nexters.boolti.presentation.screen.profileedit.introduce

data class IntroduceEditUiState(
    val introduce: String = "",
    val saving: Boolean = false,
    val showExitAlertDialog: Boolean = false,
) {
    val saveEnabled: Boolean = !saving

    companion object {
        const val MAX_LENGTH = 60
    }
}

sealed interface IntroduceEditAction {
    data class ChangeIntroduce(val introduce: String) : IntroduceEditAction
    data object Save : IntroduceEditAction
    data object ClickBack : IntroduceEditAction
    data object DismissExitAlertDialog : IntroduceEditAction
    data object ConfirmExit : IntroduceEditAction
}

sealed interface IntroduceEditEvent {
    data object NavigateUp : IntroduceEditEvent
}
