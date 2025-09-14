package com.nexters.boolti.presentation.screen.profile

import com.nexters.boolti.domain.model.PreviewList
import com.nexters.boolti.domain.model.Show
import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.model.YouTubeVideo

data class ProfileState(
    val loading: Boolean = false,
    val user: User = User.My(""),
    val isMine: Boolean = false,
    val youtubeVideos: List<YouTubeVideo> = emptyList(),
) {
    val profileSections: List<ProfileSection> = buildList {
        if (user.upcomingShow.visible) add(
            ProfileSection.UpcomingShow(
                shows = user.upcomingShow.previewItems,
                hasMoreItems = user.upcomingShow.hasMoreItems,
            )
        )
        if (user.performedShow.visible) add(
            ProfileSection.PerformedShow(
                shows = user.performedShow.previewItems,
                hasMoreItems = user.performedShow.hasMoreItems
            )
        )
        if (user.video.visible) add(
            ProfileSection.Video(
                videos = youtubeVideos,
                hasMoreItems = user.video.hasMoreItems,
            )
        )
        if (user.link.visible) add(
            ProfileSection.Link(
                links = user.link.previewItems,
                hasMoreItems = user.link.hasMoreItems
            )
        )
    }
}

private val <T : Any> PreviewList<T>.visible: Boolean
    get() = totalSize > 0 && isVisible != false && previewItems.isNotEmpty()

sealed interface ProfileSection {
    data class UpcomingShow(val shows: List<Show>, val hasMoreItems: Boolean) : ProfileSection
    data class PerformedShow(val shows: List<Show>, val hasMoreItems: Boolean) : ProfileSection
    data class Video(val videos: List<YouTubeVideo>, val hasMoreItems: Boolean) : ProfileSection
    data class Link(
        val links: List<com.nexters.boolti.domain.model.Link>,
        val hasMoreItems: Boolean
    ) : ProfileSection
}
