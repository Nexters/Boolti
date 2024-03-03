package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.LoginUserState
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class LoginResponse(
    @SerialName("signUpRequired") val signUpRequired: Boolean = false,
    @SerialName("removeCancelled") val signOutCancelled: Boolean = false,
    @SerialName("accessToken") val accessToken: String?,
    @SerialName("refreshToken") val refreshToken: String?,
) {
    fun toDomain(): LoginUserState = LoginUserState(
        signUpRequired = signUpRequired,
        signOutCancelled = signOutCancelled,
    )
}
