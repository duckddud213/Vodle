package com.tes.presentation.login

import com.tes.presentation.composebase.ViewState
import com.tes.presentation.navigation.Route

sealed class LoginViewState : ViewState {

    abstract val nextRoute: Route
    abstract val isTryingLogin: Boolean
    abstract val toastMessage: String
    abstract val lastBackPressedTime: Long
    abstract val shouldExit: Boolean
    abstract val isTryingAutoLogin: Boolean

    data class Splash(
        override val nextRoute: Route = Route.LOGIN,
        val isLoading: Boolean = false,
        override val isTryingLogin: Boolean = false,
        override val toastMessage: String = "",
        override val lastBackPressedTime: Long = 0,
        override val shouldExit: Boolean = false,
        override val isTryingAutoLogin: Boolean = true
    ) : LoginViewState()

    data class Default(
        override val nextRoute: Route = Route.LOGIN,
        val isLoading: Boolean = false,
        override val isTryingLogin: Boolean = false,
        override val toastMessage: String = "",
        override val lastBackPressedTime: Long = 0,
        override val shouldExit: Boolean = false,
        override val isTryingAutoLogin: Boolean = false
    ) : LoginViewState()

    data class Login(
        override val nextRoute: Route = Route.MAIN,
        override val isTryingLogin: Boolean = false,
        override val toastMessage: String = "",
        override val lastBackPressedTime: Long = 0,
        override val shouldExit: Boolean = false,
        override val isTryingAutoLogin: Boolean = false
    ) : LoginViewState()
}
