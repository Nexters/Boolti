package com.nexters.boolti.common.tracker.impression

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.registerOnLayoutRectChanged
import androidx.compose.ui.node.DelegatableNode.RegistrationHandle
import androidx.compose.ui.node.GlobalPositionAwareModifierNode
import androidx.compose.ui.node.LayoutAwareModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.UnplacedAwareModifierNode
import androidx.compose.ui.node.requireLayoutCoordinates
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalView
import androidx.core.view.OneShotPreDrawListener
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.util.UUID

/**
 * [threshold] 이상의 면적이 노출되면 기록함. 0은 화면 밖이라도 첫 실제 배치에서 기록함.
 *
 * [key] 생략 시 호출 위치별 랜덤 String을 저장 복원함. 목록 데이터 식별에는 명시적 key 권장.
 * [extras]는 발생 확정 시에만 평가함. [onImpressed]가 있으면 state의 중앙 콜백보다 우선함.
 * 부모 clip과 창 경계는 반영하며 다른 UI의 덮임이나 투명도는 계산하지 않음.
 */
@Composable
fun Modifier.impression(
    key: Any = rememberSaveable { UUID.randomUUID() },
    state: ImpressionState = rememberImpressionState(),
    threshold: Float = 0.5f,
    extras: () -> Map<String, Any> = { emptyMap() },
    onImpressed: ((Map<String, Any>) -> Unit)? = null,
): Modifier {
    require(threshold.isFinite() && threshold in 0f..1f) {
        "threshold must be finite and between 0 and 1"
    }
    return this then ImpressionElement(
        key = key,
        state = state,
        threshold = threshold,
        extras = extras,
        onImpressed = onImpressed,
        lifecycle = LocalLifecycleOwner.current.lifecycle,
        view = LocalView.current,
    )
}

private data class ImpressionElement(
    val key: Any,
    val state: ImpressionState,
    val threshold: Float,
    val extras: () -> Map<String, Any>,
    val onImpressed: ((Map<String, Any>) -> Unit)?,
    val lifecycle: Lifecycle,
    val view: View,
) : ModifierNodeElement<ImpressionNode>() {
    override fun create() = ImpressionNode(this)

    override fun update(node: ImpressionNode) = node.update(this)

    override fun InspectorInfo.inspectableProperties() {
        name = "impression"
        properties["key"] = key
        properties["threshold"] = threshold
        properties["state"] = state
    }
}

private class ImpressionNode(
    private var element: ImpressionElement,
) : Modifier.Node(), LayoutAwareModifierNode, GlobalPositionAwareModifierNode,
    UnplacedAwareModifierNode {
    override val shouldAutoInvalidate = false
    private var placed = false
    private var handle: RegistrationHandle? = null
    private var pendingEvaluation: OneShotPreDrawListener? = null
    private val lifecycleObserver = LifecycleEventObserver { _, _ ->
        if (!element.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            element.state.unregister(this)
        } else {
            scheduleEvaluation()
        }
    }

    fun update(next: ImpressionElement) {
        val previous = element
        val identityChanged = previous.key != next.key || previous.state !== next.state
        if (identityChanged) previous.state.unregister(this)
        if (previous.lifecycle !== next.lifecycle && isAttached) {
            previous.lifecycle.removeObserver(lifecycleObserver)
        }
        element = next
        if (previous.lifecycle !== next.lifecycle && isAttached) {
            next.lifecycle.addObserver(lifecycleObserver)
        }
        if (identityChanged || previous.threshold != next.threshold ||
            previous.lifecycle !== next.lifecycle || previous.view !== next.view
        ) {
            // 인자 갱신 중의 옛 좌표로 판정하지 않고 현재 프레임 배치가 끝난 뒤 확인함.
            scheduleEvaluation()
        }
    }

    override fun onAttach() {
        handle = registerOnLayoutRectChanged(0, 0) { evaluate() }
        element.lifecycle.addObserver(lifecycleObserver)
    }

    override fun onPlaced(coordinates: LayoutCoordinates) {
        placed = true
    }

    override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
        // 자신의 사각형이 같아도 부모의 clip/크기가 달라진 배치를 반영함.
        evaluate()
    }

    private fun scheduleEvaluation() {
        if (!isAttached) return
        pendingEvaluation?.removeListener()
        pendingEvaluation = element.view.doOnPreDraw {
            pendingEvaluation = null
            evaluate()
        }
        element.view.invalidate()
    }

    private fun evaluate() {
        if (!isAttached || !placed) return
        val coordinates = requireLayoutCoordinates()
        val active = element.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED) &&
                element.view.isShown && element.view.windowVisibility == View.VISIBLE
        val qualified = active && qualifies(coordinates, element.threshold)
        element.state.update(
            registration = this,
            key = element.key,
            qualified = qualified,
            threshold = element.threshold,
            extras = element.extras,
            onImpressed = element.onImpressed,
        )
    }

    private fun qualifies(coordinates: LayoutCoordinates, threshold: Float): Boolean {
        if (coordinates.size.width == 0 || coordinates.size.height == 0) return false
        if (threshold == 0f) return true

        val fullBounds = coordinates.boundsInWindow(clipBounds = false)
        val visibleBounds = coordinates.boundsInWindow(clipBounds = true)
        val fullArea = fullBounds.width.toDouble() * fullBounds.height
        if (fullArea <= 0.0) return false

        val visibleArea = visibleBounds.width.coerceAtLeast(0f).toDouble() *
                visibleBounds.height.coerceAtLeast(0f)
        return visibleArea > 0.0 && (visibleArea / fullArea).toFloat() >= threshold
    }

    override fun onUnplaced() {
        clearExposure()
    }

    override fun onReset() {
        clearExposure()
    }

    private fun clearExposure() {
        placed = false
        pendingEvaluation?.removeListener()
        pendingEvaluation = null
        element.state.unregister(this)
    }

    override fun onDetach() {
        clearExposure()
        handle?.unregister()
        handle = null
        element.lifecycle.removeObserver(lifecycleObserver)
    }
}
