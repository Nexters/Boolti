package com.nexters.boolti.data.network.api

import com.nexters.boolti.data.network.response.UploadUrlsDto
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

internal interface FileService {
    @POST("/app/api/v1/user/profile-images/upload-urls")
    @Multipart
    suspend fun requestUploadUrls(
        @Part file: MultipartBody.Part,
    ): UploadUrlsDto
}
