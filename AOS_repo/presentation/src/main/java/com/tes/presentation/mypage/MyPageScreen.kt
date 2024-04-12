package com.tes.presentation.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.navercorp.nid.NaverIdLoginSDK
import com.tes.presentation.R
import com.tes.presentation.main.components.VodleSnackBarHost
import com.tes.presentation.theme.vodleTypoGraphy

@Composable
fun MyPageScreen(
    viewModel: MyPageViewModel = hiltViewModel(),
    onClickBackButton: () -> Unit,
    onSuccessLogout: () -> Unit
) {
    val viewState = viewModel.uiState.collectAsState().value
    LaunchedEffect(viewState.isLogin) {
        if (!viewState.isLogin) onSuccessLogout()
    }

    val snackBarHostState = remember { SnackbarHostState() }

    ObserveToastMessage(viewState = viewState, snackBarHostState, viewModel = viewModel)

    if (viewState.isTryingSignOut) {
        SignOutDialog(
            {
                viewModel.onTriggerEvent(
                    MyPageViewEvent.OnClickSignOutConfirm(fetchNaverAccessToken())
                )
            },
            { viewModel.onTriggerEvent(MyPageViewEvent.OnCancelSignOut) }
        )
    }

    Box {
        when (viewState) {
            is MyPageViewState.Default -> MyPageView(onClickBackButton, viewModel)
            is MyPageViewState.VodleLog -> VodleLogView(vodleLogList = viewState.vodleLogList) {
                viewModel.onTriggerEvent(MyPageViewEvent.OnClickBackButtonFromVodleLogView)
            }
        }

        VodleSnackBarHost(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
            snackBarHostState = snackBarHostState
        )
    }
}

private fun fetchNaverAccessToken(): String =
    NaverIdLoginSDK.getAccessToken() ?: ""

@Composable
internal fun TopBar(onClickBackButton: () -> Unit, title: String) {
    val interactionSource = remember { MutableInteractionSource() }

    Box {
        Image(
            alignment = Alignment.CenterStart,
            modifier = Modifier.clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClickBackButton() },
            painter = painterResource(id = R.drawable.left_arrow),
            contentDescription = null
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = vodleTypoGraphy.bodyLarge
            )
        }
    }
}

@Composable
private fun ObserveToastMessage(
    viewState: MyPageViewState,
    snackBarHostState: SnackbarHostState,
    viewModel: MyPageViewModel
) {
    LaunchedEffect(key1 = viewState.toastMessage) {
        if (viewState.toastMessage.isNotEmpty()) {
            snackBarHostState.currentSnackbarData?.dismiss()
            snackBarHostState.showSnackbar(
                viewState.toastMessage,
                actionLabel = "확인",
                duration = SnackbarDuration.Short
            )
            viewModel.onTriggerEvent(MyPageViewEvent.OnFinishToast)
        }
    }
}
