package main.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.MyLocation
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tes.presentation.theme.main_red

@Composable
fun FabComponent(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    flag: Boolean = true,
    onClick: () -> Unit,
    info: String? = null
) {
    val interactionSource = remember { MutableInteractionSource() }

    CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) {
        IconButton(
            interactionSource = interactionSource,
            onClick = {
                onClick()
            },
            enabled = flag,
            modifier = modifier.then(
                Modifier
                    .clip(CircleShape)
                    .size(40.dp)
            ),
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = main_red,
                containerColor = Color.White,
                disabledContentColor = main_red,
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

@Preview
@Composable
fun PreviewFabComponent() {
    Row {
        FabComponent(
            imageVector = Icons.Outlined.MyLocation,
            onClick = {},
            info = "내 위치로 이동"
        )
        Spacer(modifier = Modifier.width(20.dp))
        FabComponent(
            imageVector = Icons.Outlined.CalendarMonth,
            onClick = {},
            info = "날짜 이동"
        )
    }
}
