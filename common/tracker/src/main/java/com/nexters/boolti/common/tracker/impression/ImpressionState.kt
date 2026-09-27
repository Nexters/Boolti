package com.nexters.boolti.common.tracker.impression

import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.listSaver

/**
 * 여러 컴포넌트의 노출 구간과 중복 기록을 관리함
 *
 * 기본은 재노출마다 기록하며, [deduplicate]가 true이면 이 상태에서 key당 한 번만 기록함.
 * key는 equals/hashCode가 변하지 않는 값을 사용함. 저장 복원 시 저장 가능한 타입이어야 함.
 * 상태 변경과 콜백은 메인 스레드에서 실행해야 함. 콜백은 UI나 Context를 장기 보관하지 않아야 함.
 */
@Stable
class ImpressionState(
    val deduplicate: Boolean = false,
    onImpressed: (ImpressionEvent) -> Unit = {},
) {
    internal var onImpressed: (ImpressionEvent) -> Unit = onImpressed
    private val impressedKeys = mutableSetOf<Any>()
    private val activeKeys = mutableMapOf<Any, Any>()
    private val activeCounts = mutableMapOf<Any, Int>()

    /** 기록 이력만 제거함. 이미 노출 중인 key는 다음 노출 구간부터 다시 기록함. */
    fun reset(key: Any) {
        impressedKeys.remove(key)
    }

    /** 모든 기록 이력을 제거함. 현재 노출 구간은 유지함. */
    fun resetAll() {
        impressedKeys.clear()
    }

    internal fun update(
        registration: Any,
        key: Any,
        qualified: Boolean,
        threshold: Float,
        extras: () -> Map<String, Any>,
        onImpressed: ((Map<String, Any>) -> Unit)?,
    ) {
        require(threshold.isFinite() && threshold in 0f..1f) {
            "threshold must be finite and between 0 and 1"
        }
        if (activeKeys[registration] != key || !qualified) unregister(registration)
        if (!qualified || activeKeys.containsKey(registration)) return

        val count = activeCounts[key] ?: 0
        activeKeys[registration] = key
        activeCounts[key] = count + 1
        if (count > 0 || (deduplicate && key in impressedKeys)) return

        val properties = try {
            extras().toMap()
        } catch (error: Throwable) {
            unregister(registration)
            throw error
        }
        // 콜백이 상태에 재진입하더라도 같은 이벤트를 다시 보내지 않음.
        if (deduplicate) impressedKeys.add(key)
        if (onImpressed != null) {
            onImpressed(properties)
        } else {
            this.onImpressed(ImpressionEvent(key, threshold, properties))
        }
    }

    internal fun unregister(registration: Any) {
        val key = activeKeys.remove(registration) ?: return
        val remaining = activeCounts.getValue(key) - 1
        if (remaining == 0) activeCounts.remove(key) else activeCounts[key] = remaining
    }

    internal fun savedKeys(): List<Any> = impressedKeys.toList()

    internal fun restoreKeys(keys: List<Any>) {
        if (deduplicate) impressedKeys.addAll(keys)
    }

    companion object {
        /** 콜백과 활성 노출은 저장하지 않음. 콜백 자동 재연결은 rememberImpressionState 사용. */
        val Saver = listSaver<ImpressionState, Any>(
            save = { listOf(it.deduplicate) + it.savedKeys() },
            restore = { values ->
                ImpressionState(deduplicate = values.first() as Boolean).apply {
                    restoreKeys(values.drop(1))
                }
            },
        )
    }
}
