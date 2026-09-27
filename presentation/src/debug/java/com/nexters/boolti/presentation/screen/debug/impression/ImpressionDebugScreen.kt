package com.nexters.boolti.presentation.screen.debug.impression

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.common.tracker.impression.ImpressionEvent
import com.nexters.boolti.common.tracker.impression.ImpressionState
import com.nexters.boolti.common.tracker.impression.impression
import com.nexters.boolti.common.tracker.impression.rememberImpressionState
import com.nexters.boolti.presentation.component.BtBackAppBar
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey10
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey80
import com.nexters.boolti.presentation.theme.Grey85
import com.nexters.boolti.presentation.theme.Orange01

@Composable
internal fun ImpressionDebugScreen(
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenLogs: () -> Unit = {},
    viewModel: ImpressionDebugViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ImpressionDebugContent(
        uiState = uiState,
        viewModelState = viewModel.impressionState,
        onSelectCase = viewModel::selectCase,
        onSelectThreshold = viewModel::selectThreshold,
        onNewSession = { viewModel.startSession() },
        onImpressed = viewModel::onImpressed,
        onLocalImpressed = viewModel::onLocalImpressed,
        onBackPressed = onBackPressed,
        onOpenLogs = onOpenLogs,
        modifier = modifier,
    )
}

@Composable
private fun ImpressionDebugContent(
    uiState: ImpressionDebugUiState,
    viewModelState: ImpressionState,
    onSelectCase: (ImpressionDebugCase) -> Unit,
    onSelectThreshold: (Float) -> Unit,
    onNewSession: () -> Unit,
    onImpressed: (ImpressionEvent) -> Unit,
    onLocalImpressed: (Map<String, Any>) -> Unit,
    onBackPressed: () -> Unit,
    onOpenLogs: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showCode by rememberSaveable { mutableStateOf(false) }
    Scaffold(
        modifier = modifier,
        topBar = { BtBackAppBar(title = "임프레션 테스트", onClickBack = onBackPressed) },
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(ImpressionDebugCase.entries, key = { it.name }) { case ->
                    FilterChip(
                        selected = uiState.case == case,
                        onClick = { onSelectCase(case) },
                        label = { Text(case.title) },
                    )
                }
            }
            Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(uiState.case.title, style = MaterialTheme.typography.titleLarge, color = Grey10)
                Text(uiState.case.guide, style = MaterialTheme.typography.bodySmall, color = Grey30)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onClick = onNewSession) { Text("새 세션") }
                    TextButton(onClick = { showCode = true }) { Text("사용 코드") }
                    TextButton(onClick = onOpenLogs) { Text("로그 뷰어") }
                }
                Text(
                    "세션 ${uiState.session} · 총 ${uiState.total}회 · 기준 ${(uiState.threshold * 100).toInt()}%",
                    style = MaterialTheme.typography.labelLarge,
                    color = Orange01,
                )
            }
            if (uiState.case == ImpressionDebugCase.Threshold) {
                Row(Modifier.padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(0f, 0.25f, 0.5f, 1f).forEach { threshold ->
                        FilterChip(
                            selected = uiState.threshold == threshold,
                            onClick = { onSelectThreshold(threshold) },
                            label = { Text("${(threshold * 100).toInt()}%") },
                        )
                    }
                }
            }
            key(uiState.session) {
                ImpressionSandbox(
                    uiState = uiState,
                    viewModelState = viewModelState,
                    onImpressed = onImpressed,
                    onLocalImpressed = onLocalImpressed,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }
            ImpressionEventPanel(uiState = uiState, modifier = Modifier.padding(horizontal = 20.dp))
            Text(
                "카드 영역 안에서 스크롤해 보세요. 새 세션은 이력·횟수·스크롤을 초기화해요. " +
                    "Debug 전용 AppTracker 이벤트로 기록돼요.",
                modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 24.dp),
                style = MaterialTheme.typography.bodySmall,
                color = Grey30,
            )
        }
    }
    if (showCode) {
        AlertDialog(
            onDismissRequest = { showCode = false },
            title = { Text("${uiState.case.title} 사용법") },
            text = {
                SelectionContainer {
                    Text(
                        text = uiState.case.code,
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            },
            confirmButton = { TextButton(onClick = { showCode = false }) { Text("닫기") } },
        )
    }
}

@Composable
private fun ImpressionSandbox(
    uiState: ImpressionDebugUiState,
    viewModelState: ImpressionState,
    onImpressed: (ImpressionEvent) -> Unit,
    onLocalImpressed: (Map<String, Any>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val localState = rememberImpressionState(
        deduplicate = uiState.case == ImpressionDebugCase.Once || uiState.case == ImpressionDebugCase.Reset,
        onImpressed = onImpressed,
    )
    val state = if (uiState.case == ImpressionDebugCase.ViewModel) viewModelState else localState
    var resetMessage by remember { mutableStateOf("") }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (uiState.case == ImpressionDebugCase.Reset) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = { state.reset(1); resetMessage = "1번 이력 삭제 · 스크롤 재진입 시 재기록" }) {
                    Text("1번 이력 삭제")
                }
                TextButton(onClick = { state.resetAll(); resetMessage = "전체 이력 삭제 · 스크롤 재진입 시 재기록" }) {
                    Text("전체 이력 삭제")
                }
            }
            Text(resetMessage.ifEmpty { "이력을 삭제해도 현재 노출 중인 카드는 바로 기록되지 않아요." },
                modifier = Modifier.height(44.dp),
                style = MaterialTheme.typography.bodySmall, color = Grey30)
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth().height(240.dp)
                .clip(RoundedCornerShape(8.dp)).background(Grey85)
                .border(1.dp, Grey80, RoundedCornerShape(8.dp)).testTag("impression-sandbox"),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(key = "spacer") {
                Box(Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                    Text("아래로 스크롤해 카드를 노출해 보세요 ↓", style = MaterialTheme.typography.bodySmall, color = Grey30)
                }
            }
            if (uiState.case == ImpressionDebugCase.Padding) {
                item(key = "padding-pair") {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        for (id in 1..2) {
                            val tracking = Modifier.impression(
                                key = id,
                                state = state,
                                extras = { sampleExtras(uiState, id) },
                            )
                            val spacing = Modifier.padding(bottom = 96.dp)
                            SampleCard(
                                label = if (id == 1) "여백 포함" else "여백 제외",
                                count = uiState.counts[id.toString()] ?: 0,
                                modifier = Modifier.weight(1f).background(Grey80)
                                    .then(if (id == 1) tracking.then(spacing) else spacing.then(tracking)),
                            )
                        }
                    }
                }
            } else {
                items(count = 8, key = { it + 1 }) { index ->
                    val id = index + 1
                    val tracking = if (uiState.case == ImpressionDebugCase.Basic) {
                        Modifier.impression(
                            extras = { sampleExtras(uiState, id) },
                            onImpressed = onLocalImpressed,
                        )
                    } else {
                        Modifier.impression(
                            key = id,
                            state = state,
                            threshold = uiState.threshold,
                            extras = { sampleExtras(uiState, id) },
                        )
                    }
                    SampleCard(
                        label = "카드 $id",
                        count = uiState.counts[id.toString()] ?: 0,
                        modifier = tracking,
                    )
                }
            }
            item(key = "bottom-space") { Spacer(Modifier.height(240.dp)) }
        }
    }
}

