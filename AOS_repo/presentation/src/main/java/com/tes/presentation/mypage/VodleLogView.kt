package com.tes.presentation.mypage

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tes.presentation.model.VodleForLog
import com.tes.presentation.theme.main_coral_bright

@Composable
internal fun VodleLogView(vodleLogList: List<VodleForLog>, onClickBackButton: () -> Unit) {
    val listState = rememberLazyListState()

    BackHandler {
        onClickBackButton()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(main_coral_bright)
            .padding(horizontal = 32.dp, vertical = 24.dp)
    ) {
        TopBar(onClickBackButton, "낙서 기록")

        LazyColumn(state = listState, contentPadding = PaddingValues(vertical = 12.dp)) {
            items(vodleLogList.size) { index ->
                VodleLog(vodleForLog = vodleLogList[index])
            }
        }
    }
}
