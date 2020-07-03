package com.square.android.presentation.view.signUp

import com.square.android.data.pojo.SignUpData
import com.square.android.presentation.view.BaseView

interface SignUpTwoView : BaseView {
    fun showData(signUpData: SignUpData)
}
