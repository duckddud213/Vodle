package com.tes.presentation.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.tes.presentation.R
import com.tes.presentation.theme.main_coral_bright
import com.tes.presentation.theme.vodleTypoGraphy

@Composable
internal fun SignOutDialog(onClickConfirmButton: () -> Unit, onClickCancelButton: () -> Unit) {
    Dialog(onDismissRequest = { onClickCancelButton() }) {
        Box(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .clip(shape = RoundedCornerShape(32.dp))
                .wrapContentHeight()
                .background(Color.White)
                .padding(top = 24.dp, bottom = 12.dp)

        ) {
            Image(
                modifier = Modifier
                    .width(120.dp)
                    .height(120.dp)
                    .align(Alignment.CenterEnd)
                    .alpha(0.5f),
                painter = painterResource(id = R.drawable.logo_panpare),
                contentDescription = null
            )

            Column {
                SignOutDialogText("정말 탈퇴하실건가요?\n더이상 보들을 만들거나 들을 수 없게됩니다!") {}

                Spacer(modifier = Modifier.height(24.dp))

                Row {
                    Spacer(modifier = Modifier.weight(1f))
                    SignOutDialogText("취소") { onClickCancelButton() }
                    Spacer(modifier = Modifier.weight(1f))
                    SignOutDialogText(text = "확인") { onClickConfirmButton() }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun SignOutDialogText(text: String, onClickText: () -> Unit) {
    OutLinedText(
        onClickText = onClickText,
        text = text,
        textStyle = vodleTypoGraphy.bodyMedium.merge(Color.Black),
        strokeColor = main_coral_bright,
        strokeWidth = 1f
    )
}
