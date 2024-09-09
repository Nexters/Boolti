package com.nexters.boolti.data.network.api

import com.nexters.boolti.data.network.response.UploadUrlsDto
import retrofit2.http.POST

interface AuthFileService {
    @POST("/app/api/v1/user/profile-images/upload-urls")
    suspend fun requestUploadUrls(): UploadUrlsDto
}
