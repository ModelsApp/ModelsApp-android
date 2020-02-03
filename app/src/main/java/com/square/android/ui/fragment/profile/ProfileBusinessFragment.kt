package com.square.android.ui.fragment.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.Profile
import com.square.android.presentation.presenter.profile.*
import com.square.android.presentation.view.profile.ProfileBusinessView
import com.square.android.ui.fragment.BaseFragment
import kotlinx.android.synthetic.main.page_profile_business.*
import org.jetbrains.anko.bundleOf

class ProfileBusinessFragment: BaseFragment(), ProfileBusinessView {

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(user: Profile.User): ProfileBusinessFragment {
            val fragment = ProfileBusinessFragment()

            val args = bundleOf(EXTRA_USER to user)
            fragment.arguments = args

            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: ProfileBusinessPresenter

    @ProvidePresenter
    fun providePresenter() = ProfileBusinessPresenter(arguments?.getParcelable(EXTRA_USER) as Profile.User)

    private var businessAdapter: ProfileItemAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.page_profile_business, container, false)
    }

    override fun showData(user: Profile.User) {
//        businessAdapter = ProfileItemAdapter(listOf(), this, businessHandler = this)
//
//        rvItems.itemAnimator = null
//        rvItems.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL,false)
//        rvItems.adapter = businessAdapter
    }

}