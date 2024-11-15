package com.nexters.boolti.domain.request

import com.nexters.boolti.domain.model.Link
import com.nexters.boolti.domain.model.Sns
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class EditProfileRequest(
    val nickname: String,
    val profileImagePath: String,
    val introduction: String,
    val sns: List<SnsDto>,
    val link: List<LinkDto>,
) {
    @Serializable
    data class LinkDto(
        val title: String,
        val link: String,
    ) {
        fun toDomain() = Link(id = UUID.randomUUID().toString(), title, link)
    }

    @Serializable
    data class SnsDto(
        val type: String,
        val username: String,
    ) {
        fun toDomain(): Sns = Sns(
            id = UUID.randomUUID().toString(),
            type = Sns.SnsType.fromString(type) ?: Sns.SnsType.INSTAGRAM,
            username = username,
        )
    }
}

fun Link.toDto(): EditProfileRequest.LinkDto = EditProfileRequest.LinkDto(name, url)
fun Sns.toDto(): EditProfileRequest.SnsDto = EditProfileRequest.SnsDto(
    type = when (type) {
        Sns.SnsType.INSTAGRAM -> "Instagram"
        Sns.SnsType.YOUTUBE -> "YouTube"
    },
    username = username,
)
