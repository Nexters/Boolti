package com.nexters.boolti.data.repository

import com.nexters.boolti.domain.repository.DebugReportRepository
import com.nexters.boolti.domain.util.suspendRunCatching
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException

/**
 * 디스코드 웹훅으로 보낸다. 본문은 2000자 제한이 있어서 로그는 .txt 파일로 첨부한다
 */
internal class DebugReportRepositoryImpl(
    private val webhookUrl: String,
    private val client: OkHttpClient,
    private val ioDispatcher: CoroutineDispatcher,
) : DebugReportRepository {

    override val canSend: Boolean
        get() = webhookUrl.isNotBlank()

    override suspend fun send(content: String, logs: String, screenshotPath: String?): Result<Unit> =
        suspendRunCatching {
            withContext(ioDispatcher) {
                val payload = buildJsonObject {
                    put("content", toDiscordContent(content))
                    // 로그에 @everyone 같은 문구가 있어도 멘션하지 않는다
                    putJsonObject("allowed_mentions") { putJsonArray("parse") {} }
                }
                val body = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("payload_json", payload.toString())
                    .addFormDataPart(
                        "files[0]",
                        "logs.txt",
                        logs.toRequestBody("text/plain; charset=utf-8".toMediaType()),
                    )
                    .apply {
                        val screenshot = screenshotPath?.let(::File)?.takeIf { it.exists() } ?: return@apply
                        addFormDataPart("files[1]", screenshot.name, screenshot.asRequestBody("image/jpeg".toMediaType()))
                    }
                    .build()

                val request = Request.Builder().url(webhookUrl).post(body).build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("디스코드 전송 실패: ${response.code}")
                }
            }
        }
}

private const val DISCORD_CONTENT_LIMIT = 2000
private const val CODE_BLOCK = "```"

/** 고정폭 글꼴로 보이게 코드 블록으로 감싸고, 2000자를 넘지 않게 자른다 */
internal fun toDiscordContent(text: String): String {
    val maxTextLength = DISCORD_CONTENT_LIMIT - (CODE_BLOCK.length + 1) * 2
    val trimmed = if (text.length > maxTextLength) text.take(maxTextLength - 1) + "…" else text
    return "$CODE_BLOCK\n$trimmed\n$CODE_BLOCK"
}
