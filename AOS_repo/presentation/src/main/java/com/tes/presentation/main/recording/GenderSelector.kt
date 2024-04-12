package com.tes.presentation.main.recording

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tes.domain.model.Gender
import com.tes.presentation.theme.lightGrey
import com.tes.presentation.theme.main_coral_darken
import com.tes.presentation.theme.vodleTypoGraphy

@Composable
internal fun GenderSelector(selectedGender: MutableState<Gender>) {
    SelectionContainer {
        Row {
            GenderText("남성", selectedGender.value == Gender.Male) {
                selectedGender.value = Gender.Male
            }
            GenderText("여성", selectedGender.value == Gender.Female) {
                selectedGender.value = Gender.Female
            }
        }
    }
}

@Composable
fun GenderText(text: String, isSelected: Boolean, onClickText: () -> Unit) {
    val color = if (isSelected) main_coral_darken else lightGrey
    val textStyle = if (isSelected) vodleTypoGraphy.bodyMedium else vodleTypoGraphy.titleMedium
    val interactionSource = remember { MutableInteractionSource() }
    Text(
        text = text,
        color = color,
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClickText()
            }
            .padding(horizontal = 8.dp),
        style = textStyle.merge(TextStyle(fontSize = 24.sp))
    )
}
