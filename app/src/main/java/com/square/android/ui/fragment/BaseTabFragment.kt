package com.square.android.ui.fragment

import com.square.android.ui.activity.BaseTabActivity

abstract class BaseTabFragment: BaseFragment(){

    open fun tabBtnClicked(){ }

    fun setTabBtnEnabled(enabled: Boolean, setEditing: Boolean = false){
        (activity as BaseTabActivity).enableBtn(enabled, setEditing)
    }

    fun isValid(item: CharSequence) = item.toString().trim().isNotEmpty()

}