package com.square.android.ui.fragment.explore.filters

abstract class BaseFilter{
    abstract fun isDefault(): Boolean
    abstract fun clear()
    abstract fun updateValues(filter: BaseFilter)
    abstract fun isEqualTo(filter: BaseFilter): Boolean
}