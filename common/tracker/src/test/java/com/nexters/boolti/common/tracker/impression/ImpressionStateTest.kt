package com.nexters.boolti.common.tracker.impression

import androidx.compose.runtime.saveable.SaverScope
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ImpressionStateTest : FunSpec({
    test("Any key는 값과 타입으로 구분하며 같은 값의 객체는 중복 처리함") {
        data class ShowKey(val id: Int)
        val events = mutableListOf<ImpressionEvent>()
        val state = ImpressionState(true, events::add)
        val token = Any()
        listOf(123, "123", ShowKey(123), ShowKey(123)).forEach { key ->
            state.unregister(token)
            state.update(token, key, true, 0.5f, { emptyMap() }, null)
        }
        events.map { it.key } shouldBe listOf(123, "123", ShowKey(123))
        state.unregister(token)
        state.reset(ShowKey(123))
        state.update(token, ShowKey(123), true, 0.5f, { emptyMap() }, null)
        events.size shouldBe 4
    }

    test("Saver는 숫자 key의 타입과 중복 이력을 보존함") {
        val state = ImpressionState(true)
        state.restoreKeys(listOf(123, 456L, "123"))
        val scope = SaverScope { it is Boolean || it is Int || it is Long || it is String }
        val saved = with(ImpressionState.Saver) { scope.save(state) }
        val restored = ImpressionState.Saver.restore(requireNotNull(saved))!!
        restored.savedKeys() shouldBe listOf(123, 456L, "123")
        restored.update(Any(), 123, true, 0.5f, { error("복원된 key 중복") }, null)
    }

    test("저장 불가능한 key는 중복 이력을 저장할 때 명확히 거부함") {
        val state = ImpressionState(true)
        state.restoreKeys(listOf(Any()))
        val scope = SaverScope { it is Boolean || it is String }
        shouldThrow<IllegalArgumentException> {
            with(ImpressionState.Saver) { scope.save(state) }
        }
    }

    test("기본 상태는 재노출마다 기록하고 노출 중에는 extras를 다시 평가하지 않음") {
        val events = mutableListOf<ImpressionEvent>()
        val state = ImpressionState(onImpressed = events::add)
        val token = Any()
        var evaluations = 0
        val extras = { mapOf("rank" to ++evaluations) }
        repeat(3) { state.update(token, "a", true, 0.5f, extras, null) }
        evaluations shouldBe 1
        state.update(token, "a", false, 0.5f, extras, null)
        state.update(token, "a", true, 0.5f, extras, null)
        events.map { it.extras["rank"] } shouldBe listOf(1, 2)
        state.savedKeys() shouldBe emptyList()
    }

    test("중복 제한은 key별로 적용하고 reset 후 다음 구간에 재발생함") {
        val events = mutableListOf<ImpressionEvent>()
        val state = ImpressionState(true, events::add)
        val token = Any()
        fun expose(key: String) = state.update(token, key, true, 0.5f, { emptyMap() }, null)
        expose("a")
        state.unregister(token)
        expose("a")
        expose("b")
        expose("a")
        events.map { it.key } shouldBe listOf("a", "b")
        state.reset("a")
        expose("a")
        events.size shouldBe 2
        state.unregister(token)
        expose("a")
        events.size shouldBe 3
        state.resetAll()
        state.savedKeys() shouldBe emptyList()
    }

    test("동일 key의 여러 등록은 모두 벗어난 뒤에만 재노출로 처리함") {
        var count = 0
        val state = ImpressionState { count++ }
        val first = Any()
        val second = Any()
        fun expose(token: Any) = state.update(token, "a", true, 1f, { emptyMap() }, null)
        expose(first)
        expose(second)
        state.unregister(first)
        expose(first)
        count shouldBe 1
        state.unregister(first)
        state.unregister(second)
        expose(second)
        count shouldBe 2
    }

    test("로컬 콜백을 우선하고 복사한 extras만 전달함") {
        var centralCount = 0
        val state = ImpressionState { centralCount++ }
        val properties = mutableMapOf<String, Any>("rank" to 1)
        var received: Map<String, Any>? = null
        state.update(Any(), "a", true, 0.5f, { properties }) { received = it }
        properties["rank"] = 2
        received shouldBe mapOf("rank" to 1)
        centralCount shouldBe 0
    }

    test("콜백 재진입과 예외 이후에도 key 중복 제한이 유지됨") {
        val state = ImpressionState(true)
        val token = Any()
        var count = 0
        shouldThrow<IllegalStateException> {
            state.update(token, "a", true, 0.5f, { emptyMap() }) {
                count++
                state.update(Any(), "a", true, 0.5f, { error("재평가 금지") }, null)
                error("콜백 실패")
            }
        }
        state.savedKeys() shouldBe listOf("a")
        count shouldBe 1
    }

    test("extras 실패 시 이력을 남기지 않으며 다음 판정에서 재시도 가능함") {
        val state = ImpressionState(true)
        val token = Any()
        shouldThrow<IllegalStateException> {
            state.update(token, "a", true, 0.5f, { error("extras 실패") }, null)
        }
        state.savedKeys() shouldBe emptyList()
        state.update(token, "a", true, 0.5f, { emptyMap() }, null)
        state.savedKeys() shouldBe listOf("a")
    }

    test("저장된 이력은 중복 제한 상태에서만 복원함") {
        val state = ImpressionState(true)
        state.restoreKeys(listOf("a"))
        state.update(Any(), "a", true, 0.5f, { error("중복 평가 금지") }, null)
        state.savedKeys() shouldBe listOf("a")
        val repeatable = ImpressionState()
        repeatable.restoreKeys(listOf("a"))
        repeatable.savedKeys() shouldBe emptyList()
    }

    test("유효하지 않은 threshold를 거부함") {
        listOf(-0.1f, 1.1f, Float.NaN, Float.POSITIVE_INFINITY).forEach { threshold ->
            shouldThrow<IllegalArgumentException> {
                ImpressionState().update(Any(), "a", true, threshold, { emptyMap() }, null)
            }
        }
    }
})
