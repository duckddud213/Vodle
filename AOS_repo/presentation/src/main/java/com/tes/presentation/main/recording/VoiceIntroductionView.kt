package com.tes.presentation.main.recording

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.tes.domain.model.Gender
import com.tes.presentation.main.MainViewEvent
import com.tes.presentation.main.MainViewModel
import com.tes.presentation.model.VoiceType
import com.tes.presentation.theme.Padding
import com.tes.presentation.theme.vodleTypoGraphy
import com.tes.presentation.utils.MediaPlayer
import com.tes.presentation.utils.RecordingModule
import main.components.BasicDialog
import main.components.ButtonComponent

@Composable
internal fun VoiceIntroductionView(
    viewModel: MainViewModel,
    selectedGenderState: MutableState<Gender>,
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
        BasicDialog {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Padding.dialogPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "보들 옵션 선택", style = vodleTypoGraphy.bodyLarge)

                Text(
                    modifier = Modifier.padding(top = 16.dp, bottom = 12.dp),
                    text = "내 성별",
                    style = vodleTypoGraphy.bodyMedium
                )
                GenderSelector(selectedGenderState)

                Text(
                    modifier = Modifier.padding(top = 24.dp, bottom = 12.dp),
                    text = "변경할 목소리 선택",
                    style = vodleTypoGraphy.bodyMedium
                )
                VoiceSelector(selectedVoiceIndex, voiceTypeList)

                Spacer(modifier = Modifier.weight(1f))

                ButtonComponent(
                    buttonText = "보들 만들기",
                    onClick = {
                        RecordingModule.recordingPermissionCheck(context).fold(
                            onSuccess = {
                                viewModel.onTriggerEvent(MainViewEvent.OnClickMakingVodleButton)
                                MediaPlayer.stopSample()
                            },
                            onFailure = {
                                viewModel.onTriggerEvent(
                                    MainViewEvent.ShowToast("마이크 권한 설정이 필요합니다")
                                )
                            }
                        )
                    },
                    buttonTextStyle = vodleTypoGraphy.titleMedium
                )
            }
        }
    }
}
