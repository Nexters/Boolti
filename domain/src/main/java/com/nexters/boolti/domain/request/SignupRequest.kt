package com.nexters.boolti.domain.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    @SerialName("nickname")
    val nickname: String?,
    @SerialName("email")
    val email: String?,
    @SerialName("phoneNumber")
    val phoneNumber: String?,
    @SerialName("oauthType")
    val oauthType: OauthType,
    @SerialName("oauthIdentity")
    val oauthIdentity: String,
    @SerialName("imgPath")
    val imgPath: String?,
)

enum class OauthType {
    @SerialName("KAKAO") KAKAO,
}