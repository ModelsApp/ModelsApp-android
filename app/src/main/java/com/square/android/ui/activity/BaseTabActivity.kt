package com.square.android.ui.activity

import android.os.Bundle
import android.util.SparseArray
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.square.android.R
import com.square.android.androidx.navigator.AppNavigator
import com.square.android.extensions.hideKeyboard
import com.square.android.ui.dialogs.DiscardChangesDialog
import com.square.android.ui.fragment.BaseTabFragment
import com.square.android.utils.ActivityUtils
import kotlinx.android.synthetic.main.base_tab_ac.*
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Forward
import java.lang.Exception

class TabData(var title: String?, var btnType: BaseTabActivity.BTN_TYPE = BaseTabActivity.BTN_TYPE.SAVE, var btnVisible: Boolean = false, var btnEnabled: Boolean = false, var setEditing: Boolean = false)

class TabFragmentData(var data: Any?, var tabData: TabData)

class TabFragmentAndData(var fragment: BaseTabFragment, var data: TabData, var isEditing: Boolean)

abstract class BaseTabActivity: BaseActivity(){

    var currentFragmentIndex: Int = -1

    val tabsList: SparseArray<TabFragmentAndData> = SparseArray()

    private var discardDialog: DiscardChangesDialog? = null

    override fun provideNavigator(): Navigator = provideTabNavigator()

    protected abstract fun provideTabNavigator(): BaseTabNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUtils.setTransparentStatusAndDrawBehind(this)

        setContentView(R.layout.base_tab_ac)

        discardDialog = DiscardChangesDialog(this)

        tabAcContainer.setAc(this)

        arrowBack.setOnClickListener { onBackPressed() }

        rightButton.setOnClickListener { getCurrentFragmentAndData().fragment.tabBtnClicked() }
    }
    enum class BTN_TYPE {
        NEXT,
        SAVE
    }

    fun enableBtn(enabled: Boolean, setEditing: Boolean = false){
        if(setEditing){
          setIsEditing(true)
        }

        rightButton.isEnabled = enabled
        getCurrentFragmentAndData().data.btnEnabled = enabled
    }

    fun setIsEditing(editing: Boolean){
        setEditing(editing)
    }

    private fun isEditing() = getCurrentFragmentAndData().isEditing

    private fun setEditing(editing: Boolean){
        getCurrentFragmentAndData().isEditing = editing
    }

    open fun initFragment(tabData: TabData?){
        tabData?.let {
            rightButton.visibility = if(it.btnVisible) View.VISIBLE else View.GONE
            titleTv.text = it.title
            rightButton.isEnabled = it.btnEnabled

            rightButton.text = when(it.btnType){
                BTN_TYPE.NEXT -> getString(R.string.next)
                BTN_TYPE.SAVE -> getString(R.string.save)
            }
        }
    }

    private fun getCurrentFragmentAndData(): TabFragmentAndData{
        return tabsList[currentFragmentIndex]
    }

    override fun getCurrentFocus(): View? {
        return if (window != null) window.currentFocus else null
    }

    fun isLastFragment(): Boolean = currentFragmentIndex == 0

    override fun onBackPressed() {
        val focusedView = currentFocus

        if(currentFocus != null){

            if(currentFocus is EditText){
                hideKeyboard()
            }

            focusedView!!.clearFocus()

        } else{
            if(isEditing()){
                discardDialog?.show()
            } else{
                super.onBackPressed()
                onBack()
            }
        }
    }

    private fun onBack(){
        tabsList.removeAt(currentFragmentIndex)
        currentFragmentIndex--

        initFragment(tabsList[currentFragmentIndex].data)
    }

    abstract class BaseTabNavigator(var tabActivity: BaseTabActivity, private var skipFirstTransaction: Boolean = false) : AppNavigator(tabActivity, R.id.tabsContainer) {

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

            tabActivity.tabsList.put(tabActivity.currentFragmentIndex, TabFragmentAndData(fragment, tabData, tabData.setEditing))
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
    }
}