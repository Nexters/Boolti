package com.nexters.boolti.data.cache

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.ComparableTimeMark
import kotlin.time.TimeSource

@Singleton
internal class MemoryCacheStore(
    private val maxSize: Int,
    private val timeSource: TimeSource.WithComparableMarks,
) : CacheStore {
    @Inject
    constructor() : this(maxSize = 200, timeSource = TimeSource.Monotonic)

    private class Entry(val value: Any, val expiresAt: ComparableTimeMark)

    // 접근 순서로 정렬해 가장 오래 안 쓴 항목부터 지운다
    private val entries = object : LinkedHashMap<CacheKey<*>, Entry>(16, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<CacheKey<*>, Entry>?) = size > maxSize
    }

    // ponytail: 키를 해시로 32개 락에 나눠 같은 키의 동시 요청만 막는다. 다른 키가 같은 락에 걸리면 잠깐 기다리니, 문제 되면 락 수를 늘린다
    private val locks = Array(32) { Mutex() }

    override suspend fun <T : Any> getOrFetch(
        cacheKey: CacheKey<T>,
        forceRefresh: Boolean,
        fetch: suspend () -> T,
    ): T {
        if (!forceRefresh) read(cacheKey)?.let { return it }
        return locks[cacheKey.hashCode().mod(locks.size)].withLock {
            // 락을 기다리는 동안 앞선 요청이 저장했을 수 있다
            if (!forceRefresh) read(cacheKey)?.let { return@withLock it }
            fetch().also { write(cacheKey, it) }
        }
    }

    override suspend fun invalidate(cacheKey: CacheKey<*>) {
        synchronized(entries) { entries.remove(cacheKey) }
    }

    override suspend fun clear() {
        synchronized(entries) { entries.clear() }
    }

    // CacheKey는 값 타입까지 비교하므로 같은 키로 꺼낸 값은 항상 T다
    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> read(cacheKey: CacheKey<T>): T? = synchronized(entries) {
        val entry = entries[cacheKey] ?: return null
        if (entry.expiresAt.hasPassedNow()) {
            entries.remove(cacheKey)
            return null
        }
        entry.value as T
    }

    private fun write(cacheKey: CacheKey<*>, value: Any) {
        synchronized(entries) { entries[cacheKey] = Entry(value, timeSource.markNow() + cacheKey.ttl) }
    }
}
