package com.square.android.ui.fragment.places.filters

abstract class BaseFilter{
    abstract fun isDefault(): Boolean
    abstract fun clear()
    abstract fun updateValues(filter: BaseFilter)
}