package com.nexters.boolti.domain.model

import kotlinx.serialization.Serializable

data class Sns(
    val id: String,
    val type: SnsType,
    val username: String,
) {
    @Serializable
    enum class SnsType {
        INSTAGRAM, YOUTUBE;

        companion object {
            fun fromString(type: String?): SnsType? = when (type?.trim()?.uppercase()) {
                "INSTAGRAM" -> INSTAGRAM
                "YOUTUBE" -> YOUTUBE
                else -> null
            }
        }
    }
}

val Sns.url: String
    get() = when (type) {
        Sns.SnsType.INSTAGRAM -> "https://www.instagram.com/$username"
        Sns.SnsType.YOUTUBE -> "https://www.youtube.com/@$username"
    }
