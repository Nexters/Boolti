package com.nexters.boolti.data.repository

import com.nexters.boolti.data.datasource.FileDataSource
import com.nexters.boolti.domain.repository.FileRepository
import java.io.File
import javax.inject.Inject

internal class FileRepositoryImpl @Inject constructor(
    private val dataSource: FileDataSource,
) : FileRepository {
    override suspend fun requestUrlForUpload(file: File): Result<String> =
        dataSource.requestUploadUrls(file).map { it.expectedUrl }
}
