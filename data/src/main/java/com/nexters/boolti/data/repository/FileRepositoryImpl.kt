package com.nexters.boolti.data.repository

import com.nexters.boolti.data.datasource.FileDataSource
import com.nexters.boolti.domain.repository.FileRepository
import javax.inject.Inject

internal class FileRepositoryImpl @Inject constructor(
    private val dataSource: FileDataSource,
) : FileRepository {
    override suspend fun requestUrlForUpload(imageUri: String): Result<String> =
        dataSource.requestUploadUrls(imageUri).map { it.expectedUrl }
}
