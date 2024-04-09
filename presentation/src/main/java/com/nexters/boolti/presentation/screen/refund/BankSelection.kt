package com.nexters.boolti.presentation.screen.refund

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.MainButton
import com.nexters.boolti.presentation.theme.Grey10
import com.nexters.boolti.presentation.theme.Grey80
import com.nexters.boolti.presentation.theme.Grey85
import com.nexters.boolti.presentation.theme.marginHorizontal

@Composable
fun BankSelection(
    onDismiss: () -> Unit,
    selectedBank: BankInfo?,
    onClick: (bankInfo: BankInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.padding(bottom = 48.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = marginHorizontal)
                .padding(bottom = 48.dp),
        ) {
            Text(
                modifier = Modifier.padding(bottom = 12.dp),
                text = stringResource(id = R.string.refund_bank_selection),
                style = MaterialTheme.typography.titleLarge,
            )
            LazyVerticalGrid(
                contentPadding = PaddingValues(vertical = 12.dp),
                columns = GridCells.Adaptive(minSize = 100.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                BankInfo.entries.forEach { bankInfo ->
                    item {
                        BackItem(
                            bankInfo = bankInfo,
                            onClick = onClick,
                            selected = if (selectedBank == null) null else selectedBank == bankInfo,
                        )
                    }
                }
            }
        }
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Grey85,
                            )
                        )
                    )
            )
            MainButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = marginHorizontal),
                label = stringResource(id = R.string.refund_select_bank),
                onClick = onDismiss,
                enabled = selectedBank != null,
            )
        }
    }
}

@Composable
fun BackItem(
    onClick: (bankInfo: BankInfo) -> Unit,
    bankInfo: BankInfo,
    modifier: Modifier = Modifier,
    selected: Boolean? = null,
) {
    Box(
        modifier = modifier
            .height(74.dp)
            .border(
                shape = RoundedCornerShape(4.dp),
                color = if (selected == true) Grey10 else Color.Transparent,
                width = 1.dp,
            )
            .clip(RoundedCornerShape(4.dp))
            .background(Grey80)
            .clickable {
                onClick(bankInfo)
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.alpha(alpha = if (selected == false) 0.4f else 1.0f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                modifier = modifier.size(32.dp),
                imageVector = ImageVector.vectorResource(bankInfo.icon),
                contentDescription = null,
            )
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = bankInfo.bankName,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
