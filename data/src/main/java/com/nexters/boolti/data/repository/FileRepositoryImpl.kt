package com.nexters.boolti.data.repository

import com.nexters.boolti.data.datasource.FileDataSource
import com.nexters.boolti.domain.repository.FileRepository
import java.io.File
import javax.inject.Inject

internal class FileRepositoryImpl @Inject constructor(
    private val dataSource: FileDataSource,
) : FileRepository {
    override suspend fun requestUrlForUpload(file: File): Result<String> =
        dataSource.requestUploadUrls(file).map { it.uploadUrl }

    override suspend fun requestFileUrl(file: File): Result<String> =
        dataSource.requestUploadUrls(file).map { it.expectedUrl }

    /*override suspend fun requestUrlForUpload(requestBody: RequestBody): Result<String> =
        dataSource.requestUploadUrls(requestBody).map { it.uploadUrl }

    override suspend fun requestFileUrl(requestBody: RequestBody): Result<String> =
        dataSource.requestUploadUrls(requestBody).map { it.expectedUrl }*/
}
