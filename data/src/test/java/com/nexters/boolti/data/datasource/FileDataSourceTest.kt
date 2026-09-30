package com.nexters.boolti.data.datasource

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.nexters.boolti.data.network.api.AuthFileService
import com.nexters.boolti.data.network.api.FileService
import com.nexters.boolti.data.network.response.UploadUrlsDto
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.RequestBody
import okio.Buffer
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream

class FileDataSourceTest : BehaviorSpec() {
    private val imageUri = "content://media/picker/0/image/1"
    private val imageBytes = byteArrayOf(1, 2, 3, 4, 5)
    private val uploadUrls = UploadUrlsDto(uploadUrl = "https://upload", expectedUrl = "https://expected")

    private val contentResolver = mockk<ContentResolver>()
    private val authFileService = mockk<AuthFileService>()
    private val fileService = mockk<FileService>()

    init {
        beforeSpec { mockkStatic(Uri::class) }
        afterSpec { unmockkStatic(Uri::class) }

        beforeTest {
            clearMocks(contentResolver, authFileService, fileService)
            every { Uri.parse(any()) } returns mockk()
            coEvery { authFileService.requestUploadUrls() } returns uploadUrls
            coEvery { fileService.requestUploadImage(any(), any(), any()) } returns Unit
        }

        fun dataSource(testDispatcher: TestDispatcher): FileDataSource {
            val context = mockk<Context> {
                every { cacheDir } returns tempdir()
                every { contentResolver } returns this@FileDataSourceTest.contentResolver
            }
            return FileDataSource(context, testDispatcher, authFileService, fileService)
        }

        fun givenInputStream(stream: InputStream?) {
            every { contentResolver.openInputStream(any()) } returns stream
        }

        given("이미지 Uri가 주어지고") {
            `when`("업로드를 요청하면") {
                then("원본과 같은 바이트를 업로드하고 expectedUrl을 돌려준다") {
                    runTest {
                        givenInputStream(ByteArrayInputStream(imageBytes))
                        val body = slot<RequestBody>()
                        coEvery { fileService.requestUploadImage(any(), any(), capture(body)) } returns Unit

                        val result = dataSource(StandardTestDispatcher(testScheduler)).requestUploadUrls(imageUri)

                        result.getOrNull() shouldBe uploadUrls
                        val uploaded = Buffer().also { body.captured.writeTo(it) }.readByteArray()
                        uploaded.contentEquals(imageBytes).shouldBeTrue()
                    }
                }
            }
        }

        given("이미지를 열 수 없는 Uri가 주어지고") {
            `when`("업로드를 요청하면") {
                then("실패를 돌려주고 업로드하지 않는다") {
                    runTest {
                        givenInputStream(null)

                        val result = dataSource(StandardTestDispatcher(testScheduler)).requestUploadUrls(imageUri)

                        result.isFailure.shouldBeTrue()
                        coVerify(exactly = 0) { fileService.requestUploadImage(any(), any(), any()) }
                    }
                }
            }
        }

        given("이미지를 읽다가 IOException이 나는 Uri가 주어지고") {
            `when`("업로드를 요청하면") {
                then("크래시 없이 실패를 돌려준다") {
                    runTest {
                        givenInputStream(object : InputStream() {
                            override fun read(): Int = throw IOException("읽기 실패")
                        })

                        val result = dataSource(StandardTestDispatcher(testScheduler)).requestUploadUrls(imageUri)

                        result.exceptionOrNull().shouldBeInstanceOf<IOException>()
                        coVerify(exactly = 0) { fileService.requestUploadImage(any(), any(), any()) }
                    }
                }
            }
        }
    }
}
