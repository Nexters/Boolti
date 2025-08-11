package com.nexters.boolti.domain.model

sealed interface User {
    val nickname: String
    val photo: String?
    val userCode: UserCode
    val introduction: String
    val link: PreviewList<Link>
    val sns: List<Sns>
    val performedShow: PreviewList<Show>
    val upcomingShow: PreviewList<Show>
    val video: PreviewList<String>

    data class My(
        val id: String,
        override val nickname: String = "",
        val email: String = "",
        override val photo: String? = null,
        override val userCode: UserCode = "",
        override val introduction: String = "",
        override val sns: List<Sns> = emptyList(),
        override val link: PreviewList<Link> = emptyPreviewList(),
        override val performedShow: PreviewList<Show> = emptyPreviewList(),
        override val upcomingShow: PreviewList<Show> = emptyPreviewList(),
        override val video: PreviewList<String> = emptyPreviewList(),
    ) : User

    data class Others(
        override val nickname: String = "",
        override val photo: String? = null,
        override val userCode: UserCode = "",
        override val introduction: String = "",
        override val sns: List<Sns> = emptyList(),
        override val link: PreviewList<Link> = emptyPreviewList(),
        override val performedShow: PreviewList<Show> = emptyPreviewList(),
        override val upcomingShow: PreviewList<Show> = emptyPreviewList(),
        override val video: PreviewList<String> = emptyPreviewList(),
    ) : User
}
