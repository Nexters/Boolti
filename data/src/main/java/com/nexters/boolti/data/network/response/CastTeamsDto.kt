package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.Cast
import com.nexters.boolti.domain.model.CastTeams
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class CastTeamsDto(
    val id: String = "",
    val name: String = "",
    val members: List<CastDto> = emptyList(),
) {
    fun toDomain(): CastTeams = CastTeams(
        id = id,
        teamName = name,
        members = members.map(CastDto::toDomain),
    )

    @Serializable
    data class CastDto(
        val id: String = "",
        val userCode: String = "",
        val userImgPath: String? = null,
        val userNickname: String = "",
        val roleName: String = "",
        val createdAt: String = LocalDateTime.MIN.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
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
