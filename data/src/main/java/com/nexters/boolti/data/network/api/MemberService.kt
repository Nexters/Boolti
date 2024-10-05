package com.nexters.boolti.data.network.api

import com.nexters.boolti.data.network.response.MemberResponse
import retrofit2.http.GET
import retrofit2.http.Path

internal interface MemberService {
    @GET("/app/papi/v1/users/{userCode}")
    suspend fun getMember(
        @Path("userCode") userCode: String,
    ): MemberResponse
}
