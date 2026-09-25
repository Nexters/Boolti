package com.nexters.boolti.domain.request

import com.nexters.boolti.domain.model.Link
import com.nexters.boolti.domain.model.Sns
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class EditProfileRequest(
    @SerialName("nickname")
    val nickname: String,
    @SerialName("profileImagePath")
    val profileImagePath: String,
    @SerialName("introduction")
    val introduction: String,
    @SerialName("sns")
    val sns: List<SnsDto>,
    @SerialName("link")
    val link: List<LinkDto>,
) {
    @Serializable
    data class LinkDto(
        @SerialName("title")
        val title: String,
        @SerialName("link")
        val link: String,
    ) {
        fun toDomain() = Link(id = UUID.randomUUID().toString(), title, link)
    }

    @Serializable
    data class SnsDto(
        @SerialName("type")
        val type: String,
        @SerialName("username")
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
        Sns.SnsType.INSTAGRAM -> "INSTAGRAM"
        Sns.SnsType.YOUTUBE -> "YOUTUBE"
    },
    username = username,
)
