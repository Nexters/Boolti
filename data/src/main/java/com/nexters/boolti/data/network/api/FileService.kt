package com.nexters.boolti.data.network.api

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Url

internal interface FileService {
    @PUT
    suspend fun requestUploadImage(
        @Header("Content-Type") contentType: String,
        @Url url: String,
        @Body file: RequestBody,
    )
}
