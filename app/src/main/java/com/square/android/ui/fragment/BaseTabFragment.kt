package com.square.android.ui.fragment

import com.square.android.ui.activity.BaseTabActivity

abstract class BaseTabFragment: BaseFragment(){

    open fun tabBtnClicked(){ }

    fun setTabBtnEnabled(enabled: Boolean, setEditing: Boolean = false){
        (activity as BaseTabActivity).enableBtn(enabled, setEditing)
    }

    fun setIsEditing(isEditing: Boolean) = (activity as BaseTabActivity).setIsEditing(isEditing)

}