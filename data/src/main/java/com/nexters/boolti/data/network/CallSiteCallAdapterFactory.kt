package com.nexters.boolti.data.network

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * API를 호출한 위치의 스택. 에러 응답의 `raw().request` tag로 붙는다.
 * `HttpException`은 OkHttp 스레드에서 만들어져 스택에 앱 코드가 없어서, 기록할 때 이 값을 원인으로 붙인다.
 */
class ApiCallSite : Throwable("API 호출 위치")

/** 요청을 보내는 순간의 스택을 저장했다가, 에러 응답에만 [ApiCallSite]로 붙인다. 응답 내용과 흐름은 바꾸지 않는다 */
internal class CallSiteCallAdapterFactory : CallAdapter.Factory() {
    override fun get(returnType: Type, annotations: Array<out Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        if (getRawType(returnType) != Call::class.java) return null
        @Suppress("UNCHECKED_CAST")
        val delegate = retrofit.nextCallAdapter(this, returnType, annotations) as CallAdapter<Any, Any>
        return object : CallAdapter<Any, Any> {
            override fun responseType(): Type = delegate.responseType()
            override fun adapt(call: Call<Any>): Any = delegate.adapt(CallSiteCall(call))
        }
    }
}

private class CallSiteCall<T>(private val delegate: Call<T>) : Call<T> by delegate {
    override fun enqueue(callback: Callback<T>) {
        val callSite = ApiCallSite()
        delegate.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) =
                callback.onResponse(call, response.withCallSite(callSite))

            override fun onFailure(call: Call<T>, t: Throwable) = callback.onFailure(call, t)
        })
    }

    override fun execute(): Response<T> {
        val callSite = ApiCallSite()
        return delegate.execute().withCallSite(callSite)
    }

    override fun clone(): Call<T> = CallSiteCall(delegate.clone())
    override fun request(): Request = delegate.request()
    override fun timeout(): Timeout = delegate.timeout()
}

private fun <T> Response<T>.withCallSite(callSite: ApiCallSite): Response<T> {
    val errorBody = errorBody() ?: return this
    val raw = raw()
    val tagged = raw.newBuilder()
        .request(raw.request.newBuilder().tag(ApiCallSite::class.java, callSite).build())
        .build()
    return Response.error(errorBody, tagged)
}
