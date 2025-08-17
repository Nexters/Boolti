package com.nexters.boolti.data.datasource

import com.nexters.boolti.data.network.api.UserService
import com.nexters.boolti.data.network.request.SaveIntroduceRequest
import com.nexters.boolti.data.network.request.SaveNicknameRequest
import com.nexters.boolti.data.network.request.SetVisibleRequest
import com.nexters.boolti.data.network.response.DuplicatedResponse
import com.nexters.boolti.data.network.response.ToggleResultDto
import com.nexters.boolti.data.network.response.UserResponse
import com.nexters.boolti.domain.request.EditProfileRequest
import com.nexters.boolti.domain.request.SignoutRequest
import javax.inject.Inject

internal class UserDataSource @Inject constructor(
    private val userService: UserService,
) {
    suspend fun getUser(): UserResponse? {
        val response = userService.getUser()
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun signout(request: SignoutRequest) = userService.signout(request)

    suspend fun edit(request: EditProfileRequest): UserResponse = userService.editProfile(request)

    suspend fun setUpcomingShowVisible(visible: Boolean): ToggleResultDto =
        userService.setUpcomingShowVisible(SetVisibleRequest(visible))

    suspend fun setPastShowVisible(visible: Boolean): ToggleResultDto =
        userService.setPastShowVisible(SetVisibleRequest(visible))

    suspend fun checkUserCodeDuplicated(userCode: String): DuplicatedResponse =
        userService.checkUserCodeDuplicate(userCode)

    suspend fun saveNickname(nickname: String) =
        userService.saveNickname(SaveNicknameRequest(nickname))

    suspend fun saveIntroduce(introduce: String) =
        userService.saveIntroduction(SaveIntroduceRequest(introduce))
}
