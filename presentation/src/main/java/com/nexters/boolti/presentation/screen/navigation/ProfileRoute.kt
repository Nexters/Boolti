package com.nexters.boolti.presentation.screen.navigation

import com.nexters.boolti.domain.model.Link
import kotlinx.serialization.Serializable

sealed interface ProfileRoute {
    @Serializable
    data object ProfileRoot : ProfileRoute

    @Serializable
    data object ProfileEdit : ProfileRoute

    @Serializable
    data object ProfileNicknameEdit : ProfileRoute

    @Serializable
    data object ProfileUserCodeEdit : ProfileRoute

    @Serializable
    data object ProfileIntroduceEdit : ProfileRoute

    @Serializable
    data object ProfileSnsEdit : ProfileRoute

    @Serializable
    data class ProfileLinkEdit(
        val linkId: String? = null,
        val linkTitle: String? = null,
        val url: String? = null,
    ) {
        constructor(link: Link?) : this(
            linkId = link?.id,
            linkTitle = link?.name,
            url = link?.url,
        )
    }
}
