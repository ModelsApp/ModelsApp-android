package com.square.android.ui.fragment.signUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.ProfileInfo
import com.square.android.presentation.presenter.signUp.SignUpThreePresenter
import com.square.android.presentation.view.signUp.SignUpThreeView
import com.square.android.ui.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_sign_up_3.*
import org.jetbrains.anko.bundleOf
import com.square.android.ui.activity.start.FacebookLoginEvent
import com.square.android.ui.activity.start.StartActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.inject

private const val EXTRA_MODEL_THIRD = "EXTRA_MODEL"

class SignUpThreeFragment: BaseFragment(), SignUpThreeView {

    @InjectPresenter
    lateinit var presenter: SignUpThreePresenter

    @ProvidePresenter
    fun providePresenter(): SignUpThreePresenter = SignUpThreePresenter(getModel())

    var images: MutableList<ByteArray?> = mutableListOf(null, null, null)

    private val eventBus: EventBus by inject()

    var shouldLogOutfb = true

    var loggedOutFromFb = false

    var fbAdded = false

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFacebookLoginEvent(event: FacebookLoginEvent) {
        fbAdded = true

        fbIc.setImageDrawable(fbIc.context.getDrawable(R.drawable.checkmark))

        fbTv.text = event.data.name+" "+event.data.surname
        fbTv.visibility = View.VISIBLE

        presenter.info.name = event.data.name
        presenter.info.surname = event.data.surname
        presenter.info.imageUrl = event.data.imageUrl
    }

    override fun validate(): Boolean {
        var allOk = true

        //TODO:F

        if(allOk){
            shouldLogOutfb = false
        }

        return true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_up_3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fbContainer.setOnClickListener { if(!fbAdded) (activity as StartActivity).logInRegister()}

        eventBus.register(this)
    }

    private fun getModel() = arguments?.getParcelable(EXTRA_MODEL_THIRD) as ProfileInfo

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(info: ProfileInfo): SignUpThreeFragment {
            val fragment = SignUpThreeFragment()

            val args = bundleOf(EXTRA_MODEL_THIRD to info)
            fragment.arguments = args

            return fragment
        }
    }

    override fun onDestroy() {
        eventBus.unregister(this)

        if(shouldLogOutfb){
            (activity as StartActivity).logOutRegister()
        }

        super.onDestroy()
    }

    override fun onStart() {
        if(!loggedOutFromFb){
            loggedOutFromFb = true
            (activity as StartActivity).logOutRegister()
        }
        super.onStart()
    }

//    TODO:F when registration finished -> if exception, logOutRegister()

//    TODO:F send fb access token to api when registration done

}
