package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.PreviewList
import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.model.emptyPreviewList
import com.nexters.boolti.domain.model.map
import com.nexters.boolti.domain.request.EditProfileRequest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class UserResponse(
    @SerialName("id")
    val id: String,
    @SerialName("nickname")
    val nickname: String? = null,
    @SerialName("email")
    val email: String? = null,
    @SerialName("imgPath")
    val imgPath: String? = null,
    @SerialName("userCode")
    val userCode: String? = null,
    @SerialName("introduction")
    val introduction: String = "",
    @SerialName("sns")
    val sns: List<EditProfileRequest.SnsDto> = emptyList(),
    @SerialName("link")
    val link: PreviewList<EditProfileRequest.LinkDto> = emptyPreviewList(),
    @SerialName("performedShow")
    val performedShow: PreviewList<ShowResponse> = emptyPreviewList(),
    @SerialName("comingSoonShow")
    val comingSoonShow: PreviewList<ShowResponse> = emptyPreviewList(),
    @SerialName("video")
    val video: PreviewList<String> = emptyPreviewList(),
) {
    fun toDomain(): User.My = User.My(
        id = id,
        nickname = nickname ?: "",
        email = email ?: "",
        photo = imgPath,
        userCode = userCode ?: "",
        introduction = introduction,
        sns = sns.filter { it.username.isNotEmpty() }.map { it.toDomain() },
        link = link.map { it.toDomain() },
        performedShow = performedShow.map { it.toDomain() },
        upcomingShow = comingSoonShow.map { it.toDomain() },
        video = video,
    )
}
