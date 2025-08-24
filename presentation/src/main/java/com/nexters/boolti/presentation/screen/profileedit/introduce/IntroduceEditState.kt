package com.nexters.boolti.presentation.screen.profileedit.introduce

data class IntroduceEditState(
    val introduce: String = "",
    val saving: Boolean = false,
    val showExitAlertDialog: Boolean = false,
) {
    val saveEnabled: Boolean = !saving
}
