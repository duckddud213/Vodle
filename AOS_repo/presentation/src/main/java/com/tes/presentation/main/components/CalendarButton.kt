package com.tes.presentation.main.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import main.components.FabComponent

@Composable
internal fun CalendarButton(
    modifier: Modifier = Modifier
) {
    FabComponent(
        modifier = modifier.then(Modifier.padding(top = 12.dp, end = 12.dp)),
        imageVector = Icons.Outlined.CalendarMonth,
        onClick = { /*TODO*/ },
        info = ""
    )
}
