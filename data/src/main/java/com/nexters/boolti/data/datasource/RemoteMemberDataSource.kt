package com.nexters.boolti.data.datasource

import com.nexters.boolti.data.network.api.MemberService
import com.nexters.boolti.data.network.response.HostedShowDto
import com.nexters.boolti.data.network.response.MemberResponse
import com.nexters.boolti.domain.model.UserCode
import com.nexters.boolti.domain.request.EditProfileRequest
import javax.inject.Inject

internal class RemoteMemberDataSource @Inject constructor(
    private val memberService: MemberService,
) {
    suspend fun getMember(userCode: UserCode): MemberResponse = memberService.getMember(userCode)
    suspend fun getLinks(userCode: UserCode): List<EditProfileRequest.LinkDto> = memberService.getLinks(userCode)
    suspend fun getPerformedShows(userCode: UserCode): List<HostedShowDto> = memberService.getPerformedShows(userCode)
    suspend fun getVideoLinks(userCode: UserCode): List<String> = memberService.getVideoLinks(userCode)
}
