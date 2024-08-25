package com.nexters.boolti.domain.request

import com.nexters.boolti.domain.model.Link
import kotlinx.serialization.Serializable

@Serializable
data class EditProfileRequest(
    val nickname: String,
    val profileImagePath: String,
    val introduction: String,
    val link: List<LinkDto>,
) {
    @Serializable
    data class LinkDto(
        val title: String,
        val link: String,
    )
}

fun Link.toDto(): EditProfileRequest.LinkDto = EditProfileRequest.LinkDto(name, url)
