package com.square.android.presentation.view.profile

import com.square.android.presentation.view.BaseView
import com.square.android.presentation.view.LoadingView

interface ActivePlanView: BaseView {
    fun handlePurchases(nullOrEmpty: Boolean)

    fun purchasesComplete()

    fun showDialog()
    fun hideDialog()
}