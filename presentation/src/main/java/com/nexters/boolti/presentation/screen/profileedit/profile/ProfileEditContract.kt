package com.nexters.boolti.presentation.screen.profileedit.profile

import com.nexters.boolti.domain.model.UserCode

data class ProfileEditUiState(
    val thumbnail: String = "",
    val uploadingThumbnail: String? = null,
    val nickname: String = "",
    val userCode: UserCode = "",
    val introduction: String = "",
    val snsCount: Int = 0,
    val upcomingShowCount: Int = 0,
    val pastShowCount: Int = 0,
    val videoCount: Int = 0,
    val linkCount: Int = 0,
    val showUpcomingShows: Boolean = false,
    val showPerformedShows: Boolean = false,
) {
    val uploading: Boolean = uploadingThumbnail != null
    val displayedThumbnail: String = uploadingThumbnail ?: thumbnail
}

sealed interface ProfileEditAction {
    data object ClickBack : ProfileEditAction
    data class SelectThumbnail(val imageUri: String) : ProfileEditAction
    data object ClickNickname : ProfileEditAction
    data object ClickUserCode : ProfileEditAction
    data object ClickIntroduction : ProfileEditAction
    data object ClickSns : ProfileEditAction
    data object ToggleUpcomingShows : ProfileEditAction
    data object TogglePastShows : ProfileEditAction
    data object ClickVideo : ProfileEditAction
    data object ClickLink : ProfileEditAction
}

sealed interface ProfileEditEvent {
    data object NavigateUp : ProfileEditEvent
    data object NavigateToNicknameEdit : ProfileEditEvent
    data object NavigateToUserCodeEdit : ProfileEditEvent
    data object NavigateToIntroductionEdit : ProfileEditEvent
    data object NavigateToSnsEdit : ProfileEditEvent
    data class NavigateToVideoEdit(val userCode: UserCode) : ProfileEditEvent
    data class NavigateToLinkEdit(val userCode: UserCode) : ProfileEditEvent
    data object ShowUnknownError : ProfileEditEvent
}
