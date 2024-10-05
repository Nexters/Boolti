package com.nexters.boolti.data.datasource

import com.nexters.boolti.data.network.api.MemberService
import com.nexters.boolti.data.network.response.MemberResponse
import javax.inject.Inject

internal class MemberDataSource @Inject constructor(
    private val memberService: MemberService,
) {
    suspend fun getMember(userCode: String): MemberResponse = memberService.getMember(userCode)
}
