package com.nexters.boolti.presentation

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.kakao.sdk.user.UserApiClient

@Composable
fun KakaoLoginButton() {
    val localContext = LocalContext.current
    TextButton(onClick = {
        UserApiClient.instance.loginWithKakaoTalk(localContext) { token, error ->
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