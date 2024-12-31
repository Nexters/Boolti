package com.nexters.boolti.presentation.screen.navigation

import com.nexters.boolti.domain.model.Sns
import kotlinx.serialization.Serializable

sealed interface ProfileRoute {
    @Serializable
    data object ProfileRoot : ProfileRoute

    @Serializable
    data object ProfileEdit : ProfileRoute

    @Serializable
    data class ProfileSnsEdit(
        val snsType: Sns.SnsType = Sns.SnsType.INSTAGRAM,
        val linkId: String? = null,
        val username: String? = null,
    ) : ProfileRoute {
        constructor(sns: Sns?) : this(
            snsType = sns?.type ?: Sns.SnsType.INSTAGRAM,
            linkId = sns?.id,
            username = sns?.username,
        )
    }
}
