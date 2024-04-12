package com.tes.presentation.main.components

import android.view.ViewGroup
import android.widget.ImageView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.tes.presentation.R
import com.tes.presentation.theme.main_coral

@Composable
fun LoadingScreen() {
    val context = LocalContext.current
    val glide = remember { Glide.with(context) }

    Dialog(onDismissRequest = {}) {
        Box(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {
            Box {
                AndroidView(
                    factory = { ctx ->
                        ImageView(ctx).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        }
                    },
                    update = { imageView ->
                        glide
                            .asGif()
                            .load(R.raw.logo_animation)
                            .apply(RequestOptions().fitCenter())
                            .into(imageView)
                            .clearOnDetach()
                    }
                )
                LinearProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 240.dp),
                    color = main_coral
                )
            }
        }
    }
}
