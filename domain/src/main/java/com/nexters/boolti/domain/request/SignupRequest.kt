package com.nexters.boolti.domain.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val nickname: String?,
    val email: String?,
    val phoneNumber: String?,
    val oauthType: OauthType,
    val oauthIdentity: String,
    val imgPath: String?,
)

enum class OauthType {
    @SerialName("KAKAO") KAKAO,
}