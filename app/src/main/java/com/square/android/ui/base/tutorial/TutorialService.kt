package com.square.android.ui.base.tutorial

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.*
import android.view.WindowManager
import com.square.android.data.Repository
import com.square.android.data.local.LocalDataManager
import com.square.android.utils.AppInBackgroundEvent
import com.square.android.utils.AppInForegroundEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.inject
import java.util.*

class TutorialLoadedEvent(val data: Tutorial?)

class TutorialService : Service() {

    private val eventBus: EventBus by inject()
    private val repository: Repository by inject()

    enum class TutorialKey {
        PLACE, BOOKING, REDEMPTIONS, SELECT_OFFER, REVIEW
    }

    private var tutorial: Tutorial? = null

    private lateinit var serviceTutorialView: TutorialView
    private var mWindowManager: WindowManager? = null
    private var windowLayoutParams: WindowManager.LayoutParams? = null

    private var localDataManager: LocalDataManager? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        eventBus.register(this)

        setupService()
    }

    override fun onDestroy() {
        eventBus.unregister(this)
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTutorialLoadedEvent(event: TutorialLoadedEvent) {
        tutorial = event.data
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAppInBackgroundEvent(event: AppInBackgroundEvent) {
        serviceTutorialView.visibility = View.GONE
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAppInForegroundEvent(event: AppInForegroundEvent) {
        serviceTutorialView.visibility = View.VISIBLE
    }

    private fun setupService() {
        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager?

        if (android.os.Build.VERSION.SDK_INT < 26) {
            windowLayoutParams = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.TYPE_TOAST,
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSLUCENT)
            windowLayoutParams!!.gravity = Gravity.CENTER or Gravity.TOP

        } else {
            windowLayoutParams = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSLUCENT)
            windowLayoutParams!!.gravity = Gravity.CENTER or Gravity.TOP
        }

        serviceTutorialView = TutorialView(this)
        serviceTutorialView.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (event.action == MotionEvent.ACTION_UP) {
                    serviceTutorialView.onTouchEvent(event)

                    return true
                }
                return false
            }
        })

        localDataManager = LocalDataManager(this)
    }

    override fun onStartCommand(intent: Intent?, flag: Int, startId: Int): Int {
        if(intent != null && intent.hasExtra(TUTORIAL_APP_EXTRA_KEY)) {
            val tutorialKey = getTutorialKey(intent)?.let { TutorialKey.valueOf(it) }

            tutorialKey?.run {
                if(tutorial == null){
                    Timer().schedule(object : TimerTask() {
                        override fun run() {
                            onStartCommand(intent, flag, startId)
                        }
                    }, 50)
                } else{
                    startTutorial(this)
                }
            }
        }

        return START_STICKY
    }

    private fun startTutorial(tutorialKeyToStart: TutorialKey) = tutorial?.run {
        tutorialKey = tutorialKeyToStart
        tutorialView = serviceTutorialView

        onFinishTutorialListener = {
            commonTutorialFinished(tutorialKeyToStart, it)
        }

        Handler(Looper.getMainLooper()).post {
            mWindowManager!!.addView(serviceTutorialView, windowLayoutParams)
            show()
        }
    }

    private fun getTutorialKey(intent: Intent): String?
            = intent.getStringExtra(TUTORIAL_APP_EXTRA_KEY)

    fun commonTutorialFinished(tutorialKey: TutorialKey, dontShowAgain: Boolean) {

        //TODO: is this handler necessary?
        Handler(Looper.getMainLooper()).post {
            mWindowManager!!.removeViewImmediate(serviceTutorialView)
            repository.setTutorialDontShowAgain(tutorialKey, dontShowAgain)
            localDataManager!!.setTutorialDontShowAgain(tutorialKey, dontShowAgain)
        }

        tutorial = null
    }

    companion object {
        val TUTORIAL_APP_EXTRA_KEY = "square_tutorial_app_key"
    }
}
