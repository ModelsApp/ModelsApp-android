package com.square.android.ui.fragment.signUp

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.square.android.R
import com.square.android.presentation.presenter.signUp.SignUpMainPresenter
import com.square.android.presentation.view.signUp.SignUpMainView
import com.square.android.ui.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_sign_up_main.*

class SignUpMainFragment: BaseFragment(), SignUpMainView {

    private var currentPagerPosition = 0

    @InjectPresenter
    lateinit var presenter: SignUpMainPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_up_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFragmentAdapter()

        btnNext.setOnClickListener {
      // make all this three fragments of one base type
            if(((viewPager.adapter as SignUpFragmentAdapter).getRegisteredFragment(currentPagerPosition) as BaseFragment).validate()){

                if(currentPagerPosition < 2){
                    viewPager.setCurrentItem(currentPagerPosition + 1)

                } else{
                    //TODO presenter.sendRequest (show progress, send all data, fill local user data like in old register and navigate to mainAc )
                }
            }
        }

        arrowBack.setOnClickListener { if(currentPagerPosition > 0) viewPager.setCurrentItem(currentPagerPosition -1, true) else activity?.onBackPressed() }

        nextTv.setOnClickListener { btnNext.callOnClick() }
    }
    private fun setupFragmentAdapter() {
        viewPager.isPagingEnabled = false
        viewPager.adapter = SignUpFragmentAdapter(childFragmentManager, presenter.profileInfo!!)

        viewPager.offscreenPageLimit = 2

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
        signUpNumber.text = (position + 1).toString() +"/"+"3"

        btnNext.text = if(position < 2) getString(R.string.next) else getString(R.string.send_request)

        nextTv.text = if(position < 2) getString(R.string.next) else getString(R.string.send_request)
        nextTv.setTextColor(ContextCompat.getColor(nextTv.context, if(position < 2) R.color.text_gray else android.R.color.black))
    }

    override fun showProgress() {

    }

    override fun hideProgress() {

    }

}