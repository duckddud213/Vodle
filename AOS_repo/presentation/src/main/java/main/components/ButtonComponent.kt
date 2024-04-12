package main.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tes.presentation.theme.Padding
import com.tes.presentation.theme.main_coral_darken
import com.tes.presentation.theme.vodleTypoGraphy

@Composable
fun ButtonComponent(
    modifier: Modifier = Modifier,
    buttonText: String,
    onClick: () -> Unit,
    buttonTextStyle: TextStyle
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier.then(
            Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .clip(shape = RoundedCornerShape(32.dp))
                .background(color = Color.White)
                .border(
                    BorderStroke(1.dp, color = main_coral_darken),
                    shape = RoundedCornerShape(32.dp)
                )
                .clickable(interactionSource = interactionSource, indication = null) { onClick() }
        )
    ) {
        Text(
            modifier = Modifier.padding(
                horizontal = Padding.textButtonHorizontalPadding,
                vertical = Padding.textButtonVerticalPadding
            ),
            text = buttonText,
            textAlign = TextAlign.Center,
            style = buttonTextStyle
        )
    }
}

@Preview
@Composable
fun PreviewButtonComponent() {
    Column() {
        ButtonComponent(
            buttonText = "현재 위치에서 검색하기",
            onClick = {},
            buttonTextStyle = vodleTypoGraphy.titleLarge
        )
        Spacer(modifier = Modifier.height(20.dp))
        ButtonComponent(
            buttonText = "보들 만들기",
            onClick = {},
            buttonTextStyle = vodleTypoGraphy.bodyMedium
        )
        Spacer(modifier = Modifier.height(20.dp))
        ButtonComponent(
            buttonText = "저장하기",
            onClick = {},
            buttonTextStyle = vodleTypoGraphy.titleMedium
        )
    }
}
