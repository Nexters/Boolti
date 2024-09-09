package com.nexters.boolti.data.datasource

import android.content.Context
import com.nexters.boolti.data.network.api.AuthFileService
import com.nexters.boolti.data.network.api.FileService
import com.nexters.boolti.data.network.response.UploadUrlsDto
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

internal class FileDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authFileService: AuthFileService,
    private val fileService: FileService,
) {
    suspend fun requestUploadUrls(file: File): Result<UploadUrlsDto> = runCatching {
        authFileService.requestUploadUrls().also {
            val url = it.uploadUrl
            fileService.requestUploadImage(
                contentType = "image/jpeg",
                url = url,
                file = file.asRequestBody("image/jpeg".toMediaType()),
            )
        }
    }.onFailure {
        it.printStackTrace()
    }
}
