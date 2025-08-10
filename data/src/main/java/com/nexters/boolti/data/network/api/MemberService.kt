package com.nexters.boolti.data.network.api

import com.nexters.boolti.data.network.response.HostedShowDto
import com.nexters.boolti.data.network.response.MemberResponse
import com.nexters.boolti.domain.request.EditProfileRequest
import retrofit2.http.GET
import retrofit2.http.Path

internal interface MemberService {
    @GET("/app/papi/v1/users/{userCode}")
    suspend fun getMember(
        @Path("userCode") userCode: String,
    ): MemberResponse

    suspend fun getLinks(
        @Path("userCode") userCode: String,
    ): List<EditProfileRequest.LinkDto> = emptyList()

    suspend fun getPerformedShows(
        @Path("userCode") userCode: String,
    ): List<HostedShowDto> = emptyList()
}
