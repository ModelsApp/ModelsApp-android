package com.square.android.ui.fragment.signUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
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
import com.square.android.data.pojo.SignUpData
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

    private var socialDialog: AddSocialDialog? = null

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

        if(!eventBus.isRegistered(this)){
            eventBus.register(this)
        }

        fbContainer.setOnClickListener { if(!fbAdded) (activity as StartActivity).logInRegister()}

        socialDialog = AddSocialDialog(activity!!)

        setupClicks()

        requirementsTv.setOnClickListener { presenter.navigateToRequirements() }
    }

    private fun setupClicks(){
        instagramContainer.setOnClickListener {
            socialDialog?.show(SocialType.Instagram, instagramTv.text.toString()) {
                instagramTv.text = "@$it"
                instagramTv.visibility = View.VISIBLE
                instagramIc.setImageDrawable(instagramIc.context.getDrawable(R.drawable.checkmark))
                instagramAdded = true
                checkAndEnableBtn()
            } }

        googleContainer.setOnClickListener {
            socialDialog?.show(SocialType.Google, googleTv.text.toString()) {
                googleTv.text = "@$it"
                googleTv.visibility = View.VISIBLE
                googleIc.setImageDrawable(googleIc.context.getDrawable(R.drawable.checkmark))
            } }

        vkContainer.setOnClickListener {
            socialDialog?.show(SocialType.VK, vkTv.text.toString()) {
                vkTv.text = "@$it"
                vkTv.visibility = View.VISIBLE
                vkIc.setImageDrawable(vkIc.context.getDrawable(R.drawable.checkmark))
            } }

        tiktokContainer.setOnClickListener {
            socialDialog?.show(SocialType.TikTok, tiktokTv.text.toString()) {
                tiktokTv.text = "@$it"
                tiktokTv.visibility = View.VISIBLE
                tiktokIc.setImageDrawable(tiktokIc.context.getDrawable(R.drawable.checkmark))
            } }

        pinterestContainer.setOnClickListener {
            socialDialog?.show(SocialType.Pinterest, pinterestTv.text.toString()) {
                pinterestTv.text = "@$it"
                pinterestTv.visibility = View.VISIBLE
                pinterestIc.setImageDrawable(pinterestIc.context.getDrawable(R.drawable.checkmark))
            } }

        yelpContainer.setOnClickListener {
            socialDialog?.show(SocialType.Yelp, yelpTv.text.toString()) {
                yelpTv.text = "@$it"
                yelpTv.visibility = View.VISIBLE
                yelpIc.setImageDrawable(yelpIc.context.getDrawable(R.drawable.checkmark))
            } }

        youtubeContainer.setOnClickListener {
            socialDialog?.show(SocialType.Youtube, youtubeTv.text.toString()) {
                youtubeTv.text = "@$it"
                youtubeTv.visibility = View.VISIBLE
                youtubeIc.setImageDrawable(youtubeIc.context.getDrawable(R.drawable.checkmark))
            } }

        snapchatContainer.setOnClickListener {
            socialDialog?.show(SocialType.Snapchat, snapchatTv.text.toString()) {
                snapchatTv.text = "@$it"
                snapchatTv.visibility = View.VISIBLE
                snapchatIc.setImageDrawable(snapchatIc.context.getDrawable(R.drawable.checkmark))
            } }

        bloggerContainer.setOnClickListener {
            socialDialog?.show(SocialType.Blogger, bloggerTv.text.toString()) {
                bloggerTv.text = "@$it"
                bloggerTv.visibility = View.VISIBLE
                bloggerIc.setImageDrawable(bloggerIc.context.getDrawable(R.drawable.checkmark))
            } }

        tripAdvisorContainer.setOnClickListener {
            socialDialog?.show(SocialType.TripAdvisor, tripAdvisorTv.text.toString()) {
                tripAdvisorTv.text = "@$it"
                tripAdvisorTv.visibility = View.VISIBLE
                tripAdvisorIc.setImageDrawable(tripAdvisorIc.context.getDrawable(R.drawable.checkmark))
            } }

        modelscomContainer.setOnClickListener {
            socialDialog?.show(SocialType.Modelscom, modelscomTv.text.toString()) {
                modelscomTv.text = "@$it"
                modelscomTv.visibility = View.VISIBLE
                modelscomIc.setImageDrawable(modelscomIc.context.getDrawable(R.drawable.checkmark))
            } }
    }

    private fun getModel() = arguments?.getParcelable(EXTRA_MODEL_THIRD) as SignUpData

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(info: SignUpData): SignUpThreeFragment {
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
