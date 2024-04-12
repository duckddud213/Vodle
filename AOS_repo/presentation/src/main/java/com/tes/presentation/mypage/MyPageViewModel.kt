package com.tes.presentation.mypage

import androidx.lifecycle.viewModelScope
import com.tes.domain.usecase.user.FetchMyVodles
import com.tes.domain.usecase.user.LogoutUseCase
import com.tes.domain.usecase.user.SignOutNaverUseCase
import com.tes.presentation.BuildConfig
import com.tes.presentation.composebase.BaseViewModel
import com.tes.presentation.model.VodleForLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val singOutUseCase: SignOutNaverUseCase,
    private val fetchMyVodles: FetchMyVodles
) : BaseViewModel<MyPageViewState, MyPageViewEvent>() {
    override fun createInitialState(): MyPageViewState = MyPageViewState.Default()

    override fun onTriggerEvent(event: MyPageViewEvent) {
        when (event) {
            MyPageViewEvent.OnClickLogout -> logout()
            MyPageViewEvent.OnClickPrivacyPolicy -> {}
            is MyPageViewEvent.OnClickSignOut -> setState { tryingSignOut() }
            MyPageViewEvent.OnClickMyVodleLog -> fetchMyVodleLogs()
            is MyPageViewEvent.ShowToast -> setState { showToast(event.message) }
            MyPageViewEvent.OnCancelSignOut -> setState { cancelSignOut() }
            is MyPageViewEvent.OnClickSignOutConfirm -> signOut(event.naverAccessToken)
            MyPageViewEvent.OnClickBackButtonFromVodleLogView -> setState { backToMyPageView() }
            MyPageViewEvent.OnFinishToast -> setState { onFinishToast() }
        }
    }

    private fun MyPageViewState.onFinishToast(): MyPageViewState =
        when (this) {
            is MyPageViewState.Default -> this.copy(toastMessage = "")
            is MyPageViewState.VodleLog -> this.copy(toastMessage = "")
        }

    private fun MyPageViewState.showToast(message: String): MyPageViewState =
        when (this) {
            is MyPageViewState.Default -> this.copy(toastMessage = message)
            is MyPageViewState.VodleLog -> this.copy(toastMessage = message)
        }

    private fun MyPageViewState.backToMyPageView(): MyPageViewState =
        when (this) {
            is MyPageViewState.Default -> this
            is MyPageViewState.VodleLog -> MyPageViewState.Default()
        }

    private fun MyPageViewState.onSuccessFetchVodleLog(
        vodleLogList: List<VodleForLog>
    ): MyPageViewState =
        when (this) {
            is MyPageViewState.Default -> MyPageViewState.VodleLog(vodleLogList)
            is MyPageViewState.VodleLog -> this
        }

    private fun fetchMyVodleLogs() {
        viewModelScope.launch {
            fetchMyVodles().fold(
                onSuccess = { vodleList ->
                    val vodleLogList = vodleList.map {
                        VodleForLog(it.id, it.date, it.address)
                    }
                    setState { onSuccessFetchVodleLog(vodleLogList) }
                },
                onFailure = {
                    setState { showToast("보들 기록을 가져오는 데 실패했습니다.") }
                }
            )
        }
    }

    private fun MyPageViewState.cancelSignOut(): MyPageViewState =
        when (this) {
            is MyPageViewState.Default -> copy(isTryingSignOut = false)
            is MyPageViewState.VodleLog -> this
        }

    private fun logout() {
        viewModelScope.launch {
            logoutUseCase().fold(
                onSuccess = {
                    setState { onSuccessLogout() }
                },
                onFailure = {
                    setState { showToast("로그아웃에 실패했습니다.") }
                }
            )
        }
    }

    private fun MyPageViewState.tryingSignOut(): MyPageViewState =
        when (this) {
            is MyPageViewState.Default -> this.copy(isTryingSignOut = true)
            is MyPageViewState.VodleLog -> this
        }

    private fun signOut(accessToken: String) {
        viewModelScope.launch {
            singOutUseCase(
                BuildConfig.NAVER_LOGIN_CLIENT_ID,
                BuildConfig.NAVER_LOGIN_CLIENT_SECRET,
                accessToken
            ).fold(
                onSuccess = {
                    logout()
                },
                onFailure = {
                    setState { showToast("회원탈퇴에 실패했습니다.") }
                }
            )
        }
    }

    private fun MyPageViewState.onSuccessLogout(): MyPageViewState =
        when (this) {
            is MyPageViewState.Default -> this.copy(isLogin = false)
            is MyPageViewState.VodleLog -> this
        }
}
