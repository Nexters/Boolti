package com.nexters.boolti.domain.model

data class Link(
    val id: String,
    val name: String,
    val url: String,
)

data class Sns(
    val id: String,
    val type: SnsType,
    val name: String,
    val username: String,
) {
    enum class SnsType {
        INSTAGRAM, YOUTUBE
    }
}

val Sns.url: String
    get() = when (type) {
        Sns.SnsType.INSTAGRAM -> "https://www.instagram.com/$username"
        Sns.SnsType.YOUTUBE -> "https://www.youtube.com/@$username"
    }
