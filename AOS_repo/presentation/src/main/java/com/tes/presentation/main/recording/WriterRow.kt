package com.tes.presentation.main.recording

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.tes.presentation.theme.Padding
import com.tes.presentation.theme.vodleTypoGraphy
import main.components.VodleCustomTextField

@Composable
internal fun WriterRow(writer: MutableState<String>) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            modifier = Modifier.padding(start = Padding.dialogPadding),
            text = "작성자",
            style = vodleTypoGraphy.bodyMedium.merge(
                TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp
                )
            )
        )
        VodleCustomTextField(
            textState = writer,
            modifier = Modifier
                .weight(1f)
                .wrapContentHeight(),
            true,
            10
        )
    }
}
