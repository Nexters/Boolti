package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.request.EditProfileRequest
import kotlinx.serialization.Serializable

@Serializable
internal data class MemberResponse(
    val nickname: String = "",
    val userCode: String = "",
    val imgPath: String = "",
    val introduction: String = "",
    val link: List<EditProfileRequest.LinkDto> = emptyList(),
) {
    fun toDomain(): User.Others = User.Others(
        nickname = nickname,
        photo = imgPath,
        userCode = userCode,
        introduction = introduction,
        link = link.map { it.toDomain() },
    )
}
