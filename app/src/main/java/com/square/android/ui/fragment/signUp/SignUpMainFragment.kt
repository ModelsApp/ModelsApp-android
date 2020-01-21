package com.square.android.ui.fragment.signUp

import android.app.Activity
import android.os.Bundle
import android.view.*
import androidx.viewpager.widget.ViewPager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.google.firebase.iid.FirebaseInstanceId
import com.square.android.App
import com.square.android.R
import com.square.android.presentation.presenter.signUp.SignUpMainPresenter
import com.square.android.presentation.view.signUp.SignUpMainView
import com.square.android.ui.dialogs.LoadingDialog
import com.square.android.ui.fragment.BaseFragment
import com.square.android.utils.TokenUtils
import kotlinx.android.synthetic.main.fragment_sign_up_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.inject

class SignUpMainFragment: BaseFragment(), SignUpMainView {

    private var currentPagerPosition = 0

    private val eventBus: EventBus by inject()

    private var loadingDialog: LoadingDialog? = null

    @InjectPresenter
    lateinit var presenter: SignUpMainPresenter

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEnableBtnEvent(event: EnableBtnEvent) {
        btnNext.isEnabled = true
        nextTv.isEnabled = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_up_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(activity!!)

        eventBus.register(this)

        setupFragmentAdapter()

        arrowBack.setOnClickListener { if(currentPagerPosition > 0) viewPager.setCurrentItem(currentPagerPosition -1, true) else activity?.onBackPressed() }

        nextTv.setOnClickListener { btnNext.callOnClick() }

        btnNext.setOnClickListener {
            if(((viewPager.adapter as SignUpFragmentAdapter).getRegisteredFragment(currentPagerPosition) as BaseFragment).validate()){
                //TODO will be (position < 2)
                if(currentPagerPosition < 1){
                    viewPager.setCurrentItem(currentPagerPosition + 1)

                } else{
                    presenter.register()
                }
            }
        }

        logInTv.setOnClickListener { activity?.onBackPressed() }
    }

    private fun setupFragmentAdapter() {
        viewPager.isPagingEnabled = false
        viewPager.adapter = SignUpFragmentAdapter(childFragmentManager, presenter.profileInfo!!)

        viewPager.offscreenPageLimit = 3

        viewPager.addOnPageChangeListener( object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) { }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) { }
            override fun onPageSelected(position: Int) {
                currentPagerPosition = position
                setUpPage(currentPagerPosition)
            }
        })

        setUpPage(0)
    }

    fun setUpPage(position: Int){
        //TODO will be (position < 2)
        btnNext.isEnabled = position < 1

        //TODO will be (position < 2)
        nextTv.isEnabled = position < 1

        //TODO will be (position 3)
        signUpNumber.text = (position + 1).toString() +"/"+"2"

        //TODO will be (position < 2)
        btnNext.text = if(position < 1) getString(R.string.next) else getString(R.string.send_request)

        //TODO will be (position < 2)
        nextTv.text = if(position < 1) getString(R.string.next) else getString(R.string.send_request)

        //TODO will be (position == 2)
        if(position == 1){
            ((viewPager.adapter as SignUpFragmentAdapter).getRegisteredFragment(currentPagerPosition) as SignUpThreeFragment).checkAndEnableBtn()
        }
    }

    override fun sendFcmToken() {
        if (presenter.repository.getUserInfo().id != 0L) {
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(activity as Activity) { instanceIdResult ->
                val newToken = instanceIdResult.token
                TokenUtils.sendTokenToApi(App.INSTANCE, presenter.repository, newToken)
            }
        }
    }

    override fun showProgress() { }

    override fun hideProgress() { }

    override fun showLoadingDialog() {
        loadingDialog?.show()
    }

    override fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }

    override fun onDestroy() {
        eventBus.unregister(this)

        super.onDestroy()
    }

}