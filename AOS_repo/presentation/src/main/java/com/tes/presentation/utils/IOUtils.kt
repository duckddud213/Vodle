package com.tes.presentation.utils

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal fun createM4aFile(context: Context): File {
    val timeStamp: String =
        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val audioFileName = "AUDIO_$timeStamp"
    val cacheDir: File = context.cacheDir
    return File.createTempFile(audioFileName, ".m4a", cacheDir)
}
