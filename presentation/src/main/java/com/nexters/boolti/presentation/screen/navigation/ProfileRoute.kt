package com.nexters.boolti.presentation.screen.navigation

import com.nexters.boolti.domain.model.Link
import kotlinx.serialization.Serializable

sealed interface ProfileRoute {
    @Serializable
    data object ProfileRoot : ProfileRoute

    @Serializable
    data object ProfileEdit : ProfileRoute

    @Serializable
    data class ProfileNicknameEdit(val nickname: String) : ProfileRoute

    @Serializable
    data class ProfileUserCodeEdit(val userCode: String) : ProfileRoute

    @Serializable
    data class ProfileIntroduceEdit(val introduction: String) : ProfileRoute

    @Serializable
    data class ProfileSnsEdit(
        val instagramUsername: String,
        val youtubeUsername: String,
    ) : ProfileRoute

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
