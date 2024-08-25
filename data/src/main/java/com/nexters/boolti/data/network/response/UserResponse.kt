package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.Link
import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.request.EditProfileRequest
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
internal data class UserResponse(
    val id: String,
    val nickname: String? = null,
    val email: String? = null,
    val imgPath: String? = null,
    val userCode: String? = null,
    val introduction: String = "",
    val link: List<EditProfileRequest.LinkDto> = emptyList(),
) {
    fun toDomain(): User {
        return User(
            id = id,
            nickname = nickname ?: "",
            email = email ?: "",
            photo = imgPath,
            userCode = userCode ?: "",
            introduction = introduction,
            link = link.map { Link(id = UUID.randomUUID().toString(), it.title, it.link) },
        )
    }
}
