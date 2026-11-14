package com.nexters.boolti.data.datasource

import android.content.Context
import androidx.core.net.toUri
import com.nexters.boolti.data.network.api.AuthFileService
import com.nexters.boolti.data.network.api.FileService
import com.nexters.boolti.data.network.response.UploadUrlsDto
import com.nexters.boolti.domain.qualifier.IoDispatcher
import com.nexters.boolti.domain.util.suspendRunCatching
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import javax.inject.Inject

internal class FileDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val authFileService: AuthFileService,
    private val fileService: FileService,
) {
    suspend fun requestUploadUrls(imageUri: String): Result<UploadUrlsDto> = suspendRunCatching {
        val file = copyToCache(imageUri)
        authFileService.requestUploadUrls().also {
            fileService.requestUploadImage(
                contentType = "image/jpeg",
                url = it.uploadUrl,
                file = file.asRequestBody("image/jpeg".toMediaType()),
            )
        }
    }.onFailure { Timber.w(it) }

    private suspend fun copyToCache(imageUri: String): File = withContext(ioDispatcher) {
        val inputStream = context.contentResolver.openInputStream(imageUri.toUri())
            ?: throw FileNotFoundException("이미지를 열 수 없어요: $imageUri")
        File(context.cacheDir, "temp_profile_image.jpg").also { file ->
            inputStream.use { input -> file.outputStream().use { input.copyTo(it) } }
        }
    }
}
