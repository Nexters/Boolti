package com.nexters.boolti.domain.request

import com.nexters.boolti.domain.model.Link
import kotlinx.serialization.Serializable
import java.util.UUID

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
    ) {
        fun toDomain(): Link = Link(id = UUID.randomUUID().toString(), title, link)
    }
}

fun Link.toDto(): EditProfileRequest.LinkDto = EditProfileRequest.LinkDto(name, url)
