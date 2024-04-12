package com.tes.presentation.main.recording

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tes.presentation.R
import com.tes.presentation.model.VoiceType
import com.tes.presentation.theme.vodleTypoGraphy

@Composable
fun VoiceSelector(selectedVoice: MutableIntState, voiceTypeList: List<VoiceType>) {
    val interactionSource = remember { MutableInteractionSource() }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Line()

        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                modifier = Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    if (selectedVoice.intValue > 0) selectedVoice.intValue -= 1
                },
                painter = painterResource(id = R.drawable.left_arrow),
                contentDescription = null
            )

            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp),
                text = voiceTypeList[selectedVoice.intValue].korean,
                style = vodleTypoGraphy.titleMedium,
                textAlign = TextAlign.Center
            )

            Image(
                modifier = Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    if (selectedVoice.intValue < voiceTypeList.lastIndex) {
                        selectedVoice.intValue += 1
                    }
                },
                painter = painterResource(id = R.drawable.right_arrow),
                contentDescription = null
            )
        }

        Line()
    }
}
