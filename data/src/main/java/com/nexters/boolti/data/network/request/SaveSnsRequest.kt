package com.nexters.boolti.data.network.request

import com.nexters.boolti.domain.request.EditProfileRequest
import kotlinx.serialization.Serializable

@Serializable
data class SaveSnsRequest(
    val sns: List<EditProfileRequest.SnsDto>
)
