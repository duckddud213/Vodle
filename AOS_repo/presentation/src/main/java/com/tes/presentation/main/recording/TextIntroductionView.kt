package com.tes.presentation.main.recording

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.tes.presentation.main.MainViewEvent
import com.tes.presentation.main.MainViewModel
import com.tes.presentation.model.VoiceType
import com.tes.presentation.theme.Padding
import com.tes.presentation.theme.vodleTypoGraphy
import com.tes.presentation.utils.MediaPlayer
import main.components.ButtonComponent

@Composable
internal fun TextIntroductionView(
    viewModel: MainViewModel,
    selectedVoiceIndex: MutableIntState,
    voiceTypeList: List<VoiceType>,
    context: Context
) {
    Dialog(
        onDismissRequest = {
            viewModel.onTriggerEvent(MainViewEvent.OnDismissRecordingDialog)
            MediaPlayer.stopSample()
        }
    ) {
        Box(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .clip(shape = RoundedCornerShape(32.dp))
                .wrapContentHeight()
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(Padding.dialogPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "보들 옵션 선택", style = vodleTypoGraphy.bodyLarge)

                Text(
                    modifier = Modifier.padding(top = 24.dp, bottom = 12.dp),
                    text = "목소리 선택",
                    style = vodleTypoGraphy.bodyMedium
                )
                VoiceSelector(selectedVoiceIndex, voiceTypeList)

                Spacer(modifier = Modifier.height(40.dp))

                ButtonComponent(
                    buttonText = "보들 만들기",
                    onClick = {
                        viewModel.onTriggerEvent(MainViewEvent.OnClickMakingVodleButton)
                        MediaPlayer.stopSample()
                    },
                    buttonTextStyle = vodleTypoGraphy.titleMedium
                )
            }
        }
    }
}
