package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.request.EditProfileRequest
import kotlinx.serialization.Serializable

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
    fun toDomain(): User.My {
        return User.My(
            id = id,
            nickname = nickname ?: "",
            email = email ?: "",
            photo = imgPath,
            userCode = userCode ?: "",
            introduction = introduction,
            link = link.map { it.toDomain() },
        )
    }
}
