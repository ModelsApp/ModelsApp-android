package com.square.android.ui.fragment

import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.square.android.data.Repository
import com.square.android.extensions.onTextChanged
import com.square.android.presentation.view.BaseView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.base.tutorial.Tutorial
import com.square.android.ui.base.tutorial.TutorialLoadedEvent
import com.square.android.ui.base.tutorial.TutorialService
import com.square.android.utils.ValidationCallback
import org.greenrobot.eventbus.EventBus
import org.koin.android.ext.android.inject
import java.util.*

abstract class BaseFragment : MvpFragment(), BaseView {
    private var snackBar: Snackbar? = null

    var isVisibleToUser : Boolean = false

    open val PERMISSION_REQUEST_CODE : Int? = null

    // TODO:F check all fields(et, spinners etc.) in fragment extending this and: show errors on empty/wrong ones and return FALSE OR return TRUE
    open fun validate(): Boolean { return false }

    private var waitAttempts: Int = 0

    protected open val tutorial: Tutorial? = null

    private val eventBus: EventBus by inject()
    val repository: Repository by inject()

    private fun checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays((activity as BaseActivity))) {

                PERMISSION_REQUEST_CODE?.let { (activity as BaseActivity).startPermissionForResult(it)}
            } else {
                startTutorialService()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays((activity as BaseActivity))) {

                    startTutorialService()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun startTutorialService(){
        val intent = Intent(Intent(activity, TutorialService::class.java))
        intent.putExtra(TutorialService.TUTORIAL_APP_EXTRA_KEY, tutorial?.tutorialKey?.name)
        activity?.startService(intent)

        waitAttempts = 0
        waitForSubscriber()
    }

    private fun waitForSubscriber(){
        if (eventBus.hasSubscriberForEvent(TutorialLoadedEvent::class.java)){
            eventBus.post(TutorialLoadedEvent(tutorial))
        } else{
            if(waitAttempts < 10){
                waitAttempts++
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        waitForSubscriber()
                    }
                }, 50)
            }
        }
    }

     fun visibleNow(){
         initService()
     }

    private fun initService(){
        try{
            var allowTutorial = true

            if( (activity as BaseActivity).tutorial != null){
                if(!(activity as BaseActivity).tutorial!!.isTutorialFinished){
                    allowTutorial = false
                }
            }

            if(allowTutorial){
                if(tutorial?.tutorialKey != null){
                    if(!repository.getTutorialDontShowAgain(tutorial?.tutorialKey!!)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            checkDrawOverlayPermission()
                        } else {
                            startTutorialService()
                        }
                    }
                }
            }
        } catch (exception: Exception){ }
    }

    override fun showMessage(message: String) {
        view?.let {
            snackBar = Snackbar.make(it, message, Snackbar.LENGTH_LONG)

            snackBar?.show()
        }
    }

    @JvmName("addTextViewValidation")
    protected fun addTextValidation(views: List<TextView>, callback: ValidationCallback<CharSequence>) {
        views.forEach {
            it.onTextChanged {
                updateValidity(views, callback)
            }
        }

        updateValidity(views, callback)
    }

    private fun updateValidity(views: List<TextView>, callback: ValidationCallback<CharSequence>) {
        val isValid = views.all { callback.isValid(it.text) }

        callback.validityChanged(isValid)
    }

    override fun showMessage(messageRes: Int) {
        showMessage(getString(messageRes))
    }

    override fun onPause() {
        super.onPause()

        snackBar?.dismiss()
    }
}