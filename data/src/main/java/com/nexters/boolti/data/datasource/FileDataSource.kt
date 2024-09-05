package com.nexters.boolti.data.datasource

import com.nexters.boolti.data.network.api.FileService
import com.nexters.boolti.data.network.response.UploadUrlsDto
import com.nexters.boolti.data.util.toImageMultipartBody
import java.io.File
import javax.inject.Inject

internal class FileDataSource @Inject constructor(
    private val fileService: FileService,
) {
    suspend fun requestUploadUrls(file: File): Result<UploadUrlsDto> = runCatching {
        fileService.requestUploadUrls().also {
            val url = it.uploadUrl
            fileService.requestUploadImage(
                url = url,
                file = file.toImageMultipartBody(),
            )
        }
    }
}
