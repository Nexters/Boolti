package com.nexters.boolti.domain.model

import java.time.LocalDateTime

data class CastTeams(
    val id: String = "",
    val teamName: String = "",
    val members: List<Cast> = emptyList(),
    val createdAt: LocalDateTime = LocalDateTime.MIN,
    val modifiedAt: LocalDateTime? = null,
)

data class Cast(
    val id: String = "",
    val userCode: String = "",
    val photo: String? = null,
    val nickname: String = "",
    val roleName: String = "",
    val createdAt: LocalDateTime = LocalDateTime.MIN,
    val modifiedAt: LocalDateTime? = null,
)
