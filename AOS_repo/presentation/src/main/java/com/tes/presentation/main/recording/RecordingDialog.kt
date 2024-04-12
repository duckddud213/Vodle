package com.tes.presentation.main.recording

import android.media.MediaRecorder
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.tes.presentation.R
import com.tes.presentation.main.MainViewEvent
import com.tes.presentation.main.MainViewModel
import com.tes.presentation.main.MainViewState
import com.tes.presentation.theme.Padding
import com.tes.presentation.theme.lightGrey
import com.tes.presentation.theme.vodleTypoGraphy
import com.tes.presentation.utils.createM4aFile
import kotlinx.coroutines.launch
import main.components.BasicDialog
import main.components.ButtonComponent
import java.io.File

@Composable
internal fun RecordingDialog(viewModel: MainViewModel, viewState: MainViewState.MakingVodle) {
    Dialog(
        onDismissRequest = { viewModel.onTriggerEvent(MainViewEvent.OnDismissRecordingDialog) }
    ) {
        val context = LocalContext.current
        val progress = remember { Animatable(0f) }
        val isRecording = remember { mutableStateOf(true) }
        val recordingFile = createM4aFile(context)
        val recorder = MediaRecorder()

        BasicDialog {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Padding.dialogPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.megaphone),
                    modifier = Modifier.weight(1f),
                    contentDescription = null
                )

                Text(
                    text = "녹음 중입니다.\n하고싶은 말을 남겨주세요!",
                    style = vodleTypoGraphy.bodyMedium
                )

                LinearProgressIndicator(
                    progress = progress.value,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                )

                Text(
                    text = "${(progress.value * 15).toInt()} / 15초",
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 4.dp, end = 12.dp),
                    style = vodleTypoGraphy.bodySmall.merge(
                        TextStyle(color = lightGrey)
                    )
                )

                ButtonComponent(
                    modifier = Modifier.padding(top = 8.dp),
                    buttonText = "녹음 끝났어요",
                    onClick = {
                        recorder.stopRecording(isRecording)
                        viewModel.onTriggerEvent(
                            MainViewEvent.OnClickFinishRecordingButton(
                                recordingFile,
                                viewState.selectedVoiceType,
                                viewState.gender
                            )
                        )
                    },
                    buttonTextStyle = vodleTypoGraphy.titleMedium
                )
            }
        }

        if (isRecording.value) {
            StartRecording(viewModel, viewState, recordingFile, recorder, progress, isRecording)
        }
    }
}

@Composable
private fun StartRecording(
    viewModel: MainViewModel,
    viewState: MainViewState.MakingVodle,
    audioFile: File,
    recorder: MediaRecorder,
    progress: Animatable<Float, AnimationVector1D>,
    isRecording: MutableState<Boolean>
) {
    val coroutineScope = rememberCoroutineScope()

    recorder.apply {
        setAudioSource(MediaRecorder.AudioSource.MIC)
        setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        setOutputFile(audioFile.absolutePath)
        setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

        prepare()
        start()
    }

    LaunchedEffect(isRecording) {
        val progressAnimation = coroutineScope.launch {
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 15000,
                    easing = LinearEasing
                )
            )
            recorder.stopRecording(isRecording)
            viewModel.onTriggerEvent(
                MainViewEvent.OnClickFinishRecordingButton(
                    audioFile,
                    viewState.selectedVoiceType,
                    viewState.gender
                )
            )
        }

        if (!isRecording.value) {
            progressAnimation.cancel()
            recorder.stopRecording(isRecording)
        }
    }
}

private fun MediaRecorder.stopRecording(isRecording: MutableState<Boolean>) {
    if (isRecording.value) {
        isRecording.value = false
        this.stop()
        this.release()
    }
}
