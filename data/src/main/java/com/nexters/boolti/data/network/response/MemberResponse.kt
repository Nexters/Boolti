package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.PreviewList
import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.model.emptyPreviewList
import com.nexters.boolti.domain.model.map
import com.nexters.boolti.domain.request.EditProfileRequest
import kotlinx.serialization.Serializable

@Serializable
internal data class MemberResponse(
    val nickname: String = "",
    val userCode: String = "",
    val imgPath: String = "",
    val introduction: String = "",
    val sns: List<EditProfileRequest.SnsDto> = emptyList(),
    val link: PreviewList<EditProfileRequest.LinkDto> = emptyPreviewList(),
    val performedShow: PreviewList<ShowResponse> = emptyPreviewList(),
    val comingSoonShow: PreviewList<ShowResponse> = emptyPreviewList(),
    val video: PreviewList<String> = emptyPreviewList(),
) {
    fun toDomain(): User.Others = User.Others(
        nickname = nickname,
        photo = imgPath,
        userCode = userCode,
        introduction = introduction,
        sns = sns.map { it.toDomain() },
        link = link.map { it.toDomain() },
        performedShow = performedShow.map { it.toDomain() },
        upcomingShow = comingSoonShow.map { it.toDomain() },
        video = video,
    )
}
