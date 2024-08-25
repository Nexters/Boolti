package com.nexters.boolti.domain.model

data class User(
    val id: String,
    val nickname: String = "",
    val email: String = "",
    val photo: String? = null,
    val userCode: String = "",
    val introduction: String = "",
    val link: List<Link> = emptyList(),
)
