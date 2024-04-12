package com.tes.presentation.login

import androidx.lifecycle.viewModelScope
import com.tes.domain.usecase.user.AutoLoginUseCase
import com.tes.domain.usecase.user.CheckAccessTokenUseCase
import com.tes.domain.usecase.user.GetNaverIdUseCase
import com.tes.domain.usecase.user.SignInNaverUseCase
import com.tes.presentation.composebase.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInNaverUseCase: SignInNaverUseCase,
    private val getNaverIdUseCase: GetNaverIdUseCase,
    private val autoLoginUseCase: AutoLoginUseCase,
    private val checkAccessTokenUseCase: CheckAccessTokenUseCase
) : BaseViewModel<LoginViewState, LoginViewEvent>() {
    override fun createInitialState(): LoginViewState =
        LoginViewState.Splash()

    override fun onTriggerEvent(event: LoginViewEvent) {
        setEvent(event)
    }

    init {
        viewModelScope.launch {
            uiEvent.collect { event ->
                handleLoginViewEvent(event)
            }
        }
    }

    private suspend fun handleLoginViewEvent(event: LoginViewEvent) {
        when (event) {
            LoginViewEvent.OnClickNaverLoginButton -> setState { onAttemptNaverLogin() }
            is LoginViewEvent.AttemptToFetchNaverId -> fetchUserNaverId(event.accessToken)
            is LoginViewEvent.OnSuccessLogin -> setState { LoginViewState.Login() }
            is LoginViewEvent.ShowToast -> setState { showToast(event.message) }
            is LoginViewEvent.OnClickBackButton -> setState {
                onClickBackButton(
                    event.backPressedTime
                )
            }

            LoginViewEvent.OnFinishToast -> setState { onFinishToast() }
            is LoginViewEvent.OnAutoLogin -> autoLogin()
            is LoginViewEvent.CheckAccessToken -> checkAccessToken()
        }
    }

    private suspend fun fetchUserNaverId(accessToken: String) {
        getNaverIdUseCase(accessToken).fold(
            onSuccess = {
                tryToLogin(it)
            },
            onFailure = { setState { showToast(it.message ?: "네이버 접속 실패") } }
        )
    }

    private fun LoginViewState.onAttemptNaverLogin(): LoginViewState {
        return when (this) {
            is LoginViewState.Default -> copy(isTryingLogin = true)
            is LoginViewState.Login -> copy(isTryingLogin = true)
            is LoginViewState.Splash -> copy(isTryingLogin = true)
        }
    }

    private suspend fun tryToLogin(naverId: String) {
        signInNaverUseCase(naverId).fold(
            onSuccess = {
                setState { LoginViewState.Login() }
            },
            onFailure = {
                setState { showToast(it.message ?: "네트워크 에러") }
            }
        )
    }

    private fun LoginViewState.showToast(message: String): LoginViewState {
        return when (this) {
            is LoginViewState.Default -> copy(toastMessage = message, isTryingLogin = false)
            is LoginViewState.Login -> copy(toastMessage = message, isTryingLogin = false)
            is LoginViewState.Splash -> copy(toastMessage = message, isTryingLogin = false)
        }
    }

    private fun LoginViewState.onFinishToast(): LoginViewState {
        return when (this) {
            is LoginViewState.Default -> copy(toastMessage = "", isTryingLogin = false)
            is LoginViewState.Login -> copy(toastMessage = "", isTryingLogin = false)
            is LoginViewState.Splash -> copy(toastMessage = "", isTryingLogin = false)
        }
    }

    private fun LoginViewState.onClickBackButton(backPressedTime: Long): LoginViewState {
        if (checkBackPressedRightBefore(backPressedTime, currentState.lastBackPressedTime)) {
            return when (this) {
                is LoginViewState.Default -> copy(shouldExit = true)
                is LoginViewState.Login -> copy(shouldExit = true)
                is LoginViewState.Splash -> copy(shouldExit = true)
            }
        }

        return when (this) {
            is LoginViewState.Default -> copy(
                lastBackPressedTime = backPressedTime,
                toastMessage = "한번 더 누르면 종료됩니다."
            )

            is LoginViewState.Login -> copy(
                lastBackPressedTime = backPressedTime,
                toastMessage = "한번 더 누르면 종료됩니다."
            )

            is LoginViewState.Splash -> copy(
                lastBackPressedTime = backPressedTime,
                toastMessage = "한번 더 누르면 종료됩니다."
            )
        }
    }

    private fun checkBackPressedRightBefore(
        backPressedTime: Long,
        lastBackPressedTime: Long
    ): Boolean {
        return backPressedTime - lastBackPressedTime < 1000
    }

    private suspend fun autoLogin() {
        autoLoginUseCase().fold(
            onSuccess = { setState { LoginViewState.Login() } },
            onFailure = { setState { LoginViewState.Default() } }
        )
    }

    private suspend fun checkAccessToken() {
        checkAccessTokenUseCase().fold(
            onSuccess = {
                onTriggerEvent(LoginViewEvent.OnAutoLogin)
            },
            onFailure = {
                setState { LoginViewState.Default() }
            }
        )
    }
}
