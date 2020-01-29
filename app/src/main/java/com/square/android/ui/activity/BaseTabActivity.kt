package com.square.android.ui.activity

import android.content.Context
import android.content.Intent
import android.util.SparseArray
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.square.android.R
import com.square.android.androidx.navigator.AppNavigator
import com.square.android.ui.fragment.BaseTabFragment
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Forward
import java.lang.Exception

class TabData(var title: String?, var btnType: BaseTabActivity.BTN_TYPE = BaseTabActivity.BTN_TYPE.SAVE, var btnVisible: Boolean = false, var btnEnabled: Boolean = true)

class TabFragmentData(var data: Any?, var tabData: TabData)

class TabFragmentAndData(var fragment: BaseTabFragment, var data: TabData)

abstract class BaseTabActivity: BaseActivity(){
    var titleTv: TextView? = null
    var navButton: TextView? = null
    var backArrow: ImageView? = null

    var currentFragmentIndex: Int = -1

    var backFromActivity: Boolean = false

    val tabsList: SparseArray<TabFragmentAndData> = SparseArray()

    override fun provideNavigator(): Navigator = provideTabNavigator()

    protected abstract fun provideTabNavigator(): BaseTabNavigator

    open fun provideNavViews(titleTv: TextView?, navButton: TextView?, backArrow: ImageView?){
        this.titleTv = titleTv
        this.navButton = navButton
        this.backArrow = backArrow
    }

    enum class BTN_TYPE {
        NEXT,
        SAVE
    }

    fun enableBtn(enabled: Boolean){
        navButton?.isEnabled = enabled
        getCurrentFragmentAndData().data.btnEnabled = enabled
    }

    open fun initFragment(tabData: TabData){
        try {
            navButton?.visibility = if(tabData.btnVisible) View.VISIBLE else View.GONE
            titleTv?.text = tabData.title
            navButton?.isEnabled = tabData.btnEnabled

            navButton?.text = when(tabData.btnType){
                BTN_TYPE.NEXT -> getString(R.string.next)
                BTN_TYPE.SAVE -> getString(R.string.save)
            }

            navButton?.setOnClickListener { getCurrentFragmentAndData().fragment.tabBtnClicked() }

            backArrow?.setOnClickListener { onBackPressed() }
        } catch (e: Exception){ }
    }

    private fun getCurrentFragmentAndData(): TabFragmentAndData{
        return tabsList[currentFragmentIndex]
    }

    abstract class BaseTabNavigator(var tabActivity: BaseTabActivity, containerId: Int, private var skipFirstTransaction: Boolean = false) : AppNavigator(tabActivity, containerId) {

        override fun createActivityIntent(context: Context?, screenKey: String?, data: Any?): Intent? {
            //TODO CHECK may not be needed(may be wrong) - back may only be triggered inside this navigator? not in new ac with own navigator?
            tabActivity.backFromActivity = true

            return createTabActivityIntent(context, screenKey, data)
        }

        abstract fun createTabActivityIntent(context: Context?, screenKey: String?, data: Any?): Intent?

        override fun createFragment(screenKey: String?, data: Any?): Fragment {
            try {
                val allData: TabFragmentData = data as TabFragmentData
                return initAndReturn(screenKey, allData.data, allData.tabData)
            } catch (e: Exception){
                throw IllegalArgumentException("BaseTabNavigator's fragment data should be type of TabFragmentData: $screenKey")
            }
        }

        private fun initAndReturn(screenKey: String?, data: Any?, tabData: TabData): BaseTabFragment {
            val fragment: BaseTabFragment = createTabFragment(screenKey, data)
            tabActivity.currentFragmentIndex ++

            tabActivity.tabsList.put(tabActivity.currentFragmentIndex, TabFragmentAndData(fragment, tabData))
            tabActivity.initFragment(tabData)

            return fragment
        }

        abstract fun createTabFragment(screenKey: String?, data: Any?): BaseTabFragment

        override fun setupFragmentTransactionAnimation(command: Command,
                                                       currentFragment: Fragment?,
                                                       nextFragment: Fragment,
                                                       fragmentTransaction: FragmentTransaction) {
            if(skipFirstTransaction){
                skipFirstTransaction = false
            } else {
                if(command is Forward){
                    fragmentTransaction.setCustomAnimations(
                            R.anim.enter_from_right,
                            R.anim.exit_to_left,
                            R.anim.enter_from_left,
                            R.anim.exit_to_right)
                } else{
                    fragmentTransaction.setCustomAnimations(R.anim.fade_in,
                            R.anim.fade_out,
                            R.anim.fade_in,
                            R.anim.fade_out)
                }
            }

        }

        //TODO BACK IS NOT TRIGGERED
        //TODO CHECK may not be triggered? check
        override fun back() {
            super.back()

            //TODO CHECK may not be needed(may be wrong) - back may only be triggered inside this navigator? not in new ac with own navigator?
            if (!tabActivity.backFromActivity) {
                tabActivity.tabsList.removeAt(tabActivity.currentFragmentIndex)
                tabActivity.currentFragmentIndex--

                tabActivity.initFragment(tabActivity.tabsList[tabActivity.currentFragmentIndex].data)
            } else {
                tabActivity.backFromActivity = false
            }
        }

    }
}