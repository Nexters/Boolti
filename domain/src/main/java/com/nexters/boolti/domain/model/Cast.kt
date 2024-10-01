package com.nexters.boolti.domain.model

data class CastTeams(
    val id: String = "",
    val teamName: String = "",
    val members: List<Cast> = emptyList(),
)

data class Cast(
    val id: String = "",
    val userCode: String = "",
    val photo: String? = null,
    val nickname: String = "",
    val roleName: String = "",
)
