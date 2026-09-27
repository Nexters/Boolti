package com.nexters.boolti.common.tracker.impression

import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImpressionModifierTest {
    @get:Rule
    val compose = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun keepTestScreenOn() {
        compose.runOnUiThread {
            compose.activity.setShowWhenLocked(true)
            compose.activity.setTurnScreenOn(true)
            compose.activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    @Test
    fun clippedAreaCrossingThresholdRecordsAgain() {
        val top = mutableIntStateOf(-51)
        val events = mutableListOf<ImpressionEvent>()
        val state = ImpressionState(onImpressed = events::add)
        compose.setContent {
            Viewport {
                Target(Modifier.offset { IntOffset(0, top.intValue) }.impression("a", state))
            }
        }
        compose.runOnIdle { assertEquals(0, events.size) }
        compose.runOnIdle { top.intValue = -50 }
        compose.runOnIdle { assertEquals(1, events.size) }
        compose.runOnIdle { top.intValue = -49 }
        compose.runOnIdle { assertEquals(1, events.size) }
        compose.runOnIdle { top.intValue = -100 }
        compose.runOnIdle { top.intValue = -50 }
        compose.runOnIdle { assertEquals(2, events.size) }
    }

    @Test
    fun zeroThresholdWaitsForPlacementButDoesNotRequireVisiblePixels() {
        val place = mutableStateOf(false)
        var count = 0
        compose.setContent {
            Layout(content = { Target(Modifier.impression(threshold = 0f) { count++ }) }) { children, _ ->
                val child = children.single().measure(Constraints.fixed(100, 100))
                layout(100, 100) { if (place.value) child.place(0, -200) }
            }
        }
        compose.runOnIdle { assertEquals(0, count) }
        compose.runOnIdle { place.value = true }
        compose.runOnIdle { assertEquals(1, count) }
        compose.runOnIdle { place.value = false }
        compose.runOnIdle { place.value = true }
        compose.runOnIdle { assertEquals(2, count) }
    }

    @Test
    fun zeroSizedTargetDoesNotRecord() {
        var count = 0
        compose.setContent {
            Box(Modifier.requiredSize(with(LocalDensity.current) { 0.toDp() }).impression(threshold = 0f) { count++ })
        }
        compose.runOnIdle { assertEquals(0, count) }
    }

    @Test
    fun largeTargetUsesItsOwnAreaAndThresholdOneRequiresFullExposure() {
        val threshold = mutableFloatStateOf(1f)
        var count = 0
        compose.setContent {
            Viewport {
                Target(Modifier.impression(threshold = threshold.floatValue) { count++ }, size = 200)
            }
        }
        compose.runOnIdle { assertEquals(0, count) }
        compose.runOnIdle { threshold.floatValue = 0.25f }
        compose.runOnIdle { assertEquals(1, count) }
    }

    @Test
    fun thresholdOneIncludesExactBoundary() {
        val top = mutableIntStateOf(-1)
        var count = 0
        compose.setContent {
            Viewport {
                Target(Modifier.offset { IntOffset(0, top.intValue) }.impression(threshold = 1f) { count++ })
            }
        }
        compose.runOnIdle { assertEquals(0, count) }
        compose.runOnIdle { top.intValue = 0 }
        compose.runOnIdle { assertEquals(1, count) }
    }

    @Test
    fun fractionalThresholdIncludesExactTenPercent() {
        val top = mutableIntStateOf(-91)
        var count = 0
        compose.setContent {
            Viewport {
                Target(Modifier.offset { IntOffset(0, top.intValue) }.impression(threshold = 0.1f) { count++ })
            }
        }
        compose.runOnIdle { assertEquals(0, count) }
        compose.runOnIdle { top.intValue = -90 }
        compose.runOnIdle { assertEquals(1, count) }
    }

    @Test
    fun latestExtrasAndCallbackAreUsedWithoutRecompositionImpressions() {
        val top = mutableIntStateOf(0)
        val rank = mutableIntStateOf(1)
        val received = mutableListOf<Int>()
        var evaluations = 0
        compose.setContent {
            val currentRank = rank.intValue
            Viewport {
                Target(
                    Modifier.offset { IntOffset(0, top.intValue) }.impression(
                        extras = { evaluations++; mapOf("rank" to currentRank) },
                    ) { received.add((it.getValue("rank") as Int) * currentRank) },
                )
            }
        }
        compose.runOnIdle { rank.intValue = 2 }
        compose.runOnIdle {
            assertEquals(listOf(1), received)
            assertEquals(1, evaluations)
            top.intValue = -100
        }
        compose.runOnIdle { top.intValue = 0 }
        compose.runOnIdle { assertEquals(listOf(1, 4), received) }
    }

    @Test
    fun newKeyIsNotEvaluatedAtOldPosition() {
        val key = mutableStateOf("a")
        val top = mutableIntStateOf(0)
        val events = mutableListOf<ImpressionEvent>()
        val state = ImpressionState(onImpressed = events::add)
        compose.setContent {
            Viewport { Target(Modifier.offset { IntOffset(0, top.intValue) }.impression(key.value, state)) }
        }
        compose.runOnIdle { key.value = "b"; top.intValue = -100 }
        compose.runOnIdle { assertEquals(listOf("a"), events.map { it.key }) }
        compose.runOnIdle { top.intValue = 0 }
        compose.runOnIdle { assertEquals(listOf("a", "b"), events.map { it.key }) }
    }

    @Test
    fun parentClipResizeIsObservedWithoutMovingTarget() {
        val size = mutableIntStateOf(49)
        var count = 0
        compose.setContent {
            // 대상 위치는 유지하고 부모 높이만 바꿔 절반 노출 경계를 검증함.
            Layout(
                modifier = Modifier.clipToBounds(),
                content = { Target(Modifier.impression { count++ }) },
            ) { children, _ ->
                val child = children.single().measure(Constraints.fixed(100, 100))
                layout(100, size.intValue) { child.place(0, 0) }
            }
        }
        compose.runOnIdle { assertEquals(0, count) }
        compose.runOnIdle { size.intValue = 50 }
        compose.runOnIdle { assertEquals(1, count) }
    }

    @Test
    fun generatedKeysAreDistinctAndSurviveStateRestoration() {
        val events = mutableListOf<ImpressionEvent>()
        val restoration = StateRestorationTester(compose)
        restoration.setContent {
            val state = rememberImpressionState(onImpressed = events::add)
            Box {
                Target(Modifier.impression(state = state))
                Target(Modifier.impression(state = state))
            }
        }
        compose.runOnIdle {
            assertEquals(2, events.size)
            assertNotEquals(events[0].key, events[1].key)
        }
        restoration.emulateSavedInstanceStateRestore()
        compose.runOnIdle {
            assertEquals(4, events.size)
            assertEquals(events.take(2).map { it.key }.toSet(), events.drop(2).map { it.key }.toSet())
        }
    }

    @Test
    fun savedDeduplicationSuppressesRestoredImpression() {
        var count = 0
        val restoration = StateRestorationTester(compose)
        restoration.setContent {
            val state = rememberImpressionState(deduplicate = true) { count++ }
            Target(Modifier.impression(state = state))
        }
        compose.runOnIdle { assertEquals(1, count) }
        restoration.emulateSavedInstanceStateRestore()
        compose.runOnIdle { assertEquals(1, count) }
    }

    @Test
    fun numericKeySurvivesRestorationAndRetainsItsType() {
        val events = mutableListOf<ImpressionEvent>()
        val restoration = StateRestorationTester(compose)
        restoration.setContent {
            val state = rememberImpressionState(deduplicate = true, onImpressed = events::add)
            Target(Modifier.impression(key = 513L, state = state))
        }
        compose.runOnIdle { assertEquals(513L, events.single().key) }
        restoration.emulateSavedInstanceStateRestore()
        compose.runOnIdle { assertEquals(1, events.size) }
    }

    @Test
    fun replacingSharedStateRechecksVisibleTarget() {
        val firstEvents = mutableListOf<ImpressionEvent>()
        val secondEvents = mutableListOf<ImpressionEvent>()
        val state = mutableStateOf(ImpressionState(onImpressed = firstEvents::add))
        compose.setContent { Target(Modifier.impression("a", state.value)) }
        compose.runOnIdle { state.value = ImpressionState(onImpressed = secondEvents::add) }
        compose.runOnIdle {
            assertEquals(1, firstEvents.size)
            assertEquals(1, secondEvents.size)
        }
    }

    @Test
    fun deduplicatedTargetDoesNotRecordOnScrollReentry() {
        val top = mutableIntStateOf(0)
        var count = 0
        val state = ImpressionState(deduplicate = true) { count++ }
        compose.setContent {
            Viewport { Target(Modifier.offset { IntOffset(0, top.intValue) }.impression("a", state)) }
        }
        compose.runOnIdle { top.intValue = -100 }
        compose.runOnIdle { top.intValue = 0 }
        compose.runOnIdle { assertEquals(1, count) }
    }

    @Test
    fun nestedScrollClipsTargetAndRecordsOnReentry() {
        var count = 0
        lateinit var scope: CoroutineScope
        lateinit var scrollTo: suspend (Int) -> Unit
        compose.setContent {
            val outer = rememberScrollState()
            val inner = rememberScrollState()
            scope = rememberCoroutineScope()
            scrollTo = { inner.scrollTo(it) }
            Viewport {
                Box(Modifier.verticalScroll(outer)) {
                    Viewport {
                        Box(Modifier.verticalScroll(inner)) {
                            Box(
                                Modifier.requiredSize(
                                    width = with(LocalDensity.current) { 100.toDp() },
                                    height = with(LocalDensity.current) { 300.toDp() },
                                ),
                            ) {
                                Target(Modifier.impression { count++ })
                            }
                        }
                    }
                }
            }
        }
        compose.runOnIdle { assertEquals(1, count); scope.launch { scrollTo(150) } }
        compose.waitForIdle()
        compose.runOnIdle { scope.launch { scrollTo(0) } }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(2, count) }
    }

    @Test
    fun lazyListReentryRecordsAgainWithoutRecordingUnplacedItems() {
        val events = mutableListOf<ImpressionEvent>()
        val state = ImpressionState(onImpressed = events::add)
        compose.setContent {
            LazyColumn(
                modifier = Modifier.requiredSize(with(LocalDensity.current) { 100.toDp() }).testTag("list"),
                state = rememberLazyListState(),
            ) {
                items(count = 30, key = { it }) { index ->
                    Target(Modifier.impression(key = index.toString(), state = state))
                }
            }
        }
        compose.runOnIdle { assertEquals(listOf("0"), events.map { it.key }) }
        compose.onNodeWithTag("list").performScrollToIndex(20)
        compose.runOnIdle { assertEquals(listOf("0", "20"), events.map { it.key }) }
        compose.onNodeWithTag("list").performScrollToIndex(0)
        compose.runOnIdle { assertEquals(listOf("0", "20", "0"), events.map { it.key }) }
    }

    @Test
    fun stoppedLifecycleDoesNotRecordAndStartRechecksPosition() {
        val events = mutableListOf<ImpressionEvent>()
        lateinit var registry: LifecycleRegistry
        compose.setContent {
            val owner = remember {
                object : LifecycleOwner {
                    override val lifecycle = LifecycleRegistry(this).also {
                        registry = it
                        it.currentState = Lifecycle.State.CREATED
                    }
                }
            }
            CompositionLocalProvider(LocalLifecycleOwner provides owner) {
                Target(Modifier.impression(state = rememberImpressionState(onImpressed = events::add)))
            }
        }
        compose.runOnIdle { assertEquals(0, events.size); registry.currentState = Lifecycle.State.STARTED }
        compose.runOnIdle { assertEquals(1, events.size); registry.currentState = Lifecycle.State.CREATED }
        compose.runOnIdle { registry.currentState = Lifecycle.State.STARTED }
        compose.runOnIdle { assertEquals(2, events.size) }
    }
}

@Composable
private fun Viewport(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier.requiredSize(with(LocalDensity.current) { 100.toDp() }).clipToBounds()) { content() }
}

@Composable
private fun Target(modifier: Modifier = Modifier, size: Int = 100) {
    Box(modifier.requiredSize(with(LocalDensity.current) { size.toDp() }))
}
