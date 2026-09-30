package com.nexters.boolti.domain.repository

interface FileRepository {
    suspend fun requestUrlForUpload(imageUri: String): Result<String>
}
