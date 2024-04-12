package com.tes.presentation.main.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.tes.presentation.theme.main_coral_darken

@Composable
fun BottomNavButtonComponent(
    imageVector: ImageVector,
    flag: Boolean = true,
    onClick: () -> Unit,
    info: String?,
    modifier: Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) {
        IconButton(
            interactionSource = interactionSource,
            onClick = {
                onClick()
            },
            enabled = flag,
            modifier = modifier,
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = main_coral_darken,
                containerColor = Color.White,
                disabledContentColor = main_coral_darken,
                disabledContainerColor = Color.White
            )
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = info,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private object NoRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor() = Color.Unspecified

    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleAlpha(0.0f, 0.0f, 0.0f, 0.0f)
}
