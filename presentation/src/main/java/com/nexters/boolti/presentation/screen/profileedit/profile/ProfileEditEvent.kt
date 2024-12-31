package com.nexters.boolti.presentation.screen.profileedit.profile

sealed interface ProfileEditEvent {
    data object UnAuthorized : ProfileEditEvent
    data object EditFailed : ProfileEditEvent
    data object OnLinkAdded : ProfileEditEvent
    data object OnLinkEdited : ProfileEditEvent
    data object OnLinkRemoved : ProfileEditEvent
    data object OnSnsAdded : ProfileEditEvent
    data object OnSnsEdited : ProfileEditEvent
    data object OnSnsRemoved : ProfileEditEvent
    data object OnSuccessEditProfile : ProfileEditEvent
}
