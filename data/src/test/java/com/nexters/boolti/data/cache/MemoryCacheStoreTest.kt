package com.nexters.boolti.data.cache

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import java.io.IOException
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TestTimeSource

class MemoryCacheStoreTest : BehaviorSpec() {
    private val textKey = cacheKey<String>("text", 10.seconds)

    init {
        Given("TTL 안에서") {
            Then("같은 키로 다시 부르면 fetch하지 않고 저장된 값을 돌려준다") {
                runTest {
                    val (store, _) = newStore()
                    var fetchCount = 0

                    store.getOrFetch(textKey) { "first".also { fetchCount++ } }
                    val second = store.getOrFetch(textKey) { "second".also { fetchCount++ } }

                    second shouldBe "first"
                    fetchCount shouldBe 1
                }
            }

            Then("forceRefresh면 다시 fetch하고 새 값으로 바꾼다") {
                runTest {
                    val (store, _) = newStore()
                    store.getOrFetch(textKey) { "old" }

                    store.getOrFetch(textKey, forceRefresh = true) { "new" } shouldBe "new"
                    store.getOrFetch(textKey) { "unused" } shouldBe "new"
                }
            }
        }

        Given("TTL이 지나면") {
            Then("다시 fetch한다") {
                runTest {
                    val (store, time) = newStore()
                    store.getOrFetch(textKey) { "old" }

                    time += 10.seconds

                    store.getOrFetch(textKey) { "new" } shouldBe "new"
                }
            }
        }

        Given("invalidate나 clear 뒤에는") {
            Then("invalidate한 키만 다시 fetch한다") {
                runTest {
                    val (store, _) = newStore()
                    val otherKey = cacheKey<String>("other", 10.seconds)
                    store.getOrFetch(textKey) { "old" }
                    store.getOrFetch(otherKey) { "other" }

                    store.invalidate(textKey)

                    store.getOrFetch(textKey) { "new" } shouldBe "new"
                    store.getOrFetch(otherKey) { "unused" } shouldBe "other"
                }
            }

            Then("clear하면 모든 키를 다시 fetch한다") {
                runTest {
                    val (store, _) = newStore()
                    store.getOrFetch(textKey) { "old" }

                    store.clear()

                    store.getOrFetch(textKey) { "new" } shouldBe "new"
                }
            }
        }

        Given("fetch가 실패하면") {
            Then("저장하지 않고 예외를 던지며, 다음 호출은 다시 fetch한다") {
                runTest {
                    val (store, _) = newStore()

                    shouldThrow<IOException> { store.getOrFetch<String>(textKey) { throw IOException() } }

                    store.getOrFetch(textKey) { "retry" } shouldBe "retry"
                }
            }
        }

        Given("같은 키를 동시에 요청하면") {
            Then("fetch는 한 번만 하고 모두 같은 값을 받는다") {
                runTest {
                    val (store, _) = newStore()
                    var fetchCount = 0

                    val results = List(3) {
                        async {
                            store.getOrFetch(textKey) {
                                fetchCount++
                                delay(100.milliseconds)
                                "value"
                            }
                        }
                    }.awaitAll()

                    results shouldBe List(3) { "value" }
                    fetchCount shouldBe 1
                }
            }
        }

        Given("최대 크기를 넘으면") {
            Then("가장 오래 안 쓴 키부터 지운다") {
                runTest {
                    val (store, _) = newStore(maxSize = 2)
                    val a = cacheKey<String>("a", 10.seconds)
                    val b = cacheKey<String>("b", 10.seconds)
                    val c = cacheKey<String>("c", 10.seconds)
                    store.getOrFetch(a) { "a" }
                    store.getOrFetch(b) { "b" }
                    store.getOrFetch(a) { "unused" } // a를 최근에 쓴 것으로 만든다

                    store.getOrFetch(c) { "c" }

                    store.getOrFetch(a) { "a2" } shouldBe "a"
                    store.getOrFetch(b) { "b2" } shouldBe "b2"
                }
            }
        }

        Given("키 문자열이 같아도 값 타입이 다르면") {
            Then("서로 다른 키로 저장한다") {
                runTest {
                    val (store, _) = newStore()
                    val textSame = cacheKey<String>("same", 10.seconds)
                    val numberSame = cacheKey<Int>("same", 10.seconds)
                    val listSame = cacheKey<List<String>>("same", 10.seconds)
                    val numberListSame = cacheKey<List<Int>>("same", 10.seconds)

                    store.getOrFetch(textSame) { "text" }
                    store.getOrFetch(numberSame) { 1 }
                    store.getOrFetch(listSame) { listOf("text") }

                    store.getOrFetch(textSame) { "unused" } shouldBe "text"
                    store.getOrFetch(numberSame) { 2 } shouldBe 1
                    store.getOrFetch(numberListSame) { listOf(1) } shouldBe listOf(1)
                }
            }
        }
    }

    private fun newStore(maxSize: Int = 200): Pair<MemoryCacheStore, TestTimeSource> {
        val time = TestTimeSource()
        return MemoryCacheStore(maxSize, time) to time
    }
}
