package com.nexters.boolti.domain.repository

import java.io.File

interface FileRepository {
    suspend fun requestUrlForUpload(file: File): Result<String>
    suspend fun requestFileUrl(file: File): Result<String>
}
