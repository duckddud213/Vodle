package com.tes.presentation.mypage

import com.tes.presentation.composebase.ViewEvent

sealed class MyPageViewEvent : ViewEvent {

    data object OnClickLogout : MyPageViewEvent()

    data object OnClickMyVodleLog : MyPageViewEvent()

    data object OnClickPrivacyPolicy : MyPageViewEvent()

    data object OnClickSignOut : MyPageViewEvent()

    data class OnClickSignOutConfirm(val naverAccessToken: String) : MyPageViewEvent()

    data object OnCancelSignOut : MyPageViewEvent()

    data class ShowToast(val message: String) : MyPageViewEvent()

    data object OnClickBackButtonFromVodleLogView : MyPageViewEvent()

    data object OnFinishToast : MyPageViewEvent()
}
