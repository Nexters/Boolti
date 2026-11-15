package com.nexters.boolti.logger

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.CustomKeysAndValues
import com.google.firebase.crashlytics.crashlytics
import com.nexters.boolti.data.network.ApiCallSite
import com.nexters.boolti.domain.extension.toErrorResponse
import okhttp3.HttpUrl
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class CrashlyticsTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (t == null || !shouldRecord(priority, t)) return
        attachCallSite(t)
        val keys = CustomKeysAndValues.Builder()
        httpErrorKeys(t).forEach { (key, value) -> keys.putString(key, value) }
        Firebase.crashlytics.recordException(t, keys.build())
    }

    companion object {
        // 네트워크 끊김·코루틴 취소는 앱 결함이 아니라서 제외해요
        internal fun shouldRecord(priority: Int, t: Throwable): Boolean =
            priority >= Log.ERROR && t !is IOException && t !is CancellationException

        // HttpException은 OkHttp 스레드에서 만들어져 스택에 앱 코드가 없어요. 요청을 보낸 위치의 스택을 원인으로 붙여요
        internal fun attachCallSite(t: Throwable) {
            val callSite = (t as? HttpException)?.response()?.raw()?.request?.tag(ApiCallSite::class.java) ?: return
            // 원인이 이미 있으면 initCause가 예외를 던져요. 같은 예외를 두 번 기록하는 경우도 여기서 걸러져요
            if (t.cause == null) runCatching { t.initCause(callSite) }
        }

        // HttpException은 스택에 앱 코드가 없어서 어느 API인지 키로 남겨요. 요청 body는 개인 정보가 섞일 수 있어서 넣지 않아요
        internal fun httpErrorKeys(t: Throwable): Map<String, String> {
            val response = (t as? HttpException)?.response() ?: return emptyMap()
            val request = response.raw().request
            val query = if (request.method == "GET") maskedQuery(request.url) else ""
            val keys = mutableMapOf("http_api" to "${request.method} ${request.url.encodedPath}$query ${response.code()}")

            // peek()으로 읽어야 에러 body를 소비하지 않아요. 에러 형식이 아니면(예: 502 HTML) 키를 빼요
            val error = runCatching {
                response.errorBody()?.source()?.peek()?.readUtf8()?.toErrorResponse()
            }.getOrNull()
            if (error != null) {
                keys["error_type"] = error.type
                keys["error_trace_id"] = error.errorTraceId
            }
            return keys
        }

        // ID·숫자 쿼리만 값을 남기고, 나머지(초대 코드·검색어 등)는 값을 가려요. 새 쿼리는 기본으로 가려져요
        private val SAFE_QUERY_NAMES = setOf("id", "showId", "salesTicketTypeId", "ticketCount", "page", "size", "limit", "part")

        private fun maskedQuery(url: HttpUrl): String {
            if (url.querySize == 0) return ""
            return (0 until url.querySize).joinToString("&", prefix = "?") { i ->
                val name = url.queryParameterName(i)
                val value = if (name in SAFE_QUERY_NAMES) url.queryParameterValue(i) else "***"
                "$name=$value"
            }
        }
    }
}