private fun sampleExtras(uiState: ImpressionDebugUiState, id: Int): Map<String, Any> = mapOf(
    "sample" to id.toString(),
    "rank" to id,
    "debug_case" to uiState.case.name,
    "test_session" to uiState.session,
    "is_debug" to "Y",
)

@Composable
private fun SampleCard(label: String, count: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().height(120.dp).background(Orange01.copy(alpha = 0.18f))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(label, style = MaterialTheme.typography.titleMedium, color = Grey10)
        Text("${count}회 발생", style = MaterialTheme.typography.bodyMedium, color = Orange01)
    }
}

@Composable
private fun ImpressionEventPanel(uiState: ImpressionDebugUiState, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("최근 이벤트 · 최대 30개", style = MaterialTheme.typography.titleMedium, color = Grey10)
        LazyColumn(
            modifier = Modifier.fillMaxWidth().height(160.dp).background(Grey85),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (uiState.entries.isEmpty()) {
                item { Text("아직 발생한 이벤트가 없어요.", style = MaterialTheme.typography.bodySmall, color = Grey30) }
            }
            items(uiState.entries, key = { it.number }) { entry ->
                val keyLabel = entry.key?.let { "${it::class.simpleName}($it)" }
                    ?: "자동 UUID · 로컬 콜백에는 미전달"
                Text(
                    "#${entry.number} · key=$keyLabel · ${(entry.threshold * 100).toInt()}%\nextras=${entry.extras}",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = Grey30,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ImpressionDebugScreenPreview() {
    BooltiTheme {
        ImpressionDebugContent(
            uiState = ImpressionDebugUiState(),
            viewModelState = remember { ImpressionState() },
            onSelectCase = {},
            onSelectThreshold = {},
            onNewSession = {},
            onImpressed = {},
            onLocalImpressed = {},
            onBackPressed = {},
            onOpenLogs = {},
        )
    }
}
