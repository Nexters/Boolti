package com.nexters.boolti.data.network.api

import com.nexters.boolti.data.network.response.UploadUrlsDto
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Url

internal interface FileService {
    @POST("/app/api/v1/user/profile-images/upload-urls")
    suspend fun requestUploadUrls(): UploadUrlsDto

    @Multipart
    @PUT
    suspend fun requestUploadImage(
        @Url url: String,
        @Part file: MultipartBody.Part,
    ): String
}
