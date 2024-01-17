package com.nexters.boolti.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.nexters.boolti.presentation.theme.BooltiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BooltiTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Column {
                        Greeting("Android")
                        KakaoLoginButton()
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun KakaoLoginButton() {
    val localContext = LocalContext.current
    TextButton(onClick = {
        UserApiClient.instance.loginWithKakaoTalk(localContext) { token, error ->
            var keyHash = Utility.getKeyHash(localContext)
            println("키 해시 : $keyHash")
            println("로그인")
            if (error != null) {
                Log.e("KakaoLoginButton", "로그인 실패", error)
            }
            else if (token != null) {
                Log.i("KakaoLoginButton", "로그인 성공 ${token.accessToken}")
            }
        }
    }) {
        Text("카카오톡으로 로그인")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BooltiTheme {
        Greeting("Android")
    }
}
