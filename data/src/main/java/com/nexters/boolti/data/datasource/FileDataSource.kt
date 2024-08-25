package com.nexters.boolti.data.datasource

import com.nexters.boolti.data.network.api.FileService
import com.nexters.boolti.data.network.response.UploadUrlsDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

internal class FileDataSource @Inject constructor(
    private val fileService: FileService,
) {
    suspend fun requestUploadUrls(file: File): Result<UploadUrlsDto> = runCatching {
        fileService.requestUploadUrls(
            file = MultipartBody.Part.create(file.asRequestBody()),
        )
    }
    suspend fun requestUploadUrls(requestBody: RequestBody): Result<UploadUrlsDto> = runCatching {
        fileService.requestUploadUrls(
            file = MultipartBody.Part.create(requestBody),
        )
    }
}
