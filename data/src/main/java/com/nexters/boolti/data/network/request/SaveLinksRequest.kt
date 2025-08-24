package com.nexters.boolti.data.network.request

import com.nexters.boolti.domain.request.EditProfileRequest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SaveLinksRequest(
    @SerialName("link") val links: List<EditProfileRequest.LinkDto>
)
