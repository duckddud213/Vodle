package com.tes.presentation.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tes.presentation.theme.main_coral_bright
import com.tes.presentation.theme.vodleTypoGraphy

@Composable
internal fun MyPageView(
    onClickBackButton: () -> Unit,
    viewModel: MyPageViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(main_coral_bright)
            .padding(horizontal = 32.dp, vertical = 24.dp)
    ) {
        TopBar(onClickBackButton, "마이페이지")

        OutLinedText(
            modifier = Modifier.padding(top = 24.dp, bottom = 12.dp),
            onClickText = { viewModel.onTriggerEvent(MyPageViewEvent.OnClickMyVodleLog) },
            text = "내 낙서기록",
            textStyle = vodleTypoGraphy.bodyMedium.merge(TextStyle(fontSize = 24.sp)),
            strokeColor = Color.White,
            strokeWidth = 1f
        )
        OutLinedText(
            modifier = Modifier.padding(vertical = 12.dp),
            onClickText = { viewModel.onTriggerEvent(MyPageViewEvent.OnClickPrivacyPolicy) },
            text = "개인정보처리 방침",
            textStyle = vodleTypoGraphy.bodyMedium.merge(TextStyle(fontSize = 24.sp)),
            strokeColor = Color.White,
            strokeWidth = 1f
        )
        OutLinedText(
            modifier = Modifier.padding(vertical = 12.dp),
            onClickText = { viewModel.onTriggerEvent(MyPageViewEvent.OnClickLogout) },
            text = "로그아웃",
            textStyle = vodleTypoGraphy.bodyMedium.merge(TextStyle(fontSize = 24.sp)),
            strokeColor = Color.White,
            strokeWidth = 1f
        )
        OutLinedText(
            modifier = Modifier.padding(vertical = 12.dp),
            onClickText = {
                viewModel.onTriggerEvent(
                    MyPageViewEvent.OnClickSignOut
                )
            },
            text = "회원탈퇴",
            textStyle = vodleTypoGraphy.bodySmall.merge(TextStyle(fontSize = 16.sp)),
            strokeColor = Color.White,
            strokeWidth = 1f
        )
    }
}
