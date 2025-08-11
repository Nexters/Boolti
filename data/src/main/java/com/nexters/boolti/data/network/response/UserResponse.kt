package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.PreviewList
import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.model.emptyPreviewList
import com.nexters.boolti.domain.model.map
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
    val sns: List<EditProfileRequest.SnsDto> = emptyList(),
    val link: PreviewList<EditProfileRequest.LinkDto> = emptyPreviewList(),
    val performedShow: PreviewList<ShowResponse> = emptyPreviewList(),
    val comingSoonShow: PreviewList<ShowResponse> = emptyPreviewList(),
    val video: PreviewList<String> = emptyPreviewList(),
) {
    fun toDomain(): User.My = User.My(
        id = id,
        nickname = nickname ?: "",
        email = email ?: "",
        photo = imgPath,
        userCode = userCode ?: "",
        introduction = introduction,
        sns = sns.map { it.toDomain() },
        link = link.map { it.toDomain() },
        performedShow = performedShow.map { it.toDomain() },
        upcomingShow = comingSoonShow.map { it.toDomain() },
        video = video,
    )
}
