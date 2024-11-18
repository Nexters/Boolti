package com.nexters.boolti.domain.model

sealed interface User {
    val nickname: String
    val photo: String?
    val userCode: String
    val introduction: String
    val link: List<Link>
    val sns: List<Sns>

    data class My(
        val id: String,
        override val nickname: String = "",
        val email: String = "",
        override val photo: String? = null,
        override val userCode: String = "",
        override val introduction: String = "",
        override val sns: List<Sns> = emptyList(),
        override val link: List<Link> = emptyList(),
    ) : User

    data class Others(
        override val nickname: String = "",
        override val photo: String? = null,
        override val userCode: String = "",
        override val introduction: String = "",
        override val sns: List<Sns> = emptyList(),
        override val link: List<Link> = emptyList(),
    ) : User
}
