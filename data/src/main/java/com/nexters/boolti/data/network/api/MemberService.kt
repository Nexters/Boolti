package com.nexters.boolti.data.network.api

import com.nexters.boolti.data.network.response.MemberResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal interface MemberService {
    @GET("/app/papi/v1/users/{userCode}")
    suspend fun getMember(
        @Query("userCode") userCode: String,
    ): MemberResponse
}
