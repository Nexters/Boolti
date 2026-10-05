package com.nexters.boolti.data.cache

import kotlin.reflect.KType
import kotlin.reflect.typeOf
import kotlin.time.Duration

internal interface CacheStore {
    /**
     * 캐시에 유효한 값이 있으면 돌려주고, 없거나 만료됐으면 [fetch]로 받아 저장한다.
     * [fetch]가 예외를 던지면 저장하지 않고 그대로 던진다.
     */
    suspend fun <T : Any> getOrFetch(
        cacheKey: CacheKey<T>,
        forceRefresh: Boolean = false,
        fetch: suspend () -> T,
    ): T

    suspend fun invalidate(cacheKey: CacheKey<*>)

    suspend fun clear()
}

/**
 * 키 문자열과 값 타입을 함께 비교한다. 같은 문자열이라도 타입이 다르면 다른 키다.
 * [cacheKey]로 만든다.
 */
internal class CacheKey<T : Any> @PublishedApi internal constructor(
    val key: String,
    val ttl: Duration,
    private val type: KType,
) {
    override fun equals(other: Any?) = other is CacheKey<*> && other.key == key && other.type == type

    override fun hashCode() = 31 * key.hashCode() + type.hashCode()
}

internal inline fun <reified T : Any> cacheKey(key: String, ttl: Duration): CacheKey<T> =
    CacheKey(key, ttl, typeOf<T>())
