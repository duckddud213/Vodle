package com.tes.presentation.utils

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import com.tes.presentation.R
import com.tes.presentation.model.VoiceType

object MediaPlayer {
    private var mediaPlayer: MediaPlayer? = null
    private const val prefix = "sample_"
    private lateinit var sampleList: List<String>
    private val prefixPattern = Regex("^$prefix")

    fun playSample(context: Context, voiceType: VoiceType) {
        stopSample()
        mediaPlayer = MediaPlayer().apply {
            val fieldName = prefix + voiceType.name.toLowerCase(Locale.current)
            val resourceId = R.raw::class.java.getField(fieldName).getInt(null)
            setDataSource(
                context,
                Uri.parse("android.resource://${context.packageName}/$resourceId")
            )
            prepare() // 미디어 준비 (동기적으로 수행)
            isLooping = false
            start()
        }
    }

    fun stopSample() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
            mediaPlayer = null
        }
    }

    fun initSampleList(context: Context) {
        val raw = R.raw::class.java
        val fields = raw.fields
        sampleList = fields.mapNotNull { field ->
            try {
                val resourceId = field.getInt(null)
                val name = context.resources.getResourceEntryName(resourceId)
                if (name.startsWith(prefix)) {
                    prefixPattern.replace(name, "")
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}
