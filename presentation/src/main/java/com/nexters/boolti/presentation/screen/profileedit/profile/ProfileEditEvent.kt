package com.nexters.boolti.presentation.screen.profileedit.profile

sealed interface ProfileEditEvent {
    data object OnLinkAdded : ProfileEditEvent
    data object OnLinkEditted : ProfileEditEvent
    data object OnLinkRemoved : ProfileEditEvent
    data object OnSuccessEditProfile : ProfileEditEvent
}
