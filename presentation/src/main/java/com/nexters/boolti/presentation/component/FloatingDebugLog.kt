package com.nexters.boolti.presentation.component

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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

    val screenWidthPx = with(density) { containerSize.width }
    val screenHeightPx = with(density) { containerSize.height }

    // 드래그 중 임시 offset
    var dragOffsetX by remember { mutableFloatStateOf(0f) }
    var dragOffsetY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    x = with(density) { state.offsetX.toPx().roundToInt() } + dragOffsetX.roundToInt(),
                    y = with(density) { state.offsetY.toPx().roundToInt() } + dragOffsetY.roundToInt()
                )
            }
    ) {
        if (state.isExpanded) {
            ExpandedLogViewer(
                onDrag = { dragAmount ->
                    dragOffsetX += dragAmount.x
                    dragOffsetY += dragAmount.y
                },
                onDragEnd = {
                    val expandedWidth = with(density) { 300.dp.toPx() }
                    val expandedHeight = with(density) { (screenHeightPx * 0.5f) }

                    val newOffsetX = with(density) {
                        (state.offsetX.toPx() + dragOffsetX).coerceIn(
                            systemBarsLeft,
                            screenWidthPx - systemBarsRight - expandedWidth
                        ).toDp()
                    }
                    val newOffsetY = with(density) {
                        (state.offsetY.toPx() + dragOffsetY).coerceIn(
                            systemBarsTop,
                            screenHeightPx - systemBarsBottom - expandedHeight
                        ).toDp()
                    }
                    DebugManager.updatePosition(newOffsetX, newOffsetY)
                    dragOffsetX = 0f
                    dragOffsetY = 0f
                }
            )
        } else {
            MinimizedLogBubble(
                onDrag = { dragAmount ->
                    dragOffsetX += dragAmount.x
                    dragOffsetY += dragAmount.y
                },
                onDragEnd = {
                    val bubbleSize = with(density) { 60.dp.toPx() }

                    val newOffsetX = with(density) {
                        (state.offsetX.toPx() + dragOffsetX).coerceIn(
                            systemBarsLeft,
                            screenWidthPx - systemBarsRight - bubbleSize
                        ).toDp()
                    }
                    val newOffsetY = with(density) {
                        (state.offsetY.toPx() + dragOffsetY).coerceIn(
                            systemBarsTop,
                            screenHeightPx - systemBarsBottom - bubbleSize
                        ).toDp()
                    }
                    DebugManager.updatePosition(newOffsetX, newOffsetY)
                    dragOffsetX = 0f
                    dragOffsetY = 0f
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
    val logs by LogCollector.allLogs.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .width(300.dp)
            .fillMaxHeight(0.5f)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black.copy(alpha = 0.95f))
    ) {
        // 헤더 (드래그 가능)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = { onDragEnd() },
                        onDragCancel = { onDragEnd() }
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
                text = "Debug Logs (${logs.size})",
                style = MaterialTheme.typography.titleSmall,
                color = Color.White
            )
            Row {
                IconButton(
                    onClick = { DebugManager.minimizeLogViewer() },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_down),
                        contentDescription = "최소화",
                        tint = Color.White
                    )
                }
                IconButton(
                    onClick = { DebugManager.closeLogViewer() },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = "닫기",
                        tint = Color.White
                    )
                }
            }
        }

        HorizontalDivider(color = Grey50)

        // 로그 리스트
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            items(logs.reversed()) { log ->
                LogItem(log)
                Spacer(modifier = Modifier.height(4.dp))
            }
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
            .background(Color.Black.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
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
                tint = Color.White
            )
        }
    }
}

@Composable
private fun LogItem(log: LogData) {
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
            .background(Color(0xFF1E1E1E), shape = RoundedCornerShape(4.dp))
            .padding(4.dp)
    ) {
        Row {
            Text(
                text = log.tag ?: "NO_TAG",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp
                ),
                color = logColor
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = DateFormat.format("HH:mm:ss", log.timestamp).toString(),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp
                ),
                color = Grey50
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = log.message,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp
            ),
            color = Color.White,
            maxLines = 2
        )
    }
}
