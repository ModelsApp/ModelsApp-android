package com.square.android.ui.fragment.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.square.android.R
import com.square.android.presentation.presenter.profile.ProfilePresenter
import com.square.android.presentation.view.profile.ProfileView
import com.square.android.ui.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment: BaseFragment(), ProfileView {

    @InjectPresenter
    lateinit var presenter: ProfilePresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        iconSettings.setOnClickListener { presenter.openSettings() }
    }

    override fun setupFragmentAdapter() {
        viewPager.adapter = ProfileFragmentAdapter(childFragmentManager)
        tabLayout.setupWithViewPager(viewPager)

        viewPager.offscreenPageLimit = 2
    }

}