package com.nexters.boolti.data.network.api

import com.nexters.boolti.data.network.response.HostedShowDto
import com.nexters.boolti.data.network.response.MemberResponse
import com.nexters.boolti.domain.request.EditProfileRequest
import retrofit2.http.GET
import retrofit2.http.Path

internal interface MemberService {
    @GET("/app/papi/v2/users/{userCode}")
    suspend fun getMember(
        @Path("userCode") userCode: String,
    ): MemberResponse

    @GET("/app/papi/v2/users/{userCode}/links")
    suspend fun getLinks(
        @Path("userCode") userCode: String,
    ): List<EditProfileRequest.LinkDto>

    @GET("/app/papi/v2/users/{userCode}/shows")
    suspend fun getPerformedShows(
        @Path("userCode") userCode: String,
    ): List<HostedShowDto>

    @GET("/app/papi/v2/users/{userCode}/videos")
    suspend fun getVideoLinks(
        @Path("userCode") userCode: String,
    ): List<String>
}
