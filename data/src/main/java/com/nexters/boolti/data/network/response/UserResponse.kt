package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.User
import kotlinx.serialization.Serializable

@Serializable
internal data class UserResponse(
    val id: String,
    val nickname: String? = null,
    val email: String? = null,
    val imgPath: String? = null,
) {
    fun toDomain(): User {
        return User(
            id = id,
            nickname = nickname ?: "",
            email = email ?: "",
            photo = imgPath,
        )
    }
}
