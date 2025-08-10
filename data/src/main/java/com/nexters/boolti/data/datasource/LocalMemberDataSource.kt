package com.nexters.boolti.data.datasource

import com.nexters.boolti.data.network.response.HostedShowDto
import com.nexters.boolti.domain.model.UserCode
import com.nexters.boolti.domain.request.EditProfileRequest
import java.util.concurrent.ConcurrentHashMap

internal class LocalMemberDataSource {
    private val linksCache = ConcurrentHashMap<UserCode, List<EditProfileRequest.LinkDto>>(300)
    private val performedShowsCache = ConcurrentHashMap<UserCode, List<HostedShowDto>>(300)
    private val videosCache = ConcurrentHashMap<UserCode, String>(300)

    fun getLinks(userCode: UserCode): List<EditProfileRequest.LinkDto>? {
        return linksCache[userCode]
    }

    fun setLink(userCode: UserCode, links: List<EditProfileRequest.LinkDto>) {
        linksCache[userCode] = links
    }

    fun removeLink(userCode: UserCode) {
        linksCache.remove(userCode)
    }

    fun getPerformedShows(userCode: UserCode): List<HostedShowDto>? {
        return performedShowsCache[userCode]
    }

    fun setPerformedShows(userCode: UserCode, shows: List<HostedShowDto>) {
        performedShowsCache[userCode] = shows
    }

    fun removePerformedShow(userCode: UserCode) {
        performedShowsCache.remove(userCode)
    }

    fun getVideo(userCode: UserCode): String? {
        return videosCache[userCode]
    }

    fun setVideo(userCode: UserCode, video: String) {
        videosCache[userCode] = video
    }

    fun removeVideo(userCode: UserCode) {
        videosCache.remove(userCode)
    }
}
