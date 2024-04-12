package main.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Draw
import androidx.compose.material.icons.outlined.MicNone
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tes.presentation.main.components.BottomNavButtonComponent
import com.tes.presentation.theme.main_coral_darken

@Composable
fun BottomNavBarComponent(
    modifier: Modifier,
    flag: Boolean = true,
    onClickWriteButton: () -> Unit,
    onClickHeadphoneButton: () -> Unit,
    onClickRecordingButton: () -> Unit,
    onClickUserButton: () -> Unit
) {
    Box(
        modifier = modifier.then(
            Modifier
                .border(
                    BorderStroke(1.dp, color = main_coral_darken),
                    shape = RoundedCornerShape(10)
                )
                .clip(shape = RoundedCornerShape(10.dp))
                .fillMaxWidth(0.88f)
                .wrapContentHeight()
                .background(color = Color.White)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            BottomNavButtonComponent(
                imageVector = Icons.Outlined.Draw,
                flag = flag,
                onClick = onClickWriteButton,
                info = "보들 그리기",
                modifier = Modifier
                    .weight(1f)
                    .height(70.dp)
            )
//            BottomNavButtonComponent(
//                imageVector = Icons.Outlined.Headphones,
//                flag = flag,
//                onClick = onClickHeadphoneButton,
//                info = "오버히어링 모드",
//                modifier = Modifier
//                    .weight(1f)
//                    .height(70.dp)
//            )
            BottomNavButtonComponent(
                imageVector = Icons.Outlined.MicNone,
                flag = flag,
                onClick = onClickRecordingButton,
                info = "녹음하기",
                modifier = Modifier
                    .weight(1f)
                    .height(70.dp)
            )
            BottomNavButtonComponent(
                imageVector = Icons.Outlined.Person,
                flag = flag,
                onClick = onClickUserButton,
                info = "마이페이지",
                modifier = Modifier
                    .weight(1f)
                    .height(70.dp)
            )
        }
    }
}

@Preview
@Composable
fun PreviewBottomNavBarComponent() {
    BottomNavBarComponent(
        modifier = Modifier,
        onClickWriteButton = {},
        onClickHeadphoneButton = {},
        onClickRecordingButton = {},
        onClickUserButton = {}
    )
}
