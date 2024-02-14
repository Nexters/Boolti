package com.nexters.boolti.presentation.screen.splash

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.domain.repository.ConfigRepository
import com.nexters.boolti.presentation.BuildConfig
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTDialog
import com.nexters.boolti.presentation.screen.MainActivity
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey50
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : ComponentActivity() {
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { true }

        setContent {
            BooltiTheme {
                val shouldUpdate by viewModel.shouldUpdate.collectAsStateWithLifecycle(null)
                SplashScreen(
                    shouldUpdate = shouldUpdate,
                    onSuccessVersionCheck = {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    },
                    onClickUpdate = {
                        val playStoreUrl = "http://play.google.com/store/apps/details?id=${BuildConfig.PACKAGE_NAME}"
                        startActivity(
                            Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse(playStoreUrl)
                                setPackage("com.android.vending")
                            }
                        )
                    },
                )
            }
        }
    }
}

@Composable
fun SplashScreen(
    shouldUpdate: Boolean?,
    modifier: Modifier = Modifier,
    onSuccessVersionCheck: () -> Unit,
    onClickUpdate: () -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (shouldUpdate) {
            true -> UpdateDialog(onClickUpdate = onClickUpdate)
            false -> onSuccessVersionCheck()
            null -> Unit
        }
    }
}

@Composable
fun UpdateDialog(onClickUpdate: () -> Unit) {
    BTDialog(
        enableDismiss = false,
        showCloseButton = false,
        positiveButtonLabel = stringResource(R.string.force_update_button),
        onClickPositiveButton = onClickUpdate,
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(R.string.force_update_title),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            modifier = Modifier
                .padding(top = 4.dp)
                .align(Alignment.CenterHorizontally),
            text = stringResource(R.string.force_update_description),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = Grey50,
        )
    }
}

@Preview
@Composable
fun UpdateDialogPreview() {
    BooltiTheme {
        Surface {
            UpdateDialog {}
        }
    }
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    configRepository: ConfigRepository,
) : ViewModel() {
    val shouldUpdate = configRepository.shouldUpdate()
}
