package main.components

import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import com.tes.presentation.R
import com.tes.presentation.main.MainViewEvent
import com.tes.presentation.main.MainViewModel
import com.tes.presentation.main.fetchLocationAndHandle
import com.tes.presentation.model.Vodle
import com.tes.presentation.model.lat
import com.tes.presentation.model.lng
import com.tes.presentation.theme.main_coral_bright
import com.tes.presentation.theme.main_coral_darken
import com.tes.presentation.theme.vodleTypoGraphy
import com.tes.presentation.utils.distanceCalculator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
@Composable
fun VodleListDialogComponent(
    viewModel: MainViewModel,
    vodleList: List<Vodle>,
    context: Context,
    scope: CoroutineScope,
    player: ExoPlayer,
    dataSourceFactory: DataSource.Factory
) {
    var index by remember { mutableStateOf(0) }
    val currentProgress = remember { Animatable(0f) }
    val isPlaying = remember { mutableStateOf(false) }
    val audioDuration = remember { mutableIntStateOf(0) }
    val playerLoad = remember { mutableStateOf(false) }
    val bufferingTime = remember { mutableIntStateOf(0) }
    var bufferingJob: Job? = null

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

            if (playbackState == Player.STATE_IDLE) {
                audioDuration.intValue = 0
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

    LaunchedEffect(isPlaying.value) {
        if (isPlaying.value) {
            val hlsMediaSource =
                HlsMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(
                        MediaItem.fromUri(vodleList.get(index).streamingURL)
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

    ProgressBarAnimation(isPlaying, audioDuration.intValue, currentProgress)
    Box(
        modifier = Modifier.then(
            Modifier
                .border(
                    BorderStroke(1.dp, main_coral_bright),
                    RoundedCornerShape(10.dp)
                )
                .clip(shape = RoundedCornerShape(10.dp))
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
                .background(color = Color.White)

        )
    ) {
        if (index != 0) {
            IconButton(
                onClick = {
                    index--
                    player.stop()
                },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = main_coral_darken,
                    containerColor = Color.White,
                    disabledContentColor = main_coral_darken,
                    disabledContainerColor = Color.White
                ),
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "backButton",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        if (index != vodleList.size - 1) {
            IconButton(
                onClick = {
                    index++
                    player.stop()
                },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = main_coral_darken,
                    containerColor = Color.White,
                    disabledContentColor = main_coral_darken,
                    disabledContainerColor = Color.White
                ),
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "forwardButton",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Column(
            modifier = Modifier
                .wrapContentHeight()
                .align(Alignment.Center)
                .padding(start = 20.dp, top = 10.dp, end = 10.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(start = 20.dp, top = 10.dp, end = 20.dp)
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
            ) {
                Text(
                    text = vodleList[index].writer,
                    textAlign = TextAlign.Start,
                    style = vodleTypoGraphy.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    text = vodleList[index].address,
                    textAlign = TextAlign.Start,
                    style = vodleTypoGraphy.bodySmall,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            Row(
                modifier = Modifier
                    .padding(start = 20.dp, top = 10.dp, end = 20.dp)
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
            ) {
                Text(
                    text = vodleList[index].category,
                    textAlign = TextAlign.Start,
                    style = vodleTypoGraphy.titleMedium,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    text = vodleList[index].date.substring(0, 13),
                    textAlign = TextAlign.Start,
                    style = vodleTypoGraphy.bodySmall,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            Row(
                modifier = Modifier
                    .padding(top = 10.dp, end = 20.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                IconButton(
                    onClick = {
                        fetchLocationAndHandle(
                            scope,
                            context,
                            onSuccess = { location ->
                                if (distanceCalculator(
                                        location.lat,
                                        location.lng,
                                        vodleList[index].location.lat,
                                        vodleList[index].location.lng
                                    )
                                ) {
                                    isPlaying.value = !isPlaying.value
                                } else {
                                    viewModel.onTriggerEvent(
                                        MainViewEvent.ShowToast(
                                            context.getString(R.string.distance_limit_message)
                                        )
                                    )
                                }
                            },
                            onFailure = {
                                viewModel.onTriggerEvent(
                                    MainViewEvent.ShowToast(
                                        context.getString(R.string.location_fetch_failure)
                                    )
                                )
                            }
                        )
                    },
                    modifier = Modifier.align(Alignment.CenterVertically),
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
