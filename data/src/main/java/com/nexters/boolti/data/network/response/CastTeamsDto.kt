package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.Cast
import com.nexters.boolti.domain.model.CastTeams
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class CastTeamsDto(
    @SerialName("id")
    val id: String = "",
    @SerialName("name")
    val name: String = "",
    @SerialName("members")
    val members: List<CastDto> = emptyList(),
) {
    fun toDomain(): CastTeams = CastTeams(
        id = id,
        teamName = name,
        members = members.map(CastDto::toDomain),
    )

    @Serializable
    data class CastDto(
        @SerialName("id")
        val id: String = "",
        @SerialName("userCode")
        val userCode: String = "",
        @SerialName("userImgPath")
        val userImgPath: String? = null,
        @SerialName("userNickname")
        val userNickname: String = "",
        @SerialName("roleName")
        val roleName: String = "",
        @SerialName("createdAt")
        val createdAt: String = LocalDateTime.MIN.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
        @SerialName("modifiedAt")
        val modifiedAt: String?,
    ) {
        fun toDomain(): Cast = Cast(
            id = id,
            userCode = userCode,
            photo = userImgPath,
            nickname = userNickname,
            roleName = roleName,
        )
    }
}
