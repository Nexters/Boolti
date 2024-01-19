package com.nexters.boolti.presentation.screen.splash

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import com.nexters.boolti.domain.repository.ConfigRepository
import com.nexters.boolti.presentation.BuildConfig
import com.nexters.boolti.presentation.screen.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : ComponentActivity() {
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val shouldUpdate = viewModel.shouldUpdate.collectAsState(initial = null)
            SplashScreen(
                shouldUpdate = shouldUpdate.value,
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
                onDismiss = { finish() }
            )
        }
    }
}

@Composable
fun SplashScreen(
    shouldUpdate: Boolean?,
    modifier: Modifier = Modifier,
    onSuccessVersionCheck: () -> Unit,
    onClickUpdate: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (shouldUpdate) {
            true -> UpdateDialog(
                onClickUpdate = onClickUpdate,
                onClickDismiss = onDismiss,
            )

            false -> onSuccessVersionCheck()
            null -> Unit
        }
    }
}

@Composable
fun UpdateDialog(onClickUpdate: () -> Unit, onClickDismiss: () -> Unit) {
    AlertDialog(
        title = { Text(text = "업데이트 필요") },
        text = { Text(text = "최신 버전으로 업데이트 후 이용할 수 있습니다.") },
        onDismissRequest = {},
        confirmButton = {
            Button(onClick = onClickUpdate) {
                Text(text = "업데이트 하기")
            }
        },
        dismissButton = {
            Button(onClick = onClickDismiss, colors = ButtonDefaults.textButtonColors()) {
                Text(text = "닫기")
            }
        }
    )
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    configRepository: ConfigRepository,
) : ViewModel() {
    val shouldUpdate = configRepository.shouldUpdate()
}
