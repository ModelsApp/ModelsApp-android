package com.square.android.ui.fragment.signUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.data.pojo.ProfileInfo
import com.square.android.presentation.presenter.signUp.SignUpThreePresenter
import com.square.android.presentation.view.signUp.SignUpThreeView
import com.square.android.ui.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_sign_up_3.*
import org.jetbrains.anko.bundleOf
import com.square.android.ui.activity.start.RegisterFacebookEvent
import com.square.android.ui.activity.start.StartActivity
import com.square.android.ui.dialogs.LoadingDialog
import com.squareup.picasso.Picasso
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.inject
import android.graphics.drawable.Drawable
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import com.squareup.picasso.Target
import java.lang.Exception
import com.square.android.R
import com.square.android.extensions.convertToByteArray

private const val EXTRA_MODEL_THIRD = "EXTRA_MODEL"

class EnableBtnEvent()

class SignUpThreeFragment: BaseFragment(), SignUpThreeView {

    @InjectPresenter
    lateinit var presenter: SignUpThreePresenter

    @ProvidePresenter
    fun providePresenter(): SignUpThreePresenter = SignUpThreePresenter(getModel())

    private var loadingDialog: LoadingDialog? = null

    private var shouldLogOutfb = true

    private var loggedOutFromFb = false

    private var fbAdded = false

    private var instagramAdded = false

    private val eventBus: EventBus by inject()

    private var instagramDialog: AddInstagramDialog? = null

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRegisterFacebookEvent(event: RegisterFacebookEvent) {
        fbAdded = true

        fbIc.setImageDrawable(fbIc.context.getDrawable(R.drawable.checkmark))

        fbTv.text = event.data.name + " " + event.data.surname
        fbTv.visibility = View.VISIBLE

        presenter.info.name = event.data.name
        presenter.info.surname = event.data.surname
        presenter.info.fbToken = event.data.fbAccessToken

        event.data.imageUrl?.let {
            showLoadingDialog()

            Handler(Looper.getMainLooper()).post {
                Picasso.get()
                        .load(it).placeholder(R.drawable.ic_profile).into(object : Target {
                            override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                                try {
                                    presenter.info.image = bitmap.convertToByteArray()

                                } catch (e: Exception) { }

                                hideLoadingDialog()
                            }

                            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                                hideLoadingDialog()
                            }

                            override fun onPrepareLoad(placeHolderDrawable: Drawable) {
                            }
                        })
            }

            checkAndEnableBtn()
        }
    }

    override fun validate(): Boolean {
        return if(!fbAdded || !instagramAdded){
            false
        } else{
            presenter.info.instagramName = instagramTv.text.toString().replace("@","")

            shouldLogOutfb = false
            true
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_up_3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(activity!!)

        eventBus.register(this)

        fbContainer.setOnClickListener { if(!fbAdded) (activity as StartActivity).logInRegister()}

        instagramDialog = AddInstagramDialog(activity!!)

        instagramContainer.setOnClickListener { instagramDialog?.show(instagramTv.text.toString()) {
            instagramTv.text = "@$it"
            instagramTv.visibility = View.VISIBLE
            instagramIc.setImageDrawable(fbIc.context.getDrawable(R.drawable.checkmark))
            instagramAdded = true
            checkAndEnableBtn()
        } }
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

        checkAndEnableBtn()

        super.onStart()
    }

    fun checkAndEnableBtn(){
        if(fbAdded && instagramAdded){
            eventBus.post(EnableBtnEvent())
        }
    }

    override fun showLoadingDialog() {
       loadingDialog?.show()
    }

    override fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }

    override fun showProgress() { }

    override fun hideProgress() { }

}
