package main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun BasicDialog(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .clip(shape = RoundedCornerShape(32.dp))
                .aspectRatio(1f)
                .background(Color.White)
        )
    ) {
        content()
    }
}
