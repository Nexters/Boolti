package com.nexters.boolti.presentation.component

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kakao.sdk.user.UserApiClient
import com.nexters.boolti.presentation.icons.IconPack
import com.nexters.boolti.presentation.icons.iconpack.Kakaotalk
import com.nexters.boolti.presentation.theme.Grey95

@Composable
fun KakaoLoginButton() {
    val localContext = LocalContext.current
    TextButton(
        onClick = {
            UserApiClient.instance.loginWithKakaoTalk(localContext) { token, error ->
                if (error != null) {
                    // TODO 로그인 실패 처리
                    Log.e("KakaoLoginButton", "로그인 실패", error)
                } else if (token != null) {
                    // TODO token.accessToken를 사용하여 로그인 성공 처리
                }
            }
        },
        modifier = Modifier
            .padding(20.dp)
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
                imageVector = IconPack.Kakaotalk, contentDescription = "카카오톡 아이콘",
                modifier = Modifier.size(width = 20.dp, height = 20.dp),
                tint = Color.Black,
            )
            Text(
                "카카오톡으로 시작하기",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium,
                color = Grey95,
                textAlign = TextAlign.Center,
            )
        }
    }
}