package com.tes.presentation.main.recording

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.tes.presentation.main.MainViewEvent
import com.tes.presentation.main.MainViewModel
import com.tes.presentation.main.MainViewState
import com.tes.presentation.theme.Padding
import com.tes.presentation.theme.lightGrey
import com.tes.presentation.theme.vodleTypoGraphy
import main.components.BasicDialog
import main.components.ButtonComponent
import main.components.VodleCustomTextField

@Composable
internal fun TypingDialog(viewModel: MainViewModel, viewState: MainViewState.MakingVodle) {
    val text = remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = { viewModel.onTriggerEvent(MainViewEvent.OnDismissRecordingDialog) }
    ) {
        BasicDialog {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Padding.dialogPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "남기고 싶은 말을 작성해주세요!",
                    style = vodleTypoGraphy.bodyMedium
                )

                VodleCustomTextField(
                    text,
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(top = 24.dp),
                    false,
                    100
                )

                Text(
                    text = "${text.value.length} / 100",
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 4.dp, end = 12.dp),
                    style = vodleTypoGraphy.bodySmall.merge(
                        TextStyle(color = lightGrey)
                    )
                )

                ButtonComponent(
                    modifier = Modifier.padding(top = 8.dp),
                    buttonText = "다 썼어요!",
                    onClick = {
                        viewModel.onTriggerEvent(
                            MainViewEvent.OnClickFinishTypingButton(
                                text.value,
                                viewState.selectedVoiceType
                            )
                        )
                    },
                    buttonTextStyle = vodleTypoGraphy.titleMedium
                )
            }
        }
    }
}
