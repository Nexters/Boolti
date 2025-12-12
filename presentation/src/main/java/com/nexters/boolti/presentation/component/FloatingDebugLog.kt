package com.nexters.boolti.presentation.component

import android.text.format.DateFormat
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mangbaam.logger.LogCollector
import com.mangbaam.logger.LogData
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.util.DebugManager
import kotlin.math.roundToInt

@Composable
fun FloatingDebugLog() {
    val state = DebugManager.logViewerState

    if (!state.isVisible) return

    val density = LocalDensity.current
    val containerSize = LocalWindowInfo.current.containerSize
    val layoutDirection = LocalLayoutDirection.current
    val systemBars = WindowInsets.systemBars

    // 시스템 바 영역 계산
    val systemBarsTop = with(density) { systemBars.getTop(this).toFloat() }
    val systemBarsBottom = with(density) { systemBars.getBottom(this).toFloat() }
    val systemBarsLeft = with(density) { systemBars.getLeft(this, layoutDirection).toFloat() }
    val systemBarsRight = with(density) { systemBars.getRight(this, layoutDirection).toFloat() }

    val screenWidthPx = containerSize.width
    val screenHeightPx = containerSize.height

    // 실제 화면 위치 (픽셀 단위로 관리)
    var currentOffsetX by remember { mutableFloatStateOf(with(density) { state.offsetX.toPx() }) }
    var currentOffsetY by remember { mutableFloatStateOf(with(density) { state.offsetY.toPx() }) }

    AnimatedContent(
        targetState = state.isExpanded,
        modifier = Modifier
            .offset {
                IntOffset(
                    x = currentOffsetX.roundToInt(),
                    y = currentOffsetY.roundToInt()
                )
            },
    ) { isExpanded ->
        if (isExpanded) {
            ExpandedLogViewer(
                onDrag = { dragAmount ->
                    currentOffsetX += dragAmount.x
                    currentOffsetY += dragAmount.y
                },
                onDragEnd = {
                    val expandedWidth = with(density) { 300.dp.toPx() }
                    val expandedHeight = screenHeightPx * 0.5f

                    val newOffsetX = currentOffsetX.coerceIn(
                        systemBarsLeft,
                        screenWidthPx - systemBarsRight - expandedWidth
                    )
                    val newOffsetY = currentOffsetY.coerceIn(
                        systemBarsTop,
                        screenHeightPx - systemBarsBottom - expandedHeight
                    )

                    currentOffsetX = newOffsetX
                    currentOffsetY = newOffsetY

                    // DebugManager에도 위치 저장 (Dp 단위로 변환)
                    with(density) {
                        DebugManager.updatePosition(newOffsetX.toDp(), newOffsetY.toDp())
                    }
                }
            )
        } else {
            MinimizedLogBubble(
                onDrag = { dragAmount ->
                    currentOffsetX += dragAmount.x
                    currentOffsetY += dragAmount.y
                },
                onDragEnd = {
                    val bubbleSize = with(density) { 60.dp.toPx() }

                    val newOffsetX = currentOffsetX.coerceIn(
                        systemBarsLeft,
                        screenWidthPx - systemBarsRight - bubbleSize
                    )
                    val newOffsetY = currentOffsetY.coerceIn(
                        systemBarsTop,
                        screenHeightPx - systemBarsBottom - bubbleSize
                    )

                    currentOffsetX = newOffsetX
                    currentOffsetY = newOffsetY

                    // DebugManager에도 위치 저장 (Dp 단위로 변환)
                    with(density) {
                        DebugManager.updatePosition(newOffsetX.toDp(), newOffsetY.toDp())
                    }
                }
            )
        }
    }
}

