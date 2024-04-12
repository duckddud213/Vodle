package com.tes.presentation.main.recording

import androidx.annotation.OptIn
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import com.tes.domain.model.RecordType
import com.tes.presentation.main.MainViewEvent
import com.tes.presentation.main.MainViewModel
import com.tes.presentation.main.MainViewState
import com.tes.presentation.main.components.LoadingScreen
import com.tes.presentation.model.VodleOption
import com.tes.presentation.model.VoiceType
import com.tes.presentation.theme.Padding
import com.tes.presentation.theme.main_coral_bright
import com.tes.presentation.theme.main_coral_darken
import com.tes.presentation.theme.vodleTypoGraphy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import main.components.ButtonComponent

@OptIn(UnstableApi::class)
@Composable
internal fun CreateVodleDialog(
    viewModel: MainViewModel,
    viewState: MainViewState.MakingVodle,
    player: ExoPlayer
) {
    val recordType: RecordType = when (viewState.vodleOption) {
        VodleOption.TEXT -> RecordType.TTS
        VodleOption.VOICE -> {
            when (viewState.selectedVoiceType) {
                VoiceType.ORIGINAL -> RecordType.NONE
                else -> RecordType.STS
            }
        }
    }
    Dialog(
        onDismissRequest = { viewModel.onTriggerEvent(MainViewEvent.OnDismissRecordingDialog) }
    ) {
        val writer = remember { mutableStateOf("") }
        val isPlaying = remember { mutableStateOf(false) }
        val audioDuration = remember { mutableIntStateOf(0) }
        val currentProgress = remember { Animatable(0f) }
        val context = LocalContext.current
        val dataSourceFactory = DefaultDataSource.Factory(context)
        val playerLoad = remember { mutableStateOf(false) }
        val interactionSource = remember { MutableInteractionSource() }
        val bufferingTime = remember { mutableIntStateOf(0) }
        var bufferingJob: Job? = null

        LaunchedEffect(bufferingTime.intValue) {
            if (bufferingTime.intValue == 10) {
                viewModel.onTriggerEvent(
                    MainViewEvent.OnFailStreaming
                )
            }
        }

        LaunchedEffect(isPlaying.value) {
            if (isPlaying.value) {
                val hlsMediaSource =
                    HlsMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(
                            MediaItem.fromUri(
                                viewState.convertedAudio.convertedAudioUrl
                            )
                        )
                player.setMediaSource(hlsMediaSource)
                player.prepare()
                playerLoad.value = true
            } else {
                player.pause()
                player.stop()
                playerLoad.value = false
            }
        }

        player.addListener(object : Player.Listener {
            @Deprecated("Deprecated in Java")
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == Player.STATE_BUFFERING) {
                    startBuffering()
                }

                if (playbackState == Player.STATE_READY) {
                    player.play()
                    audioDuration.intValue = player.duration.toInt()
                    playerLoad.value = false
                    endBuffering()
                }

                if (playbackState == Player.STATE_ENDED) {
                    audioDuration.intValue = 0
                    endBuffering()
                }
            }

            private fun startBuffering() {
                bufferingJob?.cancel()
                bufferingJob = CoroutineScope(Dispatchers.IO).launch {
                    repeat(10) {
                        delay(1000)
                        bufferingTime.intValue += 1
                    }
                }
            }

            private fun endBuffering() {
                bufferingJob?.cancel()
                bufferingJob = null
            }
        })

        ProgressBarAnimation(isPlaying, audioDuration.intValue, currentProgress)
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
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(Padding.dialogPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WriterRow(writer = writer)

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier
                        .padding(top = 10.dp, end = 20.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    IconButton(
                        interactionSource = interactionSource,
                        onClick = {
                            isPlaying.value = !isPlaying.value
                        },
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .indication(interactionSource, null),
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = main_coral_darken,
                            containerColor = Color.White,
                            disabledContentColor = main_coral_darken,
                            disabledContainerColor = Color.White
                        )
                    ) {
                        if (isPlaying.value) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "stopButton",
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "playButton",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    LinearProgressIndicator(
                        progress = { currentProgress.value },
                        modifier = Modifier.align(Alignment.CenterVertically),
                        color = main_coral_darken,
                        trackColor = main_coral_bright
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                ButtonComponent(
                    buttonText = "저장하기",
                    onClick = {
                        viewModel.onTriggerEvent(
                            MainViewEvent.OnClickSaveVodleButton(
                                viewState.recordingFile,
                                writer.value,
                                recordType,
                                viewState.convertedAudio.convertedAudioUrl,
                                viewState.location
                            )
                        )
                    },
                    buttonTextStyle = vodleTypoGraphy.titleMedium
                )
            }
        }

        if (playerLoad.value || viewState.isLoading) {
            LoadingScreen()
        }
    }
}

@Composable
private fun ProgressBarAnimation(
    isPlaying: MutableState<Boolean>,
    audioDuration: Int,
    progress: Animatable<Float, AnimationVector1D>
) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(audioDuration) {
        if (audioDuration > 0) {
            coroutineScope.launch {
                progress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = audioDuration,
                        easing = LinearEasing
                    )
                )
            }
        } else {
            isPlaying.value = false
            progress.snapTo(0f)
        }
    }
}
