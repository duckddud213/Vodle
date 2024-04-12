package com.tes.presentation.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.tes.presentation.R
import com.tes.presentation.model.VodleForLog
import com.tes.presentation.theme.main_coral
import com.tes.presentation.theme.vodleTypoGraphy

@Composable
fun VodleLog(vodleForLog: VodleForLog) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(id = R.drawable.back_spray),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        CustomStyledText(modifier = Modifier.align(Alignment.Center), address = vodleForLog.address)
        Text(
            modifier = Modifier.align(Alignment.BottomEnd),
            text = vodleForLog.date,
            style = vodleTypoGraphy.titleMedium.merge(
                TextStyle(fontSize = 16.sp)
            )
        )
    }
}

@Composable
fun CustomStyledText(modifier: Modifier = Modifier, address: String) {
    val text = buildAnnotatedString {
        withStyle(style = vodleTypoGraphy.titleLarge.toSpanStyle()) {
            append(address + "에 남긴 ")
        }

        withStyle(style = vodleTypoGraphy.titleLarge.merge(color = main_coral).toSpanStyle()) {
            append("보들")
        }
    }

    Text(modifier = modifier, text = text)
}
