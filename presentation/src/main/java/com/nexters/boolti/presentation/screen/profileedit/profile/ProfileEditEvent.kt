package com.nexters.boolti.presentation.screen.profileedit.profile

sealed interface ProfileEditEvent {
    data object OnLinkAdded : ProfileEditEvent
    data object OnLinkRemoved : ProfileEditEvent
}
