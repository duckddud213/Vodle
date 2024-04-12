package com.tes.presentation.mypage

import com.tes.presentation.composebase.ViewState
import com.tes.presentation.model.VodleForLog

sealed class MyPageViewState : ViewState {
    abstract val isLogin: Boolean
    abstract val isTryingSignOut: Boolean
    abstract val toastMessage: String

    data class Default(
        override val isLogin: Boolean = true,
        override val isTryingSignOut: Boolean = false,
        override val toastMessage: String = ""
    ) :
        MyPageViewState()

    data class VodleLog(
        val vodleLogList: List<VodleForLog>,
        override val isLogin: Boolean = true,
        override val isTryingSignOut: Boolean = false,
        override val toastMessage: String = ""
    ) :
        MyPageViewState()
}