@Composable
private fun ExpandedLogViewer(
    onDrag: (androidx.compose.ui.geometry.Offset) -> Unit,
    onDragEnd: () -> Unit
) {
    val state = DebugManager.logViewerState
    val filterTags = state.filterTags

    val logs by if (filterTags.isEmpty()) {
        LogCollector.allLogs
    } else {
        LogCollector.getLogsByTag(filterTags)
    }.collectAsStateWithLifecycle(emptyList())

    val pagerState = rememberPagerState(pageCount = { 2 })
    var isDraggingWindow by remember { mutableStateOf(false) }
    var expandedLogIds by remember { mutableStateOf(setOf<String>()) }

    Column(
        modifier = Modifier
            .width(300.dp)
            .fillMaxHeight(0.5f)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black.copy(alpha = state.opacity))
    ) {
        // 헤더 (드래그 가능)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { isDraggingWindow = true },
                        onDragEnd = {
                            isDraggingWindow = false
                            onDragEnd()
                        },
                        onDragCancel = {
                            isDraggingWindow = false
                            onDragEnd()
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount)
                    }
                }
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (pagerState.currentPage == 0) "Debug Logs (${logs.size})" else "설정",
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
            )
            Row {
                IconButton(
                    onClick = { DebugManager.minimizeLogViewer() },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_down),
                        contentDescription = "최소화",
                        tint = Color.White,
                    )
                }
                IconButton(
                    onClick = { DebugManager.closeLogViewer() },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = "닫기",
                        tint = Color.White,
                    )
                }
            }
        }

        HorizontalDivider(color = Grey50)

        // HorizontalPager로 로그 리스트와 설정 화면 전환
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = !isDraggingWindow
        ) { page ->
            when (page) {
                0 -> {
                    // 로그 리스트
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                    ) {
                        items(logs.reversed(), key = { it.id }) { log ->
                            LogItem(
                                log = log,
                                isExpanded = expandedLogIds.contains(log.id),
                                onToggle = {
                                    expandedLogIds = if (expandedLogIds.contains(log.id)) {
                                        expandedLogIds - log.id
                                    } else {
                                        expandedLogIds + log.id
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }

                1 -> {
                    // 설정 화면
                    SettingsScreen()
                }
            }
        }
    }
}

@Composable
private fun SettingsScreen() {
    val state = DebugManager.logViewerState
    val presetTags = listOf(
        "AppTracker",
        "MixpanelAPI",
        "OkHttp",
        "Config",
        "rememberNavControllerWithLog",
        "recordExceptionHandler"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 태그 필터
        Text(
            text = "필터 태그",
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 프리셋 태그
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(presetTags) { tag ->
                FilterChip(
                    selected = state.filterTags.contains(tag),
                    onClick = {
                        val newTags = if (state.filterTags.contains(tag)) {
                            state.filterTags - tag
                        } else {
                            state.filterTags + tag
                        }
                        DebugManager.updateFilterTags(newTags)
                    },
                    label = {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                )
            }
        }

        HorizontalDivider(
            color = Grey50,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        // 투명도 설정
        Text(
            text = "창 투명도: ${(state.opacity * 100).toInt()}%",
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Slider(
            value = state.opacity,
            onValueChange = { DebugManager.updateOpacity(it) },
            valueRange = 0.3f..1f,
            modifier = Modifier.fillMaxWidth()
        )

        HorizontalDivider(
            color = Grey50,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        // 로그 지우기
        TextButton(
            onClick = { LogCollector.clear() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "로그 전체 삭제",
                color = Color(0xFFF44336),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun MinimizedLogBubble(
    onDrag: (androidx.compose.ui.geometry.Offset) -> Unit,
    onDragEnd: () -> Unit
) {
    var isDragging by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(60.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = {
                        isDragging = false
                        onDragEnd()
                    },
                    onDragCancel = {
                        isDragging = false
                        onDragEnd()
                    }
                ) { change, dragAmount ->
                    change.consume()
                    onDrag(dragAmount)
                }
            }
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center,
    ) {
        IconButton(
            onClick = {
                if (!isDragging) {
                    DebugManager.openLogViewer()
                }
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_list),
                contentDescription = "로그 확장",
                tint = Color.Black,
            )
        }
    }
}

@Composable
private fun LogItem(
    log: LogData,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    val logColor = when (log.level) {
        2 -> Color(0xFF2196F3) // VERBOSE - Blue
        3 -> Color(0xFF4CAF50) // DEBUG - Green
        4 -> Color(0xFFFFC107) // INFO - Yellow
        5 -> Color(0xFFFF9800) // WARN - Orange
        6 -> Color(0xFFF44336) // ERROR - Red
        else -> Color.White
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xE61E1E1E))
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onToggle() }
                )
            }
            .padding(4.dp)
    ) {
        Row {
            Text(
                text = log.tag ?: "NO_TAG",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                ),
                color = logColor,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = DateFormat.format("HH:mm:ss", log.timestamp).toString(),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                ),
                color = Grey50,
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = log.message,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
            ),
            color = Color.White,
            maxLines = if (isExpanded) Int.MAX_VALUE else 2,
        )
    }
}
