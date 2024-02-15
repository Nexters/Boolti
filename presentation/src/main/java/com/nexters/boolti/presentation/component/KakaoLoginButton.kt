package com.nexters.boolti.presentation.component

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.theme.Grey95
import timber.log.Timber

@Composable
fun KakaoLoginButton(
    onClick: (accessToken: String, idToken: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val localContext = LocalContext.current
    TextButton(
        onClick = {
            val kakaoInstance = UserApiClient.instance
            val available = kakaoInstance.isKakaoTalkLoginAvailable(localContext)
            val loginCallback = { token: OAuthToken?, error: Throwable? ->
                if (error != null) {
                    // TODO 로그인 실패 처리
                    Timber.e("KakaoLoginButton", "로그인 실패", error)
                } else if (token != null) {
                    Timber.d("Kakao idToken ${token.idToken}")
                    onClick(token.accessToken, token.idToken!!) // todo : id token이 null일 경우 처리하기
                }
            }

            if (available) {
                kakaoInstance.loginWithKakaoTalk(localContext, callback = loginCallback)
            } else {
                kakaoInstance.loginWithKakaoAccount(localContext, callback = loginCallback)
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0XFFFFE833)),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_kakaotalk), contentDescription = "카카오톡 아이콘",
                modifier = Modifier.size(width = 20.dp, height = 20.dp),
                tint = Color.Black,
            )
            Text(
                stringResource(id = R.string.login_with_kakaotalk),
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium,
                color = Grey95,
                textAlign = TextAlign.Center,
            )
        }
    }
}