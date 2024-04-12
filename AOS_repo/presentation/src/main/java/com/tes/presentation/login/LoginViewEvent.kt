package com.tes.presentation.login

import com.tes.presentation.composebase.ViewEvent

sealed class LoginViewEvent : ViewEvent {
    data object OnClickNaverLoginButton : LoginViewEvent()

    data class OnClickBackButton(val backPressedTime: Long) : LoginViewEvent()

    data class AttemptToFetchNaverId(val accessToken: String) : LoginViewEvent()

    data object OnSuccessLogin : LoginViewEvent()

    data class ShowToast(val message: String) : LoginViewEvent()

    data object OnFinishToast : LoginViewEvent()

    data object OnAutoLogin : LoginViewEvent()

    data object CheckAccessToken : LoginViewEvent()
}
