package com.nexters.boolti.presentation.screen.business

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BtBackAppBar
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey85
import com.nexters.boolti.presentation.theme.marginHorizontal

@Composable
fun BusinessScreen(
    onBackPressed: () -> Unit,
) {
    Scaffold(
        contentColor = MaterialTheme.colorScheme.background,
        topBar = {
            BtBackAppBar(
                title = stringResource(id = R.string.business_title),
                onClickBack = onBackPressed,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = marginHorizontal)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                modifier = Modifier.padding(top = 20.dp, bottom = 16.dp),
                text = stringResource(id = R.string.business_name),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )

            val information = stringArrayResource(id = R.array.business_information)
            information.forEachIndexed { index, text ->
                val modifier = if (index == 0) Modifier else Modifier.padding(top = 4.dp)

                Text(
                    modifier = modifier,
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Grey30,
                )
            }

            BusinessMenu(
                modifier = Modifier.padding(top = 48.dp, bottom = 12.dp),
                title = stringResource(id = R.string.business_service_terms),
                url = "https://www.notion.so/boolti/b4c5beac61c2480886da75a1f3afb982"
            )
            BusinessMenu(
                modifier = Modifier.padding(bottom = 12.dp),
                title = stringResource(id = R.string.business_privacy_policy),
                url = "https://www.notion.so/boolti/5f73661efdcd4507a1e5b6827aa0da70"
            )
            BusinessMenu(
                title = stringResource(id = R.string.business_refund_policy),
                url = "https://www.notion.so/boolti/d2a89e2c19824c60bb1e928370d16989"
            )
        }
    }
}

@Composable
private fun BusinessMenu(
    title: String,
    url: String,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Grey85)
            .clickable { uriHandler.openUri(url) }
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = Grey30,
        )
        Icon(
            modifier = Modifier.size(20.dp),
            imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_right),
            tint = Grey50,
            contentDescription = null,
        )
    }
}

@Preview(backgroundColor = 0xFF090A0B)
@Composable
private fun BusinessScreenPreview() {
    BusinessScreen(onBackPressed = {})
}
