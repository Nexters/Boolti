package com.nexters.boolti.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.theme.Grey70

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BtBottomSheet(
    onDismissRequest: () -> Unit,
    dragHandle: @Composable (() -> Unit)? = { BtBottomSheetDefault.DragHandle() },
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        containerColor = MaterialTheme.colorScheme.surfaceTint,
        onDismissRequest = onDismissRequest,
        windowInsets = WindowInsets(0, 0, 0, 0),
        dragHandle = dragHandle,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    ) {
        content()
        Spacer(Modifier.windowInsetsPadding(WindowInsets.navigationBars))
    }
}

object BtBottomSheetDefault {

    @Composable
    fun DragHandle() {
        Box(
            modifier = Modifier
                .padding(top = 12.dp, bottom = 24.dp)
                .size(45.dp, 4.dp)
                .clip(CircleShape)
                .background(color = Grey70)
        )
    }
}