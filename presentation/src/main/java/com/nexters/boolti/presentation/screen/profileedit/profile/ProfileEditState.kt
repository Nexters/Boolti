package com.nexters.boolti.presentation.screen.profileedit.profile

data class ProfileEditState(
    val loading: Boolean = false,
    val thumbnail: String = "",
    val nickname: String = "",
    val introduction: String = "",
    val links: List<Pair<String, String>> = listOf(
        "YouTube" to "www.youtube.com/watch?v-AaHV1Eea1RO",
        "YouTube" to "www.youtube.com/watch?v-AaHV1Eea1RO",
        "YouTube" to "www.youtube.com/watch?v-AaHV1Eea1RO",
        "YouTube" to "www.youtube.com/watch?v-AaHV1Eea1RO",
        "YouTube" to "www.youtube.com/watch?v-AaHV1Eea1RO",
        "YouTube" to "www.youtube.com/watch?v-AaHV1Eea1RO",
        "YouTube" to "www.youtube.com/watch?v-AaHV1Eea1RO",
        "YouTube" to "www.youtube.com/watch?v-AaHV1Eea1RO",
        "YouTube" to "www.youtube.com/watch?v-AaHV1Eea1RO",
        "YouTube" to "www.youtube.com/watch?v-AaHV1Eea1RO",
        "YouTube" to "www.youtube.com/watch?v-AaHV1Eea1RO",
        "YouTube" to "www.youtube.com/watch?v-AaHV1Eea1RO",
        "YouTube" to "www.youtube.com/watch?v-AaHV1Eea1RO",
        "YouTube" to "www.youtube.com/watch?v-AaHV1Eea1RO",
        "YouTube" to "www.youtube.com/watch?v-AaHV1Eea1RO",
    ),
)
