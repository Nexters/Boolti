package com.nexters.boolti.domain.repository

import com.nexters.boolti.domain.model.Duplicated
import com.nexters.boolti.domain.model.Link
import com.nexters.boolti.domain.model.Sns
import com.nexters.boolti.domain.model.ToggleResult
import com.nexters.boolti.domain.model.UserCode

interface UserConfigRepository {
    suspend fun setUpcomingShowVisible(visible: Boolean): Result<ToggleResult>
    suspend fun setPastShowVisible(visible: Boolean): Result<ToggleResult>
    suspend fun saveNickname(nickname: String): Result<String>
    suspend fun checkUserCodeDuplicated(userCode: UserCode): Result<Duplicated>
    suspend fun saveUserCode(userCode: UserCode): Result<String>
    suspend fun saveIntroduce(introduce: String): Result<String>
    suspend fun saveSns(snsList: List<Sns>): Result<Unit>
    suspend fun saveLinks(links: List<Link>): Result<Unit>
    suspend fun saveVideos(videoLinks: List<String>): Result<Unit>
    suspend fun saveThumbnail(path: String): Result<Unit>
}
